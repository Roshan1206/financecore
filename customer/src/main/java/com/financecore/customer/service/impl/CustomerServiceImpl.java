package com.financecore.customer.service.impl;

import com.financecore.customer.dto.request.CustomerRegistrationRequest;
import com.financecore.customer.dto.request.CustomerUpdateRequest;
import com.financecore.customer.dto.response.AccountsResponse;
import com.financecore.customer.dto.response.CustomerInfoResponse;
import com.financecore.customer.dto.response.CustomersAccountsResponse;
import com.financecore.customer.dto.response.CustomersResponse;
import com.financecore.customer.dto.response.PageResponse;
import com.financecore.customer.entity.Address;
import com.financecore.customer.entity.Customer;
import com.financecore.customer.entity.enums.AddressType;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;
import com.financecore.customer.mapper.CustomerMapper;
import com.financecore.customer.repository.AddressRepository;
import com.financecore.customer.repository.CustomerRepository;
import com.financecore.customer.service.CustomerService;
import com.financecore.customer.service.communication.CommunicationClient;
import com.financecore.customer.util.EnumUtil;
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
import org.springframework.http.ResponseEntity;
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

    /**
     * Repository responsible for managing {@code Address}
     */
    private final AddressRepository addressRepository;

    /**
     * Getting data from accounts service
     */
    private final CommunicationClient communicationClient;

    /**
     * For creating and executing query
     */
    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Utility interface for Enums
     */
    private final EnumUtil enumUtil;

    /**
     * Repository responsible for managing {@code Customer}
     */
    private final CustomerRepository customerRepository;

    /**
     * Injecting required dependency via constructor injection
     */
    public CustomerServiceImpl(AddressRepository addressRepository, CommunicationClient communicationClient,
                               EntityManager entityManager, EnumUtil enumUtil,
                               CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.communicationClient = communicationClient;
        this.entityManager = entityManager;
        this.enumUtil = enumUtil;
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
     * @param pageable      Paging
     *
     * @return customers list
     */
    @Override
    public PageResponse<CustomersResponse> getCustomers(Status kycStatus, CustomerType customerType, RiskProfile riskProfile, String email,
                                                        String phoneNumber, Pageable pageable) {
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

        String modifiedPhoneNumber = phoneNumber;
        if (phoneNumber != null){
            modifiedPhoneNumber = "+" + phoneNumber.trim();
        }

        createFilter(kycStatus, customerType, riskProfile, email, modifiedPhoneNumber, cb, query, root);
        long totalCount = getTotalCount(kycStatus, customerType, riskProfile, email, modifiedPhoneNumber, cb);

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
    public CustomerInfoResponse getCustomerInfo(long customerNumber) {
        Customer customer = getCustomer(customerNumber);
        return CustomerMapper.mapToCustomerInfoResponse(customer);
    }


    /**
     * Create new customer profile with KYC initiation
     *
     * @param customerRegistrationRequest Customer details
     * @return Detailed customer info
     */
    @Override
    public CustomerInfoResponse createCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        CustomerType customerType = enumUtil.getSafeCustomerType(customerRegistrationRequest.getCustomerType());
        AddressType addressType = enumUtil.getSafeAddressType(customerRegistrationRequest.getAddress().getAddressType());

        Customer customer = CustomerMapper.mapToCustomer(customerRegistrationRequest, customerType);
        Customer savedCustomer = customerRepository.save(customer);


        Address address = CustomerMapper.mapToAddress(savedCustomer, addressType, customerRegistrationRequest.getAddress());
        addressRepository.save(address);

        return getCustomerInfo(savedCustomer.getCustomerNumber());
    }


    /**
     * Update customer information
     *
     * @param customerNumber customer number
     * @param customerUpdateRequest Update info
     *
     * @return Message
     */
    @Override
    public String updateCustomer(long customerNumber, CustomerUpdateRequest customerUpdateRequest) {
        Customer customer = getCustomer(customerNumber);
        customer.setEmail(customerUpdateRequest.getEmail());
        customer.setPhoneNumber(customerUpdateRequest.getPhoneNumber());
        customerRepository.save(customer);
        return "Customer updated successfully";
    }


    /**
     * Update KYC verification status
     *
     * @param customerNumber customer number
     * @return Message
     */
    @Override
    public String updateCustomerKyc(long customerNumber) {
        Customer customer = getCustomer(customerNumber);
        customer.setKycStatus(Status.VERIFIED);
        customerRepository.save(customer);
        return "Customer KYC updated successfully";
    }


    /**
     * Retrieve paginated list of customers with filtering
     *
     * @param customerId Customer id
     */
    @Override
    public CustomersAccountsResponse getCustomerAccounts(String customerId) {
        Customer customer = customerRepository.findById(Long.valueOf(customerId)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with customer id: " + customerId)
        );
        ResponseEntity<PageResponse<AccountsResponse>> customerAccounts = communicationClient.getCustomerAccounts(customerId);
        if (customerAccounts == null || customerAccounts.getBody() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while fetching customer accounts");
        }
        List<AccountsResponse> accountsList = customerAccounts.getBody().getContent();

        return new CustomersAccountsResponse(customer.getCustomerNumber(), customer.getFirstName(),
                customer.getLastName(), customer.getEmail(), customer.getPhoneNumber(), customer.getCustomerType(),
                customer.getKycStatus(), customer.getRiskProfile(), customer.getCreatedAt(), accountsList);
    }


    /**
     * Get customer id after validating
     *
     * @param customerNumber customer number
     * @return customer id
     */
    @Override
    public String getAndValidateCustomer(long customerNumber) {
        return getCustomer(customerNumber).getId();
    }

    /**
     * Get Customer details
     *
     * @param customerNumber customer number
     * @return Customer
     */
    @Override
    public Customer getCustomer(long customerNumber) {
        return customerRepository.findByCustomerNumber(customerNumber).orElseThrow(
                () -> {
                    String message = "Customer not found with given Customer number: " + customerNumber + ". Customer update failed.";
                    log.error(message);
                    return new  ResponseStatusException(HttpStatus.NOT_FOUND, message);
                }
        );
    }


    /**
     * Retrieve paginated list of customers with filtering
     *
     * @param kycStaus      Customer KYC Status
     * @param customerType  Customer type
     * @param riskProfile   Risk profiles
     * @param email         Customer email
     * @param phoneNumber   Customer phone number
     * @param cb            Criteria builder
     *
     * @return Total customers in search
     */
    private long getTotalCount(Status kycStaus, CustomerType customerType, RiskProfile riskProfile, String email,
                               String phoneNumber, CriteriaBuilder cb){
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Customer> root = query.from(Customer.class);

        query.select(cb.count(root));
        createFilter(kycStaus, customerType, riskProfile, email, phoneNumber, cb, query, root);
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
     * @param cb            Criteria builder
     */
    private void createFilter(Status kycStatus, CustomerType customerType, RiskProfile riskProfile, String email,
                              String phoneNumber, CriteriaBuilder cb, CriteriaQuery<?> query, Root<Customer> root) {
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
        if (!predicates.isEmpty()){
            query.where(predicates.toArray(new Predicate[0]));
        }
    }
}
