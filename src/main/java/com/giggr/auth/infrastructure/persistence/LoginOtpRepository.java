package com.giggr.auth.infrastructure.persistence;

import com.giggr.auth.domain.LoginOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginOtpRepository extends JpaRepository<LoginOtp, Long> {

    Optional<LoginOtp> findTopByEmailOrderByIdDesc(String email);
}
