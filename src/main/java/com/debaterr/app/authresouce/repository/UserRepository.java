package com.debaterr.app.authresouce.repository;


import com.debaterr.app.authresouce.entity.AuthUser;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AuthUser, String> {
    Optional<AuthUser> findByUsername(String username);
    @Query("SELECT u FROM AuthUser u WHERE LOWER(REPLACE(u.email, ' ', '')) = LOWER(REPLACE(:email, ' ', ''))")
    Optional<AuthUser> findByEmail(String email);
}
