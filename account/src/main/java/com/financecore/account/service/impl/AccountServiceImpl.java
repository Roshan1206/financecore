package com.financecore.account.service.impl;

import com.financecore.account.dto.response.BalanceResponse;
import com.financecore.account.dto.response.AccountsResponse;
import com.financecore.account.dto.response.PageResponse;
import com.financecore.account.entity.Account;
import com.financecore.account.entity.AccountProduct;
import com.financecore.account.entity.enums.AccountStatus;
import com.financecore.account.entity.enums.ProductType;
import com.financecore.account.repository.AccountRepository;
import com.financecore.account.service.AccountService;
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
     * Creating and executing queries.
     */
    @PersistenceContext
    private final EntityManager entityManager;


    /**
     * Repository responsible managing crud operations.
     */
    private final AccountRepository accountRepository;


    /**
     * Injecting required dependency via constructor injection.
     */
    public AccountServiceImpl(EntityManager entityManager, AccountRepository accountRepository){
        this.entityManager = entityManager;
        this.accountRepository = accountRepository;
    }


    /**
     * Filter and fetch accounts based on requirements.
     *
     * @param status account current status
     * @param type account product type
     * @param minAmount minimum account balance
     * @param maxAmount account balance
     * @param fromDate from when
     * @param toDate till when
     * @param pageable page no, size and sorting details
     *
     * @return get paginated accounts
     */
    @Override
    public PageResponse<AccountsResponse> getAccounts(AccountStatus status, ProductType type, BigDecimal minAmount, BigDecimal maxAmount,
                                                      LocalDate fromDate, LocalDate toDate, String customerId, Pageable pageable) {
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

        createFilter(status, type, minAmount, maxAmount, fromDate, toDate, customerId, cb, query, root, accountProductJoin);
        long totalCount = getTotalCount(status, type, minAmount, maxAmount, fromDate, toDate, customerId, cb);

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
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> {
                    log.error("Account not found with given account number: {}", accountNumber);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found with given account number: " + accountNumber);
                }
        );
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
//        String query = "SELECT a.accountNumber, a.balance, a.availableBalance, a.customInterestRate, a.customMinimumBalance, "
//                + "a.customOverDraftLimit, ap.productName, ap.minimumBalance, ap.interestRate, ap.overdraftLimit FROM Account a LEFT JOIN "
//                + "AccountProduct ap ON a.accountProduct.id = ap.id WHERE a.accountNumber = :accountNumber";
//        Tuple result = entityManager.createQuery(query, Tuple.class)
//                .setParameter("accountNumber", accountNumber)
//                .getSingleResult();

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BalanceResponse> criteriaQuery = builder.createQuery(BalanceResponse.class);
        Root<Account> root = criteriaQuery.from(Account.class);

        Predicate predicate = builder.equal(root.get("accountNumber"), accountNumber);
        criteriaQuery.where(builder.and(predicate));
        return entityManager.createQuery(criteriaQuery).getSingleResult();

//        AccountStatus status = result.get(1, AccountStatus.class);
//        BigDecimal balance = result.get(2, BigDecimal.class);
//        BigDecimal availableBalance = result.get(3, BigDecimal.class);
//        BigDecimal customInterestRate = result.get(4, BigDecimal.class);
//        BigDecimal customMinimumBalance = result.get(5, BigDecimal.class);
//        BigDecimal customOverDraftLimit = result.get(6, BigDecimal.class);
//        String productName = result.get(7, String.class);
//        BigDecimal minimumBalance = result.get(8, BigDecimal.class);
//        BigDecimal interestRate = result.get(9, BigDecimal.class);
//        BigDecimal overdraftLimit = result.get(10, BigDecimal.class);
//
//        BigDecimal accMinBal = customMinimumBalance == null ? minimumBalance : customMinimumBalance;
//        BigDecimal accIntRate = customInterestRate == null ? interestRate : customInterestRate;
//        BigDecimal accODLimit = customOverDraftLimit == null ? overdraftLimit : customOverDraftLimit;
//
//        return new BalanceResponse(accountNumber, status, productName, balance, availableBalance, accIntRate, accMinBal, accODLimit);
    }


    /**
     * Add conditions on query based on requirements.
     *
     * @param status account current status
     * @param type account product type
     * @param minAmount minimum account balance
     * @param maxAmount account balance
     * @param fromDate from when
     * @param toDate till when
     * @param cb criteria builder adding conditions
     *
     * @return total no of items
     */
    private long getTotalCount(AccountStatus status, ProductType type, BigDecimal minAmount, BigDecimal maxAmount, LocalDate fromDate,
                               LocalDate toDate, String customerId, CriteriaBuilder cb) {
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Account> root = query.from(Account.class);
        Join<Account, AccountProduct> accountProductJoin = root.join("accountProduct", JoinType.LEFT);

        query.select(cb.count(root));
        createFilter(status, type, minAmount, maxAmount, fromDate, toDate, customerId, cb, query, root, accountProductJoin);
        return entityManager.createQuery(query).getSingleResult();
    }


    /**
     * Add conditions to create desired query based on search criteria.
     *
     * @param status account current status
     * @param type account product type
     * @param minAmount minimum account balance
     * @param maxAmount maximum account balance
     * @param fromDate from when
     * @param toDate till when
     * @param cb criteria builder adding conditions
     * @param query criteria query for adding clauses
     * @param root Root entity
     * @param accountProductJoin joined table
     */
    private void createFilter(AccountStatus status, ProductType type, BigDecimal minAmount, BigDecimal maxAmount, LocalDate fromDate,
                              LocalDate toDate, String customerId, CriteriaBuilder cb, CriteriaQuery<?> query,
                              Root<Account> root, Join<Account, AccountProduct> accountProductJoin) {
        List<Predicate> predicates = new ArrayList<>();

        if (minAmount != null && maxAmount != null) {
            predicates.add(cb.between(root.get("balance"), minAmount, maxAmount));
        } else if (minAmount != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("balance"), minAmount));
        } else if (maxAmount != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("balance"), maxAmount));
        }
        if (fromDate != null) {
            if (toDate != null && !fromDate.isAfter(toDate)) {
                predicates.add(cb.between(root.get("openedAt"), fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX)));
            } else {
                predicates.add(cb.greaterThanOrEqualTo(root.get("openedAt"), fromDate.atStartOfDay()));
            }
        }
        if(status != null) {
            predicates.add(cb.equal(root.get("accountStatus"), status));
        }
        if (type != null) {
            predicates.add(cb.equal(accountProductJoin.get("productType"), type));
        }
        if (customerId != null) {
            predicates.add(cb.equal(root.get("customerId"), customerId));
        }
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
    }
}
