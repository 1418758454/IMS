<template>
  <div class="research-performance">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="main-title">科研业绩统计</h2>
      <el-divider></el-divider>
    </div>
    <!-- 筛选条件区域 -->
    <el-card class="filter-card" style="margin-bottom: 20px;">
      <div class="filter-header">
        <h3 style="margin: 0; color: #303133;">数据筛选</h3>
      </div>
      <el-form :model="filterForm" ref="filterFormRef" label-width="100px" style="margin-top: 15px;">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="年份" prop="year">
              <el-select 
                v-model="filterForm.year" 
                placeholder="请选择年份" 
                style="width: 100%;"
                @change="handleYearChange"
              >
                <el-option 
                  v-for="year in years" 
                  :key="year" 
                  :label="year" 
                  :value="year"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="部门" prop="deptId">
              <el-select 
                v-model="filterForm.deptId" 
                placeholder="请选择部门" 
                style="width: 100%;"
                @change="handleDeptChange"
                :loading="deptLoading"
              >
                <el-option 
                  v-for="dept in deptList" 
                  :key="dept.deptId" 
                  :label="dept.deptName" 
                  :value="dept.deptId"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="待审核用户" prop="userId">
              <el-select 
                v-model="filterForm.userId" 
                placeholder="请选择待审核用户" 
                style="width: 100%;"
                :loading="userLoading"
                filterable
              >
                <el-option label="全部用户" value="all"></el-option>
                <el-option 
                  v-for="user in userList" 
                  :key="user.userId" 
                  :label="user.name" 
                  :value="user.userId"
                >
                  <span>{{ user.name }} - {{ user.userId }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ user.gender }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label=" " style="margin-bottom: 0;">
              <el-button 
                type="primary" 
                icon="Search" 
                @click="handleSearch"
                :loading="searchLoading"
                style="width: 100px;"
              >
                搜索
              </el-button>
              <el-button 
                icon="Refresh" 
                @click="handleReset"
                style="margin-left: 10px;"
              >
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 年度工作量汇总卡片 -->
    <el-card class="module-card annual-workload-card">
      <div class="module-header" style="margin-bottom: 15px;">
        <div style="display: flex; align-items: center; gap: 15px;">
          <h3 class="module-title">已确认年度工作量汇总</h3>
        </div>
      </div>
      <!-- 表格数据绑定改为动态变量 -->
      <el-table :data="yearWorkloadTableData" border class="summary-table" :loading="summaryLoading">
        <el-table-column label="年份" width="120" align="center" prop="year" />
        <!-- 课题工作量：替换为subjectConfirmedWorkload -->
        <el-table-column label="课题工作量" min-width="120" align="center">
          <template v-slot="scope">{{ scope.row.subjectConfirmedWorkload }}</template>
        </el-table-column>
        <!-- 论文工作量：替换为paperConfirmedWorkload -->
        <el-table-column label="论文工作量" min-width="120" align="center">
          <template v-slot="scope">{{ scope.row.paperConfirmedWorkload }}</template>
        </el-table-column>
        <!-- 论著工作量：替换为monographConfirmedWorkload -->
        <el-table-column label="论著工作量" min-width="120" align="center">
          <template v-slot="scope">{{ scope.row.monographConfirmedWorkload }}</template>
        </el-table-column>
        <!-- 获奖工作量：替换为awardConfirmedWorkload -->
        <el-table-column label="获奖工作量" min-width="120" align="center">
          <template v-slot="scope">{{ scope.row.awardConfirmedWorkload }}</template>
        </el-table-column>
        <!-- 专利工作量：替换为patentConfirmedWorkload -->
        <el-table-column label="专利工作量" min-width="120" align="center">
          <template v-slot="scope">{{ scope.row.patentConfirmedWorkload }}</template>
        </el-table-column>
        <!-- 软著工作量：替换为softwareConfirmedWorkload -->
        <el-table-column label="软著工作量" min-width="120" align="center">
          <template v-slot="scope">{{ scope.row.softwareConfirmedWorkload }}</template>
        </el-table-column>
        <el-table-column label="总工作量" width="220" align="center">
          <template v-slot="scope">
            <span class="total-workload">{{ scope.row. totalWorkload }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 一、课题模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">一、课题</h3>
        <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
          <el-button type="primary" icon="Plus" @click="handleAdd('subject')" >新增</el-button>
          <el-button type="danger" icon="Delete" @click="handleDelete('subject')">删除</el-button>
        </div>
      </div>
      
      <el-table :data="subjectTableData" border class="module-table" ref="subjectTableRef" v-loading="loading.subject">
        <!-- 复选框列（第一列） -->
        <el-table-column type="selection" width="55" align="center"></el-table-column>

        <el-table-column label="课题" align="center" width="80">
          <template v-slot="scope">
            <!-- 结合分页参数计算连续序号 -->
            {{ (modulePagination.subject.currentPage - 1) * modulePagination.subject.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="姓名" prop="userName" align="center">
          <template v-slot="scope"><span>{{ scope.row.userName }}</span></template>
        </el-table-column>
        <el-table-column label="项目名称" prop="projectName" align="center">
          <template v-slot="scope"><span>{{ scope.row.projectName }}</span></template>
        </el-table-column>
        <el-table-column label="课题类型" prop="subjectType" align="center">
          <template v-slot="scope"><span>{{ scope.row.subjectType }}</span></template>
        </el-table-column>
        <el-table-column label="当年到位经费(万)" prop="money" align="center">
          <template v-slot="scope"><span>{{ scope.row.money }}</span></template>
        </el-table-column>
        <el-table-column label="执行时间" width="300" align="center">
          <template v-slot="scope">
            {{ scope.row.execute_time_start || '-' }} 至 {{ scope.row.execute_time_end || '-' }}
          </template>
        </el-table-column>
        
        <el-table-column label="业绩点分配比例" prop="rank" align="center">
          <template v-slot="scope"><span>{{ Number(scope.row.rank).toFixed(1) }}</span></template>
        </el-table-column>
        <el-table-column label="系数" prop="coefficient" align="center">
          <template v-slot="scope"><span>{{ scope.row.coefficient }}</span></template>
        </el-table-column>
        <!-- PDF列 -->
        <el-table-column label="PDF" align="center" width="100">
          <template v-slot="scope">
            <!-- 有PDF时显示查看链接，无则显示“无” -->
            <el-link 
              v-if="scope.row.pdfUrl" 
              type="primary" 
              icon="Document" 
              :href="scope.row.pdfUrl" 
              target="_blank"
            >
              查看
            </el-link>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="工作量" prop="workload" align="center">
          <template v-slot="scope"><span class="workload-value">{{ scope.row.workload.toFixed(3) }}</span></template>
        </el-table-column>
        <el-table-column label="审核状态" prop="status" min-width="80" align="center">
          <template v-slot="scope">
            <span :class="['status-tag', getStatusClass(scope.row.status)]">
              {{ scope.row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100" align="center">
          <template v-slot="scope">
            <div style="display: flex; gap: 8px; justify-content: center;">
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
                  <el-button type="primary" size="small" disabled>编辑</el-button>
                </span>
              </el-tooltip>
              <el-button 
                v-else 
                type="primary" 
                size="small" 
                @click="handleEdit('subject', scope.row)"
              >
                编辑
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 新增：分页控件 -->
      <div class="pagination-container" style="margin-top: 10px; text-align: right;">
        <el-pagination
          @size-change="(size) => handleSizeChange('subject', size)"  
          @current-change="(page) => handleCurrentChange('subject', page)"
          :current-page="modulePagination.subject.currentPage" 
          :page-sizes="[5, 10, 20, 50]"
          :page-size="modulePagination.subject.pageSize"  
          layout="total, sizes, prev, pager, next, jumper"
          :total="modulePagination.subject.total"> 
        </el-pagination>
      </div>

      <div class="module-summary">
        <div class="summary-header">
          <span class="summary-title">模块合计</span>
          <span class="summary-divider">：</span>
        </div>
        <div class="summary-content">
          <div class="summary-row">
            <span class="summary-label">已确认总工作量：</span>
            <span class="summary-value confirmed">{{ subjectConfirmedWorkload.toFixed(3) }} 工作量</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">预计总工作量：</span>
            <span class="summary-value estimated">{{ subjectTotalWorkload.toFixed(3) }} 工作量</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 二、论文模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">二、论文</h3>
        <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
            <el-button type="primary" icon="Plus" @click="handleAdd('paper')" >新增</el-button>
            <el-button type="danger" icon="Delete" @click="handleDelete('paper')">删除</el-button>
        </div>
      </div>
      
      <el-table :data="paperTableData" border class="module-table" ref="paperTableRef">
        <!-- 复选框列（第一列） -->
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column label="论文" align="center" width="80">
          <template v-slot="scope">
            <!-- 结合分页参数计算连续序号 -->
            {{ (modulePagination.paper.currentPage - 1) * modulePagination.paper.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="姓名" prop="userName" align="center">
          <template v-slot="scope"><span>{{ scope.row.userName }}</span></template>
        </el-table-column>
        <el-table-column label="论文名称" prop="title" width="350" align="center">
          <template v-slot="scope"><span>{{ scope.row.title }}</span></template>
        </el-table-column>
        <el-table-column label="出版刊物" prop="journal" align="center">
          <template v-slot="scope"><span>{{ scope.row.journal }}</span></template>
        </el-table-column>
        <el-table-column label="出版时间" prop="publishTime" align="center">
          <template v-slot="scope"><span>{{ scope.row.publishTime }}</span></template>
        </el-table-column>
        <el-table-column label="论文级别" prop="level" width="350" align="center">
          <template v-slot="scope"><span>{{ scope.row.level }}</span></template>
        </el-table-column>
        <el-table-column label="业绩点分配比例" prop="rank" align="center">
          <template v-slot="scope"><span>{{ scope.row.rank }}</span></template>
        </el-table-column>
        <!-- PDF列 -->
        <el-table-column label="PDF" align="center" width="100">
          <template v-slot="scope">
            <!-- 有PDF时显示查看链接，无则显示“无” -->
            <el-link 
              v-if="scope.row.pdfUrl" 
              type="primary" 
              icon="Document" 
              :href="scope.row.pdfUrl" 
              target="_blank"
            >
              查看
            </el-link>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="工作量" prop="workload" align="center">
          <template v-slot="scope"><span class="workload-value">{{ scope.row.workload.toFixed(3) }}</span></template>
        </el-table-column>
        <el-table-column label="审核状态" prop="status" min-width="80" align="center">
          <template v-slot="scope">
            <span :class="['status-tag', getStatusClass(scope.row.status)]">
              {{ scope.row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100" align="center">
          <template v-slot="scope">
            <div style="display: flex; gap: 8px; justify-content: center;">
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
                  <el-button type="primary" size="small" disabled>编辑</el-button>
                </span>
              </el-tooltip>
              <el-button 
                v-else 
                type="primary" 
                size="small" 
                @click="handleEdit('paper', scope.row)"
              >
                编辑
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 新增：分页控件 -->
      <div class="pagination-container" style="margin-top: 10px; text-align: right;">
        <el-pagination
          @size-change="(size) => handleSizeChange('paper', size)"  
          @current-change="(page) => handleCurrentChange('paper', page)"
          :current-page="modulePagination.paper.currentPage" 
          :page-sizes="[5, 10, 20, 50]"
          :page-size="modulePagination.paper.pageSize"  
          layout="total, sizes, prev, pager, next, jumper"
          :total="modulePagination.paper.total"> 
        </el-pagination>
      </div>

      <div class="module-summary">
        <div class="summary-header">
          <span class="summary-title">模块合计</span>
          <span class="summary-divider">：</span>
        </div>
        <div class="summary-content">
          <div class="summary-row">
            <span class="summary-label">已确认总工作量：</span>
            <span class="summary-value confirmed">{{ paperConfirmedWorkload.toFixed(3) }} 工作量</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">预计总工作量：</span>
            <span class="summary-value estimated">{{ paperTotalWorkload.toFixed(3) }} 工作量</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 三、论著模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">三、论著</h3>
        <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
          <el-button type="primary" icon="Plus" @click="handleAdd('monograph')" >新增</el-button>
          <el-button type="danger" icon="Delete" @click="handleDelete('monograph')">删除</el-button>
        </div>
      </div>
      
      <el-table :data="monographTableData" border class="module-table" ref="monographTableRef">
        <!-- 复选框列（第一列） -->
        <el-table-column type="selection" width="55" align="center"></el-table-column>

        <el-table-column label="论著" align="center" width="80">
          <template v-slot="scope">
            <!-- 结合分页参数计算连续序号 -->
            {{ (modulePagination.monograph.currentPage - 1) * modulePagination.monograph.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="姓名" prop="userName" align="center">
          <template v-slot="scope"><span>{{ scope.row.userName }}</span></template>
        </el-table-column>
        <el-table-column label="论著名称" prop="title" align="center">
          <template v-slot="scope"><span>{{ scope.row.title }}</span></template>
        </el-table-column>
        <el-table-column label="出版社" prop="publisher" align="center">
          <template v-slot="scope"><span>{{ scope.row.publisher }}</span></template>
        </el-table-column>
        <el-table-column label="出版时间" prop="publishTime" align="center">
          <template v-slot="scope"><span>{{ scope.row.publishTime }}</span></template>
        </el-table-column>
        <el-table-column label="论著类型" prop="monographType" align="center">
          <template v-slot="scope"><span>{{ scope.row.monographType }}</span></template>
        </el-table-column>
        <el-table-column label="业绩点分配比例" prop="rank" align="center">
          <template v-slot="scope"><span>{{ scope.row.rank }}</span></template>
        </el-table-column>
        <!-- PDF列 -->
        <el-table-column label="PDF" align="center" width="100">
          <template v-slot="scope">
            <!-- 有PDF时显示查看链接，无则显示“无” -->
            <el-link 
              v-if="scope.row.pdfUrl" 
              type="primary" 
              icon="Document" 
              :href="scope.row.pdfUrl" 
              target="_blank"
            >
              查看
            </el-link>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="工作量" prop="workload" align="center">
          <template v-slot="scope"><span class="workload-value">{{ scope.row.workload.toFixed(3) }}</span></template>
        </el-table-column>
        <el-table-column label="审核状态" prop="status" min-width="80" align="center">
          <template v-slot="scope">
            <span :class="['status-tag', getStatusClass(scope.row.status)]">
              {{ scope.row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100" align="center">
          <template v-slot="scope">
            <div style="display: flex; gap: 8px; justify-content: center;">
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
                  <el-button type="primary" size="small" disabled>编辑</el-button>
                </span>
              </el-tooltip>
              <el-button 
                v-else 
                type="primary" 
                size="small" 
                @click="handleEdit('monograph', scope.row)"
              >
                编辑
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 新增：分页控件 -->
      <div class="pagination-container" style="margin-top: 10px; text-align: right;">
        <el-pagination
          @size-change="(size) => handleSizeChange('monograph', size)"  
          @current-change="(page) => handleCurrentChange('monograph', page)"
          :current-page="modulePagination.monograph.currentPage" 
          :page-sizes="[5, 10, 20, 50]"
          :page-size="modulePagination.monograph.pageSize"  
          layout="total, sizes, prev, pager, next, jumper"
          :total="modulePagination.monograph.total"> 
        </el-pagination>
      </div>
      <div class="module-summary">
        <div class="summary-header">
          <span class="summary-title">模块合计</span>
          <span class="summary-divider">：</span>
        </div>
        <div class="summary-content">
          <div class="summary-row">
            <span class="summary-label">已确认总工作量：</span>
            <span class="summary-value confirmed">{{ monographConfirmedWorkload.toFixed(3) }} 工作量</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">预计总工作量：</span>
            <span class="summary-value estimated">{{ monographTotalWorkload.toFixed(3) }} 工作量</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 四、获奖模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">四、获奖</h3>
        <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
          <el-button type="primary" icon="Plus" @click="handleAdd('award')" >新增</el-button>
          <el-button type="danger" icon="Delete" @click="handleDelete('award')">删除</el-button>
        </div>
      </div>
      
      <el-table :data="awardTableData" border class="module-table" ref="awardTableRef">
        <!-- 复选框列（第一列） -->
        <el-table-column type="selection" width="55" align="center"></el-table-column>

        <el-table-column label="获奖" align="center" width="80">
          <template v-slot="scope">
            <!-- 结合分页参数计算连续序号 -->
            {{ (modulePagination.award.currentPage - 1) * modulePagination.award.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="姓名" prop="userName" align="center">
          <template v-slot="scope"><span>{{ scope.row.userName }}</span></template>
        </el-table-column>
        <el-table-column label="获奖名称" prop="name" align="center">
          <template v-slot="scope"><span>{{ scope.row.name }}</span></template>
        </el-table-column>
        <el-table-column label="颁奖单位" prop="organizer" align="center">
          <template v-slot="scope"><span>{{ scope.row.organizer }}</span></template>
        </el-table-column>
        <el-table-column label="获奖时间" prop="awardTime" align="center">
          <template v-slot="scope"><span>{{ scope.row.awardTime }}</span></template>
        </el-table-column>
        <el-table-column label="奖励级别" prop="level" align="center">
          <template v-slot="scope"><span>{{ scope.row.level }}</span></template>
        </el-table-column>
        <el-table-column label="业绩点分配比例" prop="rank" align="center">
          <template v-slot="scope"><span>{{ scope.row.rank }}</span></template>
        </el-table-column>
        <!-- PDF列 -->
        <el-table-column label="PDF" align="center" width="100">
          <template v-slot="scope">
            <!-- 有PDF时显示查看链接，无则显示“无” -->
            <el-link 
              v-if="scope.row.pdfUrl" 
              type="primary" 
              icon="Document" 
              :href="scope.row.pdfUrl" 
              target="_blank"
            >
              查看
            </el-link>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="工作量" prop="workload" align="center">
          <template v-slot="scope"><span class="workload-value">{{ scope.row.workload.toFixed(3) }}</span></template>
        </el-table-column>
        <el-table-column label="审核状态" prop="status" min-width="80" align="center">
          <template v-slot="scope">
            <span :class="['status-tag', getStatusClass(scope.row.status)]">
              {{ scope.row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100" align="center">
          <template v-slot="scope">
            <div style="display: flex; gap: 8px; justify-content: center;">
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
                  <el-button type="primary" size="small" disabled>编辑</el-button>
                </span>
              </el-tooltip>
              <el-button 
                v-else 
                type="primary" 
                size="small" 
                @click="handleEdit('award', scope.row)"
              >
                编辑
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 新增：分页控件 -->
      <div class="pagination-container" style="margin-top: 10px; text-align: right;">
        <el-pagination
          @size-change="(size) => handleSizeChange('award', size)"  
          @current-change="(page) => handleCurrentChange('award', page)"
          :current-page="modulePagination.award.currentPage" 
          :page-sizes="[5, 10, 20, 50]"
          :page-size="modulePagination.award.pageSize"  
          layout="total, sizes, prev, pager, next, jumper"
          :total="modulePagination.award.total"> 
        </el-pagination>
      </div>
      <div class="module-summary">
        <div class="summary-header">
          <span class="summary-title">模块合计</span>
          <span class="summary-divider">：</span>
        </div>
        <div class="summary-content">
          <div class="summary-row">
            <span class="summary-label">已确认总工作量：</span>
            <span class="summary-value confirmed">{{ awardConfirmedWorkload.toFixed(3) }} 工作量</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">预计总工作量：</span>
            <span class="summary-value estimated">{{ awardTotalWorkload.toFixed(3) }} 工作量</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 五、专利模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">五、专利</h3>
        <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
          <el-button type="primary" icon="Plus" @click="handleAdd('patent')" >新增</el-button>
          <el-button type="danger" icon="Delete" @click="handleDelete('patent')">删除</el-button>
        </div>
      </div>
      
      <el-table :data="patentTableData" border class="module-table" ref="patentTableRef">
        <!-- 复选框列（第一列） -->
        <el-table-column type="selection" width="55" align="center"></el-table-column>

        <el-table-column label="专利" align="center" width="80">
          <template v-slot="scope">
            <!-- 结合分页参数计算连续序号 -->
            {{ (modulePagination.patent.currentPage - 1) * modulePagination.patent.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="姓名" prop="userName" align="center">
          <template v-slot="scope"><span>{{ scope.row.userName }}</span></template>
        </el-table-column>
        <el-table-column label="专利名称" prop="name" align="center">
          <template v-slot="scope"><span>{{ scope.row.name }}</span></template>
        </el-table-column>
        <el-table-column label="专利类型" prop="type" align="center">
          <template v-slot="scope"><span>{{ scope.row.type }}</span></template>
        </el-table-column>
        <el-table-column label="申请时间" prop="applyTime" align="center">
          <template v-slot="scope"><span>{{ scope.row.applyTime }}</span></template>
        </el-table-column>
        <el-table-column label="授权时间" prop="authorizeTime" align="center">
          <template v-slot="scope"><span>{{ scope.row.authorizeTime }}</span></template>
        </el-table-column>
        <el-table-column label="业绩点分配比例" prop="rank" align="center">
          <template v-slot="scope"><span>{{ scope.row.rank }}</span></template>
        </el-table-column>
        <!-- PDF列 -->
        <el-table-column label="PDF" align="center" width="100">
          <template v-slot="scope">
            <!-- 有PDF时显示查看链接，无则显示“无” -->
            <el-link 
              v-if="scope.row.pdfUrl" 
              type="primary" 
              icon="Document" 
              :href="scope.row.pdfUrl" 
              target="_blank"
            >
              查看
            </el-link>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="工作量" prop="workload" align="center">
          <template v-slot="scope"><span class="workload-value">{{ scope.row.workload.toFixed(3) }}</span></template>
        </el-table-column>
        <el-table-column label="审核状态" prop="status" min-width="80" align="center">
          <template v-slot="scope">
            <span :class="['status-tag', getStatusClass(scope.row.status)]">
              {{ scope.row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100" align="center">
          <template v-slot="scope">
            <div style="display: flex; gap: 8px; justify-content: center;">
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
                  <el-button type="primary" size="small" disabled>编辑</el-button>
                </span>
              </el-tooltip>
              <el-button 
                v-else 
                type="primary" 
                size="small" 
                @click="handleEdit('patent', scope.row)"
              >
                编辑
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 新增：分页控件 -->
      <div class="pagination-container" style="margin-top: 10px; text-align: right;">
        <el-pagination
          @size-change="(size) => handleSizeChange('patent', size)"  
          @current-change="(page) => handleCurrentChange('patent', page)"
          :current-page="modulePagination.patent.currentPage" 
          :page-sizes="[5, 10, 20, 50]"
          :page-size="modulePagination.patent.pageSize"  
          layout="total, sizes, prev, pager, next, jumper"
          :total="modulePagination.patent.total"> 
        </el-pagination>
      </div>
      <div class="module-summary">
        <div class="summary-header">
          <span class="summary-title">模块合计</span>
          <span class="summary-divider">：</span>
        </div>
        <div class="summary-content">
          <div class="summary-row">
            <span class="summary-label">已确认总工作量：</span>
            <span class="summary-value confirmed">{{ patentConfirmedWorkload.toFixed(3) }} 工作量</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">预计总工作量：</span>
            <span class="summary-value estimated">{{ patentTotalWorkload.toFixed(3) }} 工作量</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 六、软著模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">六、软著</h3>
      <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
          <el-button type="primary" icon="Plus" @click="handleAdd('software')" >新增</el-button>
          <el-button type="danger" icon="Delete" @click="handleDelete('software')">删除</el-button>
        </div>
      </div>
      
      <el-table :data="softwareTableData" border class="module-table" ref="softwareTableRef">
        <!-- 复选框列（第一列） -->
        <el-table-column type="selection" width="55" align="center"></el-table-column>

        <el-table-column label="软著" align="center" width="80">
          <template v-slot="scope">
            <!-- 结合分页参数计算连续序号 -->
            {{ (modulePagination.software.currentPage - 1) * modulePagination.software.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="姓名" prop="userName" align="center">
          <template v-slot="scope"><span>{{ scope.row.userName }}</span></template>
        </el-table-column>
        <el-table-column label="软著名称" prop="name" align="center">
          <template v-slot="scope"><span>{{ scope.row.name }}</span></template>
        </el-table-column>
        <el-table-column label="申请时间" prop="applyTime" align="center">
          <template v-slot="scope"><span>{{ scope.row.applyTime }}</span></template>
        </el-table-column>
        <el-table-column label="授权时间" prop="authorizeTime" align="center">
          <template v-slot="scope"><span>{{ scope.row.authorizeTime }}</span></template>
        </el-table-column>
        <el-table-column label="业绩点分配比例" prop="rank" align="center">
          <template v-slot="scope"><span>{{ scope.row.rank }}</span></template>
        </el-table-column>
        <!-- PDF列 -->
        <el-table-column label="PDF" align="center" width="100">
          <template v-slot="scope">
            <!-- 有PDF时显示查看链接，无则显示“无” -->
            <el-link 
              v-if="scope.row.pdfUrl" 
              type="primary" 
              icon="Document" 
              :href="scope.row.pdfUrl" 
              target="_blank"
            >
              查看
            </el-link>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="工作量" prop="workload" align="center">
          <template v-slot="scope"><span class="workload-value">{{ scope.row.workload.toFixed(3) }}</span></template>
        </el-table-column>
        <el-table-column label="审核状态" prop="status" min-width="80" align="center">
          <template v-slot="scope">
            <span :class="['status-tag', getStatusClass(scope.row.status)]">
              {{ scope.row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100" align="center">
          <template v-slot="scope">
            <div style="display: flex; gap: 8px; justify-content: center;">
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
                  <el-button type="primary" size="small" disabled>编辑</el-button>
                </span>
              </el-tooltip>
              <el-button 
                v-else 
                type="primary" 
                size="small" 
                @click="handleEdit('software', scope.row)"
              >
                编辑
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 新增：分页控件 -->
      <div class="pagination-container" style="margin-top: 10px; text-align: right;">
        <el-pagination
          @size-change="(size) => handleSizeChange('software', size)"  
          @current-change="(page) => handleCurrentChange('software', page)"
          :current-page="modulePagination.software.currentPage" 
          :page-sizes="[5, 10, 20, 50]"
          :page-size="modulePagination.software.pageSize"  
          layout="total, sizes, prev, pager, next, jumper"
          :total="modulePagination.software.total"> 
        </el-pagination>
      </div>
      <div class="module-summary">
        <div class="summary-header">
          <span class="summary-title">模块合计</span>
          <span class="summary-divider">：</span>
        </div>
        <div class="summary-content">
          <div class="summary-row">
            <span class="summary-label">已确认总工作量：</span>
            <span class="summary-value confirmed">{{ softwareConfirmedWorkload.toFixed(3) }} 工作量</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">预计总工作量：</span>
            <span class="summary-value estimated">{{ softwareTotalWorkload.toFixed(3) }} 工作量</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 新增：通用新增弹窗 -->
    <el-dialog 
        :title="dialogTitle" 
        v-model="dialogVisible"
        @close="resetUploadState"
        width="1000px" 
        :close-on-click-modal="false"
        :style="{ 
          top: '40%', 
          transform: 'translateY(-50%)', 
          marginTop: '0' 
  }"
    >
      <el-form 
        :model="formData" 
        :rules="formRules[currentModule]" 
        ref="addFormRef" 
        label-width="120px"
      >
        <!-- 课题模块表单 -->
        <template v-if="currentModule === 'subject'">
          <el-form-item label="项目名称" prop="projectName">
            <el-input v-model.number="formData.projectName" placeholder="请输入项目名称"></el-input>
          </el-form-item>
          <el-form-item label="课题类型" prop="subjectType">
            <el-select v-model="formData.subjectType" placeholder="请选择" @change="handleSubjectTypeChange">
              <el-option label="国家自然科学基金" value="国家自然科学基金"></el-option>
              <el-option label="省自然科学基金" value="省自然科学基金"></el-option>
              <el-option label="其它纵向项目" value="其它纵向项目"></el-option>
              <el-option label="横向项目" value="横向项目"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="当年经费(万)" prop="money">
            <el-input v-model="formData.money" type="number" placeholder="请输入本年度到账经费数额，单位为万"></el-input>
          </el-form-item>        
          <el-form-item label="执行时间" prop="executeTime">
            <el-date-picker 
              v-model="formData.executeTime" 
              type="daterange" 
              range-separator="至" 
              start-placeholder="开始月份" 
              end-placeholder="结束月份"
              format="YYYY-MM-DD" 
              value-format="YYYY-MM-DD"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="业绩点分配比例" prop="rank">
            <el-input v-model="formData.rank" type="number" placeholder="请输入分配比例" step="0.01"></el-input>
          </el-form-item>          
          <el-form-item label="系数" prop="coefficient">
            <el-input 
              v-model="formData.coefficient" 
              disabled  
              placeholder="" 
            ></el-input>
          </el-form-item>
          <!-- PDF上传 -->
          <el-form-item label="证明材料" prop="pdfUrl" required>
            <el-upload
              class="upload-pdf"
              ref="pdfUpload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="fileList"
              :before-upload="beforePdfUpload"
              :on-success="handlePdfUpload"     
              :on-error="handleUploadError"
              :on-remove="handleRemove"
              :limit="1"
              accept=".pdf"
              :auto-upload="false" 
              :on-change="handleFileChange" 
            >
              <el-button size="small" type="primary" icon="Upload">选择PDF文件</el-button>
              <template #tip>
                <div class="el-upload__tip text-danger">
                  仅支持单个PDF文件，文件大小不超过100MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
          
        </template>
 
        <!-- 论文模块表单 -->
        <template v-if="currentModule === 'paper'">
          <el-form-item label="论文名称" prop="title">
            <el-input v-model="formData.title" placeholder="请输入论文名称"></el-input>
          </el-form-item>
          <el-form-item label="出版刊物" prop="journal">
            <el-input v-model="formData.journal" placeholder="请输入刊物名称"></el-input>
          </el-form-item>
          <el-form-item label="出版时间" prop="publishTime">
            <el-date-picker v-model="formData.publishTime" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="论文级别" prop="level">
            <el-select v-model="formData.level" placeholder="请选择" @change="handlePaperTypeChange">
              <el-option label="T1" value="T1"></el-option>
              <el-option label="T2" value="T2"></el-option>
              <el-option label="A类" value="A类"></el-option>
              <el-option label="B类" value="B类"></el-option>
              <el-option label="C类" value="C类"></el-option>
              <el-option label="其他论文，撰写提交国家级纵向项目申报书" value="其他论文，撰写提交国家级纵向项目申报书"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="系数" prop="coefficient">
            <el-input 
              v-model="formData.coefficient" 
              disabled 
              placeholder="" 
            ></el-input>
          </el-form-item>
          <el-form-item label="业绩点分配比例" prop="rank">
            <el-input v-model="formData.rank" type="number" placeholder="请输入分配比例" step="0.01"></el-input>
          </el-form-item>
          <!-- PDF上传 -->
          <el-form-item label="证明材料" prop="pdfUrl" required>
            <el-upload
              class="upload-pdf"
              ref="pdfUpload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="fileList"
              :before-upload="beforePdfUpload"
              :on-success="handlePdfUpload"     
              :on-error="handleUploadError"
              :on-remove="handleRemove"
              :limit="1"
              accept=".pdf"
              :auto-upload="false" 
              :on-change="handleFileChange" 
            >
              <el-button size="small" type="primary" icon="Upload">选择PDF文件</el-button>
              <template #tip>
                <div class="el-upload__tip text-danger">
                  仅支持单个PDF文件，文件大小不超过100MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </template>

        <!-- 论著模块表单 -->
        <template v-if="currentModule === 'monograph'">
          <el-form-item label="论著名称" prop="title">
            <el-input v-model="formData.title" placeholder="请输入论著名称"></el-input>
          </el-form-item>
          <el-form-item label="出版社" prop="publisher">
            <el-input v-model="formData.publisher" placeholder="请输入出版社"></el-input>
          </el-form-item>
          <el-form-item label="出版时间" prop="publishTime">
            <el-date-picker v-model="formData.publishTime" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="论著类型" prop="monographType">
            <el-select 
              v-model="formData.monographType" 
              placeholder="请选择论著类型" 
              @change="handleMonographTypeChange"
            >
              <el-option label="科技专著" value="科技专著"></el-option>
              <el-option label="其他著作" value="其他著作"></el-option>
            </el-select>
          </el-form-item>
           <el-form-item label="系数" prop="coefficient">
            <el-input 
              v-model="formData.coefficient" 
              disabled 
              placeholder="" 
            ></el-input>
          </el-form-item>
          <el-form-item label="业绩点分配比例" prop="rank">
            <el-input v-model="formData.rank" type="number" placeholder="请输入分配比例" step="0.01"></el-input>
          </el-form-item>
          <!-- PDF上传 -->
          <el-form-item label="证明材料" prop="pdfUrl" required>
            <el-upload
              class="upload-pdf"
              ref="pdfUpload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="fileList"
              :before-upload="beforePdfUpload"
              :on-success="handlePdfUpload"     
              :on-error="handleUploadError"
              :on-remove="handleRemove"
              :limit="1"
              accept=".pdf"
              :auto-upload="false" 
              :on-change="handleFileChange" 
            >
              <el-button size="small" type="primary" icon="Upload">选择PDF文件</el-button>
              <template #tip>
                <div class="el-upload__tip text-danger">
                  仅支持单个PDF文件，文件大小不超过100MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </template>

        <!-- 获奖模块表单 -->
        <template v-if="currentModule === 'award'">
          <el-form-item label="获奖名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入获奖名称"></el-input>
          </el-form-item>
          <el-form-item label="颁奖单位" prop="organizer">
            <el-input v-model="formData.organizer" placeholder="请输入颁奖单位"></el-input>
          </el-form-item>
          <el-form-item label="获奖时间" prop="awardTime">
            <el-date-picker v-model="formData.awardTime" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="奖励级别" prop="level">
            <el-select v-model="formData.level" placeholder="请选择" @change="handleAwardTypeChange">
              <el-option label="1级" value="1级"></el-option>
              <el-option label="2级" value="2级"></el-option>
              <el-option label="3级" value="3级"></el-option>
              <el-option label="4级" value="4级"></el-option>
              <el-option label="5级" value="5级"></el-option>
              <el-option label="6级" value="6级"></el-option>
              <el-option label="7级" value="7级"></el-option>
              <el-option label="8级" value="8级"></el-option>
            </el-select>
          </el-form-item>
          <!-- 系数输入框（禁用，不可修改） -->
          <el-form-item label="系数" prop="coefficient">
            <el-input 
              v-model="formData.coefficient" 
              disabled 
              placeholder="根据获奖类型自动生成" 
            ></el-input>
          </el-form-item>

          <el-form-item label="业绩点分配比例" prop="rank">
            <el-input v-model="formData.rank" type="number" placeholder="请输入分配比例" step="0.01"></el-input>
          </el-form-item>
          <!-- PDF上传 -->
          <el-form-item label="证明材料" prop="pdfUrl" required>
            <el-upload
              class="upload-pdf"
              ref="pdfUpload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="fileList"
              :before-upload="beforePdfUpload"
              :on-success="handlePdfUpload"     
              :on-error="handleUploadError"
              :on-remove="handleRemove"
              :limit="1"
              accept=".pdf"
              :auto-upload="false" 
              :on-change="handleFileChange" 
            >
              <el-button size="small" type="primary" icon="Upload">选择PDF文件</el-button>
              <template #tip>
                <div class="el-upload__tip text-danger">
                  仅支持单个PDF文件，文件大小不超过100MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </template>

        <!-- 专利模块表单 -->
        <template v-if="currentModule === 'patent'">
          <el-form-item label="专利名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入专利名称"></el-input>
          </el-form-item>
          <el-form-item label="专利类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择" @change="handlePatentTypeChange">
              <el-option label="其它国家或地区发明专利（美国、欧盟、日本）、国际标准" value="其它国家或地区发明专利（美国、欧盟、日本）、国际标准"></el-option>
              <el-option label="国内转让发明专利、品种、品种权" value="国内转让发明专利、品种、品种权"></el-option>
              <el-option label="国内授权发明专利，非主要农作物省级品种认定、国际品种登录、地方标准" value="国内授权发明专利，非主要农作物省级品种认定、国际品种登录、地方标准"></el-option>
              <el-option label="国内实用新型专利" value="国内实用新型专利"></el-option>
            </el-select>
          </el-form-item>
          <!-- 系数输入框（禁用，不可修改） -->
          <el-form-item label="系数" prop="coefficient">
            <el-input 
              v-model="formData.coefficient" 
              disabled 
              placeholder="根据专利类型自动生成" 
            ></el-input>
          </el-form-item>
          <el-form-item label="申请时间" prop="applyTime">
            <el-date-picker v-model="formData.applyTime" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="授权时间" prop="authorizeTime">
            <el-date-picker v-model="formData.authorizeTime" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="业绩点分配比例" prop="rank">
            <el-input v-model="formData.rank" type="number" placeholder="请输入分配比例" step="0.01"></el-input>
          </el-form-item>
          <!-- PDF上传 -->
          <el-form-item label="证明材料" prop="pdfUrl" required>
            <el-upload
              class="upload-pdf"
              ref="pdfUpload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="fileList"
              :before-upload="beforePdfUpload"
              :on-success="handlePdfUpload"     
              :on-error="handleUploadError"
              :on-remove="handleRemove"
              :limit="1"
              accept=".pdf"
              :auto-upload="false" 
              :on-change="handleFileChange" 
            >
              <el-button size="small" type="primary" icon="Upload">选择PDF文件</el-button>
              <template #tip>
                <div class="el-upload__tip text-danger">
                  仅支持单个PDF文件，文件大小不超过100MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </template>

        <!-- 软著模块表单 -->
        <template v-if="currentModule === 'software'">
          <el-form-item label="软著名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入软著名称"></el-input>
          </el-form-item>
          <el-form-item label="申请时间" prop="applyTime">
            <el-date-picker v-model="formData.applyTime" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="授权时间" prop="authorizeTime">
            <el-date-picker v-model="formData.authorizeTime" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD"></el-date-picker>
          </el-form-item>
          <el-form-item label="业绩点分配比例" prop="rank">
            <el-input v-model="formData.rank" type="number" placeholder="请输入分配比例" step="0.01"></el-input>
          </el-form-item>
          <!-- PDF上传 -->
          <el-form-item label="证明材料" prop="pdfUrl" required>
            <el-upload
              class="upload-pdf"
              ref="pdfUpload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="fileList"
              :before-upload="beforePdfUpload"
              :on-success="handlePdfUpload"     
              :on-error="handleUploadError"
              :on-remove="handleRemove"
              :limit="1"
              accept=".pdf"
              :auto-upload="false" 
              :on-change="handleFileChange" 
            >
              <el-button size="small" type="primary" icon="Upload">选择PDF文件</el-button>
              <template #tip>
                <div class="el-upload__tip text-danger">
                  仅支持单个PDF文件，文件大小不超过100MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </template>
 
      </el-form>
 
      <template v-slot:footer>
        <!-- 1. 课题模块提示 -->
        <div v-if="currentModule === 'subject'" class="module-tip" style="text-align: left;">
          <p style="font-weight: 500; color: #303133; margin-bottom: 8px;">课题类型工作量计算说明：</p>
          <p>• <span style="color: #1890ff;">国家自然科学基金，按当年到位经费1万元计算2.5业绩点。</span> - 系数 2.5</p>
          <p>• <span style="color: #40a9ff;">省自然科学基金、其它国家级课题，按当年到位经费1万元计算2业绩点。</span> - 系数 2.0</p>
          <p>• <span style="color: #69c0ff;">其它纵向项目，A级课题，按当年到位经费1万元计算1.5业绩点。</span> - 系数 1.5</p>
          <p>• <span style="color: #8cc8ff;">横向项目和科技成果转化收入，按当年到位经费1万元计算1业绩点。</span> - 系数 1.0</p>
        </div>

        <!-- 2. 论文模块提示 -->
        <div v-if="currentModule === 'paper'" class="module-tip" style="text-align: left;">
          <p>• <span style="color: #f5222d;">T1</span> - 150.0</p>
          <p>• <span style="color: #fa541c;">T2</span> - 30.0</p>
          <p>• <span style="color: #fa8c16;">A类</span> - 20.0</p>
          <p>• <span style="color: #faad14;">B类</span> - 10.0</p>
          <p>• <span style="color: #52c41a;">C类</span> - 5.0</p>
          <p>• <span style="color: #1890ff;">其他论文/国家级纵向项目申报书</span> - 3.0</p>
        </div>

        <!-- 3. 论著模块提示 -->
        <div v-if="currentModule === 'monograph'" class="module-tip" style="text-align: left;">
          <p style="font-weight: 500; color: #303133; margin-bottom: 8px;">论著工作量计算说明：</p>
          <p>• <span style="color: #faad14;">科技专著</span> - 10.0</p>
          <p>• <span style="color: #52c41a;">其他著作</span> - 5.0</p>
        </div>

        <!-- 4. 获奖模块提示（原分类说明） -->
        <div v-if="currentModule === 'award'" class="module-tip" style="text-align: left;">
          <p style="font-weight: 500; color: #303133; margin-bottom: 8px;">获奖工作量计算说明：</p>
          <p>1. <span style="color: #f5222d;">国家科技成果奖特等奖</span> - 5000业绩点</p>
          <p>2. <span style="color: #fa541c;">国家自然科学（1.5倍）、技术发明/科技进步一等奖</span> - 3000业绩点</p>
          <p>3. <span style="color: #fa8c16;">国家自然科学（1.5倍）、技术发明/科技进步二等奖，中国专利奖金奖</span> - 2500业绩点</p>
          <p>4. <span style="color: #faad14;">省部级科学技术奖一等奖，省自然科学奖一等奖（按1.5倍）、中国专利奖优秀奖，省部级教师奖一等奖</span> - 500业绩点</p>
          <p>5. <span style="color: #52c41a;">省部级科学技术奖二等奖，省级专利奖金奖，具有国家奖推荐资格的社会力量设奖一等奖、广州市市长奖、省自然科学奖二等奖，省部级教师奖二等奖</span> - 200业绩点</p>
          <p>6. <span style="color: #1890ff;">省部级科学技术奖三等奖，省专利优秀奖，广东省优秀发明人，具有国家奖推荐资格的社会力量设奖二等奖、具有省级奖推荐资格的社会力量设奖一等奖、省级农业技术推广奖一等奖、国家一级学会（协会）组织的科技类获奖一等奖、省自然科学奖三等奖，省部级教师奖三等奖</span> - 100业绩点</p>
          <p>7. <span style="color: #40a9ff;">市厅级1-2等奖，专利金奖、优秀奖，具有国家奖推荐资格的社会力量设奖三等奖，具有省级奖推荐资格的社会力量设奖二等奖、国家一级学会（协会）组织的科技类获奖二等奖、省级农业技术推广奖二等奖</span> - 50业绩点</p>
          <p>8. <span style="color: #69c0ff;">市厅级三等奖，具有省级奖推荐资格的社会力量设奖三等奖、国家一级学会（协会）组织的科技类获奖三等奖、省级农业技术推广奖三等奖</span> - 15业绩点</p>
        </div>

        <!-- 5. 专利模块提示 -->
        <div v-if="currentModule === 'patent'" class="module-tip" style="text-align: left;">
          <p style="font-weight: 500; color: #303133; margin-bottom: 8px;">专利工作量计算说明：</p>
          <p>• <span style="color: #f5222d;">国际发明专利（美国/欧盟/日本）、国际标准</span> - 40.0</p>
          <p>• <span style="color: #fa541c;">国内转让发明专利、品种、品种权</span> - 20.0</p>
          <p>• <span style="color: #faad14;">国内授权发明专利、省级品种认定等</span> - 10.0</p>
          <p>• <span style="color: #52c41a;">国内实用新型专利</span> - 3.0</p>
        </div>

        <!-- 6. 软著模块提示 -->
        <div v-if="currentModule === 'software'" class="module-tip" style="text-align: left;">
          <p style="font-weight: 500; color: #303133; margin-bottom: 8px;">软著工作量计算说明：</p>
          <p>• <span style="color: #722ed1;">软件著作权</span> - 系数 2.0（不分等级）</p>
        </div>

        <!-- 通用操作按钮（所有模块共用） -->
        <div style="margin-top: 15px; text-align: right;">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">提交</el-button>
        </div>
      </template>
    </el-dialog>

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

  </div>
</template>

<script>
import { getSubjectList, addSubject, deleteSubject, updateSubject  } from '@/api/researchPerformance/subject.js';
import { getPaperList, addPaper, deletePaper, updatePaper  } from '@/api/researchPerformance/paper.js';
import { getMonographList, addMonograph, deleteMonograph, updateMonograph  } from '@/api/researchPerformance/monograph.js';
import { getAwardList, addAward, deleteAward, updateAward  } from '@/api/researchPerformance/award.js';
import { getPatentList, addPatent, deletePatent, updatePatent  } from '@/api/researchPerformance/patent.js';
import { getSoftwareList, addSoftware, deleteSoftware, updateSoftware  } from '@/api/researchPerformance/software.js';
// import { beforePdfUpload, handlePdfUpload } from '@/api/system/upload.js';
import { getCookie } from '@/utils/cookie.js';
import { getResearchTotalWorkload, saveYearWorkload } from '@/api/researchPerformance/totalWorkload.js'; // 根据实际路径调整
import { parse } from '@vue/compiler-sfc';
import { getUserListByDept } from '@/api/check/researchCheck.js';


export default {

  data() {
    return {
      // 新增：PDF上传接口URL（若依后端标准路径）
      uploadUrl: import.meta.env.VITE_APP_BASE_API+ '/system/user/profile/uploadPdf',
      // 保留原有认证头（已正确携带Token）
      uploadHeaders: {
        Authorization: 'Bearer ' + getCookie('Admin-Token')
      },
      fileValidated: false, // 文件校验状态（true：通过，false：未通过/未选择）

      summaryLoading: false, // 汇总数据加载状态
      // 课题模块
      subjectTableData: [],
      subjectTotalWorkload: 0, // 预计总工作量（所有数据）
      subjectConfirmedWorkload: 0, // 已确认总工作量（仅已审核通过的数据）
      // 论文模块
      paperTableData: [],
      paperTotalWorkload: 0,
      paperConfirmedWorkload: 0, 
      // 论著模块
      monographTableData: [],
      monographTotalWorkload: 0,
      monographConfirmedWorkload: 0,
      // 获奖模块
      awardTableData: [],
      awardTotalWorkload: 0,
      awardConfirmedWorkload: 0,
      // 专利模块
      patentTableData: [],
      patentTotalWorkload: 0,
      patentConfirmedWorkload: 0,
      // 软著模块
      softwareTableData: [],
      softwareTotalWorkload: 0,
      softwareConfirmedWorkload: 0,

      isEdit: false, // 标识是否为编辑模式
      editRowId: null, // 编辑行的ID

      // 加载状态
      loading: {
        // yearSummary: false, // 年度汇总加载状态
        subject: false,
        paper: false,
        monograph: false,
        award: false,
        patent: false,
        software: false
      },   
      
      // 模块分页参数集合：key=模块标识，value=分页参数
      modulePagination: {
        subject: { currentPage: 1, pageSize: 10, total: 0 }, // 课题分页参数
        paper: { currentPage: 1, pageSize: 10, total: 0 },   // 论文分页参数
        monograph: { currentPage: 1, pageSize: 10, total: 0 }, // 论著分页参数
        award: { currentPage: 1, pageSize: 10, total: 0 },    // 获奖分页参数
        patent: { currentPage: 1, pageSize: 10, total: 0 },   // 专利分页参数
        software: { currentPage: 1, pageSize: 10, total: 0 }  // 软著分页参数
      },

      // 年度工作量汇总静态数据
      // selectedYear: new Date().getFullYear(), // 默认选中当前年份
      years: [],
      yearWorkloadTableData: [], // 改为空数组，从接口动态获取



      // 新增：弹窗控制
      dialogVisible: false,
      currentModule: '', // 当前模块标识：subject/paper/monograph等
      dialogTitle: '',
      formData: {
        subjectType: '',  // 课题类型（绑定下拉框）
        coefficient: null,  // 系数（自动计算，禁用修改）
        paperType: '', 
        paperCoefficient: null, 
        monographType: '',
        monographCoefficient: null,
        awardType: '',
        awardCoefficient: null,
        patentType: '',
        patentCoefficient: null,
        softwareType: '',
        softwareCoefficient: null,
        money: '0.00',
        pdfUrl: ''  // 存储上传后的PDF文件URL
      }, // 新增表单数据
      fileList: [],
      formRules: {
        // 课题模块验证规则
        subject: {
          projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
          subjectType: [{ required: true, message: '请选择课题类型', trigger: 'change' }],
          money: [
            { required: true, message: '请输入金额', trigger: 'blur' },
            // { type: 'number', message: '金额必须为数字值', trigger: 'blur' }
          ],
          executeTime: [
            { required: true, message: '请选择执行时间', trigger: 'change' },
            { 
              validator: (rule, value, callback) => {
                if (value && Array.isArray(value) && value.length === 2) {
                  callback();
                } else {
                  callback(new Error('请选择完整的执行时间范围'));
                }
              }, 
              trigger: 'change' 
            }
          ],
          rank: [{ required: true, message: '请输入分配比例', trigger: 'blur' }],
          coefficient: [{ required: true, message: '请选择系数', trigger: 'change' }],
          pdfUrl: [{ required: true, message: '请上传PDF文件', trigger: 'change' }],

        },
        // 论文模块验证规则
        paper: {
          title: [{ required: true, message: '请输入论文名称', trigger: 'blur' }],
          journal: [{ required: true, message: '请输入刊物名称', trigger: 'blur' }],
          publishTime: [{ required: true, message: '请选择出版时间', trigger: 'change' }],
          level: [{ required: true, message: '请选择论文级别', trigger: 'change' }],
          rank: [{ required: true, message: '请输入业绩点分配比例', trigger: 'blur' }],
          pdfUrl: [{ required: true, message: '请上传PDF文件', trigger: 'change' }],
        },
        // 论著模块验证规则
        monograph: {
          title: [{ required: true, message: '请输入论著名称', trigger: 'blur' }],
          publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
          publishTime: [{ required: true, message: '请选择出版时间', trigger: 'change' }],
          rank: [{ required: true, message: '请输入业绩点分配比例', trigger: 'blur' }],
          pdfUrl: [{ required: true, message: '请上传PDF文件', trigger: 'change' }],
        },
        // 获奖模块验证规则
        award: {
          name: [{ required: true, message: '请输入获奖名称', trigger: 'blur' }],
          organizer: [{ required: true, message: '请输入颁奖单位', trigger: 'blur' }],
          awardTime: [{ required: true, message: '请选择获奖时间', trigger: 'change' }],
          level: [{ required: true, message: '请选择奖励级别', trigger: 'change' }],
          rank: [{ required: true, message: '请输入业绩点分配比例', trigger: 'blur' }],
          pdfUrl: [{ required: true, message: '请上传PDF文件', trigger: 'change' }],
        },
        // 专利模块验证规则
        patent: {
          name: [{ required: true, message: '请输入专利名称', trigger: 'blur' }],
          type: [{ required: true, message: '请选择专利类型', trigger: 'change' }],
          applyTime: [{ required: true, message: '请选择申请时间', trigger: 'change' }],
          authorizeTime: [{ required: true, message: '请选择授权时间', trigger: 'change' }],
          rank: [{ required: true, message: '请输入业绩点分配比例', trigger: 'blur' }],
          pdfUrl: [{ required: true, message: '请上传PDF文件', trigger: 'change' }],
        },
        // 软著模块验证规则
        software: {
          name: [{ required: true, message: '请输入软著名称', trigger: 'blur' }],
          applyTime: [{ required: true, message: '请选择申请时间', trigger: 'change' }],
          authorizeTime: [{ required: true, message: '请选择授权时间', trigger: 'change' }],
          rank: [{ required: true, message: '请输入业绩点分配比例', trigger: 'blur' }],
          pdfUrl: [{ required: true, message: '请上传PDF文件', trigger: 'change' }],
        }
      },

      // 意见弹窗相关
      remarkDialogVisible: false,
      currentRemark: '', // 当前显示的备注内容

      // 筛选表单数据
      filterForm: {
        year: (new Date().getFullYear() - 1).toString(),
        deptId: '',
        userId: ''
      },
      // 部门列表
      deptList: [
        { deptId: 0, deptName: '全部部门' },
        { deptId: 1, deptName: '应用数学' },
        { deptId: 2, deptName: '信息与计算科学' },
        { deptId: 3, deptName: '统计' },
        { deptId: 4, deptName: '大学教学部' }
      ],
      deptLoading: false,
      // 用户列表
      userList: [],
      userLoading: false,
      // 搜索加载状态
      searchLoading: false,

    };
  },

  // 在 created() 钩子中添加年份生成逻辑
  created() {
    // 生成年份范围：最近5年到未来3年
    this.generateYearOptions(5, 3);
    
    // 如果需要在页面加载时就获取数据，可以在这里调用
    // this.refreshAllModules();
  },

  async mounted() {
    // ✅ 初始化：加载所有模块数据 + 计算汇总（一步完成）
    // this.refreshAllModules(); 
    await this.initFilterData();
    // 加载所有模块数据
    // this.getSubjectData();
    // this.getPaperData();
    // this.getMonographData();
    // this.getAwardData();
    // this.getPatentData();
    // this.getSoftwareData();
    // this.getYearWorkloadData(); // 加载年度汇总数据

  },

  methods: {
    /**
     * 上传前校验（覆盖原方法）
     * 1. 限制PDF格式 2. 限制100MB大小
     */
    beforePdfUpload(file) {
      // 1. 严格PDF格式校验（MIME类型+文件后缀）
      const isPDF = file.type === 'application/pdf' || file.name.endsWith('.pdf');
      // 2. 大小限制调整为100MB（与后端统一）
      const isLt10M = file.size / 1024 / 1024 < 100;
  
      if (!isPDF) this.$message.error('仅支持PDF格式文件！');
      if (!isLt10M) this.$message.error('文件大小不能超过100MB！');
      
      return isPDF && isLt10M; // 校验通过才允许上传
    },

    handleFileChange(file, fileList) {
      // 1. 格式校验（PDF）
      const isPDF = file.raw.type === 'application/pdf' || file.name.endsWith('.pdf');
      // 2. 大小校验（100MB）
      const isLt10M = file.size / 1024 / 1024 < 100;

      if (!isPDF || !isLt10M) {
        this.$message.error(!isPDF ? '仅支持PDF格式文件！' : '文件大小不能超过100MB！');
        this.fileList = []; // 清空错误文件
        this.fileValidated = false; // 标记校验失败
      } else {
        this.fileList = fileList.slice(-1); // 保留最新文件
        this.fileValidated = true; // 标记校验成功
        this.formData.pdfUrl = '/'; // 设置一个临时字段，用于表单验证
        // // ✅ 校验成功提示（带视觉反馈）
        // this.$message.success({
        //   message: '文件格式和大小校验通过',
        //   icon: 'el-icon-check' // 显示打勾图标
        // });
      }
    },
  
    /**
     * 文件上传失败回调
     */
    handleUploadError(err) {
      this.$message.error('文件上传失败，请检查网络或联系管理员');
      console.error('上传错误详情:', err);
    },
  
    /**
     * 移除已上传文件时回调
     */
    handleRemove() {
      this.formData.pdfUrl = ''; // 清除表单绑定的PDF URL
      this.$refs.addFormRef.validateField('pdfUrl'); // 触发表单校验（确保必填提示正确）
    },

    // 课题模块数据获取方法
    getSubjectData() {
      const params = {
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || undefined,
        isAudit: false, //非审核页面将不用过滤已审核通过的数据
      };
      // ✅ 动态生成额外参数：选中具体年份时传递year，否则不传递
      // const extraParams = this.selectedYear ? { year: this.selectedYear } : {};
      return this.getModuleData(
        'subject',          // 模块标识
        getSubjectList,     // 课题列表接口
        'subjectTableData', // 表格数据字段
        'subjectTotalWorkload', // 预计总工作量字段
        'subjectConfirmedWorkload', // 已确认总工作量字段
        '课题数据加载失败',
        params
        // extraParams 
      );
    },
    // 论文模块数据获取方法
    getPaperData() { 
      const params = {
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || undefined,
        isAudit: false,
      };
      return this.getModuleData(
        'paper',          // 模块标识
        getPaperList,     // 论文列表接口
        'paperTableData', // 表格数据字段
        'paperTotalWorkload', // 总工作量字段
        'paperConfirmedWorkload',
        '论文数据加载失败',
        params
      );
    },
    // 论著模块数据获取方法
    getMonographData() { 
      const params = {
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || undefined,
        isAudit: false,
      };
      return this.getModuleData(
        'monograph',          // 模块标识
        getMonographList,     // 专著列表接口
        'monographTableData', // 表格数据字段
        'monographTotalWorkload', // 总工作量字段
        'monographConfirmedWorkload',
        '论著数据加载失败',
        params
      );
    },
    // 获奖模块数据获取方法
    getAwardData() { 
      const params = {
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || undefined,
        isAudit: false,
      };
      return this.getModuleData(
        'award',          // 模块标识
        getAwardList,     // 获奖列表接口
        'awardTableData', // 表格数据字段
        'awardTotalWorkload', // 总工作量字段
        'awardConfirmedWorkload',
        '获奖数据加载失败',
        params
      );
    },
    // 专利模块数据获取方法
    getPatentData() { 
      const params = {
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || undefined,
        isAudit: false,
      };
      return this.getModuleData(
        'patent',          // 模块标识
        getPatentList,     // 专利列表接口
        'patentTableData', // 表格数据字段
        'patentTotalWorkload', // 总工作量字段
        'patentConfirmedWorkload',
        '专利数据加载失败',
        params
      );
    },
    // 软著模块数据获取方法
    getSoftwareData() { 
      const params = {
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || undefined,
        isAudit: false,
      };
      return this.getModuleData(
        'software',          // 模块标识
        getSoftwareList,     // 软著列表接口
        'softwareTableData', // 表格数据字段
        'softwareTotalWorkload', // 总工作量字段
        'softwareConfirmedWorkload',
        '软著数据加载失败',
        params
      );
    },
    
    /**
     * 通用模块数据获取方法
     * @param {String} module - 模块标识（如 'paper'/'monograph'）
     * @param {Function} apiFunc - 模块对应的列表接口（如 getPaperList/getMonographList）
     * @param {String} tableDataKey - 表格数据存储字段（如 'paperTableData'）
     * @param {String} totalWorkloadKey - 总工作量存储字段（如 'paperTotalWorkload'）
     * @param {String} errorMsg - 加载失败提示文本
     * @param {Object} extraParams - 额外参数（如年份）
     */
    getModuleData(module, apiFunc, tableKey, totalKey, confirmedKey, errorMsg, extraParams = {}) {
      this.loading[module] = true;
      const { currentPage, pageSize } = this.modulePagination[module];
      // 合并分页参数与额外参数（如year）
      const params = { pageNum: currentPage, pageSize, ...extraParams };
  
      return apiFunc(params) // 接口调用时传递年份
        .then(response => {
          this[tableKey] = response.data.records;
          this.modulePagination[module].total = response.data.total;
        })
        .catch(() => this.$message.error(errorMsg))
        .finally(() => this.loading[module] = false);
    },

    async fetchTotalWorkload(year, deptId, userId) {
      this.summaryLoading = true;
      try {
        // 调用科研汇总接口（参数与教学页面一致）
        const response = await getResearchTotalWorkload({ year, deptId, userId, mode: 'search' });
        // 赋值年度汇总表格数据
        this.yearWorkloadTableData = [response.data];
        // 提取各模块工作量（需与后端返回字段匹配）
        console.log(response.data);
        this.subjectConfirmedWorkload = response.data.subjectConfirmedWorkload || 0;
        this.subjectTotalWorkload = response.data.subjectEstimatedWorkload || 0;
        this.paperConfirmedWorkload = response.data.paperConfirmedWorkload || 0;
        this.paperTotalWorkload = response.data.paperEstimatedWorkload || 0;
        this.monographConfirmedWorkload = response.data.monographConfirmedWorkload || 0;
        this.monographTotalWorkload = response.data.monographEstimatedWorkload || 0;
        this.awardConfirmedWorkload = response.data.awardConfirmedWorkload || 0;
        this.awardTotalWorkload = response.data.awardEstimatedWorkload || 0;
        this.patentConfirmedWorkload = response.data.patentConfirmedWorkload || 0;
        this.patentTotalWorkload = response.data.patentEstimatedWorkload || 0;
        this.softwareConfirmedWorkload = response.data.softwareConfirmedWorkload || 0;
        this.softwareTotalWorkload = response.data.softwareEstimatedWorkload || 0;
      } catch (error) {
        this.$message.error(`汇总数据加载失败：${error.message || '请联系管理员'}`);
        this.yearWorkloadTableData = [];
        // 重置模块工作量
        Object.keys(this).forEach(key => {
          if (key.includes('ConfirmedWorkload') || key.includes('TotalWorkload')) {
            this[key] = 0;
          }
        });
      } finally {
        this.summaryLoading = false;
      }
    },

    // ======== 新增数据 ========
    // 新增：打开新增弹窗
    handleAdd(module) {
      this.currentModule = module;
      this.isEdit = false;
      this.editRowId = null;
      this.dialogTitle = `新增${this.getModuleName(module)}`;
      this.formData = this.initFormData(module);
      this.resetUploadState();
      this.dialogVisible = true;
    },

    // 课题类型变化时触发：计算系数
    handleSubjectTypeChange(subjectType) {
      // 系数映射表：key为课题类型下拉框的value，value为对应系数
      const coefficientMap = {
        '国家自然科学基金': 2.5,        // 国家自科
        '省自然科学基金': 2,          // 省自科
        '其它纵向项目': 1.5,  // 其他纵向
        '横向项目': 1     // 横向
      };
      // 根据选择的课题类型设置系数
      this.formData.coefficient = coefficientMap[subjectType] || null;
    },
    handlePaperTypeChange() {
      // 论文类型-系数映射表
      const coefficientMap = {
        'T1': 150.0,
        'T2': 30,
        'A类': 20,
        'B类': 10,
        'C类': 5,
        '其他论文，撰写提交国家级纵向项目申报书': 3,
      };
      // 根据选中的类型自动匹配系数
      console.log('handlePaperTypeChange', this.formData.level);
      this.formData.coefficient = coefficientMap[this.formData.level] || null;
    },
    handleMonographTypeChange() {
      // 论著类型-系数映射表
      const coefficientMap = {
        '科技专著': 10.0,
        '其他著作': 5.0,
      };
      this.formData.coefficient = coefficientMap[this.formData.monographType] || null;
    },
    handleAwardTypeChange() {
      // 获奖类型-系数映射表
      const coefficientMap = {
        '1级': 5000.0,
        '2级': 3000.0,
        '3级': 2500.0,
        '4级': 500.0,
        '5级': 200.0,
        '6级': 100.0,
        '7级': 50.0,
        '8级': 15.0,

      };
      this.formData.coefficient = coefficientMap[this.formData.level] || null;
    },
    handlePatentTypeChange() {
      // 专利类型-系数映射表
      const coefficientMap = {
        '其它国家或地区发明专利（美国、欧盟、日本）、国际标准': 40.0,
        '国内转让发明专利、品种、品种权': 20.0,
        '国内授权发明专利，非主要农作物省级品种认定、国际品种登录、地方标准': 10.0,
        '国内实用新型专利': 3.0,
      };
      this.formData.coefficient = coefficientMap[this.formData.type] || null;
    },


    // 新增：初始化表单数据
    initFormData(module) {
      switch (module) {
        case 'subject':
          // return { projectName: '', subjectType: '', execute_time_start: null, execute_time_end: null, rank: null, coefficient: '' };
          return { projectName: '', subjectType: '', executeTime: [], rank: '1.0', coefficient: ''};
        case 'paper':
          return { title: '', journal: '', publishTime: '', level: '', rank: '1.0' };
        case 'monograph':
          return { title: '', publisher: '', publishTime: '', rank: '1.0' };
        case 'award':
          return { name: '', organizer: '', awardTime: '', level: '', rank: '1.0' };
        case 'patent':
          return { name: '', type: '', applyTime: '', authorizeTime: '', rank: '1.0' };
        case 'software':
          return { name: '', applyTime: '', authorizeTime: '', rank: '1.0' };
        default:
          return {};
      }
    },
 
    // 新增：获取模块名称
    getModuleName(module) {
      const map = { subject: '课题', paper: '论文', monograph: '论著', award: '获奖', patent: '专利', software: '软著' };
      return map[module] || '';
    },
 
    // 新增：提交表单到数据库
    async submitForm() {
      // 步骤1：检查文件是否已选择且校验通过
      if (this.fileList.length === 0 || !this.fileValidated) {
        this.$message.error('请选择并上传通过校验的PDF文件');
        return;
      }
    
      // 步骤2：触发表单其他字段校验（不含PDF，已通过前端校验）
      this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) return; // 其他字段校验失败
    
        try {
          // 步骤3：手动触发PDF上传（此时才真正上传文件）
          this.$refs.pdfUpload.submit();
        } catch (error) {
          this.$message.error('文件上传失败，请重试');
        }
      });
    },
  
    handlePdfUpload(response, file) {
      if (response.code === 200) {
        // this.$message.success(`证明材料上传成功: ${file.name}`);
        this.$message.success(`证明材料上传成功`);
        const pdfUrl = response.pdfUrl; // 保存当前的pdfUrl值

        // 创建提交数据的副本
        console.log('原始表单',this.formData);
        let submitData = { ...this.formData };
        submitData.pdfUrl = pdfUrl; // 绑定后端返回的PDF URL
        console.log('上传表单',submitData);

        // 转换rank为数字类型（关键修改）
        if (typeof submitData.rank === 'string') {
          submitData.rank = parseFloat(submitData.rank);
        }

        // 关键修改：添加当前选中的年份参数
        submitData.year = this.selectedYear;
        console.log('提交表单',submitData);

        // 1.如果是课题模块且有执行时间，进行数据转换
        if (this.currentModule === 'subject') {
          submitData.execute_time_start = submitData.executeTime[0];
          submitData.execute_time_end = submitData.executeTime[1];
          submitData.money = parseFloat(submitData.money);
          // 删除原来的executeTime字段（可选）
          delete submitData.executeTime;
        }

        // 2. 调用通用提交方法（传入当前模块名和处理后的数据）
        this.submitModuleData(this.currentModule, submitData);

        this.fileList = []; // 提前清空，避免视觉残留
        this.dialogVisible = false; // 关闭弹窗，触发@close事件执行resetUploadState

      } else {
        this.$message.error(`上传失败: ${response.msg || '服务器处理异常'}`);
      }
    },
 
    // 新增：根据模块获取接口
    getApiByModule(module) {
      const apiMap = {
        subject: addSubject,       // 课题新增接口
        paper: addPaper,           // 论文新增接口
        monograph: addMonograph,   // 论著新增接口
        award: addAward,           // 获奖新增接口
        patent: addPatent,         // 专利新增接口
        software: addSoftware      // 软著新增接口
      };
      return apiMap[module]; // 返回对应模块的接口函数
    },
 
    // 新增：刷新模块数据
    refreshModuleData(module) {
      switch (module) {
        case 'subject':
          this.getSubjectData(); // 原有获取课题数据的方法
          break;
        case 'paper':
          this.getPaperData();  // 原有获取论文数据的方法
          break;
        case 'monograph':
          this.getMonographData(); // 论著数据刷新
          break;
        case 'award':
          this.getAwardData();     // 获奖数据刷新
          break;
        case 'patent':
          this.getPatentData();    // 专利数据刷新
          break;
        case 'software':
          this.getSoftwareData();  // 软著数据刷新
          break;
      }
    },

    // 处理编辑按钮点击
    handleEdit(module, row) {
      this.currentModule = module;
      this.isEdit = true;
      this.editRowId = row.id;
      this.dialogTitle = `编辑${this.getModuleName(module)}`;
      
      // 根据不同模块初始化表单数据
      this.initEditFormData(module, row);
      
      this.dialogVisible = true;
    },
    
    // 初始化编辑表单数据
    initEditFormData(module, row) {
      // 深拷贝行数据到表单
      this.formData = { ...row };
      
      // 特殊处理各模块字段
      switch (module) {
        case 'subject':
          // 处理课题模块的执行时间
          if (row.execute_time_start && row.execute_time_end) {
            this.formData.executeTime = [row.execute_time_start, row.execute_time_end];
          }
          this.formData.rank = row.rank ? row.rank.toString() : '1.0';
          break;
        case 'paper':
          this.formData.publishTime = row.publishTime || null;
          this.formData.rank = row.rank ? row.rank.toString() : '1.0';
          break;
        case 'monograph':
          this.formData.publishTime = row.publishTime || null;
          this.formData.rank = row.rank ? row.rank.toString() : '1.0';
          break;
        case 'award':
          this.formData.awardTime = row.awardTime || null;
          this.formData.rank = row.rank ? row.rank.toString() : '1.0';
          break;
        case 'patent':
          this.formData.applyTime = row.applyTime || null;
          this.formData.authorizeTime = row.authorizeTime || null;
          this.formData.rank = row.rank ? row.rank.toString() : '1.0';
          break;
        case 'software':
          this.formData.applyTime = row.applyTime || null;
          this.formData.authorizeTime = row.authorizeTime || null;
          this.formData.rank = row.rank ? row.rank.toString() : '1.0';
          break;
      }
    },

    // 通用提交方法：适用于所有模块
    // submitModuleData(module, submitData) {
    //   const api = this.getApiByModule(module); // 获取当前模块的接口函数
    //   if (!api) {
    //     this.$message.error("未找到模块接口");
    //     return;
    //   }
  
    //   api(submitData) // 调用对应模块的接口
    //     .then(res => {
    //       if (res.code === 200) {
    //         this.$message.success(res.msg ||'新增成功');
    //         this.dialogVisible = false; // 关闭弹窗
    //         this.refreshModuleData(module); // 刷新当前模块数据（动态传入模块名）
    //         // 新增成功后更新汇总数据
    //         this.updateYearWorkloadData();
    //       } else {
    //         this.$message.error(res.msg || '新增失败');
    //       }
    //     })
    //     .catch(() => this.$message.error('网络异常，请重试'));
    // },
     submitModuleData(module, submitData) {
      // 获取对应模块的API
      const apiMap = {
        subject: this.isEdit ? updateSubject : addSubject,
        paper: this.isEdit ? updatePaper : addPaper,
        monograph: this.isEdit ? updateMonograph : addMonograph,
        award: this.isEdit ? updateAward : addAward,
        patent: this.isEdit ? updatePatent : addPatent,
        software: this.isEdit ? updateSoftware : addSoftware
      };
      
      const api = apiMap[module];
      if (!api) {
        this.$message.error("未找到模块接口");
        return;
      }
    
      api(submitData)
        .then(res => {
          if (res.code === 200) {
            this.$message.success(this.isEdit ? res.msg || '更新成功' : res.msg || '新增成功');
            this.dialogVisible = false;
            this.refreshModuleData(module);
            this.updateYearWorkloadData();
            
            // 重置编辑状态
            this.isEdit = false;
            this.editRowId = null;
          } else {
            this.$message.error(res.msg || (this.isEdit ? '更新失败' : '新增失败'));
          }
        })
        .catch(() => this.$message.error('网络异常，请重试'));
    },

    // 删除数据
    handleDelete(module) {
      // 1. 首先确定模块
      const tableRef = this.$refs[`${module}TableRef`]; // 如 module='subject' → this.$refs.subjectTable
      if (!tableRef) {
        this.$message.error("未找到模块表格，请检查ref是否正确");
        return;
      }

      // 2. 获取选中的行数据（通过ref引用表格）
      const selectedRows = tableRef.getSelectionRows();
      if (selectedRows.length === 0) {
        this.$message.warning('请先选中要删除的数据行');
        return;
      }
  
      // 3. 显示确认弹窗（根据模块动态提示）
      this.$confirm(`确定删除选中的 ${selectedRows.length} 条${this.getModuleName(module)}数据吗？`, "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        // 用户点击了"确定"，执行删除逻辑
        this.executeDelete(module, selectedRows);
      }).catch(() => {
        // 用户点击了"取消"或关闭弹窗
        this.$message.info("已取消删除");
      });
    },  
    
    // 执行删除操作
    executeDelete(module, selectedRows) {
      // 根据模块调用对应后端接口
      const apiMap = {
        subject: deleteSubject,        // 课题删除接口
        paper: deletePaper,     // 论文删除接口
        monograph: deleteMonograph, // 论著删除接口
        award: deleteAward,        // 获奖删除接口
        patent: deletePatent,      // 专利删除接口
        software: deleteSoftware   // 软著删除接口
      };
      
      const deleteApi = apiMap[module];
      if (!deleteApi) {
        this.$message.error("未找到对应模块的删除接口");
        return;
      }

      // 获取要删除的ID列表
      const ids = selectedRows.map(row => row.id);
      console.log('删除的ID', ids);

      // 调用删除接口
      deleteApi(ids).then(res => {
        if (res.code === 200) {
          this.$message.success("删除成功");
          // 刷新当前模块表格数据并清空选中状态
          this.refreshModuleData(module);
          const tableRef = this.$refs[`${module}TableRef`];
          if (tableRef) {
            tableRef.clearSelection();
          }
          // 删除成功后更新汇总数据
          this.updateYearWorkloadData();
        } else {
          this.$message.error(res.msg || "删除失败");
        }
      }).catch(error => {
        this.$message.error("网络异常，请重试");
        console.error("删除失败:", error);
      });
    },
    // 辅助方法：根据模块标识获取模块名称（如 'subject' → '课题'）
    getModuleName(module) {
      const moduleMap = { subject: "课题", paper: "论文", monograph: "论著", award: "获奖", patent: "专利", software: "软著" };
      return moduleMap[module] || "数据";
    },
  
    // 辅助方法：刷新模块数据（根据模块标识调用对应数据获取方法）
    refreshModuleData(module) {
      const refreshMap = {
        subject: this.getSubjectData, // 获取课题数据的方法
        paper: this.getPaperData,   // 获取论文数据的方法
        monograph: this.getMonographData,
        award: this.getAwardData,
        patent: this.getPatentData,
        software: this.getSoftwareData,
      };
      const refreshMethod = refreshMap[module];
      if (refreshMethod && typeof refreshMethod === "function") {
        refreshMethod(); // 调用对应模块的数据刷新方法
      }
    },

     /**
       * 每页条数变化事件（独立模块版）
       * @param {String} module - 模块标识（如 'subject'/'paper'）
       * @param {Number} pageSize - 新的每页条数
       */
      handleSizeChange(module, pageSize) {
        // 更新当前模块的 pageSize，并重置页码为1
        this.modulePagination[module].pageSize = pageSize;
        this.modulePagination[module].currentPage = 1;
        // 重新加载当前模块数据
        this.refreshModuleData(module); 
      },
    
      /**
       * 页码变化事件（独立模块版）
       * @param {String} module - 模块标识
       * @param {Number} currentPage - 新的页码
       */
      handleCurrentChange(module, currentPage) {
        // 更新当前模块的页码
        this.modulePagination[module].currentPage = currentPage;
        // 重新加载当前模块数据
        this.refreshModuleData(module);
      },

      // 重置文件上传相关状态（核心解决方法）
      resetUploadState() {
        this.fileList = []; // 清空已选择文件列表
        this.formData.pdfUrl = ''; // 清除表单绑定的PDF URL
        this.fileValidated = false; // 重置文件校验状态
      },


    /**
     * 获取年度工作量汇总数据
     * 调用后端接口，传递selectedYear参数
     */
    // getYearWorkloadData() {
    //   this.loading.yearSummary = true;
    //   // 假设后端新增接口：/api/workload/year-summary
    //   request({
    //     url: '/api/workload/year-summary',
    //     method: 'get',
    //     params: { year: this.selectedYear } // 传递年份参数
    //   })
    //     .then(response => {
    //       this.yearWorkloadTableData = response.data; // 后端返回格式：[{year: '2024', ...}]
    //     })
    //     .catch(() => {
    //       this.$message.error('年度汇总数据加载失败');
    //     })
    //     .finally(() => {
    //       this.loading.yearSummary = false;
    //     });
    // },

    /**
     * 前端计算年度工作量汇总（核心方法）
     * 直接读取各模块的 totalWorkload 变量，汇总为年度数据
     */
    calculateYearWorkload() {
      // 构造年度汇总数据（单个对象）
      const summaryData = {
        year: this.filterForm.year || '所有年份',  // 当前选中年份
        subjectWorkload: this.subjectConfirmedWorkload, // 已确认课题模块总和
        paperWorkload: this.paperConfirmedWorkload,     // 论文模块总和
        monographWorkload: this.monographConfirmedWorkload, // 论著模块总和
        awardWorkload: this.awardConfirmedWorkload,     // 获奖模块总和
        patentWorkload: this.patentConfirmedWorkload,   // 专利模块总和
        softwareWorkload: this.softwareConfirmedWorkload, // 软著模块总和
      };
      // // 计算总工作量（各模块之和）
      // summaryData.totalWorkload = Object.values(summaryData)
      //   .filter(value => typeof value === 'number') // 过滤非数字值（如year）
      //   .reduce((sum, value) => sum + value, 0);
      // 计算总工作量（各模块之和）- 明确指定需要计算的字段
      const workloadFields = [
        'subjectWorkload', 
        'paperWorkload', 
        'monographWorkload', 
        'awardWorkload', 
        'patentWorkload', 
        'softwareWorkload'
      ];
      
      summaryData.totalWorkload = workloadFields.reduce((sum, field) => {
        return sum + Number(summaryData[field] || 0);
      }, 0);
  
      // 将计算结果赋值给表格数据（保持数组格式，适配表格渲染）
      this.yearWorkloadTableData = [summaryData];
    },
  
    // 修改 refreshAllModules 方法
    refreshAllModules() {
      return new Promise((resolve) => {
        Promise.all([
          this.getSubjectData(),
          this.getPaperData(),
          this.getMonographData(),
          this.getAwardData(),
          this.getPatentData(),
          this.getSoftwareData(),
        ]).then(() => {
          this.calculateYearWorkload();
          resolve();
        });
      });
    },

    // 在 methods 中添加新方法
    updateYearWorkloadData() {
      // 刷新所有模块数据
      this.refreshAllModules().then(() => {
        // 构造要发送到后端的数据
        const workloadData = {
          year: this.selectedYear || '所有年份',
          subjectWorkload: this.subjectTotalWorkload,
          paperWorkload: this.paperTotalWorkload,
          monographWorkload: this.monographTotalWorkload,
          awardWorkload: this.awardTotalWorkload,
          patentWorkload: this.patentTotalWorkload,
          softwareWorkload: this.softwareTotalWorkload,
          totalWorkload: this.subjectTotalWorkload + 
                        this.paperTotalWorkload + 
                        this.monographTotalWorkload + 
                        this.awardTotalWorkload + 
                        this.patentTotalWorkload + 
                        this.softwareTotalWorkload
        };
        
        // 保存到后端
        // this.saveYearWorkloadToBackend(workloadData);
      });
    },

    // 保存年度工作量数据到后端
    saveYearWorkloadToBackend(workloadData) {
      saveYearWorkload(workloadData)
        .then(response => {
          if (response.code === 200) {
            console.log('年度工作量数据保存成功');
          } else {
            console.warn('年度工作量数据保存失败:', response.msg);
          }
        })
        .catch(error => {
          console.error('保存年度工作量数据时出错:', error);
        });
    },

    /**
     * 动态生成年份选项
     * @param {number} pastYears - 向前追溯的年数
     * @param {number} futureYears - 向后扩展的年数
     */
    generateYearOptions(pastYears, futureYears) {
      const currentYear = new Date().getFullYear();
      const startYear = currentYear - pastYears;
      const endYear = currentYear + futureYears;
      
      // 生成从 startYear 到 endYear 的连续年份数组
      this.years = Array.from(
        { length: endYear - startYear + 1 },
        (_, index) => startYear + index
      );
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

    /**
   * 初始化筛选数据
   */
  async initFilterData() {
    // 默认选中第一个部门（应用数学）
    if (this.deptList.length > 0) {
      this.filterForm.deptId = this.deptList[0].deptId;
      this.filterForm.userId = ''; // 重置为全部用户
      await this.getUserList(this.deptList[0].deptId);
    }
  },

  /**
   * 部门变化事件
   */
  async handleDeptChange(deptId) {
    this.filterForm.userId = ''; // 清空用户选择
    if (deptId !== undefined && deptId !== null) {
      await this.getUserList(deptId);
    } else {
      this.userList = [];
    }
  },

  /**
   * 获取用户列表
   */
  async getUserList(deptId) {
    this.userLoading = true;
    try {
      // 调用根据部门获取用户列表接口，根据你的实际API调整
      const response = await getUserListByDept({ deptId });
      if (response.code === 200) {
        this.userList = response.data || [];
      } else {
        this.$message.error('用户列表加载失败');
      }
    } catch (error) {
      this.$message.error('用户列表加载失败');
    } finally {
      this.userLoading = false;
    }
  },

  /**
   * 搜索按钮点击事件
   */
  async handleSearch() {
    this.searchLoading = true;
    try {
      // 验证必填字段
      if (!this.filterForm.year) {
        this.$message.warning('请选择年份');
        return;
      }
      if (this.filterForm.deptId === undefined || this.filterForm.deptId === null || this.filterForm.deptId === '') {
        this.$message.warning('请选择部门');
        return;
      }
      if (!this.filterForm.userId) {
        this.$message.warning('请选择用户');
        return;
      }

      // 刷新所有模块数据 - 这个需要根据具体页面调整
      await this.refreshAllModules();
      // 新增：获取后端汇总数据
      await this.fetchTotalWorkload(
        this.filterForm.year,
        this.filterForm.deptId,
        this.filterForm.userId
      );
      
      this.$message.success('搜索完成');
    } catch (error) {
      this.$message.error('搜索失败');
    } finally {
      this.searchLoading = false;
    }
  },

  /**
   * 重置筛选条件
   */
  handleReset() {
    this.$refs.filterFormRef.resetFields();
    this.filterForm.year = (new Date().getFullYear() - 1).toString();
    // 重置为第一个部门
    if (this.deptList.length > 0) {
      this.filterForm.deptId = this.deptList[0].deptId;
      this.filterForm.userId = ''; // 重置为全部用户
      this.getUserList(this.deptList[0].deptId);
    } else {
      this.userList = [];
    }

    // ✅ 重置后清空所有表格数据 - 这个需要根据具体页面调整
    this.subjectTableData = [];
    this.paperTableData = [];
    this.monographTableData = [];
    this.awardTableData = [];
    this.patentTableData = [];
    this.softwareTableData = [];
    this.yearWorkloadTableData = [];
    
    // ✅ 重置总工作量为0 - 这个需要根据具体页面调整
    this.subjectTotalWorkload = 0;
    this.paperTotalWorkload = 0;
    this.monographTotalWorkload = 0;
    this.awardTotalWorkload = 0;
    this.patentTotalWorkload = 0;
    this.softwareTotalWorkload = 0;
  },

  /**
   * 年份变化事件
   */
  handleYearChange(year) {
    this.filterForm.year = year;
    // 如果已经选择了部门和用户，自动触发搜索
    // if (this.filterForm.deptId) {
    //   this.handleSearch();
    // }
  },



  }
};
</script>

<style scoped>
.research-performance {
  padding: 20px;
  background-color: #fafafa;
}
.page-header {
  margin-bottom: 30px;
}
.main-title {
  font-size: 24px;
  color: #1f2f3d;
  font-weight: 600;
  text-align: center;
  margin-bottom: 10px;
}
.module-card {
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}
.module-header {
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
.module-table {
  width: 100%;
  font-size: 14px;
}
/* 表头样式 */
:deep(.el-table th) {
  background-color: #f0f2f5;
  color: #303133;
  font-weight: 500;
}

/* 表格斑马纹行 */
:deep(.el-table__row:nth-child(even)) {
  background-color: #fafafa;
}

/* 表格悬停行高亮 */
:deep(.el-table__row:hover) {
  background-color: #f5f9ff;
}

/* 单元格内边距 */
:deep(.el-table td) {
  padding: 12px 8px;
}

:deep(.module-header) {
  display: flex;
  justify-content: space-between; /* 关键：两端对齐 */
  align-items: center; /* 垂直居中 */
  padding: 12px 20px;
}

.workload-value {
  color: #2c6ecb;
  font-weight: 500;
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

.annual-workload-card {
  border-top: 3px solid #409eff;
}
.summary-table {
  font-size: 14px;
}
.total-workload {
  font-size: 16px;
  font-weight: 600;
  color: #e67700;
}
.total-year{
  font-size: 16px;
  font-weight: 600;
  color:#409eff;
}
/* 上传文件提示样式 */
.uploaded-file {
  display: flex;
  align-items: center;
  margin-top: 10px;
  padding: 8px 12px;
  background: #f0f9eb;
  border-radius: 4px;
  color: #13ce66;
}
.uploaded-file .el-icon {
  margin-right: 8px;
}
.uploaded-file .el-button {
  margin-left: 10px;
  color: #ff4d4f;
}

/* 上传按钮样式（与页面其他主按钮统一） */
.upload-pdf .el-button {
  padding: 8px 16px; /* 调整内边距，与页面按钮尺寸一致 */
  font-size: 14px;
  background-color: #409eff; /* 主色调，与.module-header中的按钮保持一致 */
  border-color: #409eff;
}

/* 上传按钮悬停效果 */
.upload-pdf .el-button:hover {
  background-color: #3391e8;
  border-color: #3391e8;
}
/* 上传错误提示文本（替换默认的红色提示） */
.el-upload__tip.text-danger {
  color: #ff4d4f; /* 统一错误文本颜色 */
  font-size: 13px;
  margin-top: 5px;
}

/* 上传失败时的文件项样式（如已选择但校验失败的文件） */
:deep(.el-upload-list__item.is-error) {
  border-color: #ff4d4f; /* 错误边框 */
}

/* 错误状态的文件名称颜色 */
:deep(.el-upload-list__item.is-error .el-upload-list__item-name) {
  color: #ff4d4f;
}

/* 错误图标颜色（如❌图标） */
:deep(.el-upload-list__item.is-error .el-icon-error) {
  color: #ff4d4f;
}
/* 已上传文件项（优化现有样式） */
.uploaded-file {
  display: flex;
  align-items: center;
  margin-top: 10px;
  padding: 10px 14px; /* 增加内边距，提升可读性 */
  background: #f0f9eb;
  border-radius: 4px;
  color: #13ce66;
  transition: all 0.2s; /* 平滑过渡效果 */
}

/* 已上传文件项悬停效果 */
.uploaded-file:hover {
  background: #e1f3d8; /* 加深背景色，提示可交互 */
}

/* 删除按钮优化（增强点击反馈） */
.uploaded-file .el-button {
  margin-left: 10px;
  color: #ff4d4f;
  padding: 4px 8px; /* 缩小按钮尺寸，避免视觉冲突 */
  font-size: 12px;
}

.uploaded-file .el-button:hover {
  background: #fff1f0; /* 悬停时背景色，强化反馈 */
}

.module-tip {
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 15px;
  text-align: left !important; /* 强制居左 */
}
.module-tip p {
  margin: 0 0 5px 0; /* 统一段落间距 */
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

/* 筛选卡片样式 */
.filter-card {
  border-top: 3px solid #67c23a;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-header {
  padding: 12px 20px;
  background-color: #f0f9eb;
  border-bottom: 1px solid #e1f3d8;
}

</style>
