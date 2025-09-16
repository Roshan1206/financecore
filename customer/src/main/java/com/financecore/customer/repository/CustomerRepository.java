package com.financecore.customer.repository;

import com.financecore.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for using Address entity
 *
 * @author Roshan
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerNumber(String customerNumber);
}
