<template>
  <div class="teaching-performance">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="main-title">教学业绩统计</h2>
      <el-divider></el-divider>
    </div>

    <el-card class="year-export-card" style="margin-bottom: 20px;">
      <div class="year-export-content">
        <div>
          <h3 class="year-export-title">年度全部导出</h3>
          <div class="year-export-desc">按选择年份导出全部教学数据，不受部门和用户筛选影响。</div>
        </div>
        <div class="year-export-actions">
          <el-select
            v-model="yearAllForm.year"
            placeholder="请选择年份"
            style="width: 140px;"
          >
            <el-option
              v-for="year in years"
              :key="year"
              :label="year"
              :value="year"
            ></el-option>
          </el-select>
          <el-checkbox v-model="yearAllIncludePdf">包含PDF附件</el-checkbox>
          <el-button
            type="warning"
            icon="Download"
            :loading="exportLoading"
            @click="handleExportYearAll"
          >
            导出该年度全部数据
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card class="statistics-content-card">

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
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="筛选导出">
              <el-checkbox v-model="filteredIncludePdf">包含PDF附件</el-checkbox>
              <el-button
                type="success"
                icon="Download"
                :loading="exportLoading"
                style="margin-left: 12px;"
                @click="handleExportFiltered"
              >
                按当前筛选条件导出
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="module-card annual-workload-card">
      <div class="module-header">
        <h3 class="module-title">已确认年度工作量汇总</h3>
      </div>
      <el-table 
        :data="yearWorkloadTableData" 
        class="summary-table" 
        border 
        stripe
        :loading="summaryLoading"
      >
        <el-table-column prop="year" label="年份" align="center" width="100">
          <template #default="scope"><span class="total-year">{{ scope.row.year }}</span></template>
        </el-table-column>
        <!-- 各模块列 -->
        <el-table-column prop="theoryCourseConfirmedWorkload" label="本科理论课" align="center" />
        <el-table-column prop="experimentCourseConfirmedWorkload" label="本科实验课" align="center" />
        <el-table-column prop="practicalTeachingConfirmedWorkload" label="实践教学" align="center" />
        <el-table-column prop="thesisCourseConfirmedWorkload" label="毕业论文" align="center" />
        <el-table-column prop="scienceInnovationConfirmedWorkload" label="科技创新" align="center" />
        <el-table-column prop="competitionConfirmedWorkload" label="学科竞赛" align="center" />
        <el-table-column prop="textbookConfirmedWorkload" label="出版教材" align="center" />
        <el-table-column prop="educationReformConfirmedWorkload" label="教改项目" align="center" />
        <el-table-column prop="educationReformPaperConfirmedWorkload" label="教改论文" align="center" />
        <el-table-column prop="proctorConfirmedWorkload" label="监考" align="center" />
        <el-table-column prop="graduateTheoryCourseConfirmedWorkload" label="研究生理论课" align="center" />
        <el-table-column prop="graduateGuideStudentConfirmedWorkload" label="研究生指导学生" align="center" />
        <el-table-column prop="totalWorkload" label="总工作量" align="center" width="120">
          <template #default="scope"><span class="total-workload">{{ scope.row.totalWorkload.toFixed(3) }}</span></template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 使用子组件方式展示各模块 -->
    <!-- 本科理论课审核子组件 -->
    <UndergraduateTheory 
      :data="moduleData.undergraduateTheory" 
      :loading="loading.undergraduateTheory"
      :pagination="modulePagination.undergraduateTheory"
      :year="Number(filterForm.year)"
      module-key="undergraduateTheory"
      mode="search"
      :confirmed-workload="moduleWorkload.undergraduateTheory.confirmed"
      :estimated-workload="moduleWorkload.undergraduateTheory.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('undergraduateTheory', size)"
      @current-change="(page) => handleCurrentChange('undergraduateTheory', page)"
    />

    <!-- 本科实验课审核子组件 -->
    <UndergraduateExperiment 
      :data="moduleData.undergraduateExperiment" 
      :loading="loading.undergraduateExperiment"
      :pagination="modulePagination.undergraduateExperiment"
      :year="Number(filterForm.year)"
      module-key="undergraduateExperiment"
      mode="search"
      :confirmed-workload="moduleWorkload.undergraduateExperiment.confirmed"
      :estimated-workload="moduleWorkload.undergraduateExperiment.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('undergraduateExperiment', size)"
      @current-change="(page) => handleCurrentChange('undergraduateExperiment', page)"
    />

    <!-- 实践教学审核子组件 -->
    <UndergraduatePractice 
      :data="moduleData.practiceTeaching" 
      :loading="loading.practiceTeaching"
      :pagination="modulePagination.practiceTeaching"
      :year="Number(filterForm.year)"
      module-key="practiceTeaching"
      mode="search"
      :confirmed-workload="moduleWorkload.practiceTeaching.confirmed"
      :estimated-workload="moduleWorkload.practiceTeaching.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('practiceTeaching', size)"
      @current-change="(page) => handleCurrentChange('practiceTeaching', page)"
    />

    <!-- 毕业论文审核子组件 -->
    <UndergraduateThesis 
      :data="moduleData.undergraduateThesis" 
      :loading="loading.undergraduateThesis"
      :pagination="modulePagination.undergraduateThesis"
      :year="Number(filterForm.year)"
      module-key="undergraduateThesis"
      mode="search"
      :confirmed-workload="moduleWorkload.undergraduateThesis.confirmed"
      :estimated-workload="moduleWorkload.undergraduateThesis.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('undergraduateThesis', size)"
      @current-change="(page) => handleCurrentChange('undergraduateThesis', page)"
    />

    <!-- 科技创新审核子组件 -->
    <ScienceInnovation 
      :data="moduleData.scienceInnovation" 
      :loading="loading.scienceInnovation"
      :pagination="modulePagination.scienceInnovation"
      :year="Number(filterForm.year)"
      module-key="scienceInnovation"
      mode="search"
      :confirmed-workload="moduleWorkload.scienceInnovation.confirmed"
      :estimated-workload="moduleWorkload.scienceInnovation.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('scienceInnovation', size)"
      @current-change="(page) => handleCurrentChange('scienceInnovation', page)"
    />

    <!-- 学科竞赛审核子组件 -->
    <CompetitionModule 
      :data="moduleData.competition" 
      :loading="loading.competition"
      :pagination="modulePagination.competition"
      :year="Number(filterForm.year)"
      module-key="competition"
      mode="search"
      :confirmed-workload="moduleWorkload.competition.confirmed"
      :estimated-workload="moduleWorkload.competition.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('competition', size)"
      @current-change="(page) => handleCurrentChange('competition', page)"
    />

    <!-- 出版教材审核子组件 -->
    <TextbookModule 
      :data="moduleData.textbook" 
      :loading="loading.textbook"
      :pagination="modulePagination.textbook"
      :year="Number(filterForm.year)"
      module-key="textbook"
      mode="search"
      :confirmed-workload="moduleWorkload.textbook.confirmed"
      :estimated-workload="moduleWorkload.textbook.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('textbook', size)"
      @current-change="(page) => handleCurrentChange('textbook', page)"
    />

    <!-- 教改项目审核子组件 -->
    <EducationReform 
      :data="moduleData.educationReform" 
      :loading="loading.educationReform"
      :pagination="modulePagination.educationReform"
      :year="Number(filterForm.year)"
      module-key="educationReform"
      mode="search"
      :confirmed-workload="moduleWorkload.educationReform.confirmed"
      :estimated-workload="moduleWorkload.educationReform.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('educationReform', size)"
      @current-change="(page) => handleCurrentChange('educationReform', page)"
    />

    <!-- 教改论文审核子组件 -->
    <EducationReformPaper 
      :data="moduleData.educationReformPaper" 
      :loading="loading.educationReformPaper"
      :pagination="modulePagination.educationReformPaper"
      :year="Number(filterForm.year)"
      module-key="educationReformPaper"
      mode="search"
      :confirmed-workload="moduleWorkload.educationReformPaper.confirmed"
      :estimated-workload="moduleWorkload.educationReformPaper.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('educationReformPaper', size)"
      @current-change="(page) => handleCurrentChange('educationReformPaper', page)"
    />

    <!-- 监考模块审核子组件 -->
    <ProctorModule 
      :data="moduleData.proctor" 
      :loading="loading.proctor"
      :pagination="modulePagination.proctor"
      :year="Number(filterForm.year)"
      module-key="proctor"
      mode="search"
      :confirmed-workload="moduleWorkload.proctor.confirmed"
      :estimated-workload="moduleWorkload.proctor.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('proctor', size)"
      @current-change="(page) => handleCurrentChange('proctor', page)"
    />

    <!-- 研究生理论课审核子组件 -->
    <GraduateTheoryCourse 
      :data="moduleData.graduateTheory" 
      :loading="loading.graduateTheory"
      :pagination="modulePagination.graduateTheory"
      :year="Number(filterForm.year)"
      module-key="graduateTheory"
      mode="search"
      :confirmed-workload="moduleWorkload.graduateTheory.confirmed"
      :estimated-workload="moduleWorkload.graduateTheory.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('graduateTheory', size)"
      @current-change="(page) => handleCurrentChange('graduateTheory', page)"
    />

    <!-- 研究生指导学生审核子组件 -->
    <GraduateGuideStudent 
      :data="moduleData.graduateGuideStudent" 
      :loading="loading.graduateGuideStudent"
      :pagination="modulePagination.graduateGuideStudent"
      :year="Number(filterForm.year)"
      module-key="graduateGuideStudent"
      mode="search"
      :confirmed-workload="moduleWorkload.graduateGuideStudent.confirmed"
      :estimated-workload="moduleWorkload.graduateGuideStudent.estimated"
      @audit="handleAuditPass"
      @reject="handleAuditReject"
      @delete="handleDelete"
      @size-change="(size) => handleSizeChange('graduateGuideStudent', size)"
      @current-change="(page) => handleCurrentChange('graduateGuideStudent', page)"
    />

    <!-- 实践教学、毕业论文、科技创新、学科竞赛、出版教材、教改项目、教改论文、监考、研究生理论课、研究生指导学生 -->

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
import { ElMessage, ElLoading } from 'element-plus';
import { saveAs } from 'file-saver';
import { exportTeachingStatistics } from '@/api/statistics/export.js';
// // 导入筛选相关的API
// import { getUserListByDept } from '@/api/check/researchCheck.js';
// // 导入审核子组件
// import UndergraduateTheoryCheck from './components/UndergraduateTheoryCheck.vue';
// import UndergraduateExperimentCheck from './components/UndergraduateExperimentCheck.vue';
import { getUserListByDept} from '@/api/check/researchCheck.js';
import { auditUndergraduateTheory, auditUndergraduateExperiment,auditPracticeTeaching,auditThesis,auditScienceInnovation,auditCompetition,auditTextbook,auditEducationReform,auditEducationReformPaper,auditProctor,auditGraduateTheoryCourse,auditGraduateGuideStudent, } from '@/api/check/teachingCheck.js';
import UndergraduateTheory from '../teaching/UndergraduateTheory.vue';
import UndergraduateExperiment from '../teaching/UndergraduateExperiment.vue';
import UndergraduatePractice from '../teaching/UndergraduatePractice.vue';
import UndergraduateThesis from '../teaching/UndergraduateThesis.vue';
import ScienceInnovation from '../teaching/ScienceInnovation.vue';
import CompetitionModule from '../teaching/CompetitionModule.vue';
import TextbookModule from '../teaching/Textbook.vue';
import EducationReform from '../teaching/EducationReform.vue';
import EducationReformPaper from '../teaching/EducationReformPaper.vue';
import ProctorModule from '../teaching/Proctor.vue';
import GraduateTheoryCourse from '../teaching/GraduateTheoryCourse.vue';
import GraduateGuideStudent from '../teaching/GraduateGuideStudent.vue';
import { getTeachingTotalWorkload } from '@/api/teaching/teachingTotalWorkload';
// 1. 导入子组件和API（理论课）
import { 
  getTheoryCourseList, 
  addTheoryCourse, 
  updateTheoryCourse, 
  deleteTheoryCourse 
} from '@/api/teaching/theoryCourse'; // 导入JS文件中的API函数
// 2. 导入子组件和API（实验课）
import { 
  getExperimentCourseList, 
  addExperimentCourse, 
  updateExperimentCourse, 
  deleteExperimentCourse 
} from '@/api/teaching/experimentCourse';
// // 导入教学业绩审核API
import { 
  getPracticeTeachingList, 
  addPracticeTeaching, 
  updatePracticeTeaching, 
  deletePracticeTeaching,
} from '@/api/teaching/practiceTeaching';

import { 
  getThesisList, 
  addThesis, 
  updateThesis, 
  deleteThesis,
} from '@/api/teaching/thesisCourse';

import { 
  getScienceInnovationList, 
  addScienceInnovation, 
  updateScienceInnovation, 
  deleteScienceInnovation,
} from '@/api/teaching/scienceInnovation';

import { 
  getCompetitionList, 
  addCompetition, 
  updateCompetition, 
  deleteCompetition,
} from '@/api/teaching/competition';

import { 
  getTextbookList, 
  addTextbook, 
  updateTextbook, 
  deleteTextbook,
} from '@/api/teaching/textbook';

import { 
  getEducationReformList, 
  addEducationReform, 
  updateEducationReform, 
  deleteEducationReform,
} from '@/api/teaching/educationReform';

import { 
  getEducationReformPaperList, 
  addEducationReformPaper, 
  updateEducationReformPaper, 
  deleteEducationReformPaper,
} from '@/api/teaching/educationReformPaper';

import { 
  getProctorList, 
  addProctor, 
  updateProctor, 
  deleteProctor,
} from '@/api/teaching/proctor';

import { 
  getGraduateTheoryCourseList, 
  addGraduateTheoryCourse, 
  updateGraduateTheoryCourse, 
  deleteGraduateTheoryCourse,
} from '@/api/teaching/graduateTheoryCourse';

import { 
  getGraduateGuideStudentList, 
  addGraduateGuideStudent, 
  updateGraduateGuideStudent, 
  deleteGraduateGuideStudent,
} from '@/api/teaching/graduateGuideStudent';

export default {
  name: 'TeachingPerformanceCheck',
  components: {
    UndergraduateTheory,
    UndergraduateExperiment,
    UndergraduatePractice,
    UndergraduateThesis,
    ScienceInnovation,
    CompetitionModule,
    TextbookModule,
    EducationReform,
    EducationReformPaper,
    ProctorModule,
    GraduateTheoryCourse,
    GraduateGuideStudent
  },
  data() {
    return {
      // 筛选表单数据
      filterForm: {
        year: (new Date().getFullYear() - 1).toString(),
        deptId: '',
        userId: ''
      },
      yearAllForm: {
        year: (new Date().getFullYear() - 1).toString()
      },
      // 部门列表
      deptList: [
        { deptId: 0, deptName: '所有部门' },
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
      exportLoading: false,
      filteredIncludePdf: false,
      yearAllIncludePdf: false,

      // 模块数据
      moduleData: {
        undergraduateTheory: [],
        undergraduateExperiment: [],
        practiceTeaching: [],
        undergraduateThesis: [],
        scienceInnovation: [],
        competition: [],
        textbook: [],
        educationReform: [],
        educationReformPaper: [],
        proctor: [],
        graduateTheory: [],
        graduateGuideStudent: []
      },

      // 加载状态
      loading: {
        undergraduateTheory: false,
        undergraduateExperiment: false,
        practiceTeaching: false,
        undergraduateThesis: false,
        scienceInnovation: false,
        competition: false,
        textbook: false,
        educationReform: false,
        educationReformPaper: false,
        proctor: false,
        graduateTheory: false,
        graduateGuideStudent: false
      },

      // 模块分页参数
      modulePagination: {
        undergraduateTheory: { currentPage: 1, pageSize: 10, total: 0 },
        undergraduateExperiment: { currentPage: 1, pageSize: 10, total: 0 },
        practiceTeaching: { currentPage: 1, pageSize: 10, total: 0 },
        undergraduateThesis: { currentPage: 1, pageSize: 10, total: 0 },
        scienceInnovation: { currentPage: 1, pageSize: 10, total: 0 },
        competition: { currentPage: 1, pageSize: 10, total: 0 },
        textbook: { currentPage: 1, pageSize: 10, total: 0 },
        educationReform: { currentPage: 1, pageSize: 10, total: 0 },
        educationReformPaper: { currentPage: 1, pageSize: 10, total: 0 },
        proctor: { currentPage: 1, pageSize: 10, total: 0 },
        graduateTheory: { currentPage: 1, pageSize: 10, total: 0 },
        graduateGuideStudent: { currentPage: 1, pageSize: 10, total: 0 }
      },

      // 年份选项
      years: [],

      // 模块工作量（已确认/预计）
      moduleWorkload: {
        undergraduateTheory: { confirmed: 0, estimated: 0 },
        undergraduateExperiment: { confirmed: 0, estimated: 0 },
        practiceTeaching: { confirmed: 0, estimated: 0 },
        undergraduateThesis: { confirmed: 0, estimated: 0 },
        scienceInnovation: { confirmed: 0, estimated: 0 },
        competition: { confirmed: 0, estimated: 0 },
        textbook: { confirmed: 0, estimated: 0 },
        educationReform: { confirmed: 0, estimated: 0 },
        educationReformPaper: { confirmed: 0, estimated: 0 },
        proctor: { confirmed: 0, estimated: 0 },
        graduateTheory: { confirmed: 0, estimated: 0 },
        graduateGuideStudent: { confirmed: 0, estimated: 0 }
      },
      // 年度工作量汇总表数据
      yearWorkloadTableData: [],
      // 汇总数据加载状态
      summaryLoading: false,

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

      // API映射表
      MODULE_API_MAP: {
        undergraduateTheory: {
          listApi: getTheoryCourseList,
          auditApi: auditUndergraduateTheory,
          deleteApi: deleteTheoryCourse, 
        },
        undergraduateExperiment: {
          listApi: getExperimentCourseList,
          auditApi: auditUndergraduateExperiment,
          deleteApi: deleteExperimentCourse
        },
        practiceTeaching: {
          listApi: getPracticeTeachingList,
          auditApi: auditPracticeTeaching,
          deleteApi: deletePracticeTeaching
        },
        undergraduateThesis: {
          listApi: getThesisList,
          auditApi: auditThesis,
          deleteApi: deleteThesis
        },
        scienceInnovation: {
          listApi: getScienceInnovationList,
          auditApi: auditScienceInnovation,
          deleteApi: deleteScienceInnovation
        },
        competition: {
          listApi: getCompetitionList,
          auditApi: auditCompetition,
          deleteApi: deleteCompetition
        },
        textbook: {
          listApi: getTextbookList,
          auditApi: auditTextbook,
          deleteApi: deleteTextbook
        },
        educationReform: {
          listApi: getEducationReformList,
          auditApi: auditEducationReform,
          deleteApi: deleteEducationReform
        },
        educationReformPaper: {
          listApi: getEducationReformPaperList,
          auditApi: auditEducationReformPaper,
          deleteApi: deleteEducationReformPaper
        },
        proctor: {
          listApi: getProctorList,
          auditApi: auditProctor,
          deleteApi: deleteProctor
        },
        graduateTheory: {
          listApi: getGraduateTheoryCourseList,
          auditApi: auditGraduateTheoryCourse,
          deleteApi: deleteGraduateTheoryCourse
        },
        graduateGuideStudent: {
          listApi: getGraduateGuideStudentList,
          auditApi: auditGraduateGuideStudent,
          deleteApi: deleteGraduateGuideStudent
        }
      }
    };
  },

  created() {
    this.generateYearOptions(5, 3);
  },

  async mounted() {
    await this.initFilterData();
  },

  methods: {
    /**
     * 初始化筛选数据
     */
    async initFilterData() {
      if (this.deptList.length > 0) {
        this.filterForm.deptId = this.deptList[0].deptId;
        this.filterForm.userId = '';
        await this.getUserList(this.deptList[0].deptId);
      }
    },

    /**
     * 部门变化事件
     */
    async handleDeptChange(deptId) {
      this.filterForm.userId = '';
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
        if (!this.filterForm.year) {
          this.$message.warning('请选择年份');
          return;
        }
        if (this.filterForm.deptId === undefined || this.filterForm.deptId === null || this.filterForm.deptId === '') {
          this.$message.warning('请选择部门');
          return;
        }

        await this.refreshAllModules();
        // 新增：获取汇总数据
        await this.fetchTotalWorkload(
          this.filterForm.year,
          this.filterForm.deptId,
          this.filterForm.userId || undefined
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
      if (this.deptList.length > 0) {
        this.filterForm.deptId = this.deptList[0].deptId;
        this.filterForm.userId = '';
        this.getUserList(this.deptList[0].deptId);
      } else {
        this.userList = [];
      }

      // 清空所有模块数据
      Object.keys(this.moduleData).forEach(key => {
        this.moduleData[key] = [];
      });

      // 清空汇总数据
      this.yearWorkloadTableData = [];
      Object.keys(this.moduleWorkload).forEach(key => {
        this.moduleWorkload[key] = { confirmed: 0, estimated: 0 };
      });
    },

    /**
     * 年份变化事件
     */
    handleYearChange(year) {
      this.filterForm.year = year;
    },

    async handleExportFiltered() {
      if (!this.filterForm.year) {
        this.$message.warning('请选择年份');
        return;
      }
      if (this.filterForm.deptId === undefined || this.filterForm.deptId === null || this.filterForm.deptId === '') {
        this.$message.warning('请选择部门');
        return;
      }
      await this.downloadStatisticsExport({
        scope: 'FILTERED',
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || 'all',
        includePdf: this.filteredIncludePdf
      });
    },

    async handleExportYearAll() {
      if (!this.yearAllForm.year) {
        this.$message.warning('请选择年份');
        return;
      }
      await this.downloadStatisticsExport({
        scope: 'YEAR_ALL',
        year: this.yearAllForm.year,
        includePdf: this.yearAllIncludePdf
      });
    },

    async downloadStatisticsExport(payload) {
      this.exportLoading = true;
      const loadingInstance = ElLoading.service({
        lock: true,
        text: payload.includePdf ? '正在导出数据并打包PDF，请稍候...' : '正在导出数据，请稍候...',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      try {
        const data = await exportTeachingStatistics(payload);
        const ext = payload.includePdf ? 'zip' : 'xlsx';
        const type = payload.includePdf
          ? 'application/zip'
          : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
        saveAs(new Blob([data], { type }), `教学统计_${payload.year}.${ext}`);
        this.$message.success('导出完成');
      } catch (error) {
        this.$message.error('导出失败');
      } finally {
        loadingInstance.close();
        this.exportLoading = false;
      }
    },

    // 通用模块数据获取方法（替代所有单独的 getXXXData 方法）
    getModuleData(moduleKey) {
      const params = {
        year: this.filterForm.year,
        deptId: this.filterForm.deptId,
        userId: this.filterForm.userId || undefined,
        isAudit: false
      };

      return this.getModuleDataWithPagination(
        moduleKey,
        moduleKey,
        `${this.getModuleName(moduleKey)}数据加载失败`,
        params
      );
    },

    // 通用分页数据获取方法
    getModuleDataWithPagination(module, dataKey, errorMsg, extraParams = {}) {
      this.loading[module] = true;
      const { currentPage, pageSize } = this.modulePagination[module];
      const { listApi } = this.MODULE_API_MAP[module];
      const params = { pageNum: currentPage, pageSize, ...extraParams };

      return listApi(params)
        .then(response => {
          this.moduleData[dataKey] = response.data.records;
          this.modulePagination[module].total = response.data.total;
        })
        .catch(() => this.$message.error(errorMsg))
        .finally(() => this.loading[module] = false);
    },

    refreshAllModules() {
      return new Promise((resolve) => {
        // 获取所有模块key
        const moduleKeys = Object.keys(this.moduleData);
        // 为每个模块创建数据获取Promise
        const modulePromises = moduleKeys.map(moduleKey => 
          this.getModuleData(moduleKey)
        );
        
        Promise.all(modulePromises).then(() => {
          resolve();
        });
      });
    },

    // 从后端获取年度工作量汇总数据
    async fetchTotalWorkload(year, deptId, userId) {
      this.summaryLoading = true;
      try {
        const response = await getTeachingTotalWorkload({ year, deptId, userId, mode: 'search' });
        this.yearWorkloadTableData = [response.data];
        this.extractModuleWorkload(response.data);
      } catch (error) {
        this.$message.error(`汇总数据加载失败：${error.message || '请联系管理员'}`);
        this.yearWorkloadTableData = [];
      } finally {
        this.summaryLoading = false;
      }
    },
  
    // 提取各模块工作量
    extractModuleWorkload(summaryData) {
      Object.assign(this.moduleWorkload, {
        undergraduateTheory: { 
          confirmed: summaryData.theoryCourseConfirmedWorkload || 0, 
          estimated: summaryData.theoryCourseEstimatedWorkload || 0 
        },
        undergraduateExperiment: { 
          confirmed: summaryData.experimentCourseConfirmedWorkload || 0, 
          estimated: summaryData.experimentCourseEstimatedWorkload || 0 
        },
        practiceTeaching: { 
          confirmed: summaryData.practicalTeachingConfirmedWorkload || 0, 
          estimated: summaryData.practicalTeachingEstimatedWorkload || 0 
        },
        undergraduateThesis: { 
          confirmed: summaryData.thesisCourseConfirmedWorkload || 0, 
          estimated: summaryData.thesisCourseEstimatedWorkload || 0 
        },
        scienceInnovation: { 
          confirmed: summaryData.scienceInnovationConfirmedWorkload || 0, 
          estimated: summaryData.scienceInnovationEstimatedWorkload || 0 
        },
        competition: { 
          confirmed: summaryData.competitionConfirmedWorkload || 0, 
          estimated: summaryData.competitionEstimatedWorkload || 0 
        },
        textbook: { 
          confirmed: summaryData.textbookConfirmedWorkload || 0, 
          estimated: summaryData.textbookEstimatedWorkload || 0 
        },
        educationReform: { 
          confirmed: summaryData.educationReformConfirmedWorkload || 0, 
          estimated: summaryData.educationReformEstimatedWorkload || 0 
        },
        educationReformPaper: { 
          confirmed: summaryData.educationReformPaperConfirmedWorkload || 0, 
          estimated: summaryData.educationReformPaperEstimatedWorkload || 0 
        },
        proctor: { 
          confirmed: summaryData.proctorConfirmedWorkload || 0, 
          estimated: summaryData.proctorEstimatedWorkload || 0 
        },
        graduateTheory: { 
          confirmed: summaryData.graduateTheoryCourseConfirmedWorkload || 0, 
          estimated: summaryData.graduateTheoryCourseEstimatedWorkload || 0 
        },
        graduateGuideStudent: { 
          confirmed: summaryData.graduateGuideStudentConfirmedWorkload || 0, 
          estimated: summaryData.graduateGuideStudentEstimatedWorkload || 0 
        }
      });
    },

    /**
     * 删除数据
     */
    handleDelete(module) {
      const { deleteApi } = this.MODULE_API_MAP[module];
      const ids = []; // 这里需要从子组件传递选中的行ID

      if (ids.length === 0) {
        this.$message.warning('请先选中要删除的数据行');
        return;
      }

      this.$confirm(`确定删除选中的 ${ids.length} 条${this.getModuleName(module)}数据吗？`, "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteApi(ids).then(res => {
          if (res.code === 200) {
            this.$message.success("删除成功");
            this.refreshModuleData(module);
          } else {
            this.$message.error(res.msg || "删除失败");
          }
        }).catch(error => {
          this.$message.error("网络异常，请重试");
          console.error("删除失败:", error);
        });
      }).catch(() => {
        this.$message.info("已取消删除");
      });
    },


    /**
     * 刷新模块数据
     */
    // refreshModuleData(module) {
    //   const refreshMap = {
    //     undergraduateTheory: this.getUndergraduateTheoryData,
    //     undergraduateExperiment: this.getUndergraduateExperimentData
    //   };
    //   const refreshMethod = refreshMap[module];
    //   if (refreshMethod && typeof refreshMethod === "function") {
    //     refreshMethod();
    //   }
    // },
    refreshModuleData(module) {
      this.getModuleData(module);
    },

    /**
     * 每页条数变化事件
     */
    handleSizeChange(module, pageSize) {
      this.modulePagination[module].pageSize = pageSize;
      this.modulePagination[module].currentPage = 1;
      this.refreshModuleData(module);
    },
    
    /**
     * 页码变化事件
     */
    handleCurrentChange(module, currentPage) {
      this.modulePagination[module].currentPage = currentPage;
      this.refreshModuleData(module);
    },

    /**
     * 辅助方法：根据模块标识获取模块名称
     */
    getModuleName(module) {
      const moduleMap = {
        undergraduateTheory: "本科理论课",
        undergraduateExperiment: "本科实验课",
        practiceTeaching: "实践教学",
        undergraduateThesis: "毕业论文",
        scienceInnovation: "科技创新",
        competition: "学科竞赛",
        textbook: "出版教材",
        educationReform: "教改项目",
        educationReformPaper: "教改论文",
        proctor: "监考模块",
        graduateTheory: "研究生理论课",
        graduateGuideStudent: "研究生指导学生"
      };
      return moduleMap[module] || "数据";
    },

    /**
     * 动态生成年份选项
     */
    generateYearOptions(pastYears, futureYears) {
      const currentYear = new Date().getFullYear();
      const startYear = currentYear - pastYears;
      const endYear = currentYear + futureYears;
      
      this.years = Array.from(
        { length: endYear - startYear + 1 },
        (_, index) => startYear + index
      );
    },

    /**
     * 审核通过
     */
    handleAuditPass(row, module) {
      this.$confirm('确定要审核通过该记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
        // 确认后调用审核接口
      }).then(() => {
        this.submitAudit(row, module, '已通过', '');
      }).catch((error) => {
         // 添加这行来查看具体错误
        console.log('确认框错误:', error);
        if (error !== 'cancel' && error !== 'close') {
          this.$message.error('操作失败: ' + error.toString());
        } else {
          this.$message.info('已取消操作');
        }
      });
    },

    /**
     * 退回修改
     */
    handleAuditReject(row, module) {
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
      const { auditApi } = this.MODULE_API_MAP[module];
      const auditData = {
        id: row.id,
        status: status,
        remark: remark || ''
      };

      auditApi(auditData).then(res => {
        if (res.code === 200) {
          this.$message.success(`${status}成功`);
          this.refreshModuleData(module);
        } else {
          this.$message.error(res.msg || `${status}失败`);
        }
      }).catch(error => {
        this.$message.error('网络异常，请重试');
        console.error('审核失败:', error);
      });
    },
    


  }
};
</script>

<style scoped>
.teaching-performance {
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
.filter-card {
  border-top: 3px solid #67c23a;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}
.statistics-content-card {
  margin-bottom: 20px;
  border-top: 3px solid #409eff;
}
.filter-header {
  padding: 12px 20px;
  background-color: #f0f9eb;
  border-bottom: 1px solid #e1f3d8;
}
.module-card {
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  border-radius: 8px;
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
}
.annual-workload-card {
  border-top: 3px solid #409eff;
}
.summary-table {
  font-size: 14px;
}
.total-year {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}
.total-workload {
  font-size: 16px;
  font-weight: 600;
  color: #e67700;
}
.year-export-card {
  border-top: 3px solid #e6a23c;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.year-export-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}
.year-export-title {
  font-size: 16px;
  color: #2c3e50;
  font-weight: 600;
  margin: 0 0 6px;
}
.year-export-desc {
  font-size: 13px;
  color: #606266;
}
.year-export-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
  flex-shrink: 0;
}
@media (max-width: 768px) {
  .year-export-content {
    align-items: flex-start;
    flex-direction: column;
  }
  .year-export-actions {
    justify-content: flex-start;
  }
}
</style>
