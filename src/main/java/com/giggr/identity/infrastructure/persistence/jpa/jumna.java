package com.giggr.identity.infrastructure.persistence.jpa;

import com.giggr.identity.domain.identity.DigitalId;
import com.giggr.identity.domain.identity.DigitalIdentity;
import com.giggr.identity.domain.identity.Email;
import com.giggr.identity.domain.identity.IdentityRepository;

import java.util.Optional;

public class jumna implements IdentityRepository {
    @Override
    public void save(DigitalIdentity identity) {

    }

    @Override
    public Optional<DigitalIdentity> findByEmail(Email email) {
        return Optional.empty();
    }

    @Override
    public Optional<DigitalIdentity> findByDigitalId(DigitalId digitalId) {
        return Optional.empty();
    }
}
