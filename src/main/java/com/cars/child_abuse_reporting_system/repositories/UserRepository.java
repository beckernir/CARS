package com.cars.child_abuse_reporting_system.repositories;

import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.enums.Role;
import com.cars.child_abuse_reporting_system.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByRole(Role role);

    Page<User> findAllByRole(String role, Pageable pageable);

    // Count ACTIVE users
    long countByAccountStatus(Status status);


    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findUsersRegisteredAfter(@Param("date") LocalDateTime date);

    // Count by roles
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countUsersByRole();

    List<User> findByRole(Role admin);
}
