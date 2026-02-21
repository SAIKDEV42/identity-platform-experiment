package com.giggr.identity.domain.identity;
import java.util.Optional;

public interface IdentityRepository {

    void save(DigitalIdentity identity);

    Optional<DigitalIdentity> findByEmail(Email email);

    Optional<DigitalIdentity> findByDigitalId(DigitalId digitalId);

}

