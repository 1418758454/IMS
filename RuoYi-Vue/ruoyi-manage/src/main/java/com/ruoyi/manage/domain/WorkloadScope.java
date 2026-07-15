package com.ruoyi.manage.domain;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WorkloadScope {

    private static final Pattern DATE_LIKE_YEAR = Pattern.compile("^([0-9]{4})(?:[-/].*)$");

    private final Long userId;
    private final String year;

    private WorkloadScope(Long userId, String year) {
        this.userId = userId;
        this.year = year;
    }

    public static WorkloadScope of(Long userId, Object year) {
        return new WorkloadScope(userId, normalizeYear(year));
    }

    public static String normalizeYear(Object year) {
        if (year == null) {
            return null;
        }
        String normalizedYear = String.valueOf(year).trim();
        Matcher matcher = DATE_LIKE_YEAR.matcher(normalizedYear);
        return matcher.matches() ? matcher.group(1) : normalizedYear;
    }

    public Long getUserId() {
        return userId;
    }

    public String getYear() {
        return year;
    }

    public boolean isValid() {
        return userId != null && year != null && !year.isEmpty();
    }

    public Integer teachingYear() {
        return Integer.valueOf(year);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkloadScope)) {
            return false;
        }
        WorkloadScope that = (WorkloadScope) o;
        return Objects.equals(userId, that.userId) && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, year);
    }
}
