<template>
  <div class="review-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>注册信息审核</h2>
      <div class="divider"></div>
    </div>

    <!-- 搜索栏（可选） -->
    <div class="search-bar">
      <el-input 
        v-model="searchParams.keyword" 
        placeholder="输入工号/姓名搜索" 
        style="width: 300px" 
        prefix-icon="Search"
      />
      <el-button type="primary" @click="fetchReviewList">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 待审核列表 -->
    <el-table 
      :data="tableData" 
      border 
      stripe 
      style="width: 100%" 
      v-loading="loading"
    >
      <el-table-column type="index" label="序号" width="70" align="center" />
      <el-table-column prop="id" label="id号" width="65" align="center"  v-if="false" />
      <el-table-column prop="userId" label="工号" width="130" align="center" />
      <el-table-column prop="name" label="姓名" width="90" align="center" />
      <el-table-column prop="gender" label="性别" width="70" align="center" />
      <!-- <el-table-column prop="birthDate" label="出生年月" width="105" align="center"  /> -->
      <el-table-column prop="politicalStatus" label="政治面貌" width="135" align="center" />
      <el-table-column prop="title" label="职称" width="120" align="center" />
      <el-table-column prop="department" label="部门" width="140" align="center" />
      <el-table-column prop="position" label="职务" width="120" align="center" />
      <!-- <el-table-column prop="currentPosition" label="现有岗位" width="140" align="center" /> -->
      <el-table-column prop="highestEducation" label="最高学历" width="100" align="center" />
      <el-table-column prop="phone" label="电话" width="140" align="center" />    
      <el-table-column prop="createTime" label="提交时间" width="200" align="center" />
      <el-table-column prop="status" label="状态" width="120" align="center">
        <template #default="scope">
          <el-tag :type="statusType[scope.row.status]">
            {{ statusText[scope.row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="225" align="center">
        <template #default="scope">
            <!-- 修改按钮 -->
            <el-button 
                type="primary" 
                size="small" 
                @click="handleEdit(scope.row)" 
                :disabled="scope.row.status !== 0" 
            >
                修改
            </el-button>

            <el-button 
                type="success" 
                size="small" 
                @click="handlePass(scope.row)"
                :disabled="scope.row.status !== 0"
            >
                通过
            </el-button>
            <el-button 
                type="danger" 
                size="small" 
                @click="handleReject(scope.row)"
                :disabled="scope.row.status !== 0"
            >
                拒绝
            </el-button>

        </template>
      </el-table-column>
    </el-table>

    <!-- 分页控件 -->
    <div class="pagination">
      <el-pagination 
        :current-page="pagination.currentPage"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 拒绝原因弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="400px">
      <el-input 
        v-model="rejectReason" 
        type="textarea" 
        rows="3" 
        placeholder="请输入拒绝原因（选填）"
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 新增修改弹窗表单 -->
    <el-dialog v-model="editDialogVisible" title="修改注册信息" width="600px">
        <el-form 
        ref="editFormRef" 
        :model="editForm" 
        :rules="editRules" 
        label-width="120px"
        >
        <!-- 工号 -->
        <el-form-item label="工号" prop="userId">
            <el-input v-model="editForm.userId" disabled placeholder="工号不可修改" />
            <!-- <el-input v-model="editForm.userId"  placeholder="请输入工号" /> -->

        </el-form-item>

        <!-- 姓名 -->
        <el-form-item label="姓名" prop="name">
            <el-input v-model="editForm.name" placeholder="请输入姓名" />
        </el-form-item>

        <!-- 性别 -->
        <el-form-item label="性别" prop="gender">
            <el-select v-model="editForm.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            </el-select>
        </el-form-item>



        <!-- 政治面貌 -->
        <el-form-item label="政治面貌" prop="politicalStatus">
            <el-select v-model="editForm.politicalStatus" placeholder="请选择政治面貌">
            <el-option label="中共党员" value="中共党员" />
            <el-option label="中共预备党员" value="中共预备党员" />
            <el-option label="共青团员" value="共青团员" />
            <el-option label="群众" value="群众" />
            <el-option label="其他" value="其他" />
            </el-select>
        </el-form-item>

        <!-- 职称 -->
        <el-form-item label="职称" prop="title">
            <el-select v-model="editForm.title" placeholder="请选择职称">
            <el-option label="教授" value="教授" />
            <el-option label="副教授" value="副教授" />
            <el-option label="高级实验师" value="高级实验师" />
            <el-option label="实验师" value="实验师" />
            <el-option label="讲师" value="讲师" />
            <el-option label="助教" value="助教" />
            </el-select>
        </el-form-item>

        <!-- 部门 -->
        <el-form-item label="部门" prop="department">
          <el-select v-model="editForm.department" placeholder="请选择部门">
            <el-option label="应用数学" value="应用数学" />
            <el-option label="信息与计算科学" value="信息与计算科学" />
            <el-option label="统计" value="统计" />
            <el-option label="大学教学部" value="大学教学部" />
          </el-select>
        </el-form-item>

        <!-- 职务 -->
        <el-form-item label="职务" prop="position">
          <el-select v-model="editForm.position" placeholder="请选择职务">
            <el-option label="系主任" value="系主任" />
            <el-option label="系副主任" value="系副主任" />
            <el-option label="无" value="无" />
          </el-select>
        </el-form-item>

        <!-- 现有岗位 -->
        <el-form-item label="现有岗位" prop="currentPosition">
            <el-input v-model="editForm.currentPosition" placeholder="请输入现有岗位" />
        </el-form-item>

        <!-- 最高学历 -->
        <el-form-item label="最高学历" prop="highestEducation">
            <el-select v-model="editForm.highestEducation" placeholder="请选择最高学历">
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
            </el-select>
        </el-form-item>

        <el-form-item label="本科毕业学校" prop="undergradSchool">
            <el-input v-model="editForm.undergradSchool" placeholder="请输入本科毕业院校全称" />
        </el-form-item>

        <el-form-item label="本科毕业时间" prop="undergradTime">
            <el-date-picker
            v-model="editForm.undergradTime"
            type="date"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            placeholder="请选择本科毕业时间"
            style="width: 100%"
            />
        </el-form-item>

        <template v-if="showEditMasterEducation">
            <el-form-item label="硕士毕业学校" prop="masterSchool">
            <el-input v-model="editForm.masterSchool" placeholder="请输入硕士毕业院校全称" />
            </el-form-item>

            <el-form-item label="硕士毕业时间" prop="masterTime">
            <el-date-picker
                v-model="editForm.masterTime"
                type="date"
                format="YYYY年MM月DD日"
                value-format="YYYY-MM-DD"
                placeholder="请选择硕士毕业时间"
                style="width: 100%"
            />
            </el-form-item>
        </template>

        <template v-if="showEditDoctorEducation">
            <el-form-item label="博士毕业学校" prop="doctorSchool">
            <el-input v-model="editForm.doctorSchool" placeholder="请输入博士毕业院校全称" />
            </el-form-item>

            <el-form-item label="博士毕业时间" prop="doctorTime">
            <el-date-picker
                v-model="editForm.doctorTime"
                type="date"
                format="YYYY年MM月DD日"
                value-format="YYYY-MM-DD"
                placeholder="请选择博士毕业时间"
                style="width: 100%"
            />
            </el-form-item>

            <el-form-item label="博士后经历" prop="postdoctoralExperience">
            <el-input
                v-model="editForm.postdoctoralExperience"
                type="textarea"
                rows="2"
                placeholder="如有，请填写博士后单位及时间"
            />
            </el-form-item>
        </template>

        <!-- 电话 -->
        <el-form-item label="电话" prop="phone">
            <el-input v-model="editForm.phone" placeholder="请输入手机号码" />
        </el-form-item>

        </el-form>

        <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit">确定修改</el-button>
        </template>
    </el-dialog>

  </div>
</template>
 
<script setup>
import { computed, ref, onMounted, reactive } from 'vue';
import { getReviewList, reviewRegistration, updateRegistration  } from '@/api/register.js'; // 后端接口
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  hasDoctorEducation,
  hasMasterEducation,
  normalizeEducationPayload
} from '@/utils/educationBackground.js';

const editDialogVisible = ref(false); // 修改弹窗显示状态
const editFormRef = ref(null); // 表单引用
const editForm = reactive({}); // 修改表单数据
const currentEditRow = ref(null); // 当前修改的行数据
// 修改表单验证规则（与注册页面保持一致）
const editRules = reactive({
  userId: [{ required: true, message: '工号不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthDate: [{ required: true, message: '请选择出生年月', trigger: 'change' }],
  politicalStatus: [{ required: true, message: '请选择政治面貌', trigger: 'change' }],
  department: [{ required: true, message: '请选择部门', trigger: 'change' }],
  position: [{ required: true, message: '请选择职务', trigger: 'change' }],
  currentPosition: [{ required: true, message: '请输入现有岗位', trigger: 'blur' }],
  highestEducation: [{ required: true, message: '请选择最高学历', trigger: 'change' }],
  undergradSchool: [{ required: true, message: '请输入本科毕业学校', trigger: 'blur' }],
  undergradTime: [{ required: true, message: '请选择本科毕业时间', trigger: 'change' }],
  masterSchool: [
    {
      validator: (rule, value, callback) => {
        if (hasMasterEducation(editForm.highestEducation) && !value) {
          callback(new Error('请输入硕士毕业学校'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  masterTime: [
    {
      validator: (rule, value, callback) => {
        if (hasMasterEducation(editForm.highestEducation) && !value) {
          callback(new Error('请选择硕士毕业时间'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ],
  doctorSchool: [
    {
      validator: (rule, value, callback) => {
        if (hasDoctorEducation(editForm.highestEducation) && !value) {
          callback(new Error('请输入博士毕业学校'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  doctorTime: [
    {
      validator: (rule, value, callback) => {
        if (hasDoctorEducation(editForm.highestEducation) && !value) {
          callback(new Error('请选择博士毕业时间'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});
const searchParams = reactive({
  keyword: '' // 搜索关键词（工号/姓名）
});

// 状态映射（0-待审核，1-通过，2-拒绝）
const statusText = { 0: '待审核', 1: '已通过', 2: '已拒绝' };
const statusType = { 0: 'info', 1: 'success', 2: 'danger' };
const showEditMasterEducation = computed(() => hasMasterEducation(editForm.highestEducation));
const showEditDoctorEducation = computed(() => hasDoctorEducation(editForm.highestEducation));

// 拒绝弹窗相关
const rejectDialogVisible = ref(false);
const rejectReason = ref('');
let currentRow = ref(null); // 当前操作的行数据


// 获取待审核列表
const fetchReviewList  = async () => {
  loading.value = true;
  try {
    const res = await getReviewList({
      status: 0, // 只查待审核
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      keyword: searchParams.keyword
    });
    console.log(res.data);
    tableData.value = res.data.records;
    pagination.total = res.data.total;
  } catch (error) {
    console.error('获取列表失败', error);
  } finally {
    loading.value = false;
  }
};

// ======== 修改事件处理 ========
// 打开修改弹窗并填充数据
const handleEdit = (row) => {
  console.log('修改行数据', row)
  currentEditRow.value = row; // 暂存当前行数据
  // 复制行数据到表单（避免直接修改表格数据）
  Object.assign(editForm, { ...row }); 
  editDialogVisible.value = true; // 显示弹窗
};
 
// 提交修改
const handleEditSubmit = async () => {
  // 表单验证
  await editFormRef.value.validate();
 
  try {
    // 调用后端修改接口）
    const response = await updateRegistration(normalizeEducationPayload(editForm));
    if (response.code === 200) {
      ElMessage.success('修改成功');
      editDialogVisible.value = false; // 关闭弹窗
      fetchReviewList(); // 刷新列表
    } else {
      ElMessage.error(response.msg || '修改失败');
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '修改失败：网络异常');
  }
};

// 审核通过
const handlePass = async (row) => {
  console.log('审核',row.userId)
  try {
     const response = await reviewRegistration({
      id: row.id, 
      status: 1 
    });
    console.log('返回数据',response)
    if (response.code === 200) { 
      ElMessage.success('审核通过');
      fetchReviewList(); // 刷新列表
    } else {
      // 处理业务失败（如 code=500，后端返回具体错误）
      ElMessage.error(response.msg || '审核失败：业务处理异常');
    }
  } catch (error) {
    // 3. 处理网络错误或 HTTP 状态码非 200 的情况
    ElMessage.error(error.response?.data?.msg || '审核失败：网络异常');
  }

};

// 审核拒绝（打开弹窗）
const handleReject = (row) => {
  ElMessageBox.confirm(
    `确定要拒绝工号为 ${row.userId} 的注册申请吗？`,
    '确认拒绝',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    // 用户点击确定，执行拒绝操作
    try {
      const response = await reviewRegistration({
        id: row.id,
        status: 2, // 拒绝状态
      });
      if (response.code === 200) { 
        ElMessage.success('已拒绝');
        fetchReviewList(); // 刷新列表
      } else {
        ElMessage.error(response.msg || '操作失败');
      }
    } catch (error) {
      ElMessage.error(error.msg || '操作失败');
    }
  }).catch(() => {
    // 用户点击取消或关闭弹窗
    ElMessage.info('已取消操作');
  });
};

// 确认拒绝
const confirmReject = async () => {
  try {
    const response = await reviewRegistration({
      id: currentRow.value.userId,
      status: 2, // 拒绝状态
      rejectReason: rejectReason.value
    });
    if (response.code === 200) { 
      ElMessage.success('已拒绝');
      rejectDialogVisible.value = false;
      fetchReviewList(); // 刷新列表
    }
    // ElMessage.success('已拒绝');
    // rejectDialogVisible.value = false;
    // fetchReviewList(); // 刷新列表
  } catch (error) {
    ElMessage.error(error.msg || '操作失败');
  }
};

// 分页相关
const handleSizeChange = (pageSize) => {
  pagination.pageSize = pageSize;
   fetchReviewList();
};
const handleCurrentChange = (currentPage) => {
  pagination.currentPage = currentPage;
   fetchReviewList();
};

// 搜索和重置
const resetSearch = () => {
  searchParams.keyword = '';
   fetchReviewList();
};

// 页面加载时获取数据
onMounted(() => {
   fetchReviewList();
});
</script>

<style scoped>
.review-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}
.page-header {
  margin-bottom: 20px;
}
.divider {
  width: 80px;
  height: 3px;
  background: #409eff;
  margin-top: 10px;
}
.search-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  align-items: center;
}
.pagination {
  margin-top: 20px;
  text-align: right;
}
/* 增加表格行高 */
:deep(.el-table__row) {
  height: 80px !important; /* 行高默认约 40px */
}
/* 增大字体大小 */
:deep(.el-table__cell) {
  font-size: 15px !important; /* 字体默认 14px */
}
</style>
