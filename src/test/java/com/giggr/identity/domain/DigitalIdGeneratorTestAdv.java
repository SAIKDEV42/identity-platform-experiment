package com.giggr.identity.domain;

import com.giggr.identity.domain.identity.DigitalId;
import com.giggr.identity.domain.identity.DigitalIdGenerator;
import com.giggr.identity.domain.identity.EntityType;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DigitalIdGeneratorTestAdv {

    static class MutableClock extends Clock {

        private Instant instant;
        private final ZoneOffset zone;

        MutableClock(Instant instant) {
            this.instant = instant;
            this.zone = ZoneOffset.UTC;
        }

        public void setInstant(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneOffset getZone() {
            return zone;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }


    @Test
    void shouldAlwaysGenerateIncreasingIds() {
        DigitalIdGenerator generator =
                new DigitalIdGenerator(EntityType.INDIVIDUAL, Clock.systemUTC());

        DigitalId previous = generator.generate();

        for (int i = 0; i < 1000; i++) {
            DigitalId current = generator.generate();
            assertTrue(current.value().compareTo(previous.value()) > 0,
                    "IDs must be strictly increasing");
            previous = current;
        }
    }

    @Test
    void shouldHandleClockRollbackGracefully() {
        MutableClock clock = new MutableClock(
                Instant.parse("2026-01-01T00:00:05Z")
        );

        DigitalIdGenerator generator =
                new DigitalIdGenerator(EntityType.INDIVIDUAL, clock);

        DigitalId id1 = generator.generate();

        // Move clock backwards
        clock.setInstant(Instant.parse("2026-01-01T00:00:01Z"));

        DigitalId id2 = generator.generate();

        assertTrue(id2.value().compareTo(id1.value()) > 0,
                "ID must still increase even if clock moves backwards");
    }


    @Test
    void shouldGenerateLargeNumberOfUniqueIds() {
        DigitalIdGenerator generator =
                new DigitalIdGenerator(EntityType.INDIVIDUAL, Clock.systemUTC());

        Set<String> ids = new HashSet<>();

        for (int i = 0; i < 50_000; i++) {
            ids.add(generator.generate().value());
        }

        assertEquals(50_000, ids.size(),
                "All generated IDs must be unique");
    }

    @Test
    void shouldGenerateUniqueIdsUnderHighConcurrency() throws InterruptedException {
        DigitalIdGenerator generator =
                new DigitalIdGenerator(EntityType.INDIVIDUAL, Clock.systemUTC());

        int threads = 50;
        int perThread = 1000;

        Set<String> ids = java.util.concurrent.ConcurrentHashMap.newKeySet();
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    ids.add(generator.generate().value());
                }
                latch.countDown();
            }).start();
        }

        latch.await();

        assertEquals(threads * perThread, ids.size(),
                "All IDs must be unique under concurrency");
    }

    @Test
    void shouldGenerateCorrectTimestampAtCustomEpoch() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-01-01T00:00:00Z"),
                ZoneOffset.UTC
        );

        DigitalIdGenerator generator =
                new DigitalIdGenerator(EntityType.INDIVIDUAL, clock);

        DigitalId id = generator.generate();

        // timestamp portion should be 13 zeros
        String timestampPart = id.value().substring(3, 16);

        assertEquals("0000000000000", timestampPart);
    }

    @Test
    void shouldPadTimestampWithLeadingZeros() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-01-01T00:00:00.001Z"),
                ZoneOffset.UTC
        );

        DigitalIdGenerator generator =
                new DigitalIdGenerator(EntityType.INDIVIDUAL, clock);

        DigitalId id = generator.generate();

        String timestampPart = id.value().substring(3, 16);

        assertEquals(13, timestampPart.length());
        assertTrue(timestampPart.startsWith("000000000000"),
                "Timestamp must be left padded with zeros");
    }


    @Test
    void fixedClockShouldProduceDeterministicSequence() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-01-01T00:00:10Z"),
                ZoneOffset.UTC
        );

        DigitalIdGenerator generator =
                new DigitalIdGenerator(EntityType.INDIVIDUAL, clock);

        DigitalId id1 = generator.generate();
        DigitalId id2 = generator.generate();
        DigitalId id3 = generator.generate();

        assertTrue(id1.value().endsWith("00"));
        assertTrue(id2.value().endsWith("01"));
        assertTrue(id3.value().endsWith("02"));
    }










}

