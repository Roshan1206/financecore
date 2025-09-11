package com.financecore.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountBeneficiaryRepository extends JpaRepository<AccountRepository, Long> {
}
