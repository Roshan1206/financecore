package com.financecore.transaction.repoitory;

import com.financecore.transaction.entity.TransactionLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for TransactionLimit entity
 *
 * @author Roshan
 */
@Repository
public interface TransactionLimitRepository extends JpaRepository<TransactionLimit, Long> {
}
