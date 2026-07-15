package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.WorkloadScope;

import java.util.Collection;

public interface WorkloadRefreshService {

    void refreshResearch(Long userId, String year);

    void refreshResearch(Collection<WorkloadScope> scopes);

    void refreshTeaching(Long userId, Integer year);

    void refreshTeaching(Collection<WorkloadScope> scopes);
}
