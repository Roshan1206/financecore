package com.financecore.account.service.impl;

import com.financecore.account.entity.AccountProduct;
import com.financecore.account.repository.AccountProductRepository;
import com.financecore.account.service.AccountProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation class for account products.
 *
 * @author Roshan
 */
@Service
public class AccountProductServiceImpl implements AccountProductService {

    /**
     * Repository for {@link AccountProduct}
     */
    private final AccountProductRepository productRepository;

    /**
     * Injecting required dependency using constructor injection.
     */
    public AccountProductServiceImpl(AccountProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get Product of account
     *
     * @param productName Account product name
     * @return AccountProduct
     */
    @Override
    public AccountProduct getAccountProduct(String productName) {
        return productRepository.findByProductName(productName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        );
    }
}
