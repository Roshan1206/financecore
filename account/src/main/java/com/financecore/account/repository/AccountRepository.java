package com.financecore.account.repository;

import com.financecore.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository class for Account entity.
 *
 * @author Roshan
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find Account using account number
     *
     * @param accountNumber used for finding account
     */
    Optional<Account> findByAccountNumber(String accountNumber);
}
