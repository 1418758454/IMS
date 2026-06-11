<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <h2 class="title">基本资料修改</h2>
      <el-divider></el-divider>

      <el-form 
        ref="profileFormRef" 
        :model="form" 
        :rules="formRules" 
        label-width="140px"
        size="default"
      >
        <!-- 一、基础信息 -->
        <el-form-item label="姓名" prop="name" required>
          <el-input v-model="form.name" placeholder="请输入真实姓名" max-length="50" />
        </el-form-item>

        <el-form-item label="性别" prop="gender" required>
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>

        <el-form-item label="出生年月" prop="birthDate" required>
          <el-date-picker
            v-model="form.birthDate"
            type="date"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            placeholder="请选择出生年月"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="籍贯" prop="nativePlace" required>
          <el-input v-model="form.nativePlace" placeholder="请输入籍贯（如：山东济南）" />
        </el-form-item>

        <el-form-item label="政治面貌" prop="politicalStatus" required>
          <el-select v-model="form.politicalStatus" placeholder="请选择政治面貌">
            <el-option label="中共党员" value="中共党员" />
            <el-option label="中共预备党员" value="中共预备党员" />
            <el-option label="共青团员" value="共青团员" />
            <el-option label="群众" value="群众" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <!-- 二、职业信息 -->
        <el-form-item label="职称" prop="title" required>
          <el-select v-model="form.title" placeholder="请选择职称">
            <el-option label="教授" value="教授" />
            <el-option label="副教授" value="副教授" />
            <el-option label="高级实验师" value="高级实验师" />
            <el-option label="实验师" value="实验师" />
            <el-option label="讲师" value="讲师" />
            <el-option label="助教" value="助教" />
          </el-select>
        </el-form-item>

        <el-form-item label="现有岗位" prop="currentPosition" required>
          <el-input v-model="form.currentPosition" placeholder="请输入当前岗位（如：数学系教师）" />
        </el-form-item>

        <el-form-item label="部门" prop="department" required>
          <el-select v-model="form.department" placeholder="请选择部门">
            <el-option label="应用数学" value="应用数学" />
            <el-option label="信息与计算科学" value="信息与计算科学" />
            <el-option label="统计" value="统计" />
            <el-option label="大学教学部" value="大学教学部" />
          </el-select>
        </el-form-item>

        <el-form-item label="职务" prop="position" required>
          <el-select v-model="form.position" placeholder="请选择职务">
            <el-option label="系主任" value="系主任" />
            <el-option label="系副主任" value="系副主任" />
            <el-option label="无" value="无" />
          </el-select>
        </el-form-item>

        <!-- 三、教育背景 -->
        <el-form-item label="本科毕业学校" prop="undergradSchool" required>
          <el-input v-model="form.undergradSchool" placeholder="请输入本科毕业院校全称" />
        </el-form-item>

        <el-form-item label="本科毕业时间" prop="undergradTime" required>
          <el-date-picker
            v-model="form.undergradTime"
            type="date"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            placeholder="请选择本科毕业时间"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="最高学历" prop="highestEducation" required>
          <el-select v-model="form.highestEducation" placeholder="请选择最高学历">
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="最高学历获得时间" prop="highestEducationTime" required>
          <el-date-picker
            v-model="form.highestEducationTime"
            type="date"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            placeholder="请选择最高学历获得时间"
            style="width: 100%"
          />
        </el-form-item>

        <!-- 四、联系方式 -->
        <el-form-item label="电话" prop="phone" required>
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>

        <el-form-item label="E-mail" prop="email" required>
          <el-input v-model="form.email" type="email" placeholder="请输入常用邮箱" />
        </el-form-item>

        <el-form-item label="微信" prop="wechat">
          <el-input v-model="form.wechat" placeholder="请输入微信号" />
        </el-form-item>

        <el-form-item label="QQ" prop="qq">
          <el-input v-model="form.qq" placeholder="请输入QQ号" />
        </el-form-item>

        <el-form-item label="个人网站" prop="personalWebsite">
          <el-input v-model="form.personalWebsite" placeholder="请输入个人网站URL" />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item style="margin-top: 30px; display: flex; justify-content: flex-end; gap: 20px;">
          <el-button type="primary" size="large" style="width: 180px;" @click="submitForm">
            保存修改
          </el-button>
          <el-button size="large" style="width: 150px;" @click="closeForm">
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { updateUserProfile } from "@/api/system/user"; // 假设接口支持更新所有字段

const { proxy } = getCurrentInstance();
const router = useRouter();
const props = defineProps({
  user: {
    type: Object,
    required: true
  }
});

// 表单数据（与注册表单字段对齐，移除密码相关字段）
const form = reactive({
  name: '',
  gender: '',
  birthDate: '',
  nativePlace: '',
  politicalStatus: '',
  title: '',
  department: '', 
  position: '', 
  currentPosition: '',
  undergradSchool: '',
  undergradTime: '',
  highestEducation: '',
  highestEducationTime: '',
  overseasExperience: '',
  researchDirection: '',
  phone: '',
  email: '',
  wechat: '',
  qq: '',
  personalWebsite: ''
});

// 表单验证规则（复用注册表单的验证逻辑）
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
  politicalStatus: [
    { required: true, message: '请选择政治面貌', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请选择职称', trigger: 'change' }
  ],
  department: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  position: [
    { required: true, message: '请选择职务', trigger: 'change' }
  ],
  currentPosition: [
    { required: true, message: '请输入现有岗位', trigger: 'blur' }
  ],
  undergradSchool: [
    { required: true, message: '请输入本科毕业学校', trigger: 'blur' }
  ],
  undergradTime: [
    { required: true, message: '请选择本科毕业时间', trigger: 'change' }
  ],
  highestEducation: [
    { required: true, message: '请选择最高学历', trigger: 'change' }
  ],
  highestEducationTime: [
    { required: true, message: '请选择最高学历获得时间', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
  // 其他字段验证规则可按需添加（如研究方向、海外经历等）
});

// 表单引用
const profileFormRef = ref(null);

// 提交修改
const submitForm = async () => {
  try {
    await profileFormRef.value.validate();
    // 调用更新接口（确保接口支持所有字段）
    const response = await updateUserProfile(form);
    if (response.code === 200) {
      ElMessage.success('资料修改成功');
      // 通知父组件更新用户信息
      emit('update:user', { ...props.user, ...form });
      closeForm();
    } else {
      ElMessage.error(response.msg || '修改失败，请重试');
    }
  } catch (error) {
    ElMessage.error('表单验证失败，请检查必填项');
  }
};

// 关闭表单
const closeForm = () => {
  // 根据实际场景选择关闭方式（如路由跳转或弹窗关闭）
  // router.back(); // 或调用父组件的关闭方法
  proxy.$tab.closePage();
};

// 监听用户数据变化，回显表单
watch(() => props.user, (user) => {
  if (user) {
    // 将用户数据赋值给表单（确保字段名与后端返回一致）
    Object.assign(form, {
      name: user.name || '',
      gender: user.gender || '',
      birthDate: user.birthDate || '',
      nativePlace: user.nativePlace || '',
      politicalStatus: user.politicalStatus || '',
      title: user.title || '',
      department: user.department || '', 
      position: user.position || '',
      currentPosition: user.currentPosition || '',
      undergradSchool: user.undergradSchool || '',
      undergradTime: user.undergradTime || '',
      highestEducation: user.highestEducation || '',
      highestEducationTime: user.highestEducationTime || '',
      overseasExperience: user.overseasExperience || '',
      researchDirection: user.researchDirection || '',
      phone: user.phone || '',
      email: user.email || '',
      wechat: user.wechat || '',
      qq: user.qq || '',
      personalWebsite: user.personalWebsite || ''
    });
  }
}, { immediate: true });

// 定义事件（用于通知父组件更新用户信息）
const emit = defineEmits(['update:user']);
</script>

<style scoped lang="scss">
.profile-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.profile-card {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

/* 统一表单样式与注册页面一致 */
:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-form-item__content) {
  margin-left: 0 !important;
}
</style>