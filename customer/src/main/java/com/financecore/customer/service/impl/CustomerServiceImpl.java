package com.financecore.customer.service.impl;

import com.financecore.customer.dto.request.CustomerRegistrationRequest;
import com.financecore.customer.dto.request.CustomerUpdateRequest;
import com.financecore.customer.dto.response.AccountsResponse;
import com.financecore.customer.dto.response.CustomerDocumentResponse;
import com.financecore.customer.dto.response.CustomerInfoResponse;
import com.financecore.customer.dto.response.CustomersAccountsResponse;
import com.financecore.customer.dto.response.CustomersResponse;
import com.financecore.customer.dto.response.PageResponse;
import com.financecore.customer.entity.Address;
import com.financecore.customer.entity.Customer;
import com.financecore.customer.entity.CustomerDocument;
import com.financecore.customer.entity.enums.AddressType;
import com.financecore.customer.entity.enums.CustomerType;
import com.financecore.customer.entity.enums.DocumentType;
import com.financecore.customer.entity.enums.RiskProfile;
import com.financecore.customer.entity.enums.Status;
import com.financecore.customer.feign.AccountFeignClient;
import com.financecore.customer.mapper.CustomerMapper;
import com.financecore.customer.repository.AddressRepository;
import com.financecore.customer.repository.CustomerDocumentRepository;
import com.financecore.customer.repository.CustomerRepository;
import com.financecore.customer.service.CustomerService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
     * Getting data from accounts service
     */
    private final AccountFeignClient accountFeignClient;

    /**
     * Repository responsible for managing {@code Address}
     */
    private final AddressRepository addressRepository;

    /**
     * For creating and executing query
     */
    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Utility interface for Enums
     */
    private final EnumUtil enumUtil;


    private final Environment environment;

    /**
     * Repository responsible for managing {@code CustomerDocument}
     */
    private final CustomerDocumentRepository customerDocumentRepository;

    /**
     * Repository responsible for managing {@code Customer}
     */
    private final CustomerRepository customerRepository;

    private final WebClient webClient;


    @Value("${customer.config.file.upload-dir}")
    private String uploadDir;

    /**
     * Injecting required dependency via constructor injection
     */
    public CustomerServiceImpl(AccountFeignClient accountFeignClient, AddressRepository addressRepository,
                               EntityManager entityManager, EnumUtil enumUtil, Environment environment,
                               CustomerDocumentRepository customerDocumentRepository,
                               CustomerRepository customerRepository, WebClient webClient) {
        this.accountFeignClient = accountFeignClient;
        this.addressRepository = addressRepository;
        this.entityManager = entityManager;
        this.enumUtil = enumUtil;
        this.environment = environment;
        this.customerDocumentRepository = customerDocumentRepository;
        this.customerRepository = customerRepository;
        this.webClient = webClient;
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
    public CustomerInfoResponse getCustomerInfo(String customerNumber) {
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

        Address address = CustomerMapper.mapToAddress(customer, addressType, customerRegistrationRequest.getAddress());
        addressRepository.save(address);

        return this.getCustomerInfo(savedCustomer.getCustomerNumber());
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
    public String updateCustomer(String customerNumber, CustomerUpdateRequest customerUpdateRequest) {
        Customer customer = getCustomer(customerNumber);
        customer.setEmail(customerUpdateRequest.getEmail());
        customer.setPhoneNumber(customerUpdateRequest.getPhoneNumber());
        customerRepository.save(customer);
        return "Customer updated successfully";
    }


    /**
     * Upload customer documents
     *
     * @param customerNumber customer number
     * @param file           Document
     * @param documentType   document type
     * @param documentNumber document number
     * @return {@code CustomerDocumentResponse}
     */
    @Override
    public CustomerDocumentResponse uploadDocuments(String customerNumber, MultipartFile file, String documentType,
                                                    String documentNumber) {
        Customer customer = getCustomer(customerNumber);
        String fileName = customerNumber + System.currentTimeMillis();
        String errorMessage = "Something went wrong. Please try again later....";

        java.nio.file.Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("Error creating uploads directory", e.getCause());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
            }
        }

        java.nio.file.Path filePath = uploadPath.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Error happened while copying file to directory.", e.getCause());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        }
        DocumentType customerDocumentType = enumUtil.getSafeDocumentType(documentType);
        CustomerDocument customerDocument = CustomerMapper.mapToCustomerDocument(customer, customerDocumentType, documentNumber, fileName, filePath.toString());
        CustomerDocument savedDocument = customerDocumentRepository.save(customerDocument);
        return CustomerMapper.mapToCustomerDocumentResponse(savedDocument);
    }

    /**
     * Update KYC verification status
     *
     * @param customerNumber customer number
     * @return Message
     */
    @Override
    public String updateCustomerKyc(String customerNumber) {
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
        ResponseEntity<PageResponse<AccountsResponse>> customerAccounts;
        if (environment.matchesProfiles("feign")){
            customerAccounts = accountFeignClient.getCustomerAccounts(customerId);
        }else {
            customerAccounts = webClient
                    .get()
                    .uri("/customer/" + customerId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError,
                            res -> Mono.error(new ResponseStatusException(res.statusCode(), "Internal Server error")))
                    .bodyToMono(new ParameterizedTypeReference<ResponseEntity<PageResponse<AccountsResponse>>>() {})
                    .retry(3)
                    .block();
        }
        if (customerAccounts == null || customerAccounts.getBody() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while fetching customer accounts");
        }
        List<AccountsResponse> accountsList = customerAccounts.getBody().getContent();

        return new CustomersAccountsResponse(customer.getCustomerNumber(), customer.getFirstName(),
                customer.getLastName(), customer.getEmail(), customer.getPhoneNumber(), customer.getCustomerType(),
                customer.getKycStatus(), customer.getRiskProfile(), customer.getCreatedAt(), accountsList);
    }

    /**
     * Get Customer details
     *
     * @param customerNumber customer number
     * @return Customer
     */
    private Customer getCustomer(String customerNumber) {
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
