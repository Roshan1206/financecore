package com.financecore.transaction.repoitory;

import com.financecore.transaction.entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for TransactionCategory entity
 *
 * @author Roshan
 */
@Repository
public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
}
