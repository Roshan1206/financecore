package com.financecore.account.repository;

import com.financecore.account.entity.AccountProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class for AccountProduct entity.
 *
 * @author Roshan
 */
@Repository
public interface AccountProductRepository extends JpaRepository<AccountProduct, Long> {
}
