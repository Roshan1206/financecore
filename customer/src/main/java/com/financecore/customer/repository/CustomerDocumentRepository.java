package com.financecore.customer.repository;

import com.financecore.customer.entity.CustomerDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for using Address entity
 *
 * @author Roshan
 */
@Repository
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, Long> {
}
