<template>
  <div class="teaching-performance business-table-scroll-scope">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="main-title">教学业绩</h2>
      <el-divider></el-divider>
    </div>

    <!-- 年度工作量汇总卡片 -->
    <!-- <el-card class="module-card annual-workload-card">
      <div class="module-header" style="margin-bottom: 15px;">
        <div style="display: flex; align-items: center; gap: 15px;">
          <h3 class="module-title">年度工作量汇总</h3>
          <el-select 
            v-model="selectedYear" 
            placeholder="选择年份" 
            style="width: 150px;"
            @change="handleYearChange"
          >
            <el-option v-for="year in years" :key="year" :label="year" :value="year"></el-option>
          </el-select>
        </div>
      </div>
      <div class="summary-content" style="text-align: right; padding: 10px 0;">
        <span class="summary-label">总工作量：</span>
        <span class="summary-value" style="font-size: 18px; color: #1890ff;">
          {{ calculateTotalWorkload() }}
        </span>
      </div>
    </el-card> -->
    <!-- 年度工作量汇总卡片（修改后） -->
    <el-card class="module-card annual-workload-card">
      <div class="module-header" style="margin-bottom: 15px;">
        <div style="display: flex; align-items: center; gap: 15px;">
          <h3 class="module-title">已确认年度工作量汇总</h3>
          <el-select 
            v-model="selectedYear" 
            placeholder="选择年份" 
            style="width: 150px;"
            @change="handleYearChange"
          >
            <el-option v-for="year in years" :key="year" :label="year" :value="year"></el-option>
          </el-select>
        </div>
      </div>

      <!-- 汇总表格（复用科研模块样式，数据来自后端汇总表） -->
      <el-table 
        :data="yearWorkloadTableData" 
        class="summary-table" 
        border 
        stripe
        v-loading="loading"
      >
        <!-- 年份列 -->
        <el-table-column 
          prop="year" 
          label="年份" 
          align="center" 
          width="100"
        >
          <template #default="scope">
            <span class="total-year">{{ scope.row.year }}</span>
          </template>
        </el-table-column>

        <!-- 各模块工作量列（按教学模块顺序排列） -->
        <!-- 各模块工作量列（按教学模块顺序排列，显示已确认工作量） -->
        <el-table-column prop="theoryCourseConfirmedWorkload" label="本科理论课" align="center">
          <template #default="scope">
            {{ Number(scope.row.theoryCourseConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="experimentCourseConfirmedWorkload" label="本科实验课" align="center">
          <template #default="scope">
            {{ Number(scope.row.experimentCourseConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="practicalTeachingConfirmedWorkload" label="实践教学" align="center">
          <template #default="scope">
            {{ Number(scope.row.experimentCourseConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="thesisCourseConfirmedWorkload" label="毕业论文" align="center">
          <template #default="scope">
            {{ Number(scope.row.thesisCourseConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="scienceInnovationConfirmedWorkload" label="科技创新" align="center">
          <template #default="scope">
            {{ Number(scope.row.scienceInnovationConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="competitionConfirmedWorkload" label="学科竞赛" align="center">
          <template #default="scope">
            {{ Number(scope.row.competitionConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="textbookConfirmedWorkload" label="出版教材" align="center">
          <template #default="scope">
            {{ Number(scope.row.textbookConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="educationReformConfirmedWorkload" label="教改项目" align="center">
          <template #default="scope">
            {{ Number(scope.row.educationReformConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="educationReformPaperConfirmedWorkload" label="教改论文" align="center">
          <template #default="scope">
            {{ Number(scope.row.educationReformPaperConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="proctorConfirmedWorkload" label="监考" align="center">
          <template #default="scope">
            {{ Number(scope.row.proctorConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="graduateTheoryCourseConfirmedWorkload" label="研究生理论课" align="center">
          <template #default="scope">
            {{ Number(scope.row.graduateTheoryCourseConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>
        <el-table-column prop="graduateGuideStudentConfirmedWorkload" label="研究生指导学生" align="center">
          <template #default="scope">
            {{ Number(scope.row.graduateGuideStudentConfirmedWorkload || 0).toFixed(3) }}
          </template>
        </el-table-column>

        <!-- 总工作量列（高亮显示） -->
        <el-table-column 
          prop="totalWorkload" 
          label="总工作量" 
          align="center" 
          width="120"
        >
          <template #default="scope">
            <!-- <span class="total-workload">{{ scope.row.totalWorkload.toFixed(3) }}</span> -->
             <span class="total-workload">{{ scope.row.totalWorkload.toFixed(3) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 理论课子组件 -->
    <UndergraduateTheory 
        :data="moduleData.undergraduateTheory" 
        :year="selectedYear"
        :confirmed-workload="moduleWorkload.undergraduateTheory.confirmed"
        :estimated-workload="moduleWorkload.undergraduateTheory.estimated"
        module-key="undergraduateTheory"
        mode="show"
        @save-row="handleSaveRow"
        @delete-row="handleDeleteRow"
    />

    <!-- 实验课子组件 -->
    <UndergraduateExperiment 
        :data="moduleData.undergraduateExperiment" 
        :year="selectedYear"
        :confirmed-workload="moduleWorkload.undergraduateExperiment.confirmed"
        :estimated-workload="moduleWorkload.undergraduateExperiment.estimated"
        module-key="undergraduateExperiment"
        mode="show"  
        @save-row="handleSaveRow"
        @delete-row="handleDeleteRow"
    />

    <!-- 实践教学子组件 -->
    <UndergraduatePractice 
        :data="moduleData.practiceTeaching" 
        :year="selectedYear"
        :confirmed-workload="moduleWorkload.practiceTeaching.confirmed"
        :estimated-workload="moduleWorkload.practiceTeaching.estimated"
        module-key="practiceTeaching"
        mode="show"  
        @save-row="handleSaveRow"
        @delete-row="handleDeleteRow"
    />

    <!-- 毕业论文子组件 -->
    <UndergraduateThesis 
        :data="moduleData.undergraduateThesis" 
        :year="selectedYear"
        :confirmed-workload="moduleWorkload.undergraduateThesis.confirmed"
        :estimated-workload="moduleWorkload.undergraduateThesis.estimated"
        module-key="undergraduateThesis"
        mode="show"
        @save-row="handleSaveRow"
        @delete-row="handleDeleteRow"
    />

    <!-- 科技创新子组件 -->
    <ScienceInnovation 
        :data="moduleData.scienceInnovation"  
        :year="selectedYear"
        :confirmed-workload="moduleWorkload.scienceInnovation.confirmed"
        :estimated-workload="moduleWorkload.scienceInnovation.estimated"
        module-key="scienceInnovation"       
        mode="show"  
        @save-row="handleSaveRow"             
        @delete-row="handleDeleteRow"     
    />

    <!-- 学科竞赛子组件 -->
    <CompetitionModule 
        :data="moduleData.competition"  
        :year="selectedYear"
        :confirmed-workload="moduleWorkload.competition.confirmed"
        :estimated-workload="moduleWorkload.competition.estimated"
        module-key="competition"      
        mode="show"  
        @save-row="handleSaveRow"       
        @delete-row="handleDeleteRow"   
    />

    <!-- 出版教材子组件 -->
    <TextbookModule 
        :data="moduleData.textbook"  
        :year="selectedYear"
        :confirmed-workload="moduleWorkload.textbook.confirmed"
        :estimated-workload="moduleWorkload.textbook.estimated"
        module-key="textbook"        
        mode="show"  
        @save-row="handleSaveRow"    
        @delete-row="handleDeleteRow"
    />

    <!-- 教改项目子组件 -->
    <EducationReform 
        :data="moduleData.educationReform"  
        :year="selectedYear"
        :confirmed-workload="moduleWorkload.educationReform.confirmed"
        :estimated-workload="moduleWorkload.educationReform.estimated"
        module-key="educationReform"   
        mode="show"       
        @save-row="handleSaveRow"           
        @delete-row="handleDeleteRow"       
    />

    <!-- 教改论文子组件 -->
    <EducationReformPaper 
        :data="moduleData.educationReformPaper"  
        :year="selectedYear"
        :confirmed-workload="moduleWorkload.educationReformPaper.confirmed"
        :estimated-workload="moduleWorkload.educationReformPaper.estimated"
        module-key="educationReformPaper" 
        mode="show"         
        @save-row="handleSaveRow"                
        @delete-row="handleDeleteRow"            
    />

    <!-- 监考模块子组件 -->
    <ProctorModule 
        :data="moduleData.proctor"  
        :year="selectedYear"
        :confirmed-workload="moduleWorkload.proctor.confirmed"
        :estimated-workload="moduleWorkload.proctor.estimated"
        module-key="proctor"       
        mode="show"   
        @save-row="handleSaveRow"   
        @delete-row="handleDeleteRow" 
    />

    <!-- 研究生理论课子组件 -->
    <GraduateTheoryCourse 
      :data="moduleData.graduateTheory"  
      :year="selectedYear"
      :confirmed-workload="moduleWorkload.graduateTheory.confirmed"
      :estimated-workload="moduleWorkload.graduateTheory.estimated"
      module-key="graduateTheory"  
      mode="show"        
      @save-row="handleSaveRow"          
      @delete-row="handleDeleteRow"      
    />

    <!-- 研究生指导学生子组件 -->
    <GraduateGuideStudent 
      :data="moduleData.graduateGuideStudent"  
      :year="selectedYear"
      :confirmed-workload="moduleWorkload.graduateGuideStudent.confirmed"
      :estimated-workload="moduleWorkload.graduateGuideStudent.estimated"
      module-key="graduateGuideStudent" 
      mode="show"         
      @save-row="handleSaveRow"                
      @delete-row="handleDeleteRow"            
    />


  </div>

</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'; // 顶部添加导入
import { getTeachingTotalWorkload } from '@/api/teaching/teachingTotalWorkload';
// 1. 导入子组件和API（理论课）
import UndergraduateTheory from './UndergraduateTheory.vue';
import { 
  getTheoryCourseList, 
  addTheoryCourse, 
  updateTheoryCourse, 
  deleteTheoryCourse 
} from '@/api/teaching/theoryCourse'; // 导入JS文件中的API函数
// 2. 导入子组件和API（实验课）
import UndergraduateExperiment from './UndergraduateExperiment.vue';
import { 
  getExperimentCourseList, 
  addExperimentCourse, 
  updateExperimentCourse, 
  deleteExperimentCourse 
} from '@/api/teaching/experimentCourse';
// 3. 导入实践教学子组件
import UndergraduatePractice from './UndergraduatePractice.vue';
// 导入实践教学API（需确保后端接口已开发）
import { 
  getPracticeTeachingList, 
  addPracticeTeaching, 
  updatePracticeTeaching, 
  deletePracticeTeaching 
} from '@/api/teaching/practiceTeaching';
// 4. 导入毕业论文子组件和API
import UndergraduateThesis from './UndergraduateThesis.vue';
import { 
  getThesisList, 
  addThesis, 
  updateThesis, 
  deleteThesis 
} from '@/api/teaching/thesisCourse';
// 5. 导入科技创新子组件
import ScienceInnovation from './ScienceInnovation.vue'; 
// 新增：导入科技创新API（替换为实际后端接口）
import { 
  getScienceInnovationList,  // 列表查询接口
  addScienceInnovation,      // 新增接口
  updateScienceInnovation,   // 更新接口
  deleteScienceInnovation    // 删除接口
} from '@/api/teaching/scienceInnovation';  // 接口路径需与后端一致
// 6. 导入学科竞赛子组件
import CompetitionModule from './CompetitionModule.vue'; 
// 导入API接口（需提前创建competitionApi.js）
import { 
  getCompetitionList,   // 列表查询
  addCompetition,       // 新增
  updateCompetition,    // 更新
  deleteCompetition     // 删除
} from '@/api/teaching/competition';
// 7. 导入出版教材子组件
import TextbookModule from './Textbook.vue'; 
// 导入出版教材API（需提前创建textbookApi.js）
import { 
  getTextbookList,   // 列表查询接口
  addTextbook,       // 新增接口
  updateTextbook,    // 更新接口
  deleteTextbook     // 删除接口
} from '@/api/teaching/textbook';
// 8. 导入教改项目子组件
import EducationReform from './EducationReform.vue'; 
// 导入API（假设接口文件为educationReform.js）
import { 
  getEducationReformList,  // 列表查询接口
  addEducationReform,      // 新增接口
  updateEducationReform,   // 更新接口
  deleteEducationReform    // 删除接口
} from '@/api/teaching/educationReform';
// 9. 导入教改论文子组件
import EducationReformPaper from './EducationReformPaper.vue'; 
// 导入教改论文API（假设接口文件为educationReformPaper.js）
import { 
  getEducationReformPaperList,  // 列表查询接口
  addEducationReformPaper,      // 新增接口
  updateEducationReformPaper,   // 更新接口
  deleteEducationReformPaper    // 删除接口
} from '@/api/teaching/educationReformPaper';
// 9. 导入监考子组件
import ProctorModule from './Proctor.vue'; 
import { 
  getProctorList,   // 列表查询接口
  addProctor,       // 新增接口
  updateProctor,    // 更新接口
  deleteProctor     // 删除接口
} from '@/api/teaching/proctor';
// 10. 导入研究生理论课子组件
import GraduateTheoryCourse from './GraduateTheoryCourse.vue';
import { 
  getGraduateTheoryCourseList, 
  addGraduateTheoryCourse, 
  updateGraduateTheoryCourse, 
  deleteGraduateTheoryCourse 
} from '@/api/teaching/graduateTheoryCourse'; // 导入JS文件中的API函数
// 11. 导入指导学生子组件
import GraduateGuideStudent from './GraduateGuideStudent.vue'; 
// 导入指导学生API（graduateGuideStudent.js）
import { 
  getGraduateGuideStudentList,  // 列表查询接口
  addGraduateGuideStudent,      // 新增接口
  updateGraduateGuideStudent,   // 更新接口
  deleteGraduateGuideStudent    // 删除接口
} from '@/api/teaching/graduateGuideStudent';  // API路径需与实际文件位置一致



export default {
  components: { UndergraduateTheory , UndergraduateExperiment,UndergraduatePractice,UndergraduateThesis,ScienceInnovation,CompetitionModule,
                TextbookModule,EducationReform,EducationReformPaper,ProctorModule, GraduateTheoryCourse,GraduateGuideStudent},
  data() {
    return {
      selectedYear: new Date().getFullYear() - 1,
      years: [],
      // 3. 多模块数据统一管理（键为模块key，值为数据数组）
      moduleData: {
        undergraduateTheory: [], // 理论课数据
        undergraduateExperiment: [], // 实验课数据
        practiceTeaching: [], // 实践教学数据存储
        undergraduateThesis: [], // 毕业论文模块数据存储
        scienceInnovation: [], // 科技创新模块数据存储
        competition: [], // 学科竞赛模块数据存储
        textbook: [],
        educationReform: [],
        educationReformPaper: [],  // 存储教改论文模块数据
        proctor: [],
        graduateTheory: [],
        graduateGuideStudent: []

      },
      // 初始化 moduleWorkload 对象，为所有模块提供默认值
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
      yearWorkloadTableData: [], // 汇总表数据（存储后端返回的各模块工作量）
      loading: false // 表格加载状态
    };
  },
  mounted() {
    // 初始化加载当前年份数据（默认加载第一个模块）
    this.handleYearChange(this.selectedYear);
  },
  created() {
    // 生成年份范围：最近10年 到 未来2年（可自定义调整范围）
    this.generateYearOptions(5, 3); 

    // 扩展API映射表（核心：父组件通过module-key调用对应API）
    this.MODULE_API_MAP = {
        undergraduateTheory: {  // 理论课模块key
                listApi: getTheoryCourseList, // 列表查询API
                addApi: addTheoryCourse,      // 新增API
                updateApi: updateTheoryCourse,// 更新API
                deleteApi: deleteTheoryCourse // 删除API
        },
        undergraduateExperiment: {  // 实验课API映射
            listApi: getExperimentCourseList,
            addApi: addExperimentCourse,
            updateApi: updateExperimentCourse,
            deleteApi: deleteExperimentCourse
        },
        // 新增实践教学API映射
        practiceTeaching: {  
            listApi: getPracticeTeachingList, // 列表查询API
            addApi: addPracticeTeaching,      // 新增API
            updateApi: updatePracticeTeaching,// 更新API
            deleteApi: deletePracticeTeaching // 删除API
        },
        undergraduateThesis: {  // 毕业论文模块API映射
                listApi: getThesisList,
                addApi: addThesis,
                updateApi: updateThesis,
                deleteApi: deleteThesis
        },
        // 新增：科技创新模块API映射（键名必须与module-key一致）
        scienceInnovation: {  
            listApi: getScienceInnovationList,    // 列表查询接口
            addApi: addScienceInnovation,         // 新增接口
            updateApi: updateScienceInnovation,   // 更新接口
            deleteApi: deleteScienceInnovation    // 删除接口
        },
        // 新增学科竞赛模块API映射
        competition: {  
            listApi: getCompetitionList,    // 列表查询接口
            addApi: addCompetition,         // 新增接口
            updateApi: updateCompetition,   // 更新接口
            deleteApi: deleteCompetition    // 删除接口
        },
        // 新增：出版教材模块API映射
        textbook: {  
            listApi: getTextbookList,    // 列表查询接口
            addApi: addTextbook,         // 新增接口
            updateApi: updateTextbook,   // 更新接口
            deleteApi: deleteTextbook    // 删除接口
        },
        educationReform: {  // 模块标识需与module-key一致
            listApi: getEducationReformList,  // 列表查询接口
            addApi: addEducationReform,       // 新增接口
            updateApi: updateEducationReform, // 更新接口
            deleteApi: deleteEducationReform  // 删除接口
        },
        educationReformPaper: {  // 模块标识需与module-key一致
            listApi: getEducationReformPaperList,  // 列表查询接口
            addApi: addEducationReformPaper,       // 新增接口
            updateApi: updateEducationReformPaper, // 更新接口
            deleteApi: deleteEducationReformPaper  // 删除接口
        },
        proctor: {  // 键名必须与module-key="proctor"一致
            listApi: getProctorList,  // 对应proctor.js中的列表接口
            addApi: addProctor,       // 对应新增接口
            updateApi: updateProctor, // 对应更新接口
            deleteApi: deleteProctor  // 对应删除接口
        },
        graduateTheory: {  // 模块标识需与 module-key 一致
          listApi: getGraduateTheoryCourseList,  // 列表查询接口
          addApi: addGraduateTheoryCourse,       // 新增接口
          updateApi: updateGraduateTheoryCourse, // 更新接口
          deleteApi: deleteGraduateTheoryCourse  // 删除接口
        },
         graduateGuideStudent: {  // 模块标识需与module-key一致
          listApi: getGraduateGuideStudentList,  // 列表查询接口
          addApi: addGraduateGuideStudent,       // 新增接口
          updateApi: updateGraduateGuideStudent, // 更新接口
          deleteApi: deleteGraduateGuideStudent  // 删除接口
        }


    };
  },
  methods: {
    isCrossYearMessage(message) {
      return /\d{4}/.test(message || '') && /(页面查看|切换|选择|请选择)/.test(message || '');
    },
    showSaveResult(message) {
      if (this.isCrossYearMessage(message)) {
        return ElMessageBox.alert(message, '保存成功', {
          confirmButtonText: '我知道了',
          type: 'success',
          center: true,
          customClass: 'year-jump-message-box'
        });
      }
      this.$message.success(message);
      return Promise.resolve();
    },
    /**
     * 动态生成年份选项
     * @param {number} pastYears - 向前追溯的年数（如10表示包含当前年往前10年）
     * @param {number} futureYears - 向后扩展的年数（如2表示包含当前年往后2年）
     */
    generateYearOptions(pastYears, futureYears) {
      const currentYear = new Date().getFullYear();
      const startYear = currentYear - pastYears; // 起始年份（如当前2024-10=2014）
      const endYear = currentYear + futureYears; // 结束年份（如2024+2=2026）
      
      // 生成从 startYear 到 endYear 的连续年份数组
      this.years = Array.from(
        { length: endYear - startYear + 1 }, // 数组长度 = 年份数量
        (_, index) => startYear + index // 填充年份值
      );
    },
    async handleYearChange(year) {
      this.loading = true; // 新增：表格加载状态（需在data中定义loading: false）
      try {
        // 1. 加载所有模块明细数据（改用Promise.all控制异步顺序）
        const moduleKeys = Object.keys(this.MODULE_API_MAP);
        const modulePromises = moduleKeys.map(async (moduleKey) => {
          const { listApi } = this.MODULE_API_MAP[moduleKey];
          const response = await listApi({ pageNum: 1, pageSize: 1000, year, isAudit: false });
          this.moduleData[moduleKey] = response.data.records;
        });
        await Promise.all(modulePromises); // 等待所有明细数据加载完成
    
        // 2. 新增：加载后端汇总表数据
        await this.fetchTotalWorkload(year);
    
      } catch (error) {
        this.$message.error(`数据加载失败：${error.message}`);
      } finally {
        this.loading = false; // 关闭加载状态
      }
    },
    // 新增：从后端汇总表获取年度工作量数据
    async fetchTotalWorkload(year) {
      try {
        // const userId = SecurityUtils.getUsername(); // 获取当前用户ID（需确保SecurityUtils可用）
        const response = await getTeachingTotalWorkload({ year }); // 调用后端汇总表接口
        this.yearWorkloadTableData = [response.data]; // 表格数据需为数组格式    
        this.extractModuleWorkload(response.data);// 从后端返回的汇总数据中提取各模块工作量
      } catch (error) {
        this.$message.error(`汇总数据加载失败：${error.message || '请联系管理员'}`);
        this.yearWorkloadTableData = []; // 异常时清空表格数据
      }
    },
    // 新增：从后端汇总数据中提取各模块工作量
    extractModuleWorkload(summaryData) {
      // 从汇总数据中提取各模块的已确认和预计工作量
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

    /** 通用保存方法（支持新增/更新，适配所有模块） */
    async handleSaveRow(moduleKey, row) {
      const isUpdate = !!row.id;
      const { addApi, updateApi } = this.MODULE_API_MAP[moduleKey];
      const api = isUpdate ? updateApi : addApi;

      try {
        const response = await api({
          ...row,
          year: this.selectedYear,
        //   userId: this.$store.state.user.id // 当前用户ID（从全局状态获取）
        });

        

        // 更新模块数据
        const moduleData = this.moduleData[moduleKey];
        if (isUpdate) {
          const index = moduleData.findIndex(item => item.id === row.id);
          moduleData.splice(index, 1, response.data);
        } else {
          moduleData.push(response.data);
        }
        console.log(response.msg);
        // 2. 关键修复：保存成功后，重新调用当前模块的列表接口，通过后端筛选数据
         this.getModuleData(moduleKey, this.selectedYear);
         await this.fetchTotalWorkload(this.selectedYear);
        await this.showSaveResult(response.msg || (isUpdate ? '更新成功' : '新增成功'));
        return;
      } catch (error) {

        const errMsg = error.response?.data?.msg || "保存失败，请重试";
        this.$message.error(`${this.getModuleName(moduleKey)}保存失败：${errMsg}`);
      }
    },

    // 新增：封装模块数据刷新方法（复用handleYearChange中的接口调用逻辑）
    getModuleData(moduleKey, year) {
        const { listApi } = this.MODULE_API_MAP[moduleKey];
        listApi({ pageNum: 1, pageSize: 1000, year, isAudit: false })
            .then(response => {
            this.moduleData[moduleKey] = response.data.records; // 后端返回的仅为当前年份数据
            })
            .catch(error => {
            this.$message.error(`${this.getModuleName(moduleKey)}数据刷新失败：${error.message}`);
            });
    },

    /** 通用删除方法（适配所有模块） */
    async handleDeleteRow(moduleKey, rowId) {
      const { deleteApi } = this.MODULE_API_MAP[moduleKey];
      try {
        await deleteApi([rowId],{params: { year: this.selectedYear }}); // 调用批量删除API（单条也用数组格式）
        // 从模块数据中移除
        this.moduleData[moduleKey] = this.moduleData[moduleKey].filter(
          item => item.id !== rowId
        );
        await this.fetchTotalWorkload(this.selectedYear);
        this.$message.success('删除成功');
      } catch (error) {
        this.$message.error(`${this.getModuleName(moduleKey)}删除失败：${error.message}`);
      }
    },

    /** 计算总工作量（累加所有模块数据） */
    calculateTotalWorkload() {
        return Object.values(this.moduleData)
            .flat() // 合并所有模块数据
            .filter(row => row) // ✅ 过滤掉 undefined、null 等无效元素
            .reduce((sum, row) => {
            // 即使 row 有效，仍需确保 workload 存在（避免非数字值）
            const workload = row.workload ?? 0; // 若 workload 为 undefined/null，默认取 0
            return sum + Number(workload);
            }, 0)
            .toFixed(3);
    },

    /** 获取模块名称（用于错误提示） */
    getModuleName(moduleKey) {
      const nameMap = {
        undergraduateTheory: '本科理论课',
        undergraduateExperiment: '本科实验课',
        undergraduatePractice: '本科教学实践',
        undergraduateThesis: '本科毕业论文指导',
        scienceInnovation: '科技创新',
        competition: '学科竞赛',
        textbook: '出版教材',
        educationReform: '教改项目',
        educationReformPaper: '教改论文',
        proctor: '监考模块',
        graduateTheory: '研究生理论课',
        graduateGuideStudent: '研究生指导学生'
        
      };
      return nameMap[moduleKey] || moduleKey;
    }
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

</style>
