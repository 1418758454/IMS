<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="title">用户注册</h2>
      <el-divider></el-divider>

      <el-form 
        ref="registerFormRef" 
        :model="formData" 
        :rules="formRules" 
        label-width="140px"
        size="default"
      >
        <!-- 一、基础信息 -->
        <el-form-item label="姓名" prop="name" required>
          <el-input v-model="formData.name" placeholder="请输入真实姓名" max-length="50" />
        </el-form-item>

        <el-form-item label="性别" prop="gender" required>
          <el-select v-model="formData.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>

        <el-form-item label="出生年月" prop="birthDate" required>
          <el-date-picker
            v-model="formData.birthDate"
            type="date"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            placeholder="请选择出生年月"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="籍贯" prop="nativePlace" required>
          <el-input v-model="formData.nativePlace" placeholder="请输入籍贯（如：山东济南）" />
        </el-form-item>

        <el-form-item label="政治面貌" prop="politicalStatus" required>
          <el-select v-model="formData.politicalStatus" placeholder="请选择政治面貌">
            <el-option label="中共党员" value="中共党员" />
            <el-option label="中共预备党员" value="中共预备党员" />
            <el-option label="共青团员" value="共青团员" />
            <el-option label="群众" value="群众" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <!-- 二、职业信息 -->
        <el-form-item label="职称" prop="title">
          <!-- <el-input v-model="formData.title" placeholder="请输入职称（如：教授、工程师）" /> -->
           <el-select v-model="formData.title" placeholder="请选择职称">
            <el-option label="教授" value="教授" />
            <el-option label="副教授" value="副教授" />
            <el-option label="高级实验师" value="高级实验师" />
            <el-option label="实验师" value="实验师" />
            <el-option label="讲师" value="讲师" />
            <el-option label="助教" value="助教" />
          </el-select>
        </el-form-item>

        <el-form-item label="部门" prop="department">
          <el-select v-model="formData.department" placeholder="请选择部门">
            <el-option label="应用数学" value="应用数学" />
            <el-option label="信息与计算科学" value="信息与计算科学" />
            <el-option label="统计" value="统计" />
            <el-option label="大学教学部" value="大学教学部" />
          </el-select>
        </el-form-item>

        <el-form-item label="职务" prop="position">
          <el-select v-model="formData.position" placeholder="请选择职务">
            <el-option label="系主任" value="系主任" />
            <el-option label="系副主任" value="系副主任" />
            <el-option label="无" value="无" />
          </el-select>
        </el-form-item>

        <el-form-item label="现有岗位" prop="currentPosition" required>
          <el-input v-model="formData.currentPosition" placeholder="请输入当前岗位（如：数学系教师）" />
        </el-form-item>

        <!-- 三、教育背景 -->
        <el-form-item label="最高学历" prop="highestEducation" required>
          <el-select v-model="formData.highestEducation" placeholder="请选择最高学历">
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
        </el-form-item>

        <el-form-item label="本科毕业学校" prop="undergradSchool" required>
          <el-input v-model="formData.undergradSchool" placeholder="请输入本科毕业院校全称" />
        </el-form-item>

        <el-form-item label="本科毕业时间" prop="undergradTime" required>
          <el-date-picker
            v-model="formData.undergradTime"
            type="date"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            placeholder="请选择本科毕业时间"
            style="width: 100%"
          />
        </el-form-item>

        <template v-if="showMasterEducation">
          <el-form-item label="硕士毕业学校" prop="masterSchool" required>
            <el-input v-model="formData.masterSchool" placeholder="请输入硕士毕业院校全称" />
          </el-form-item>

          <el-form-item label="硕士毕业时间" prop="masterTime" required>
            <el-date-picker
              v-model="formData.masterTime"
              type="date"
              format="YYYY年MM月DD日"
              value-format="YYYY-MM-DD"
              placeholder="请选择硕士毕业时间"
              style="width: 100%"
            />
          </el-form-item>
        </template>

        <template v-if="showDoctorEducation">
          <el-form-item label="博士毕业学校" prop="doctorSchool" required>
            <el-input v-model="formData.doctorSchool" placeholder="请输入博士毕业院校全称" />
          </el-form-item>

          <el-form-item label="博士毕业时间" prop="doctorTime" required>
            <el-date-picker
              v-model="formData.doctorTime"
              type="date"
              format="YYYY年MM月DD日"
              value-format="YYYY-MM-DD"
              placeholder="请选择博士毕业时间"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="博士后经历" prop="postdoctoralExperience">
            <el-input
              v-model="formData.postdoctoralExperience"
              type="textarea"
              rows="2"
              placeholder="如有，请填写博士后单位及时间"
            />
          </el-form-item>
        </template>

        <!-- 四、研究与经历 -->
        <el-form-item label="出国访学经历" prop="overseasExperience">
          <el-input 
            v-model="formData.overseasExperience" 
            type="textarea" 
            rows="2" 
            placeholder="如有，请填写访学国家、机构及时间（如：2020.01-2021.01 美国哈佛大学访学）" 
          />
        </el-form-item>

        <el-form-item label="主要研究方向" prop="researchDirection" required>
          <el-input 
            v-model="formData.researchDirection" 
            placeholder="请输入主要研究方向（如：应用数学、计算数学）" 
          />
        </el-form-item>

        <!-- 五、联系方式 -->
        <el-form-item label="电话" prop="phone" required>
          <el-input v-model="formData.phone" placeholder="请输入手机号码" />
        </el-form-item>

        <el-form-item label="E-mail" prop="email" required>
          <el-input v-model="formData.email" type="email" placeholder="请输入常用邮箱" />
        </el-form-item>

        <el-form-item label="微信" prop="wechat">
          <el-input v-model="formData.wechat" placeholder="请输入微信号" />
        </el-form-item>

        <el-form-item label="QQ" prop="qq">
          <el-input v-model="formData.qq" placeholder="请输入QQ号" />
        </el-form-item>

        <el-form-item label="个人网站" prop="personalWebsite">
          <el-input v-model="formData.personalWebsite" placeholder="请输入个人网站URL（可选）" />
        </el-form-item>

        <!-- 六、注册信息（账号密码） -->
        <el-form-item label="登录工号" prop="userId" required>
          <el-input v-model="formData.userId" placeholder="请输入工号" />
        </el-form-item>

        <el-form-item label="登录密码" prop="password" required>
          <el-input 
            v-model="formData.password" 
            type="password" 
            placeholder="请设置密码（至少8位，包含字母和数字）" 
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword" required>
          <el-input 
            v-model="formData.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
          />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item style="margin-top: 30px; display: flex; justify-content: flex-end; gap: 20px; width: calc(100% - 350px); margin-left: auto;">
          <el-button type="primary" size="large" style="width: 180px;" @click="submitForm">
            提交注册
          </el-button>
          <el-button size="large" style="width: 150px;" @click="resetForm">
            重置表单
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage ,ElMessageBox} from 'element-plus';
import { registerUser } from '@/api/register.js'; // 导入注册接口
import {
  hasDoctorEducation,
  hasMasterEducation,
  normalizeEducationPayload
} from '@/utils/educationBackground.js';

// 路由实例
const router = useRouter();

// 表单数据
const formData = reactive({
  // 基础信息
  name: '',
  gender: '',
  birthDate: '',
  nativePlace: '',
  politicalStatus: '',
  // 职业信息
  title: '',
  department: '',
  position: '无', // 默认为无
  currentPosition: '',
  // 教育背景
  undergradSchool: '',
  undergradTime: '',
  masterSchool: '',
  masterTime: '',
  doctorSchool: '',
  doctorTime: '',
  postdoctoralExperience: '',
  highestEducation: '',
  highestEducationTime: '',
  // 研究与经历
  overseasExperience: '',
  researchDirection: '',
  // 联系方式
  phone: '',
  email: '',
  wechat: '',
  qq: '',
  personalWebsite: '',
  // 注册信息
  userId: '',
  password: '',
  confirmPassword: ''
});

// 表单验证规则
const formRules = reactive({
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthDate: [
    { required: true, message: '请选择出生年月', trigger: 'change' }
  ],
  nativePlace: [
    { required: true, message: '请输入籍贯', trigger: 'blur' }
  ],
  /** 政治面貌 */
  politicalStatus: [
    { required: true, message: '请选择政治面貌', trigger: 'change' }
  ],
  /** 职称  */
  title: [
    { required: true, message: '请选择职称', trigger: 'blur' }
  ],
  /** 部门 */
  department: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  /** 职务 */
  position: [
    { required: true, message: '请选择职务', trigger: 'change' }
  ],
  /** 现有岗位 */
  currentPosition: [
    { required: true, message: '请输入现有岗位', trigger: 'blur' }
  ],
  /**本科毕业学校 */
  undergradSchool: [
    { required: true, message: '请输入本科毕业学校', trigger: 'blur' }
  ],
  /**本科毕业时间 */
  undergradTime: [
    { required: true, message: '请输入本科毕业时间', trigger: 'blur' }
  ],
  masterSchool: [
    {
      validator: (rule, value, callback) => {
        if (hasMasterEducation(formData.highestEducation) && !value) {
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
        if (hasMasterEducation(formData.highestEducation) && !value) {
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
        if (hasDoctorEducation(formData.highestEducation) && !value) {
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
        if (hasDoctorEducation(formData.highestEducation) && !value) {
          callback(new Error('请选择博士毕业时间'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ],
  /**最高学历 */
  highestEducation: [
    { required: true, message: '请选择最高学历', trigger: 'blur' }
  ],
  /**主要研究方向 */
  researchDirection: [
    { required: true, message: '请输入主要研究方向', trigger: 'blur' }
  ],

  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  userId: [
    { required: true, message: '请设置登录账号', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: '账号只能包含字母、数字和下划线（4-20位）', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 8, message: '密码长度不能少于8位', trigger: 'blur' },
    { pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== formData.password) {
          callback(new Error('两次密码输入不一致'));
        } else {
          callback();
        }
      }, 
      trigger: 'blur' 
    }
  ]
  // 其他字段验证规则可按需添加（如学历、毕业时间等）
});

// 表单引用
const registerFormRef = ref(null);
const showMasterEducation = computed(() => hasMasterEducation(formData.highestEducation));
const showDoctorEducation = computed(() => hasDoctorEducation(formData.highestEducation));

// };
const submitForm = async () => {
  await registerFormRef.value.validate();
  const submitData = normalizeEducationPayload(formData);
  delete submitData.confirmPassword;
  console.log(submitData);
  const response = await registerUser(submitData);
  if (response.code === 200) {
    ElMessageBox.alert('注册申请已提交，等待管理员审核', '注册成功', {
      confirmButtonText: '确定',
      type: 'success',
      callback: () => {
        // 清空表单（可选）
        registerFormRef.value.resetFields();
        router.push('/login');
      }
    });
  } else {
    ElMessage.error(response.msg);
  }
};

// 重置表单
const resetForm = () => {
  registerFormRef.value.resetFields();
};
</script>

<style scoped lang="scss">
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url("../assets/images/login-background.jpg"); // 复用登录页背景图
  background-size: cover;
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 800px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

:deep(.el-form-item__content) {
  margin-left: 0 !important; // 修复label-width导致的布局偏移
}
</style>
