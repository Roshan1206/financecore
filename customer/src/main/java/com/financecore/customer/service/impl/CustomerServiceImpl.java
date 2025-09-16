package com.financecore.customer.service.impl;

import com.financecore.customer.dto.response.CustomerInfoResponse;
import com.financecore.customer.dto.response.CustomersResponse;
import com.financecore.customer.dto.response.PageResponse;
import com.financecore.customer.entity.Customer;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;
import com.financecore.customer.mapper.CustomerMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Customer.
 * Responsible for managing customers.
 *
 * @author Roshan
 */
@Slf4j
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
     * @param kycStatus     Customer KYC Status
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
    public PageResponse<CustomersResponse> getCustomers(Status kycStatus, CustomerType customerType, RiskProfile riskProfile, String email,
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

        String modifiedPhoneNumber = "+" + phoneNumber.trim();

        createFilter(kycStatus, customerType, riskProfile, email, modifiedPhoneNumber, accountNumber, cb, query, root);
        long totalCount = getTotalCount(kycStatus, customerType, riskProfile, email, modifiedPhoneNumber, accountNumber, cb);

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
     * Get detailed information for customer.
     *
     * @param customerNumber Customer number
     * @return customer information
     */
    @Override
    public CustomerInfoResponse getCustomerInfo(String customerNumber) {
        Customer customer = customerRepository.findByCustomerNumber(customerNumber).orElseThrow(
                () -> {
                    String message = "Customer not found with given Customer number: " + customerNumber;
                    log.debug(message);
                    return new  ResponseStatusException(HttpStatus.NOT_FOUND, message);
                }
        );
        return CustomerMapper.mapToCustomerInfoResponse(customer);
    }


    /**
     * Retrieve paginated list of customers with filtering
     *
     * @param kycStaus      Customer KYC Status
     * @param customerType  Customer type
     * @param riskProfile   Risk profiles
     * @param email         Customer email
     * @param phoneNumber   Customer phone number
     * @param accountNumber Customer account number
     * @param cb            Criteria builder
     *
     * @return Total customers in search
     */
    private long getTotalCount(Status kycStaus, CustomerType customerType, RiskProfile riskProfile, String email,
                               String phoneNumber, String accountNumber, CriteriaBuilder cb){
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Customer> root = query.from(Customer.class);

        query.select(cb.count(root));
        createFilter(kycStaus, customerType, riskProfile, email, phoneNumber, accountNumber, cb, query, root);
        return entityManager.createQuery(query).getSingleResult();
    }


    /**
     * Retrieve paginated list of customers with filtering
     *
     * @param kycStatus     Customer KYC Status
     * @param customerType  Customer type
     * @param riskProfile   Risk profiles
     * @param email         Customer email
     * @param phoneNumber   Customer phone number
     * @param accountNumber Customer account number
     * @param cb            Criteria builder
     */
    private void createFilter(Status kycStatus, CustomerType customerType, RiskProfile riskProfile, String email,
                              String phoneNumber, String accountNumber, CriteriaBuilder cb, CriteriaQuery<?> query, Root<Customer> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (kycStatus != null){
            predicates.add(cb.equal(root.get("kycStatus"), kycStatus));
        }
        if (customerType != null){
            predicates.add(cb.equal(root.get("customerType"), customerType));
        }
        if (riskProfile != null){
            predicates.add(cb.equal(root.get("riskProfile"), riskProfile));
        }
        if (email != null){
            predicates.add(cb.equal(root.get("email"), email));
        }
        if (phoneNumber != null){
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
