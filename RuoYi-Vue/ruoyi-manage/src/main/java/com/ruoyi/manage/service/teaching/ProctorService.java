package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.Proctor;

import java.util.List;

/**
 * 监考模块服务接口
 */
public interface ProctorService extends IService<Proctor> {

    /**
     * 分页查询监考记录
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @param userId   用户ID
     * @param year     年份（可选）
     * @param isAudit
     * @return 分页结果
     */
    IPage<Proctor> getProctorPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    /**
     * 新增监考记录
     * @param proctor 监考实体
     * @return 新增结果
     */
    boolean addProctor(Proctor proctor);

    /**
     * 批量删除监考记录
     * @param ids 监考记录ID列表
     * @return 删除结果
     */
    boolean removeProctorByIds(List<Long> ids);

    /**
     * 计算单条监考记录的工作量
     * @param userId 用户ID
     * @param proctor 监考实体
     * @return 工作量
     */
    double countWorkload(Long userId, Proctor proctor);

    /**
     * 计算并更新用户的监考总工作量
     * @param userId 用户ID
     * @param year 年份
     * @return 总工作量
     */
    double countTotalWorkload(Long userId, Integer year);

    IPage<Proctor> getProctorPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditProctor(Long id, String status, String remark);
}