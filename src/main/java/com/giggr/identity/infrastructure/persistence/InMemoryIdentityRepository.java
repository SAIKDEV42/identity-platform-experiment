package com.giggr.identity.infrastructure.persistence;

import com.giggr.identity.domain.identity.DigitalId;
import com.giggr.identity.domain.identity.DigitalIdentity;
import com.giggr.identity.domain.identity.Email;
import com.giggr.identity.domain.identity.IdentityRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryIdentityRepository implements IdentityRepository {

    private final Map<Email, DigitalIdentity> storageByEmail = new ConcurrentHashMap<>();
    private final Map<DigitalId, DigitalIdentity> storageById = new ConcurrentHashMap<>();

    @Override
    public void save(DigitalIdentity identity) {

        storageByEmail.put(identity.getProfile().email(), identity);

        if (identity.getDigitalId() != null) {
            storageById.put(identity.getDigitalId(), identity);
        }
    }

    @Override
    public Optional<DigitalIdentity> findByEmail(Email email) {
        return Optional.ofNullable(storageByEmail.get(email));
    }

    @Override
    public Optional<DigitalIdentity> findByDigitalId(DigitalId digitalId) {
        return Optional.ofNullable(storageById.get(digitalId));
    }


}

