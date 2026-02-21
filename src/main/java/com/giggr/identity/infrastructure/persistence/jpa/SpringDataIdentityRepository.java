package com.giggr.identity.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SpringDataIdentityRepository
        extends JpaRepository<JpaDigitalIdentityEntity, String> {

    Optional<JpaDigitalIdentityEntity> findByEmail(String email);

    Optional<JpaDigitalIdentityEntity> findByDigitalId(String digitalId);

}
