package com.financecore.transaction.repoitory;

import com.financecore.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Transaction entity.
 *
 * @author Roshan
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @NativeQuery("SELECT t.from_account_id FROM transaction t WHERE t.from_account_id = :accountId LIMIT 1")
    Optional<Long> getAccountId(@Param("accountId") long accountId);
}
