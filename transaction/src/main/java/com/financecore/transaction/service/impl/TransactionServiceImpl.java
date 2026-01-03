package com.financecore.transaction.service.impl;

import com.financecore.transaction.service.communication.CommunicationClient;
import com.financecore.transaction.constants.Constant;
import com.financecore.transaction.dto.request.SelfTransferRequest;
import com.financecore.transaction.dto.request.TransferRequest;
import com.financecore.transaction.dto.request.UpdateAccountRequest;
import com.financecore.transaction.dto.response.BalanceResponse;
import com.financecore.transaction.dto.response.PageResponse;
import com.financecore.transaction.dto.response.TransactionResponse;
import com.financecore.transaction.dto.response.TransferResponse;
import com.financecore.transaction.entity.Transaction;
import com.financecore.transaction.entity.TransactionCategory;
import com.financecore.transaction.entity.enums.Channel;
import com.financecore.transaction.entity.enums.ParentCategory;
import com.financecore.transaction.entity.enums.Status;
import com.financecore.transaction.entity.enums.TransactionType;
import com.financecore.transaction.mapper.TransactionMapper;
import com.financecore.transaction.repoitory.TransactionCategoryRepository;
import com.financecore.transaction.repoitory.TransactionRepository;
import com.financecore.transaction.service.TransactionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for transactions.
 *
 * @author Roshan
 */
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final CommunicationClient communicationClient;

    /**
     * To perform db operations
     */
    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Repository responsible for managing transactions
     */
    private final TransactionRepository transactionRepository;

    /**
     * Repository responsible for managing Transaction categories
     */
    private final TransactionCategoryRepository transactionCategoryRepository;


    /**
     * Injecting required dependency via constructor injection
     */
    public TransactionServiceImpl(CommunicationClient communicationClient, EntityManager entityManager,
                                  TransactionRepository transactionRepository,
                                  TransactionCategoryRepository transactionCategoryRepository) {
        this.communicationClient = communicationClient;
        this.entityManager = entityManager;
        this.transactionRepository = transactionRepository;
        this.transactionCategoryRepository = transactionCategoryRepository;
    }


    /**
     * Get paginated transaction for a particular account
     *
     * @param accountId account from which transactions happened
     * @param fromDate transactions from date
     * @param toDate transactions till date
     * @param amount transaction amount
     * @param type transaction type
     * @param status transaction status
     * @param channel channel from which transaction happened
     * @param pageable paging information
     *
     * @return Page of transactions
     */
    @Override
    public PageResponse<TransactionResponse> getTransactions(String accountId, LocalDate fromDate, LocalDate toDate, BigDecimal amount,
                                                     TransactionType type, Status status, Channel channel, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> transactionRoot = query.from(Transaction.class);

        createFilters(accountId, fromDate, toDate, amount, type, status, channel, cb, transactionRoot, query);

        if (pageable.getSort().isSorted()){
            List<Order> orders = new ArrayList<>();

            pageable.getSort().forEach(sortOrder -> {
                String property = sortOrder.getProperty();
                Path<?> path = transactionRoot.get(property);

                if (sortOrder.isAscending()) {
                    orders.add(cb.asc(path));
                } else {
                    orders.add(cb.desc(path));
                }
            });
            query.orderBy(orders);
        }

        long totalCount = getTotalCount(accountId, fromDate, toDate, amount, type, status, channel, cb);

        TypedQuery<Transaction> typedQuery = entityManager.createQuery(query)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult((int) pageable.getOffset());

        List<Transaction> transactions = typedQuery.getResultList();
        List<TransactionResponse> responses = transactions.stream().map(TransactionMapper::mapToTransactionResponse).toList();
        PageImpl<TransactionResponse> transactionResponses = new PageImpl<>(responses, pageable, totalCount);
        return new PageResponse<>(transactionResponses);
    }


    /**
     * Get detailed transaction information with full audit trail
     *
     * @param transactionId transaction ID
     * @return transaction
     */
    @Override
    public TransactionResponse getDetailedTransaction(long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with given id: " + transactionId)
        );
        return TransactionMapper.mapToTransactionResponse(transaction);
    }


    /**
     * Create withdrawal for self
     *
     * @param accountNumber account number
     * @param selfTransferRequest account number and amount
     */
    @Override
    @Transactional
    public TransferResponse createWithdrawal(String accountNumber, SelfTransferRequest selfTransferRequest) {
        log.debug("Withdrawal initiated...");
        validateAccountNumberAndBalance(selfTransferRequest.getAmount(), accountNumber);
        Channel channel = mapToChannel(selfTransferRequest.getChannel());
        TransactionType transactionType = TransactionType.DEBIT;

        TransactionCategory transactionCategory = new TransactionCategory(Constant.WITHDRAWAL, ParentCategory.TRANSFER);
        TransactionCategory savedTransactionCategory = transactionCategoryRepository.save(transactionCategory);
        BigDecimal amount = selfTransferRequest.getAmount();
        updateAccount(accountNumber, Constant.DEBIT, amount);
        Transaction transaction = new Transaction(accountNumber, amount, channel, transactionType, savedTransactionCategory, null);
        transaction.setStatus(Status.COMPLETED);
        Transaction savedTransaction = transactionRepository.save(transaction);
        transactionRepository.save(savedTransaction);
        return TransactionMapper.createTransferResponse(savedTransaction);
    }


    /**
     * Create deposit for self
     *
     * @param accountNumber account number
     * @param selfTransferRequest account number and amount
     */
    @Override
    @Transactional
    public TransferResponse createDeposit(String accountNumber, SelfTransferRequest selfTransferRequest) {
        log.debug("Deposit initiated...");
        validateAccountNumber(accountNumber);
        Channel channel = mapToChannel(selfTransferRequest.getChannel());
        TransactionType transactionType = TransactionType.CREDIT;

        TransactionCategory transactionCategory = new TransactionCategory(Constant.DEPOSIT, ParentCategory.TRANSFER);
        TransactionCategory savedTransactionCategory = transactionCategoryRepository.save(transactionCategory);
        BigDecimal amount = selfTransferRequest.getAmount();
        Transaction transaction = new Transaction(accountNumber, amount, channel, transactionType, savedTransactionCategory, null);
        Transaction savedTransaction = transactionRepository.save(transaction);
        updateAccount(accountNumber, Constant.CREDIT, amount);
        savedTransaction.setStatus(Status.COMPLETED);
        transactionRepository.save(savedTransaction);
        return TransactionMapper.createTransferResponse(savedTransaction);
    }


    /**
     * Create deposit for self
     *
     * @param accountNumber   account number
     * @param transferRequest account number, channel and amount
     */
    @Override
    @Transactional
    public TransferResponse createTransfer(String accountNumber, TransferRequest transferRequest) {
        log.debug("Transfer initiated...");
        BigDecimal amount = transferRequest.getAmount();
        String toAccountNumber = transferRequest.getToAccountNumber();

        validateAccountNumberAndBalance(amount, accountNumber);
        validateAccountNumber(toAccountNumber);

        Channel channel = mapToChannel(transferRequest.getChannel());
        TransactionType transactionType = TransactionType.TRANSFER;

        updateAccount(accountNumber, Constant.DEBIT, amount);
        TransactionCategory transactionCategory = new TransactionCategory(Constant.TRANSFER, ParentCategory.TRANSFER);
        TransactionCategory savedTransactionCategory = transactionCategoryRepository.save(transactionCategory);
        Transaction transaction = new Transaction(accountNumber, toAccountNumber, amount, channel, transactionType, savedTransactionCategory, null);
        Transaction savedTransaction = transactionRepository.save(transaction);
        updateAccount(toAccountNumber, Constant.CREDIT, amount);
        savedTransaction.setStatus(Status.COMPLETED);
        transactionRepository.save(savedTransaction);
        return TransactionMapper.createTransferResponse(savedTransaction);
    }


    /**
     * Get number of transaction for a particular account for paging
     *
     * @param accountId account from which transactions happened
     * @param fromDate transactions from date
     * @param toDate transactions till date
     * @param amount transaction amount
     * @param type transaction type
     * @param status transaction status
     * @param channel channel from which transaction happened
     * @param cb CriteriaBuilder for creating query
     *
     * @return no of transactions
     */
    private long getTotalCount(String accountId, LocalDate fromDate, LocalDate toDate, BigDecimal amount, TransactionType type,
                               Status status, Channel channel, CriteriaBuilder cb){
        log.debug("Getting total rows for transaction");
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Transaction> transactionRoot = query.from(Transaction.class);

        query.select(cb.count(transactionRoot));
        createFilters(accountId, fromDate, toDate, amount, type, status, channel, cb, transactionRoot, query);

        return entityManager.createQuery(query).getSingleResult();
    }


    /**
     * Apply filters in transaction query
     *
     * @param accountId       account from which transactions happened
     * @param fromDate        transactions from date
     * @param toDate          transactions till date
     * @param amount          transaction amount
     * @param type            transaction type
     * @param status          transaction status
     * @param channel         channel from which transaction happened
     * @param cb              CriteriaBuilder for creating query
     * @param transactionRoot root entity for transaction
     * @param query           query
     */
    private void createFilters(String accountId, LocalDate fromDate, LocalDate toDate, BigDecimal amount, TransactionType type, Status status, Channel channel,
                               CriteriaBuilder cb, Root<Transaction> transactionRoot, CriteriaQuery<?> query) {
        log.debug("Adding required filter while building query for search query");
        List<Predicate> predicates = new ArrayList<>();

        if (accountId != null){
            Optional<Long> accId = transactionRepository.getAccountId(Long.parseLong(accountId));
            if (accId.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id not found. Account id: " + accountId);
            }
            predicates.add(cb.equal(transactionRoot.get("fromAccountId"), accountId));
        }
        if (fromDate != null && fromDate.isBefore(LocalDate.now())){
            if (toDate != null && toDate.isBefore(LocalDate.now())){
                if (toDate.isBefore(fromDate)){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is not in correct order");
                }
                predicates.add(cb.between(transactionRoot.get("date"), fromDate, toDate));
            } else {
                predicates.add(cb.greaterThanOrEqualTo(transactionRoot.get("date"), fromDate));
            }
        }
        if (amount != null && !amount.equals(BigDecimal.ZERO)){
            predicates.add(cb.greaterThanOrEqualTo(transactionRoot.get("amount"), amount));
        }
        if (type != null){
            predicates.add(cb.equal(transactionRoot.get("type"), type));
        }
        if (status != null){
            predicates.add(cb.equal(transactionRoot.get("status"), status));
        }
        if (channel != null){
            predicates.add(cb.equal(transactionRoot.get("channel"), channel));
        }
        if (!predicates.isEmpty()){
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
    }


    /**
     * Validate account balance after validating account
     *
     * @param amount transaction amount
     * @param accountNumber account number
     */
    private void validateAccountNumberAndBalance(BigDecimal amount, String accountNumber) {
        validateAccountNumber(accountNumber);
        log.debug("Checking whether account has required balance for transaction");
        ResponseEntity<BalanceResponse> accountBalance = communicationClient.getAccountBalance(accountNumber);

        if (null == accountBalance || accountBalance.getBody() != null || !accountBalance.getStatusCode().is2xxSuccessful()){
            log.error("Cannot retrieve account balance");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something bad happened....");
        }
        BigDecimal currentBalance = accountBalance.getBody().getAvailableBalance();
        if (currentBalance.compareTo(amount) < 0){
            log.error(Constant.INSUFFICIENT_BALANCE);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.INSUFFICIENT_BALANCE);
        }
    }


    /**
     * Validate account balance after validating account
     *
     * @param accountNumber account number
     */
    private void validateAccountNumber(String accountNumber) {
        log.debug("Validating account through accounts service");
        boolean isTransferAccountValid = communicationClient.validateAccount(accountNumber);
        if (!isTransferAccountValid){
            log.error(Constant.INVALID_ACCOUNT + "{}", accountNumber);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constant.INVALID_ACCOUNT + accountNumber);
        }
    }


    /**
     * Send request to accounts service to update balance
     *
     * @param accountNumber account number
     * @param operation credit/debit
     * @param amount amount to be updated
     */
    private void updateAccount(String accountNumber, String operation, BigDecimal amount) {
        log.debug("Sending HTTP request to update account balance for account number {}", accountNumber);
        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(accountNumber, operation, amount);
        communicationClient.updateAccountBalance(accountNumber, updateAccountRequest);
    }


    /**
     * Map to Channel Enum
     *
     * @param channel channel value
     */
    private Channel mapToChannel(String channel) {
        String channelValue = channel.trim().toUpperCase();
        return switch (channelValue) {
            case "ATM" -> Channel.ATM;
            case "ONLINE" -> Channel.ONLINE;
            case "MOBILE" -> Channel.MOBILE;
            case "UPI" -> Channel.UPI;
            case "BRANCH" -> Channel.BRANCH;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Channel value is not correct");
        };
    }
}
