package com.giggr.auth.infrastructure.persistence;

import com.giggr.auth.domain.RegisteredDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisteredDeviceRepository
        extends JpaRepository<RegisteredDevice, Long> {

    Optional<RegisteredDevice> findByDeviceId(String deviceId);
}
