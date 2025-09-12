package com.financecore.transaction.entity;

import com.financecore.transaction.entity.enums.LimitType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class for managing transaction limits for customer
 *
 * @author Roshan
 */
@Entity
@Table(name = "transaction_limits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "limit_id")
    private long id;

    @Column(name = "account_id", nullable = false)
    private long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "limit_type")
    private LimitType limitType;

    @Column(name = "limit_amount")
    private BigDecimal limitAmount;

    @Column(name = "used_amount")
    private BigDecimal usedAmount;

    @Column(name = "reset_frequency")
    private String resetFrequency;

    @Column(name = "last_reset_date")
    private LocalDate lastResetDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
