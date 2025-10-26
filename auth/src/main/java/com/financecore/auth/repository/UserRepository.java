package com.financecore.auth.repository;

import com.financecore.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing user entity
 *
 * @author Roshan
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Query method required for UserDetailsService bean
     */
    Optional<User> findByEmail(String email);
}
