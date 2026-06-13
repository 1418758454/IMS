package com.ruoyi.manage.domain.statistics;

/**
 * Request body for statistics xlsx export.
 */
public class StatisticsExportRequest {
    /**
     * FILTERED: export by current filter; YEAR_ALL: export all records in the year.
     */
    private String scope;

    private String year;

    private Long deptId;

    private String userId;

    private Boolean includePdf;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIncludePdf() {
        return includePdf;
    }

    public void setIncludePdf(Boolean includePdf) {
        this.includePdf = includePdf;
    }
}
