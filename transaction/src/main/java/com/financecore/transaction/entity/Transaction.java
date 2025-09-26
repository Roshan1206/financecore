package com.financecore.transaction.entity;

import com.financecore.transaction.entity.enums.Channel;
import com.financecore.transaction.entity.enums.Status;
import com.financecore.transaction.entity.enums.TransactionType;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class for transactions with required fields.
 *
 * @author Roshan
 */
@Entity
@Table(name = "transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private long id;

    @Column(name = "transaction_reference", unique = true, nullable = false)
    private String reference;

    @Column(name = "from_account_id", nullable = false)
    private long fromAccountId;

    @Column(name = "to_account_id", nullable = false)
    private long toAccountId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "transaction_type", columnDefinition = "transaction_enum", nullable = false)
    private TransactionType type;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id", nullable = false)
    private TransactionCategory category;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "transaction_status", columnDefinition = "transaction_status_enum", nullable = false)
    private Status status;

    @CreationTimestamp
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "value_date")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "channel", columnDefinition = "channel_enum", nullable = false)
    private Channel channel;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "merchant_info", columnDefinition = "jsonb")
    private String merchantInfo;

    @Column(name = "location", columnDefinition = "jsonb")
    private String location;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Transaction(String reference, long fromAccountId, long toAccountId, TransactionType type,
                       BigDecimal amount, String currencyCode, String description, TransactionCategory category,
                       Channel channel, String referenceNumber, String merchantInfo, String location) {
        this.reference = reference;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.type = type;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.description = description;
        this.category = category;
        this.status = Status.COMPLETED;
        this.date = LocalDate.now();
        this.channel = channel;
        this.referenceNumber = referenceNumber;
        this.merchantInfo = merchantInfo;
        this.location = location;
    }
}
