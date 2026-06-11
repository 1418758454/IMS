import request from '@/utils/request';
import { parseStrEmpty } from '@/utils/ruoyi';

/**
 * 查询人员基本信息详情（根据ID）
 * @param id 人员ID（必填）
 * @returns {*}
 */
export function getBasicInformation(id) {
  return request({
    url: `/manage/information/${parseStrEmpty(id)}`, // 处理空ID（转换为null或空字符串）
    method: 'get'
  });
}