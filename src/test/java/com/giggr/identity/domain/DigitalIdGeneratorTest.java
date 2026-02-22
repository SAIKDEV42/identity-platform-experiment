//package com.giggr.identity.domain;
//
//import com.giggr.identity.domain.identity.DigitalId;
//import com.giggr.identity.domain.identity.DigitalIdGenerator;
//import com.giggr.identity.domain.identity.EntityType;
//import org.junit.jupiter.api.Test;
//
//import java.time.Clock;
//import java.time.Instant;
//import java.time.ZoneOffset;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.concurrent.CountDownLatch;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class DigitalIdGeneratorTest {
//
//    private static final Clock FIXED_CLOCK =
//            Clock.fixed(
//                    Instant.parse("2026-01-01T00:00:01Z"),
//                    ZoneOffset.UTC
//            );
//
//    @Test
//    void shouldGenerate18DigitNumericId() {
//        DigitalIdGenerator generator =
//                new DigitalIdGenerator(EntityType.INDIVIDUAL, FIXED_CLOCK);
//
//        DigitalId id = generator.generate();
//
//        assertEquals(18, id.value().length());
//        assertTrue(id.value().matches("\\d{18}"));
//    }
//
//    @Test
//    void shouldPrefixWithEntityCode() {
//        DigitalIdGenerator generator =
//                new DigitalIdGenerator(EntityType.INDIVIDUAL, FIXED_CLOCK);
//
//        DigitalId id = generator.generate();
//
//        assertTrue(id.value().startsWith("101"));
//    }
//
//    @Test
//    void shouldStartSequenceAtZero() {
//        DigitalIdGenerator generator =
//                new DigitalIdGenerator(EntityType.INDIVIDUAL, FIXED_CLOCK);
//
//        DigitalId id = generator.generate();
//
//        assertTrue(id.value().endsWith("00"));
//    }
//
//    @Test
//    void shouldIncrementSequenceWithinSameMillisecond() {
//        DigitalIdGenerator generator =
//                new DigitalIdGenerator(EntityType.INDIVIDUAL, FIXED_CLOCK);
//
//        DigitalId id1 = generator.generate();
//        DigitalId id2 = generator.generate();
//
//        assertTrue(id1.value().endsWith("00"));
//        assertTrue(id2.value().endsWith("01"));
//    }
//
//    @Test
//    void shouldResetSequenceWhenTimestampChanges() {
//        Clock clock1 = Clock.fixed(
//                Instant.parse("2026-01-01T00:00:01Z"),
//                ZoneOffset.UTC
//        );
//
//        Clock clock2 = Clock.fixed(
//                Instant.parse("2026-01-01T00:00:02Z"),
//                ZoneOffset.UTC
//        );
//
//        DigitalIdGenerator generator =
//                new DigitalIdGenerator(EntityType.INDIVIDUAL, clock1);
//
//        DigitalId id1 = generator.generate();
//
//        // Simulate time change by creating new generator with new clock
//        generator = new DigitalIdGenerator(EntityType.INDIVIDUAL, clock2);
//
//        DigitalId id2 = generator.generate();
//
//        assertTrue(id2.value().endsWith("00"));
//    }
//
//    @Test
//    void shouldNotGenerateDuplicatesUnderConcurrency() throws InterruptedException {
//        DigitalIdGenerator generator =
//                new DigitalIdGenerator(EntityType.INDIVIDUAL, Clock.systemUTC());
//
//        int threads = 20;
//        int perThread = 100;
//
//        Set<String> ids = new HashSet<>();
//        CountDownLatch latch = new CountDownLatch(threads);
//
//        for (int i = 0; i < threads; i++) {
//            new Thread(() -> {
//                for (int j = 0; j < perThread; j++) {
//                    synchronized (ids) {
//                        ids.add(generator.generate().value());
//                    }
//                }
//                latch.countDown();
//            }).start();
//        }
//
//        latch.await();
//
//        assertEquals(threads * perThread, ids.size());
//    }
//
//    @Test
//    void shouldThrowIfClockBeforeCustomEpoch() {
//        Clock beforeEpoch = Clock.fixed(
//                Instant.parse("2025-12-31T23:59:59Z"),
//                ZoneOffset.UTC
//        );
//
//        DigitalIdGenerator generator =
//                new DigitalIdGenerator(EntityType.INDIVIDUAL, beforeEpoch);
//
//        assertThrows(IllegalStateException.class, generator::generate);
//    }
//
//
//
//
//
//
//
//
//
//}
//
