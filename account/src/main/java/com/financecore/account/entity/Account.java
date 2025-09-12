package com.financecore.account.entity;

import com.financecore.account.entity.enums.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User Account information
 *
 * @author Roshan
 */
@Entity
@Table(name = "account")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private long id;

    @Column(name = "account_number", unique = true, nullable = false, updatable = false)
    private String accountNumber;

//    reference from customer service
    @Column(name = "customer_id", unique = true, nullable = false, updatable = false)
    private long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private AccountProduct accountProduct;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(name = "available_balance", precision = 10, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "custom_interest_rate", precision = 10, scale = 2)
    private BigDecimal customInterestRate;

    @Column(name = "custom_minimum_balance", precision = 10, scale = 2)
    private BigDecimal customMinimumBalance;

    @Column(name = "custom_overdraft_limit", precision = 10, scale = 2)
    private BigDecimal customOverDraftLimit;

    @CreationTimestamp
    @Column(name = "opened_at", nullable = false, updatable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @UpdateTimestamp
    @Column(name = "last_activity_date", nullable = false, insertable = false)
    private LocalDate lastActivityDate;

    @OneToMany(mappedBy = "account")
    private final Set<AccountBeneficiary> accountBeneficiaries = new HashSet<>();
}
