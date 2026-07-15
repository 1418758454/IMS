package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.WorkloadScope;
import com.ruoyi.manage.domain.research.ResearchTotalWorkload;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.WorkloadRefreshMapper;
import com.ruoyi.manage.service.WorkloadRefreshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class WorkloadRefreshServiceImpl implements WorkloadRefreshService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadRefreshServiceImpl.class);

    private final WorkloadRefreshMapper mapper;

    public WorkloadRefreshServiceImpl(WorkloadRefreshMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshResearch(Long userId, String year) {
        WorkloadScope scope = WorkloadScope.of(userId, year);
        if (!scope.isValid()) {
            return;
        }
        ResearchTotalWorkload total = mapper.aggregateResearch(userId, scope.getYear());
        if (total == null) {
            mapper.deleteResearch(userId, scope.getYear());
        } else {
            mapper.upsertResearch(total);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshResearch(Collection<WorkloadScope> scopes) {
        for (WorkloadScope scope : distinctScopes(scopes)) {
            if (scope.isValid()) {
                refreshResearch(scope.getUserId(), scope.getYear());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshTeaching(Long userId, Integer year) {
        WorkloadScope scope = WorkloadScope.of(userId, year);
        if (!scope.isValid()) {
            return;
        }
        TeachingTotalWorkload total = mapper.aggregateTeaching(userId, year);
        if (total == null) {
            mapper.deleteTeaching(userId, year);
        } else {
            mapper.upsertTeaching(total);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshTeaching(Collection<WorkloadScope> scopes) {
        for (WorkloadScope scope : distinctScopes(scopes)) {
            if (!scope.isValid()) {
                continue;
            }
            try {
                refreshTeaching(scope.getUserId(), scope.teachingYear());
            } catch (NumberFormatException ex) {
                log.warn("Skip workload refresh with invalid teaching year: userId={}, year={}",
                        scope.getUserId(), scope.getYear());
            }
        }
    }

    private Set<WorkloadScope> distinctScopes(Collection<WorkloadScope> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(scopes);
    }
}
