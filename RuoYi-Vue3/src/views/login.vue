<template>
  <div class="login">
    <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">数学系信息管理系统</h3>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          size="large"
          auto-complete="off"
          placeholder="工号"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          size="large"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="large"
          type="primary"
          style="width:100%;"
          @click.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <div style="float: right; margin-top: 10px;">
          <el-button
            type="text"
            @click="showResetPasswordDialog"
            style="padding: 0; margin-right: 15px;"
          >
            忘记密码
          </el-button>
          <router-link 
            v-if="register" 
            class="link-type" 
            :to="'/register'"
            style="line-height: 32px;"
          >
            立即注册
          </router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2018-2026 byTrueTown</span>
    </div>

    <!-- 重置密码对话框 -->
  <el-dialog 
    v-model="resetPasswordDialogVisible" 
    title="重置密码" 
    width="400px"
    :close-on-click-modal="false"
  >
    <el-form 
      ref="resetPasswordRef" 
      :model="resetPasswordForm" 
      :rules="resetPasswordRules" 
      label-width="80px"
    >
      <el-form-item label="工号" prop="username">
        <el-input v-model="resetPasswordForm.username" placeholder="请输入工号" />
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="resetPasswordForm.name" placeholder="请输入姓名" />
      </el-form-item>      
      <el-form-item label="新密码" prop="newPassword">
        <el-input 
          v-model="resetPasswordForm.newPassword" 
          type="password" 
          placeholder="请输入新密码" 
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="resetPasswordDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleResetPassword">确 定</el-button>
      </div>
    </template>
  </el-dialog>
  </div>


</template>

<script setup>
import { resetUserPwd } from "@/api/login";
// import { resetUserPwd } from "@/api/system/user"; 
import Cookies from "js-cookie";
import { encrypt, decrypt } from "@/utils/jsencrypt";
import useUserStore from '@/store/modules/user'

const userStore = useUserStore()
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const loginForm = ref({
  // username: "admin",
  // password: "admin123",
  username: "",
  password: "",
  rememberMe: false
});

const loginRules = {
  username: [{ required: true, trigger: "blur", message: "请输入您的工号" }],
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }]
};

const loading = ref(false);
// 注册开关
const register = ref(true);
const redirect = ref(undefined);

// 添加重置密码相关状态
const resetPasswordDialogVisible = ref(false);
const resetPasswordForm = ref({
  username: '',
  newPassword: '',
  name: ''
});
const resetPasswordRules = {
  username: [{ required: true, trigger: "blur", message: "请输入工号" }],
  newPassword: [{ required: true, trigger: "blur", message: "请输入新密码" }],
  name: [{ required: true, trigger: "blur", message: "请输入姓名" }]
};

// 显示重置密码对话框
function showResetPasswordDialog() {
  resetPasswordForm.value.username = loginForm.value.username;
  resetPasswordDialogVisible.value = true;
}

// const { proxy } = getCurrentInstance();
const resetPasswordRef = ref(); // 添加这行
// 处理密码重置
function handleResetPassword() {
  proxy.$refs.resetPasswordRef.validate(valid => {
    if (valid) {
      // 调用重置密码API
      resetUserPwd(resetPasswordForm.value.username, resetPasswordForm.value.newPassword, resetPasswordForm.value.name)
        .then(response => {
          proxy.$message.success("密码重置成功");
          resetPasswordDialogVisible.value = false;
        })
        .catch(error => {
          proxy.$message.error("密码重置失败: " + error.message);
        });
    }
  });
}

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect;
}, { immediate: true });

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true;
      // 勾选了需要记住密码设置在 cookie 中设置记住用户名和密码
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 });
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 });
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 });
      } else {
        // 否则移除
        Cookies.remove("username");
        Cookies.remove("password");
        Cookies.remove("rememberMe");
      }
      // 调用action的登录方法
      userStore.login(loginForm.value).then(() => {
        const query = route.query;
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== "redirect") {
            acc[cur] = query[cur];
          }
          return acc;
        }, {});
        router.push({ path: "/index" , query: { username: loginForm.value.username }}).catch(() => { });
        // router.push({ path: redirect.value || "/", query: otherQueryParams });
      }).catch(() => {
        loading.value = false;
      });
    }
  });
}

function getCookie() {
  const username = Cookies.get("username");
  const password = Cookies.get("password");
  const rememberMe = Cookies.get("rememberMe");
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password),
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
  };
}

getCookie();
</script>

<style lang='scss' scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/53345bf8-8ab7-4578-ae87-16d9b20522a4.png");
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}
.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
</style>
