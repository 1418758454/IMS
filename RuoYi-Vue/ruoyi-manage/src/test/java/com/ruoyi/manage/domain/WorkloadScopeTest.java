package com.ruoyi.manage.domain;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class WorkloadScopeTest extends TestCase {

    public void testSameUserAndYearAreDeduplicated() {
        Set<WorkloadScope> scopes = new HashSet<>();
        scopes.add(WorkloadScope.of(10L, "2026"));
        scopes.add(WorkloadScope.of(10L, 2026));

        assertEquals(1, scopes.size());
    }

    public void testInvalidScopeIsDetected() {
        assertFalse(WorkloadScope.of(null, 2026).isValid());
        assertFalse(WorkloadScope.of(10L, null).isValid());
        assertFalse(WorkloadScope.of(10L, "  ").isValid());
    }

    public void testTeachingYearCanBeParsed() {
        assertEquals(Integer.valueOf(2026), WorkloadScope.of(10L, "2026").teachingYear());
    }

    public void testDateLikeYearIsNormalizedToFourDigits() {
        assertEquals("2024", WorkloadScope.of(10L, "2024-10-09").getYear());
    }
}
