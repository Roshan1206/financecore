package com.financecore.transaction.entity;

import com.financecore.transaction.entity.enums.ParentCategory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for managing transactions category with required fields.
 *
 * @author Roshan
 */
@Entity
@Table(name = "transaction_subcategory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subcategory_id")
    private long id;

    @Column(name = "subcategory_name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "parent_category", columnDefinition = "parent_category_enum", nullable = false)
    private ParentCategory parentCategory;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();


    public TransactionCategory(String name, ParentCategory parentCategory){
        this(name, parentCategory, true);
    }


    public TransactionCategory(String name, ParentCategory parentCategory, boolean isActive){
        this.name = name;
        this.parentCategory = parentCategory;
        this.isActive = isActive;
    }
}
