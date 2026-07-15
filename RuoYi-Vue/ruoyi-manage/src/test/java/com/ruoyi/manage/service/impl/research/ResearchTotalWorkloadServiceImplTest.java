package com.ruoyi.manage.service.impl.research;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.manage.domain.research.ResearchTotalWorkload;
import com.ruoyi.manage.mapper.research.ResearchTotalWorkloadMapper;
import junit.framework.TestCase;
import org.apache.ibatis.exceptions.TooManyResultsException;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;

public class ResearchTotalWorkloadServiceImplTest extends TestCase {

    public void testSingleUserQueryLimitsLegacyDuplicateRows() throws Exception {
        ResearchTotalWorkload expected = new ResearchTotalWorkload();
        expected.setUserId(BigDecimal.valueOf(123456L));
        expected.setYear("2026");
        expected.setSubjectEstimatedWorkload(BigDecimal.valueOf(5));

        ResearchTotalWorkloadMapper mapper = (ResearchTotalWorkloadMapper) Proxy.newProxyInstance(
                ResearchTotalWorkloadMapper.class.getClassLoader(),
                new Class<?>[]{ResearchTotalWorkloadMapper.class},
                (proxy, method, args) -> {
                    if ("selectOne".equals(method.getName())) {
                        QueryWrapper<?> query = (QueryWrapper<?>) args[0];
                        String sql = query.getCustomSqlSegment().toUpperCase();
                        if (!sql.contains("ORDER BY ID DESC") || !sql.contains("LIMIT 1")) {
                            throw new TooManyResultsException("legacy duplicate summaries");
                        }
                        return expected;
                    }
                    return primitiveDefault(method.getReturnType());
                }
        );

        ResearchTotalWorkloadServiceImpl service = new ResearchTotalWorkloadServiceImpl();
        Field mapperField = ResearchTotalWorkloadServiceImpl.class
                .getDeclaredField("researchTotalWorkloadMapper");
        mapperField.setAccessible(true);
        mapperField.set(service, mapper);

        try {
            ResearchTotalWorkload actual = service.getTotalWorkload(2026, 123456L);
            assertSame(expected, actual);
        } catch (TooManyResultsException exception) {
            fail("单用户年度汇总查询必须限制为最新一条，避免旧重复数据导致查询失败");
        }
    }

    private static Object primitiveDefault(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (returnType == boolean.class) {
            return false;
        }
        if (returnType == char.class) {
            return '\0';
        }
        return 0;
    }
}
