<template>
  <div class="page-container">
    <!-- 卡片容器 -->
    <div class="info-card">
      <!-- 头部标题区 -->
      <div class="card-header">
        <h2 class="title">基本信息</h2>
        <div class="divider"></div>
      </div>

      <!-- 内容区 -->
      <div class="card-body">
        <table class="info-table">
          <!-- 基础信息行 -->
          <tr><td class="label">姓名</td><td class="value">{{ formData.name || '-' }}</td></tr>
          <tr><td class="label">性别</td><td class="value">{{ formData.gender || '-' }}</td></tr>
          <tr><td class="label">出生年月</td><td class="value">{{ formatDate(formData.birthDate) || '-' }}</td></tr>
          <tr><td class="label">籍贯</td><td class="value">{{ formData.nativePlace || '-' }}</td></tr>
          <tr><td class="label">政治面貌</td><td class="value">{{ formData.politicalStatus || '-' }}</td></tr>
          <tr><td class="label">职称</td><td class="value">{{ formData.title || '-' }}</td></tr>
          <tr><td class="label">现有岗位</td><td class="value">{{ formData.currentPosition || '-' }}</td></tr>

          <!-- 教育背景行 -->
          <tr class="section-separator"><td colspan="2"><span class="section-title">教育背景</span></td></tr>
          <tr><td class="label">本科毕业学校</td><td class="value">{{ formData.undergradSchool || '-' }}</td></tr>
          <tr><td class="label">本科毕业时间</td><td class="value">{{ formatDate(formData.undergradGraduationTime) || '-' }}</td></tr>
          <tr><td class="label">最高学历</td><td class="value">{{ formData.highestEducation || '-' }}</td></tr>
          <tr><td class="label">最高学历获得时间</td><td class="value">{{ formatDate(formData.highestEducationTime) || '-' }}</td></tr>

          <!-- 科研与经历行 -->
          <tr class="section-separator"><td colspan="2"><span class="section-title">科研与经历</span></td></tr>
          <tr><td class="label">出国访学经历</td><td class="value">{{ formData.overseasExperience || '-' }}</td></tr>
          <tr><td class="label">主要研究方向</td><td class="value">{{ formData.researchDirection || '-' }}</td></tr>

          <!-- 联系方式行 -->
          <tr class="section-separator"><td colspan="2"><span class="section-title">联系方式</span></td></tr>
          <tr>
            <td class="label">电话</td>
            <td class="value contact-item">
              <a :href="`tel:${formData.phone}`" class="link">{{ formData.phone || '-' }}</a>
            </td>
          </tr>
          <tr>
            <td class="label">E-mail</td>
            <td class="value contact-item">
              <a :href="`mailto:${formData.email}`" class="link">{{ formData.email || '-' }}</a>
            </td>
          </tr>
          <tr><td class="label">微信</td><td class="value">{{ formData.wechat || '-' }}</td></tr>
          <tr><td class="label">QQ</td><td class="value">{{ formData.qq || '-' }}</td></tr>
          <tr>
            <td class="label">个人网站</td>
            <td class="value contact-item">
              <a :href="formData.personalWebsite" target="_blank" class="link">{{ formData.personalWebsite || '-' }}</a>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</template>

<!-- <script>
import { getBasicInformation } from "@/api/basicInformation";
import { parseStrEmpty } from "@/utils/ruoyi";

export default {
  data() {
    return {
      formData: {
        name: '', gender: '', birthDate: '', nativePlace: '', politicalStatus: '',
        title: '', currentPosition: '', undergradSchool: '', undergradGraduationTime: '',
        highestEducation: '', highestEducationTime: '', overseasExperience: '',
        researchDirection: '', phone: '', email: '', wechat: '', qq: '', personalWebsite: ''
      }
    };
  },
  mounted() {
    this.getBasicInfoData(1); // 实际场景替换为动态ID
  },
  methods: {
    getBasicInfoData(id) {
      getBasicInformation(id)
        .then(response => {
          const res = response;
          if (res.code === 200 && res.data) {
            this.formData = { ...res.data };
          } else {
            this.$message.error(res.msg || "获取数据失败");
          }
        })
        .catch(error => {
          console.error("接口调用失败：", error);
          this.$message.error("网络异常，请稍后重试");
        });
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return `${date.getFullYear()}年${String(date.getMonth() + 1).padStart(2, '0')}月`;
    }
  }
};
</script> -->

<style scoped>
/* 页面容器 */
.page-container {
  padding: 30px;
  background-color: #f9fafb; /* 浅灰背景提升卡片层次感 */
  min-height: 100vh;
}

/* 信息卡片 */
.info-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05); /* 柔和阴影 */
  max-width: 900px;
  margin: 0 auto; /* 居中显示 */
  overflow: hidden;
}

/* 卡片头部 */
.card-header {
  padding: 25px 30px 15px;
}
.title {
  font-size: 22px;
  font-weight: 600;
  color: #1e40af; /* 深蓝色主色调 */
  margin: 0;
}
.divider {
  height: 3px;
  width: 60px;
  background: linear-gradient(90deg, #3b82f6, #93c5fd); /* 渐变分割线 */
  margin-top: 12px;
  border-radius: 2px;
}

/* 卡片内容区 */
.card-body {
  padding: 0 30px 30px;
}

/* 表格样式 */
.info-table {
  width: 100%;
  border-collapse: separate; /* 分离边框模式 */
  border-spacing: 0;
}
.info-table td {
  padding: 14px 12px;
  border-bottom: 1px solid #f1f5f9; /* 浅色分隔线 */
}
.info-table tr:last-child td {
  border-bottom: none; /* 最后一行无边框 */
}

/* 标签列 */
.label {
  width: 30%;
  font-weight: 500;
  color: #475569; /* 深灰文字 */
  text-align: right;
  padding-right: 20px;
  background-color: #f8fafc;
}

/* 内容列 */
.value {
  color: #1e293b; /* 主文字色 */
  text-align: left;
  padding-left: 20px;
  line-height: 1.6; /* 行高提升可读性 */
}

/* 区块分隔行 */
.section-separator {
  position: relative;
  height: 40px;
}
.section-separator td {
  padding: 0;
  border-bottom: none;
}
.section-title {
  display: inline-block;
  padding: 4px 12px;
  background: #e0f2fe; /* 浅蓝色背景 */
  color: #1e40af;
  font-size: 14px;
  font-weight: 500;
  border-radius: 4px;
  margin-top: 10px;
}

/* 联系方式链接 */
.contact-item {
  position: relative;
}
.link {
  color: #3b82f6; /* 链接蓝色 */
  text-decoration: none;
  padding-bottom: 2px;
  border-bottom: 1px dashed #93c5fd;
  transition: all 0.2s;
}
.link:hover {
  color: #1d4ed8; /* hover加深 */
  border-bottom-color: #3b82f6;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .page-container {
    padding: 15px;
  }
  .label {
    width: 35%;
    font-size: 13px;
  }
  .value {
    font-size: 13px;
    padding-left: 10px;
  }
  .card-header {
    padding: 20px 15px 10px;
  }
  .card-body {
    padding: 0 15px 20px;
  }
}
</style>



<!-- 模板2 -->
<!-- <template>
  <div class="table-container">
    <h2 class="title">基本信息</h2>
    <table class="info-table">
      <tr><td class="label">姓名</td><td>{{ formData.name }}</td></tr>
      <tr><td class="label">性别</td><td>{{ formData.gender }}</td></tr>
      <tr><td class="label">出生年月</td><td>{{ formatDate(formData.birthDate) }}</td></tr>
      <tr><td class="label">籍贯</td><td>{{ formData.nativePlace }}</td></tr>
      <tr><td class="label">政治面貌</td><td>{{ formData.politicalStatus }}</td></tr>
      <tr><td class="label">职称</td><td>{{ formData.title }}</td></tr>
      <tr><td class="label">现有岗位</td><td>{{ formData.currentPosition }}</td></tr>
      <tr><td class="label">本科毕业学校</td><td>{{ formData.undergradSchool }}</td></tr>
      <tr><td class="label">本科毕业时间</td><td>{{ formatDate(formData.undergradTime) }}</td></tr>
      <tr><td class="label">最高学历</td><td>{{ formData.highestEducation }}</td></tr>
      <tr><td class="label">最高学历获得时间</td><td>{{ formatDate(formData.highestEducationTime) }}</td></tr>
      <tr><td class="label">出国访学经历</td><td>{{ formData.overseasExperience }}</td></tr>
      <tr><td class="label">主要研究方向</td><td>{{ formData.researchDirection }}</td></tr>
      <tr>
        <td class="label">联系方式</td>
        <td class="contact-container">
          <div class="contact-row">
            <div class="contact-item">
              <span class="contact-label">电话</span>
              <span class="contact-underline">{{ formData.phone }}</span>
            </div>
            <div class="contact-item">
              <span class="contact-label">E-mail</span>
              <span class="contact-underline">{{ formData.email }}</span>
            </div>
          </div>
          <div class="contact-row">
            <div class="contact-item">
              <span class="contact-label">微信</span>
              <span class="contact-underline">{{ formData.wechat }}</span>
            </div>
            <div class="contact-item">
              <span class="contact-label">QQ</span>
              <span class="contact-underline">{{ formData.qq }}</span>
            </div>
          </div>
        </td>
      </tr>
      <tr><td class="label">个人网站</td><td>{{ formData.personalWebsite }}</td></tr>
    </table>
  </div>
</template>

<script>
import { getBasicInformation } from "@/api/basicInformation"; // 导入接口
import { parseStrEmpty } from "@/utils/ruoyi"; // 复用工具函数（处理空值）

export default {
  data() {
    return {
      formData: {
        name: '',
        gender: '',
        birthDate: '',
        nativePlace: '',
        politicalStatus: '',
        title: '',
        currentPosition: '',
        undergradSchool: '',
        undergradTime: '',
        highestEducation: '',
        highestEducationTime: '',
        overseasExperience: '',
        researchDirection: '',
        phone: '', email: '', wechat: '', qq: '',
        personalWebsite: ''
      }
    };
  },
  mounted() {
    // 从路由参数获取人员ID（根据实际场景调整）
    // const id = parseStrEmpty(this.$route.params.id); 
    // if (id) {
    //   this.getBasicInfoData(id); // 调用接口加载数据
    // } else {
    //   this.$message.warning("未找到人员ID"); // 无ID时提示
    // }
    this.getBasicInfoData(1); // 调用接口加载数据
  },
  methods: {
    /**
     * 调用接口获取人员详情
     * @param {number} id - 人员ID
     */
    getBasicInfoData(id) {
      getBasicInformation(id)
        .then(response => {
          const res = response; 
          console.log(res);
          if (res.code === 200) { // 若后端返回状态码，需判断成功条件
            const data = res.data;
            // 字段映射（根据后端实际返回调整）
            this.formData = {
              name: data.name || "",
              gender: data.gender || "",
              birthDate: data.birthDate || "",
              nativePlace: data.nativePlace || "",
              politicalStatus: data.politicalStatus || "",
              title: data.title || "",
              currentPosition: data.currentPosition || "",
              undergradSchool: data.undergradSchool || "",
              undergradTime: data.undergradGraduationTime || "",
              highestEducation: data.highestEducation || "",
              highestEducationTime: data.highestEducationTime || "",
              overseasExperience: data.overseasExperience || "",
              researchDirection: data.researchDirection || "",
              phone: data.phone || "",
              email: data.email || "",
              wechat: data.wechat || "",
              qq: data.qq || "",
              personalWebsite: data.personalWebsite || ""                                     
            };
          } else {
            this.$message.error(res.msg || "获取数据失败");
          }
        })
        .catch(error => {
          console.error("接口调用失败：", error);
          this.$message.error("网络异常，请稍后重试");
        });
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return `${date.getFullYear()}年${date.getMonth() + 1}月`;
    }
  }
};
</script>

<style scoped>
.table-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 20px;
}
 
.title {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}
 
.info-table {
  border-collapse: collapse;
  width: 800px;
}
 
.info-table td {
  padding: 12px 20px;
  border: 1px solid #ddd;
  text-align: center;
}
 
.label {
  background-color: #f5f5f5;
  font-weight: bold;
  text-align: center;
  vertical-align: middle;
  width: 200px;
}
 
.vertical-align-top {
  vertical-align: top;
  padding-top: 18px; /* 顶部对齐调整 */
}
 
.contact-container {
  padding: 8px 12px;
}
 
.contact-row {
  display: flex;
  margin-bottom: 10px;
  gap: 15px;
}
 
.contact-label {
  min-width: 50px; /* 固定标签宽度 */
  text-align: right;
  margin-right: 8px;
}
 
 
.contact-item {
  flex: 1; /* 每个联系方式项平均分配宽度 */
  display: flex;
  align-items: flex-end;
}
 
.contact-underline {
  flex: 1; /* 下划线填满项目剩余空间 */
  border-bottom: 1px solid #333;
  min-width: 0; /* 取消最小宽度限制，允许完全收缩 */
  padding: 0 0 2px;
  word-break: break-all;
  text-align: center;
  min-height: 20px;
}

</style> -->


