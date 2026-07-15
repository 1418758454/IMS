package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.WorkloadScope;
import com.ruoyi.manage.domain.research.ResearchTotalWorkload;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.WorkloadRefreshMapper;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.Arrays;

public class WorkloadRefreshServiceImplTest extends TestCase {

    private FakeWorkloadRefreshMapper mapper;
    private WorkloadRefreshServiceImpl service;

    @Override
    protected void setUp() {
        mapper = new FakeWorkloadRefreshMapper();
        service = new WorkloadRefreshServiceImpl(mapper);
    }

    public void testResearchAggregateIsUpserted() {
        ResearchTotalWorkload total = new ResearchTotalWorkload();
        total.setUserId(BigDecimal.TEN);
        total.setYear("2026");
        mapper.researchResult = total;

        service.refreshResearch(10L, "2026");

        assertEquals(1, mapper.researchAggregateCount);
        assertEquals(1, mapper.researchUpsertCount);
        assertEquals(0, mapper.researchDeleteCount);
    }

    public void testMissingTeachingDetailsDeleteStaleSummary() {
        mapper.teachingResult = null;

        service.refreshTeaching(10L, 2026);

        assertEquals(1, mapper.teachingAggregateCount);
        assertEquals(0, mapper.teachingUpsertCount);
        assertEquals(1, mapper.teachingDeleteCount);
    }

    public void testDuplicateAndInvalidScopesAreSkipped() {
        mapper.researchResult = new ResearchTotalWorkload();

        service.refreshResearch(Arrays.asList(
                WorkloadScope.of(10L, 2026),
                WorkloadScope.of(10L, "2026"),
                WorkloadScope.of(null, 2026),
                WorkloadScope.of(10L, null)
        ));

        assertEquals(1, mapper.researchAggregateCount);
    }

    private static class FakeWorkloadRefreshMapper implements WorkloadRefreshMapper {
        private ResearchTotalWorkload researchResult;
        private TeachingTotalWorkload teachingResult;
        private int researchAggregateCount;
        private int teachingAggregateCount;
        private int researchUpsertCount;
        private int teachingUpsertCount;
        private int researchDeleteCount;
        private int teachingDeleteCount;

        @Override
        public ResearchTotalWorkload aggregateResearch(Long userId, String year) {
            researchAggregateCount++;
            return researchResult;
        }

        @Override
        public TeachingTotalWorkload aggregateTeaching(Long userId, Integer year) {
            teachingAggregateCount++;
            return teachingResult;
        }

        @Override
        public int upsertResearch(ResearchTotalWorkload workload) {
            researchUpsertCount++;
            return 1;
        }

        @Override
        public int upsertTeaching(TeachingTotalWorkload workload) {
            teachingUpsertCount++;
            return 1;
        }

        @Override
        public int deleteResearch(Long userId, String year) {
            researchDeleteCount++;
            return 1;
        }

        @Override
        public int deleteTeaching(Long userId, Integer year) {
            teachingDeleteCount++;
            return 1;
        }
    }
}
