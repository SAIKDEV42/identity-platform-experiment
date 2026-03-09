package com.giggr.identity.domain.identity;


import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public class DigitalIdGenerator {

    private static final long CUSTOM_EPOCH =
            Instant.parse("2026-01-01T00:00:00Z").toEpochMilli();

    private static final int MAX_SEQUENCE = 99;

    //private final EntityType entityType;
    private final Clock clock;

    private long lastTimestamp = -1L;
    private int sequence = 0;

    public DigitalIdGenerator( Clock clock) {
         this.clock = Objects.requireNonNull(clock, "Clock required");
    }

    public synchronized DigitalId generate(EntityType entityType) {

        long currentTimestamp = currentTimeMillis();

        // guard against system time before custom epoch
        if (currentTimestamp < 0) {
            throw new IllegalStateException("System clock is before custom epoch");
        }

        // clock rollback protection
        if (currentTimestamp < lastTimestamp) {
            currentTimestamp = lastTimestamp;
        }

        // Same millisecond
        if (currentTimestamp == lastTimestamp) {
            if (sequence < MAX_SEQUENCE) {
                sequence++;
            } else {
                // Wait until next millisecond
                currentTimestamp = waitNextMillis(currentTimestamp);
                sequence = 0;
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        String id = formatId(entityType, currentTimestamp, sequence);

        return new DigitalId(id);
    }

    private long currentTimeMillis() {
        return Instant.now(clock).toEpochMilli() - CUSTOM_EPOCH;
    }

    private long waitNextMillis(long currentTimestamp) {
        long now = currentTimeMillis();
        while (now <= currentTimestamp) {
            now = currentTimeMillis();
        }
        return now;
    }

    private String formatId(EntityType type, long timestamp, int sequence) {
        return String.format("%s%013d%02d",
                type.getCode(),
                timestamp,
                sequence);
    }
}
