package com.financecore.account.service;

import com.financecore.account.dto.request.UpdateAccountRequest;
import com.financecore.account.dto.response.BalanceResponse;
import com.financecore.account.dto.response.AccountsResponse;
import com.financecore.account.dto.response.PageResponse;
import com.financecore.account.entity.enums.AccountStatus;
import com.financecore.account.entity.enums.ProductType;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface containing methods for service class.
 *
 * @author Roshan
 */
public interface AccountService {

    /**
     * Filter and fetch accounts based on requirements.
     *
     * @param status account current status
     * @param type account product type
     * @param minAmount minimum account balance
     * @param maxAmount account balance
     * @param fromDate from when
     * @param toDate till when
     * @param customerId customer id
     * @param pageable page no, size and sorting details
     *
     * @return get paginated accounts
     */
    PageResponse<AccountsResponse> getAccounts(AccountStatus status, ProductType type, BigDecimal minAmount, BigDecimal maxAmount,
                                               LocalDate fromDate, LocalDate toDate, String customerId, Pageable pageable);


    /**
     * update account status for given account number
     *
     * @param accountNumber account to be updated
     * @param accountStatus status to be updated
     *
     * @return message
     */
    String updateAccountStatus(String accountNumber, AccountStatus accountStatus);


    /**
     * Get account balance, available balance, min balance, interest rate, overdraft limit, type and product name
     * for given account number
     *
     * @param accountNumber account number
     * @return Balance response
     */
    BalanceResponse getAccountBalance(String accountNumber);


    /**
     * Update account balance.
     *
     * @param updateAccountRequest update info
     */
    void updateAccountBalance(String accountNumber, UpdateAccountRequest updateAccountRequest);


    /**
     * check if account exists or not
     *
     * @param accountNumber account number
     */
    boolean isAccountValid(String accountNumber);


    /**
     * Filter and fetch accounts based on requirements.
     *
     * @param customerId customer id
     * @param pageable page no, size and sorting details
     *
     * @return get paginated accounts
     */
    PageResponse<AccountsResponse> getCustomerAccounts(String customerId, Pageable pageable);
}
