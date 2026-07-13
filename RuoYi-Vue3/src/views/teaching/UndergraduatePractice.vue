<template>
  <el-card class="module-card">
    <!-- 模块头部（与实验课一致，仅修改标题） -->
    <div class="module-header">
      <h3 class="module-title">三、本科教学（实践教学）</h3>
      <!-- 新增按钮：只在非审核模式下显示 -->
      <el-button 
        v-if="mode === 'show'"
        type="primary" 
        icon="Plus" 
        size="small" 
        :disabled="!hasTaskScreenshot"
        @click="addRow"
      >
        新增行
      </el-button>
    </div>

    <TeachingTaskScreenshotPanel
      v-if="mode === 'show'"
      :year="year"
      module-type="practice"
      @attachment-state-change="hasTaskScreenshot = $event"
    />

    <!-- 表格（列结构调整为实践教学字段，样式与实验课完全一致） -->
    <el-table 
      :data="tableData" 
      border 
      class="inline-edit-table"
      :header-cell-style="headerCellStyle"
      :row-class-name="tableRowClassName"
    >
      <!-- 序号列（与实验课相同：width=60，居中） -->
      <el-table-column label="序号" type="index" width="60" align="center">
        <template v-slot="scope">
          <!-- 结合分页参数计算连续序号 -->
          {{ (pagination.currentPage - 1) * pagination.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>

      <el-table-column v-if="mode === 'check' || mode === 'search'" label="姓名" prop="userName" align="center">
        <template v-slot="scope"><span>{{ scope.row.userName }}</span></template>
      </el-table-column>

      <el-table-column v-if="mode === 'check' || mode === 'search'" label="个人教学任务截图（PDF）" min-width="180" align="center">
        <template v-slot="scope">
          <TeachingTaskScreenshotViewer :user-id="scope.row.userId" :year="scope.row.year" module-type="practice" />
        </template>
      </el-table-column>

      <!-- 课程名称（与实验课相同：width=250，居中） -->
      <el-table-column prop="courseName" label="课程名称" width="250" align="center">
        <template v-slot="scope">
          <el-input 
            v-if="scope.row.isEditing" 
            v-model="scope.row.courseName" 
            size="small" 
            placeholder="请输入课程名称"
            :maxlength="50"
            class="edit-input"
          ></el-input>
          <span v-else>{{ scope.row.courseName || '-' }}</span>
        </template>
      </el-table-column>

      <!-- 课程类型（替换实验课的“授课学生”列，width=190，居中） -->
      <el-table-column prop="courseType" label="课程类型" min-width="100" align="center">
        <template v-slot="scope">
          <el-select 
            v-if="scope.row.isEditing" 
            v-model="scope.row.courseType" 
            size="small" 
            placeholder="请选择课程类型"
            class="edit-select"
          >
            <el-option label="课程设计" value="课程设计"></el-option>
            <el-option label="全程实习" value="全程实习"></el-option>
            <el-option label="带队实习" value="带队实习"></el-option>
          </el-select>
          <span v-else>{{ scope.row.courseType || '-'}}</span>
        </template>
      </el-table-column>

      <!-- 计划天数（替换实验课的“实验学时”列，width=120，居中） -->
      <el-table-column prop="planDays" label="计划天数(每周按5天计算)" width="220" align="center">
        <template v-slot="scope">
          <el-input 
            v-if="scope.row.isEditing" 
            v-model.number="scope.row.planDays" 
            size="small" 
            type="number" 
            min="1"
            placeholder="0"
            class="edit-input"
          ></el-input>
          <span v-else>{{ scope.row.planDays || '-' }}</span>
        </template>
      </el-table-column>

      <!-- 班级数（替换实验课的“学生人数”列，width=120，居中） -->
      <el-table-column prop="classCount" label="班级数(若分组，每组≥15人的组按一个班级计算)" width="320" align="center">
        <template v-slot="scope">
          <el-input 
            v-if="scope.row.isEditing" 
            v-model.number="scope.row.classCount" 
            size="small" 
            type="number" 
            min="1"
            placeholder="0"
            class="edit-input"
          ></el-input>
          <span v-else>{{ scope.row.classCount || '-' }}</span>
        </template>
      </el-table-column>

      <!-- 工作量列（与实验课完全相同：width=150，居中，蓝色强调） -->
      <el-table-column prop="workload" label="工作量" width="150" align="center">
        <template v-slot="scope">
          <span class="workload-value">{{ (scope.row.workload || 0).toFixed(3) }}</span>
        </template>
      </el-table-column>

      <!-- 审核状态列 -->
      <el-table-column label="审核状态" prop="status" min-width="80" align="center">
        <template v-slot="scope">
          <span :class="['status-tag', getStatusClass(scope.row.status)]">
            {{ scope.row.status }}
          </span>
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column label="操作" min-width="260" align="center">
        <template v-slot="scope">
           <!-- 审核模式的操作按钮 -->
          <div v-if="mode === 'check' && scope.row.isEditing" class="edit-actions">
            <el-button type="primary" size="small" @click="saveRow(scope.row)">保存</el-button>
            <el-button size="small" @click="cancelRow(scope.row, scope.$index)">取消</el-button>
          </div>
          <div v-else-if="mode === 'check'" class="check-actions">
            <el-button 
              type="success" 
              size="small" 
              @click="$emit('audit', scope.row, moduleKey)"
              :disabled="scope.row.status === '已通过'"
            >
              审核通过
            </el-button>
            <el-button 
              type="warning" 
              size="small" 
              @click="$emit('reject', scope.row, moduleKey)"
              :disabled="scope.row.status === '退回修改'"
            >
              退回修改
            </el-button>
            <el-button type="primary" size="small" icon="Edit" @click="editRow(scope.row)">修改</el-button>
            <el-button type="danger" size="small" icon="Delete" @click="deleteRow(scope.row.id, scope.$index)">删除</el-button>
          </div>
          
          <!-- 非审核模式的操作按钮 -->
          <div v-else>
            <div v-if="scope.row.isEditing" class="edit-actions">
              <el-button type="primary" size="small" @click="saveRow(scope.row)">保存</el-button>
              <el-button type="default" size="small" @click="cancelRow(scope.row, scope.$index)">取消</el-button>
            </div>
            <div v-else class="view-actions">
              <!-- 查看意见按钮（仅当有备注且状态为退回修改时显示） -->
              <el-button 
                v-if="scope.row.status === '退回修改' && scope.row.remark"
                type="warning" 
                size="small"
                @click="showRemark(scope.row.remark)"
              >
                查看意见
              </el-button>
              
              <!-- 编辑按钮 -->
              <el-tooltip 
                v-if="scope.row.status === '已通过'" 
                content="已通过不可修改" 
                placement="top"
              >
                <span>
                  <el-button type="primary" size="small" disabled icon="Edit">编辑</el-button>
                </span>
              </el-tooltip>
              <el-button 
                v-else 
                type="primary" 
                size="small" 
                icon="Edit"
                @click="editRow(scope.row)"
              >
                编辑
              </el-button>
              <el-button type="danger" size="small" icon="Delete" @click="deleteRow(scope.row.id, scope.$index)">删除</el-button>
            </div>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加分页器 -->
    <div class="pagination-container" v-if="pagination" style="margin-top: 10px; text-align: right;">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[5, 10, 20, 50]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total">
      </el-pagination>
    </div>

    <!-- 模块汇总（与实验课完全相同） -->
    <div class="module-summary" v-if="mode != 'check'">
      <div class="summary-header">
        <span class="summary-title">模块合计</span>
        <span class="summary-divider">：</span>
      </div>
      <div class="summary-content">
        <div class="summary-row">
          <span class="summary-label">已确认总工作量：</span>
          <span class="summary-value confirmed">{{ confirmedWorkload.toFixed(3) }} 工作量</span>
        </div>
        <div class="summary-row">
          <span class="summary-label">预计总工作量：</span>
          <span class="summary-value estimated">{{ estimatedWorkload.toFixed(3) }} 工作量</span>
        </div>
      </div>
    </div>

    <!-- 查看意见弹窗 -->
    <el-dialog 
      title="审核意见" 
      v-model="remarkDialogVisible"
      width="500px"
    >
      <div class="remark-content">
        <el-alert
          title="您的提交已被退回，请根据以下意见进行修改"
          type="warning"
          :closable="false"
          style="margin-bottom: 15px;"
          show-icon
        />
        
        <div class="remark-text">
          {{ currentRemark || '暂无具体修改意见' }}
        </div>
      </div>

      <template v-slot:footer>
        <el-button type="primary" @click="remarkDialogVisible = false">我知道了</el-button>
      </template>
    </el-dialog>

  </el-card>
</template>

<script>
import { ElMessage } from 'element-plus';
import { Edit, Delete } from '@element-plus/icons-vue';
import TeachingTaskScreenshotPanel from './components/TeachingTaskScreenshotPanel.vue';
import TeachingTaskScreenshotViewer from './components/TeachingTaskScreenshotViewer.vue';

export default {
  components: { TeachingTaskScreenshotPanel, TeachingTaskScreenshotViewer },
  props: { 
    data: { type: Array, required: true }, 
    year: { type: Number, required: true },
    mode: { type: String, default: '' },
    moduleKey: { type: String, required: true },
    confirmedWorkload: { type: Number, default: 0 }, // 新增
    estimatedWorkload: { type: Number, default: 0 },
    // 添加分页属性
    pagination: {
      type: Object,
      default: () => ({
        currentPage: 1,
        pageSize: 10,
        total: 0
      })
    }
  },
  emits: ['save-row', 'delete-row', 'audit', 'reject','size-change', 'current-change'], // Vue 3 新增：声明事件类型
  
  data() { 
    return { 
      tableData: JSON.parse(JSON.stringify(this.data)), // 深拷贝父组件传入的data
      // 意见弹窗相关
      remarkDialogVisible: false,
      currentRemark: '', // 当前显示的备注内容
      hasTaskScreenshot: false,
    }; 
  },
  watch: { 
    // 监听父组件data变化，同步更新表格数据
    data: { 
      handler(newVal) { 
        this.tableData = JSON.parse(JSON.stringify(newVal)); 
      }, 
      deep: true 
    } 
  },
  methods: {
    // 新增行（与实验课逻辑一致，字段适配实践教学）
    addRow() {
      this.tableData.push({
        id: null,
        courseName: '', // 课程名称（必填）
        courseType: '', // 课程类型（实习/实训/课程设计）
        planDays: 1, // 计划天数（默认1）
        classCount: 1, // 班级数（默认1）
        workload: 0, // 工作量（自动计算）
        isEditing: true // 编辑状态标记
      });
    },

    // 编辑行（与实验课完全相同）
    editRow(row) {
      row._originData = JSON.parse(JSON.stringify(row)); // 保存原始数据用于取消编辑
      row.isEditing = true; 
    },

    // 保存行（字段适配实践教学，模块标识改为practiceTeaching）
    saveRow(row) {
      // 校验必填项（与实验课逻辑一致，字段名调整）
      if (!row.courseName) return ElMessage.warning('课程名称不能为空');
      if (!row.courseType) return ElMessage.warning('请选择课程类型');
      if (row.planDays < 1) return ElMessage.warning('计划天数不能小于1');
      if (row.classCount < 1) return ElMessage.warning('班级数不能小于1');
      
      // 提交前清理临时字段
      delete row.isEditing;
      delete row._originData;
      // 触发父组件保存事件，模块标识为practiceTeaching
      this.$emit('save-row', 'practiceTeaching', row); 
    },

    // 取消行编辑（与实验课完全相同）
    cancelRow(row, index) {
      if (row.id === null) {
        // 新增行未保存，直接删除
        this.tableData.splice(index, 1);
      } else {
        // 回滚到原始数据
        this.tableData.splice(index, 1, row._originData);
      }
    },

    // 删除行（模块标识改为practiceTeaching）
    deleteRow(rowId, index) {
      this.tableData.splice(index, 1);
      this.$emit('delete-row', 'practiceTeaching', rowId); // 触发父组件删除事件
    },

    // 计算模块合计工作量（与实验课逻辑一致）
    calculateModuleTotal() {
      return this.tableData.reduce((sum, row) => sum + Number(row.workload || 0), 0).toFixed(3);
    },

    // 课程类型格式化（实践教学特有：实习/实训/课程设计）
    // formatCourseType(type) {
    //   const typeMap = {
    //     internship: '实习',
    //     training: '实训',
    //     course_design: '课程设计'
    //   };
    //   return typeMap[type] || '-';
    // },

    // 表头样式（与实验课完全相同：首列蓝色背景，其他浅灰）
    headerCellStyle({ column, columnIndex }) {
      return columnIndex === 0 ? { background: '#e6f7ff' } : { background: '#f5f7fa' };
    },

    // 行样式（斑马纹，与实验课完全相同）
    tableRowClassName({ row, rowIndex }) { 
      return rowIndex % 2 === 0 ? 'even-row' : ''; 
    },

    /**
     * 根据审核状态返回对应的CSS类名
     */
    getStatusClass(status) {
      const statusMap = {
        '已通过': 'status-passed',
        '退回修改': 'status-rejected', 
        '待审核': 'status-pending'
      };
      return statusMap[status] || '';
    },

    // 显示审核意见
    showRemark(remark) {
      this.currentRemark = remark;
      this.remarkDialogVisible = true;
    },
    // 添加分页事件处理
    handleSizeChange(size) {
      this.$emit('size-change', size);
    },
    
    handleCurrentChange(page) {
      this.$emit('current-change', page);
    },

  }
};
</script>

<style scoped>
/* 所有样式与实验课完全一致，无任何省略或修改 */
.module-card {
  margin: 0 20px 30px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  border: none;
}
.module-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e6e6e6;
}
.module-title {
  font-size: 18px;
  color: #2c3e50;
  font-weight: 500;
  padding-left: 10px;
  border-left: 4px solid #409eff;
  margin: 0;
}
.inline-edit-table {
  width: 100%;
  font-size: 14px;
}
:deep(.el-table) td {
  padding: 12px 8px;
}
:deep(.el-table__row:hover) {
  background-color: #f5f9ff;
}
:deep(.even-row) {
  background-color: #fafafa;
}
.workload-value {
  color: #2c6ecb;
  font-weight: 500;
}
/* 模块合计样式（与科研业绩保持一致） */
.module-summary {
  text-align: right;
  padding: 12px 20px;
  font-size: 14px;
  color: #303133;
  border-top: 1px dashed #e6e6e6;
  background-color: #fafafa;
}
.summary-header {
  display: flex;
  align-items: center;
  margin-bottom: 0;
}

.summary-title {
  font-weight: 600;
  font-size: 15px;
  color: #1f2f3d;
  letter-spacing: 0.5px;
  position: relative;
  padding-left: 12px;
}

.summary-title::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border-radius: 2px;
}

.summary-divider {
  font-weight: 600;
  color: #1f2f3d;
}

.summary-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}
.summary-row {
  margin-bottom: 5px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}
.summary-row:last-child {
  margin-bottom: 0;
}
.summary-label {
  color: #606266;
  margin-right: 8px;
}
.summary-value {
  font-weight: 500;
  color: #e67700;
  margin-left: 8px;
  min-width: 80px;
  text-align: right;
}
/* 已确认工作量样式 */
.summary-value.confirmed {
  color: #67c23a; /* 绿色，表示已确认 */
}

/* 预计工作量样式 */
.summary-value.estimated {
  color: #e6a23c; /* 橙色，表示预计 */
}
.edit-input {
  width: 100% !important;
}
.edit-actions, .view-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}
/* 状态标签基础样式 */
.status-tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  display: inline-block;
  min-width: 60px;
  text-align: center;
}

/* 已通过状态 */
.status-passed {
  color: #67C23A;
  background-color: #f0f9eb;
  border: 1px solid #c2e7b0;
}

/* 退回修改状态 */
.status-rejected {
  color: #E6A23C;
  background-color: #fdf6ec;
  border: 1px solid #f5dab1;
}

/* 待审核状态 */
.status-pending {
  color: #409EFF;
  background-color: #ecf5ff;
  border: 1px solid #b3d8ff;
}
/* 禁用按钮的样式调整 */
:deep(.el-button.is-disabled) {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 提示框样式 */
:deep(.el-tooltip) {
  display: inline-block;
}

/* 意见内容样式 */
.remark-content {
  line-height: 1.6;
}

.remark-text {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 4px;
  border-left: 4px solid #e6a23c;
  white-space: pre-wrap;
  min-height: 60px;
  color: #606266;
  font-size: 14px;
}
/* 分页控件容器样式 */
.pagination-container {
  padding: 10px 20px;
  margin-bottom: 20px;
  text-align: center;
  width: 100%; /* 确保占满整个容器宽度 */
  box-sizing: border-box; /* 包含padding在宽度计算内 */
}

/* 调整分页控件本身的样式 */
:deep(.el-pagination) {
  display: inline-flex; /* 使用flex布局 */
  flex-wrap: wrap; /* 允许换行 */
  justify-content: center;
  margin-top: -10px;
  gap: 5px; /* 元素之间的间距 */
}
</style>
