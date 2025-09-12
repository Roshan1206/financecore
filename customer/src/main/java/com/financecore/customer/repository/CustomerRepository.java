package com.financecore.customer.repository;

import com.financecore.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for using Address entity
 *
 * @author Roshan
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
