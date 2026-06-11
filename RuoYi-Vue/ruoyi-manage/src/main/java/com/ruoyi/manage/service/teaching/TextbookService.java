package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.Textbook;

import java.util.List;

/**
 * 出版教材服务接口
 */
public interface TextbookService extends IService<Textbook> {

    /**
     * 分页查询出版教材列表
     */
    IPage<Textbook> getTextbookPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    /**
     * 新增出版教材
     */
    boolean addTextbook(Textbook textbook);

    /**
     * 批量删除出版教材
     */
    boolean removeTextbookByIds(List<Long> ids);

    /**
     * 计算单本教材工作量
     */
    double countWorkload(Long userId, Textbook textbook);

    /**
     * 计算并更新总工作量
     */
    double countTotalWorkload(Long userId, Integer year);

    IPage<Textbook> getTextbookPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditTextbook(Long id, String status, String remark);
}