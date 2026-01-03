package com.financecore.account.service.impl;

import com.financecore.account.constant.Constants;
import com.financecore.account.dto.request.AccountSearchCriteria;
import com.financecore.account.dto.request.CreateAccountRequest;
import com.financecore.account.dto.request.UpdateAccountRequest;
import com.financecore.account.dto.response.BalanceResponse;
import com.financecore.account.dto.response.AccountsResponse;
import com.financecore.account.dto.response.PageResponse;
import com.financecore.account.entity.Account;
import com.financecore.account.entity.AccountProduct;
import com.financecore.account.entity.enums.AccountStatus;
import com.financecore.account.repository.AccountRepository;
import com.financecore.account.service.AccountProductService;
import com.financecore.account.service.AccountService;
import com.financecore.account.service.communication.CommunicationClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for AccountService.
 * Responsible for managing accounts.
 *
 * @author Roshan
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    /**
     * Calls to customer service
     */
    private final CommunicationClient communicationClient;

    /**
     * Creating and executing queries.
     */
    @PersistenceContext
    private final EntityManager entityManager;


    /**
     * Service class for managing AccountProduct
     */
    private final AccountProductService accountProductService;


    /**
     * Repository responsible managing crud operations.
     */
    private final AccountRepository accountRepository;


    /**
     * Injecting required dependency via constructor injection.
     */
    public AccountServiceImpl(CommunicationClient communicationClient, EntityManager entityManager,
                              AccountProductService accountProductService, AccountRepository accountRepository){
        this.communicationClient = communicationClient;
        this.entityManager = entityManager;
        this.accountProductService = accountProductService;
        this.accountRepository = accountRepository;
    }


    /**
     * Filter and fetch accounts based on requirements.
     *
     * @param searchCriteria options to be used while search operations
     * @param pageable  page no, size and sorting details
     * @return get paginated accounts
     */
    @Override
    public PageResponse<AccountsResponse> getAccounts(AccountSearchCriteria searchCriteria, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountsResponse> query = cb.createQuery(AccountsResponse.class);
        Root<Account> root = query.from(Account.class);
        Join<Account, AccountProduct> accountProductJoin = root.join("accountProduct", JoinType.LEFT);

        query.select(cb.construct(
                AccountsResponse.class,
                root.get("accountNumber"),
                root.get("customerId"),
                accountProductJoin.get("productName"),
                accountProductJoin.get("productType"),
                root.get("accountStatus"),
                root.get("openedAt"),
                root.get("balance"),
                root.get("availableBalance")
        ));

        createFilter(searchCriteria, cb, query, root, accountProductJoin);
        long totalCount = getTotalCount(searchCriteria, cb);

        if (pageable.getSort().isSorted()){
            List<Order> orders = new ArrayList<>();

            pageable.getSort().forEach(order -> {
                String property = order.getProperty();
                Path<?> path = root.get(property);

                if (order.isAscending()){
                    orders.add(cb.asc(path));
                } else {
                    orders.add(cb.desc(path));
                }
            });
            query.orderBy(orders);
        }

        List<AccountsResponse> accountsResponses = entityManager.createQuery(query)
                                                            .setFirstResult((int) pageable.getOffset())
                                                            .setMaxResults(pageable.getPageSize())
                                                            .getResultList();
        PageImpl<AccountsResponse> responsePage = new PageImpl<>(accountsResponses, pageable, totalCount);
        return new PageResponse<>(responsePage);
    }


    /**
     * update account status for given account number
     *
     * @param accountNumber account to be updated
     * @param accountStatus status to be updated
     *
     * @return message
     */
    @Override
    public String updateAccountStatus(String accountNumber, AccountStatus accountStatus) {
        Account account = getAccount(accountNumber);
        account.setAccountStatus(accountStatus);
        accountRepository.save(account);
        return "Account updated successfully";
    }


    /**
     * Get account balance, available balance, min balance, interest rate, overdraft limit, type and product name
     * for given account number
     *
     * @param accountNumber account number
     * @return Balance response
     */
    @Override
    public BalanceResponse getAccountBalance(String accountNumber) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BalanceResponse> criteriaQuery = builder.createQuery(BalanceResponse.class);
        Root<Account> root = criteriaQuery.from(Account.class);

        criteriaQuery.select(builder.construct(
                BalanceResponse.class,
                root.get("accountNumber"),
                root.get("balance"),
                root.get("availableBalance")
        ));

        Predicate predicate = builder.equal(root.get("accountNumber"), accountNumber);
        criteriaQuery.where(builder.and(predicate));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


    /**
     * Update account balance.
     *
     * @param accountNumber owner account number
     * @param updateAccountRequest update info
     */
    @Override
    public void updateAccountBalance(String accountNumber, UpdateAccountRequest updateAccountRequest) {
        BigDecimal amount = updateAccountRequest.getAmount();
        Account fromAccount = getAccount(accountNumber);
        boolean accountOperation = updateAccountRequest.getOperation().equals(Constants.CREDIT);

        updateAccountBalance(amount, accountOperation, fromAccount);
        if (!accountNumber.equals(updateAccountRequest.getToAccountNumber())){
            Account toAccount = getAccount(updateAccountRequest.getToAccountNumber());
            updateAccountBalance(amount, !accountOperation, toAccount);
        }
    }


    /**
     * check if account exists or not
     *
     * @param accountNumber account number
     */
    @Override
    public boolean isAccountValid(String accountNumber) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<Account> root = criteriaQuery.from(Account.class);

        criteriaQuery.select(criteriaBuilder.tuple(root.get("accountNumber"), root.get("accountStatus")));
        Predicate predicate = criteriaBuilder.equal(root.get("accountNumber"), accountNumber);
        criteriaQuery.where(criteriaBuilder.and(predicate));
        Tuple result = entityManager.createQuery(criteriaQuery).getSingleResult();
        if (result == null){
            return false;
        }
        AccountStatus status = result.get(1, AccountStatus.class);
        return status.equals(AccountStatus.ACTIVE);
    }


    /**
     * Filter and fetch accounts based on requirements.
     *
     * @param customerId customer id
     * @param pageable   page no, size and sorting details
     * @return get paginated accounts
     */
    @Override
    public PageResponse<AccountsResponse> getCustomerAccounts(String customerId, Pageable pageable) {
        AccountSearchCriteria searchCriteria = new AccountSearchCriteria();
        searchCriteria.setCustomerId(customerId);
        return getAccounts(searchCriteria, pageable);
    }


    /**
     * Creates new account for user. validates user from customer service
     *
     * @param request required info
     *
     * @return created account response
     */
    @Override
    public AccountsResponse createNewAccount(CreateAccountRequest request) {
        String customerId = communicationClient.getAndValidateCustomer(request.getCustomerNumber());
        if (customerId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        AccountProduct product = accountProductService.getAccountProduct(request.getProductName());
        Account account = Account.builder()
                            .customerId(customerId)
                            .accountProduct(product)
                            .accountStatus(AccountStatus.ACTIVE).build();
        Account savedAccount = accountRepository.save(account);
        return new AccountsResponse(savedAccount.getAccountNumber(), customerId, product.getProductName(),
                product.getProductType(), savedAccount.getAccountStatus(), savedAccount.getOpenedAt(),
                savedAccount.getBalance(), savedAccount.getAvailableBalance());
    }


    /**
     * Update account balance.
     *
     * @param amount    amount to be updated
     * @param operation true -> CREDIT, false -> DEBIT
     * @param account Account to be updated
     */
    private void updateAccountBalance(BigDecimal amount, boolean operation, Account account) {
        if (operation){
            account.setBalance(account.getBalance().add(amount));
            account.setAvailableBalance(account.getAvailableBalance().add(amount));
        } else {
            account.setBalance(account.getBalance().subtract(amount));
            account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
        }
        accountRepository.save(account);
    }


    /**
     * Add conditions on query based on requirements.
     *
     * @param searchCriteria predicates to be used during search
     * @param cb criteria builder adding conditions
     *
     * @return total no of items
     */
    private long getTotalCount(AccountSearchCriteria searchCriteria, CriteriaBuilder cb) {
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Account> root = query.from(Account.class);
        Join<Account, AccountProduct> accountProductJoin = root.join("accountProduct", JoinType.LEFT);

        query.select(cb.count(root));
        createFilter(searchCriteria, cb, query, root, accountProductJoin);
        return entityManager.createQuery(query).getSingleResult();
    }


    /**
     * Add conditions to create desired query based on search criteria.
     *
     * @param searchCriteria options to be used while search operations
     * @param cb criteria builder adding conditions
     * @param query criteria query for adding clauses
     * @param root Root entity
     * @param accountProductJoin joined table
     */
    private void createFilter(AccountSearchCriteria searchCriteria, CriteriaBuilder cb, CriteriaQuery<?> query,
                              Root<Account> root, Join<Account, AccountProduct> accountProductJoin) {
        List<Predicate> predicates = new ArrayList<>();

        BigDecimal minAmount = searchCriteria.getMinAmount();
        BigDecimal maxAmount = searchCriteria.getMaxAmount();
        LocalDate fromDate = searchCriteria.getFromDate();
        if (minAmount != null && maxAmount != null) {
            predicates.add(cb.between(root.get("balance"), minAmount, maxAmount));
        } else if (minAmount != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("balance"), minAmount));
        } else if (maxAmount != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("balance"), maxAmount));
        }
        if (fromDate != null) {
            LocalDate toDate = searchCriteria.getToDate();
            if (toDate != null && !fromDate.isAfter(toDate)) {
                predicates.add(cb.between(root.get("openedAt"), fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX)));
            } else {
                predicates.add(cb.greaterThanOrEqualTo(root.get("openedAt"), fromDate.atStartOfDay()));
            }
        }
        if(searchCriteria.getAccountStatus() != null) {
            predicates.add(cb.equal(root.get("accountStatus"), searchCriteria.getAccountStatus()));
        }
        if (searchCriteria.getProductType() != null) {
            predicates.add(cb.equal(accountProductJoin.get("productType"), searchCriteria.getProductType()));
        }
        if (searchCriteria.getCustomerId() != null) {
            predicates.add(cb.equal(root.get("customerId"), searchCriteria.getCustomerId()));
        }
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
    }


    /**
     * Get customer account
     *
     * @param accountNumber account no
     * @return Account
     */
    private Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> {
                    log.error("Account not found with given account number: {}", accountNumber);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found with given account number: " + accountNumber);
                }
        );
    }
}
