<template>
  <div class="research-performance">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="main-title">科研业绩审核</h2>
      <el-divider></el-divider>
    </div>

    <!-- 筛选条件区域 -->
    <el-card class="filter-card" style="margin-bottom: 20px;">
      <div class="filter-header">
        <h3 style="margin: 0; color: #303133;">审核数据筛选</h3>
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

    <!-- 一、课题模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">一、课题</h3>
        <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
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
        <el-table-column label="操作" min-width="180" align="center">
          <template v-slot="scope">
              <el-button 
                type="success" 
                size="small" 
                @click="handleAudit(scope.row, 'subject', '已通过')"
              >
                审核通过
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="handleReject(scope.row, 'subject')"
                :disabled="scope.row.status === '退回修改'"
              >
                退回修改
              </el-button>
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
    </el-card>

    <!-- 二、论文模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">二、论文</h3>
        <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
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
        <el-table-column label="论文级别" prop="level"  align="center">
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
        <el-table-column label="操作" min-width="180" align="center">
          <template v-slot="scope">
              <el-button 
                type="success" 
                size="small" 
                @click="handleAudit(scope.row, 'paper', '已通过')"
              >
                审核通过
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="handleReject(scope.row, 'paper')"
                :disabled="scope.row.status === '退回修改'"
              >
                退回修改
              </el-button>
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
    </el-card>

    <!-- 三、论著模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">三、论著</h3>
        <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
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
        <el-table-column label="操作" min-width="180" align="center">
          <template v-slot="scope">
              <el-button 
                type="success" 
                size="small" 
                @click="handleAudit(scope.row, 'monograph', '已通过')"
              >
                审核通过
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="handleReject(scope.row, 'monograph')"
                :disabled="scope.row.status === '退回修改'"
              >
                退回修改
              </el-button>
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
    </el-card>

    <!-- 四、获奖模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">四、获奖</h3>
        <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
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
        <el-table-column label="操作" min-width="180" align="center">
          <template v-slot="scope">
              <el-button 
                type="success" 
                size="small" 
                @click="handleAudit(scope.row, 'award', '已通过')"
              >
                审核通过
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="handleReject(scope.row, 'award')"
                :disabled="scope.row.status === '退回修改'"
              >
                退回修改
              </el-button>
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
    </el-card>

    <!-- 五、专利模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">五、专利</h3>
        <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
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
        <el-table-column label="操作" min-width="180" align="center">
          <template v-slot="scope">
              <el-button 
                type="success" 
                size="small" 
                @click="handleAudit(scope.row, 'patent', '已通过')"
              >
                审核通过
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="handleReject(scope.row, 'patent')"
                :disabled="scope.row.status === '退回修改'"
              >
                退回修改
              </el-button>
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
    </el-card>

    <!-- 六、软著模块 -->
    <el-card class="module-card">
      <div class="module-header">
        <h3 class="module-title">六、软著</h3>
      <div class="button-group" style="display: flex; gap: 5px;">  <!-- 关键：gap控制间距 -->
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
        <el-table-column label="操作" min-width="180" align="center">
          <template v-slot="scope">
              <el-button 
                type="success" 
                size="small" 
                @click="handleAudit(scope.row, 'software', '已通过')"
              >
                审核通过
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="handleReject(scope.row, 'software')"
                :disabled="scope.row.status === '退回修改'"
              >
                退回修改
              </el-button>
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
    </el-card>

    <!-- 退回修改弹窗 -->
    <el-dialog 
      title="退回修改" 
      v-model="rejectDialogVisible"
      width="500px" 
      :close-on-click-modal="false"
    >
      <el-form 
        :model="rejectForm" 
        :rules="rejectRules" 
        ref="rejectFormRef" 
        label-width="80px"
      >
        <el-form-item label="退回意见" prop="remark">
          <el-input
            v-model="rejectForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入退回修改的具体原因和建议..."
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      
      <template v-slot:footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确定退回</el-button>
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
import { saveYearWorkload } from '@/api/researchPerformance/totalWorkload.js'; 
import { parse } from '@vue/compiler-sfc';
import { getUserListByDept, auditSubject, auditPaper, auditMonograph, auditAward, auditPatent, auditSoftware} from '@/api/check/researchCheck.js';


export default {

  data() {
    return {
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

      // 新增：PDF上传接口URL（若依后端标准路径）
      uploadUrl: import.meta.env.VITE_APP_BASE_API+ '/system/user/profile/uploadPdf',
      // 保留原有认证头（已正确携带Token）
      uploadHeaders: {
        Authorization: 'Bearer ' + getCookie('Admin-Token')
      },
    //   fileValidated: false, // 文件校验状态（true：通过，false：未通过/未选择）

      // 课题模块
      subjectTableData: [],
      subjectTotalWorkload: 0,
      // 论文模块
      paperTableData: [],
      paperTotalWorkload: 0,
      // 论著模块
      monographTableData: [],
      monographTotalWorkload: 0,
      // 获奖模块
      awardTableData: [],
      awardTotalWorkload: 0,
      // 专利模块
      patentTableData: [],
      patentTotalWorkload: 0,
      // 软著模块
      softwareTableData: [],
      softwareTotalWorkload: 0,

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
    //   selectedYear: new Date().getFullYear(), // 默认选中当前年份
      years: [],

      // 退回修改弹窗相关
      rejectDialogVisible: false,
      currentRejectData: null,
      currentRejectModule: '',
      rejectForm: {
        remark: ''
      },
      rejectRules: {
        remark: [
          { required: true, message: '请输入退回意见', trigger: 'blur' },
          { min: 2, message: '请输入至少两个字', trigger: 'blur' }
        ]
      },
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
    // 初始化筛选数据
    await this.initFilterData();
  },

  methods: {
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

        // 刷新所有模块数据
        await this.refreshAllModules();
        
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

      // ✅ 重置后清空所有表格数据
      this.subjectTableData = [];
      this.paperTableData = [];
      this.monographTableData = [];
      this.awardTableData = [];
      this.patentTableData = [];
      this.softwareTableData = [];
      this.yearWorkloadTableData = [];
      
      // ✅ 重置总工作量为0
      this.subjectTotalWorkload = 0;
      this.paperTotalWorkload = 0;
      this.monographTotalWorkload = 0;
      this.awardTotalWorkload = 0;
      this.patentTotalWorkload = 0;
      this.softwareTotalWorkload = 0;
    },

    /**
     * 年份变化事件（修改原有方法）
     */
    handleYearChange(year) {
      this.filterForm.year = year;
      // 如果已经选择了部门和用户，自动触发搜索
      // if (this.filterForm.deptId) {
      //   this.handleSearch();
      // }
    },

    // 课题模块数据获取方法
    getSubjectData() {
      // ✅ 动态生成额外参数：选中具体年份时传递year，否则不传递
      // const extraParams = this.selectedYear ? { year: this.selectedYear } : {};
      const params = {
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || undefined,
        isAudit: true, //当前为审核页面，后端将过滤已审核通过的数据
      };
      console.log('传递给后端的参数：', params);
      
      return this.getModuleData(
        'subject',          // 模块标识
        getSubjectList,     // 课题列表接口
        'subjectTableData', // 表格数据字段
        'subjectTotalWorkload', // 总工作量字段
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
        isAudit: true,
      };

      return this.getModuleData(
        'paper',          // 模块标识
        getPaperList,     // 论文列表接口
        'paperTableData', // 表格数据字段
        'paperTotalWorkload', // 总工作量字段
        '论文数据加载失败',
        params // 新增：传递年份参数
      );
    },
    // 论著模块数据获取方法
    getMonographData() { 
      const params = {
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || undefined,
        isAudit: true,
      };

      return this.getModuleData(
        'monograph',          // 模块标识
        getMonographList,     // 专著列表接口
        'monographTableData', // 表格数据字段
        'monographTotalWorkload', // 总工作量字段
        '论著数据加载失败',
        params // 新增：传递年份参数
      );
    },
    // 获奖模块数据获取方法
    getAwardData() { 
      const params = {
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || undefined,
        isAudit: true,
      };

      return this.getModuleData(
        'award',          // 模块标识
        getAwardList,     // 获奖列表接口
        'awardTableData', // 表格数据字段
        'awardTotalWorkload', // 总工作量字段
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
        isAudit: true,
      };

      return this.getModuleData(
        'patent',          // 模块标识
        getPatentList,     // 专利列表接口
        'patentTableData', // 表格数据字段
        'patentTotalWorkload', // 总工作量字段
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
        isAudit: true,
      };

      return this.getModuleData(
        'software',          // 模块标识
        getSoftwareList,     // 软著列表接口
        'softwareTableData', // 表格数据字段
        'softwareTotalWorkload', // 总工作量字段
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
    getModuleData(module, apiFunc, tableKey, totalKey, errorMsg, extraParams = {}) {
      this.loading[module] = true;
      const { currentPage, pageSize } = this.modulePagination[module];
      // 合并分页参数与额外参数（如year）
      const params = { pageNum: currentPage, pageSize, ...extraParams };
  
      return apiFunc(params) // 接口调用时传递年份
        .then(response => {
          this[tableKey] = response.data.records;
          this.modulePagination[module].total = response.data.total;
          this[totalKey] = response.data.records.reduce(
            (sum, item) => sum + Number(item.workload || 0), 0
          );
        })
        .catch(() => this.$message.error(errorMsg))
        .finally(() => this.loading[module] = false);
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
        //   this.updateYearWorkloadData();
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
        //   this.calculateYearWorkload();
          resolve();
        });
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
     * 审核通过
     */
    handleAudit(row, module, status) {
      this.$confirm(`确定要${status === '已通过' ? '审核通过' : '退回修改'}该记录吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.submitAudit(row, module, status, '');
      }).catch(() => {
        this.$message.info('已取消操作');
      });
    },

    /**
     * 退回修改
     */
    handleReject(row, module) {
      this.currentRejectData = row;
      this.currentRejectModule = module;
      this.rejectForm.remark = '';
      this.rejectDialogVisible = true;
    },

    /**
     * 提交退回修改
     */
    submitReject() {
      this.$refs.rejectFormRef.validate((valid) => {
        if (valid) {
          this.submitAudit(
            this.currentRejectData, 
            this.currentRejectModule, 
            '退回修改', 
            this.rejectForm.remark
          );
          this.rejectDialogVisible = false;
        }
      });
    },

    /**
     * 提交审核到后端
     */
    submitAudit(row, module, status, remark) {
      const auditData = {
        id: row.id,
        status: status,
        remark: remark || ''
      };

      // 根据模块调用对应的审核接口
      const apiMap = {
        subject: auditSubject,
        paper: auditPaper,
        monograph: auditMonograph,
        award: auditAward,
        patent: auditPatent,
        software: auditSoftware
      };

      const auditApi = apiMap[module];
      if (!auditApi) {
        this.$message.error('未找到对应的审核接口');
        return;
      }

      auditApi(auditData).then(res => {
        if (res.code === 200) {
          this.$message.success(`${status}成功`);
          // 刷新当前模块数据
          this.refreshModuleData(module);
          // 更新年度汇总数据
        //   this.updateYearWorkloadData();
        } else {
          this.$message.error(res.msg || `${status}失败`);
        }
      }).catch(error => {
        this.$message.error('网络异常，请重试');
        console.error('审核失败:', error);
      });
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
.summary-label {
  color: #606266;
}
.summary-value {
  font-weight: 500;
  color: #e67700;
  margin-left: 8px;
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
.annual-workload-card {
  margin-top: 20px;
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

/* 禁用按钮的样式调整 */
:deep(.el-button.is-disabled) {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 提示框样式 */
:deep(.el-tooltip) {
  display: inline-block;
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

</style>
