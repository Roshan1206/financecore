package com.financecore.customer.service.impl;

import com.financecore.customer.dto.response.CustomersResponse;
import com.financecore.customer.dto.response.PageResponse;
import com.financecore.customer.entity.Customer;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;
import com.financecore.customer.repository.CustomerRepository;
import com.financecore.customer.service.CustomerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Customer.
 * Responsible for managing customers.
 *
 * @author Roshan
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(EntityManager entityManager, CustomerRepository customerRepository) {
        this.entityManager = entityManager;
        this.customerRepository = customerRepository;
    }


    /**
     * Retrieve paginated list of customers with filtering
     *
     * @param status        Customer KYC Status
     * @param customerType  Customer type
     * @param riskProfile   Risk profiles
     * @param email         Customer email
     * @param phoneNumber   Customer phone number
     * @param accountNumber Customer account number
     * @param pageable      Paging
     *
     * @return customers list
     */
    @Override
    public PageResponse<CustomersResponse> getCustomers(Status status, CustomerType customerType, RiskProfile riskProfile, String email,
                                                        String phoneNumber, String accountNumber, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomersResponse> query = cb.createQuery(CustomersResponse.class);
        Root<Customer> root = query.from(Customer.class);

        query.select(cb.construct(
                CustomersResponse.class,
                root.get("customerNumber"),
                root.get("firstName"),
                root.get("lastName"),
                root.get("email"),
                root.get("phoneNumber"),
                root.get("customerType"),
                root.get("kycStatus"),
                root.get("riskProfile"),
                root.get("createdAt")
        ));

        createFilter(status, customerType, riskProfile, email, phoneNumber, accountNumber, cb, query, root);
        long totalCount = getTotalCount(status, customerType, riskProfile, email, phoneNumber, accountNumber, cb);

        if (pageable.getSort().isSorted()){
            List<Order> orders = new ArrayList<>();

            pageable.getSort().forEach(sortOrder -> {
                String property = sortOrder.getProperty();
                Path<?> path = root.get(property);

                if (sortOrder.isAscending()) {
                    orders.add(cb.asc(path));
                } else {
                    orders.add(cb.desc(path));
                }
            });
            query.orderBy(orders);
        }
        List<CustomersResponse> customersResponses = entityManager.createQuery(query)
                                                                .setFirstResult((int) pageable.getOffset())
                                                                .setMaxResults(pageable.getPageSize())
                                                                .getResultList();
        Page<CustomersResponse> page = new PageImpl<>(customersResponses, pageable, totalCount);
        return new PageResponse<>(page);
    }


    /**
     * Retrieve paginated list of customers with filtering
     *
     * @param status        Customer KYC Status
     * @param customerType  Customer type
     * @param riskProfile   Risk profiles
     * @param email         Customer email
     * @param phoneNumber   Customer phone number
     * @param accountNumber Customer account number
     * @param cb            Criteria builder
     *
     * @return Total customers in search
     */
    private long getTotalCount(Status status, CustomerType customerType, RiskProfile riskProfile, String email,
                               String phoneNumber, String accountNumber, CriteriaBuilder cb){
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Customer> root = query.from(Customer.class);

        query.select(cb.count(root));
        createFilter(status, customerType, riskProfile, email, phoneNumber, accountNumber, cb, query, root);
        return entityManager.createQuery(query).getSingleResult();
    }


    /**
     * Retrieve paginated list of customers with filtering
     *
     * @param status        Customer KYC Status
     * @param customerType  Customer type
     * @param riskProfile   Risk profiles
     * @param email         Customer email
     * @param phoneNumber   Customer phone number
     * @param accountNumber Customer account number
     * @param cb            Criteria builder
     */
    private void createFilter(Status status, CustomerType customerType, RiskProfile riskProfile, String email,
                              String phoneNumber, String accountNumber, CriteriaBuilder cb, CriteriaQuery<?> query, Root<Customer> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (status != null){
            predicates.add(cb.equal(root.get("status"), status));
        }
        if (customerType != null){
            predicates.add(cb.equal(root.get("customerType"), customerType));
        }
        if (riskProfile != null){
            predicates.add(cb.equal(root.get("riskProfile"), riskProfile));
        }
        if (email != null){
            predicates.add(cb.equal(root.get("phoneNumber"), phoneNumber));
        }
        if (accountNumber != null){
            predicates.add(cb.equal(root.get("accountNumber"), accountNumber));
        }
        if (!predicates.isEmpty()){
            query.where(predicates.toArray(new Predicate[0]));
        }
    }
}
