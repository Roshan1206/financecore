package com.financecore.transaction.repoitory;

import com.financecore.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Transaction entity.
 *
 * @author Roshan
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
