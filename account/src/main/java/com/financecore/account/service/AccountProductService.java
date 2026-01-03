package com.financecore.account.service;

import com.financecore.account.entity.AccountProduct;

/**
 * Service for managing account products.
 *
 * @author Roshan
 */
public interface AccountProductService {

    /**
     * Get Product of account
     *
     * @param productName Account product name
     * @return AccountProduct
     */
    AccountProduct getAccountProduct(String productName);
}
