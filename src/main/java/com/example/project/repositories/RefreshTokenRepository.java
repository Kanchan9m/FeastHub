package com.example.project.repositories;

import com.example.project.model.RefreshToken;
import com.example.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
//    Optional<RefreshToken> findByToken(String token);

    @Query("""
       SELECT rt
       FROM RefreshToken rt
       JOIN FETCH rt.user
       WHERE rt.token = :token
       """)
    Optional<RefreshToken> findByToken(@Param("token") String token);

    @Modifying
    @Query("""
       UPDATE RefreshToken r
       SET r.revoked = true
       WHERE r.user = :user
       """)
    void revokeAllByUser(User user);

}
