package com.financecore.account.repository;

import com.financecore.account.entity.AccountBeneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountBeneficiaryRepository extends JpaRepository<AccountBeneficiary, Long> {
}
