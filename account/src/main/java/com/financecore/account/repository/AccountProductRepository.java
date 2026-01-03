package com.financecore.account.repository;

import com.financecore.account.entity.AccountProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository class for AccountProduct entity.
 *
 * @author Roshan
 */
@Repository
public interface AccountProductRepository extends JpaRepository<AccountProduct, Long> {

    /**
     * Get account product based on product name
     *
     * @param productName product name
     * @return product if present
     */
    Optional<AccountProduct> findByProductName(String productName);
}
