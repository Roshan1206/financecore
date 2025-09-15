package com.financecore.transaction.service.impl;

import com.financecore.transaction.dto.response.PageResponse;
import com.financecore.transaction.dto.response.TransactionResponse;
import com.financecore.transaction.entity.Transaction;
import com.financecore.transaction.entity.enums.Channel;
import com.financecore.transaction.entity.enums.Status;
import com.financecore.transaction.entity.enums.TransactionType;
import com.financecore.transaction.mapper.TransactionMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service class for transactions.
 *
 * @author Roshan
 */
@Service
public class TransactionServiceImpl implements TransactionService {

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
     * Injecting required dependency via constructor injection
     */
    public TransactionServiceImpl(EntityManager entityManager, TransactionRepository transactionRepository) {
        this.entityManager = entityManager;
        this.transactionRepository = transactionRepository;
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

        long totalCount =
                getTotalCount(accountId, fromDate, toDate, amount, type, status, channel, cb);

        TypedQuery<Transaction> typedQuery = entityManager.createQuery(query)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult((int) pageable.getOffset());

        List<Transaction> transactions = typedQuery.getResultList();
        List<TransactionResponse> responses = transactions.stream().map(TransactionMapper::mapToTransactionResponse).toList();
        PageImpl<TransactionResponse> transactionResponses = new PageImpl<>(responses, pageable, totalCount);
        return new PageResponse<>(transactionResponses);
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
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Transaction> transactionRoot = query.from(Transaction.class);

        query.select(cb.count(transactionRoot));
        createFilters(accountId, fromDate, toDate, amount, type, status, channel, cb, transactionRoot, query);

        return entityManager.createQuery(query).getSingleResult();
    }


    /**
     * Apply filters in transaction query
     *
     * @param accountId account from which transactions happened
     * @param fromDate transactions from date
     * @param toDate transactions till date
     * @param amount transaction amount
     * @param type transaction type
     * @param status transaction status
     * @param channel channel from which transaction happened
     * @param cb CriteriaBuilder for creating query
     * @param transactionRoot root entity for transaction
     * @param query query
     */
    private void createFilters(String accountId, LocalDate fromDate, LocalDate toDate, BigDecimal amount, TransactionType type, Status status, Channel channel,
                               CriteriaBuilder cb, Root<Transaction> transactionRoot, CriteriaQuery<?> query) {
        List<Predicate> predicates = new ArrayList<>();

        if (accountId != null){
            long accId = transactionRepository.getAccountId(Long.parseLong(accountId)).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id not found. Account id: " + accountId)
            );
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
}
