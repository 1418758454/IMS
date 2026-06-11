<template>
  <div class="table-container">
    <h2 class="title">基本信息</h2>
    <table class="info-table">
      <tr><td class="label">姓名</td><td>{{ formData.name }}</td></tr>
      <tr><td class="label">性别</td><td>{{ formData.gender }}</td></tr>
      <tr><td class="label">出生年月</td><td>{{ formatDate(formData.birthDate) }}</td></tr>
      <tr><td class="label">籍贯</td><td>{{ formData.nativePlace }}</td></tr>
      <tr><td class="label">政治面貌</td><td>{{ formData.politicalStatus }}</td></tr>
      <tr><td class="label">职称</td><td>{{ formData.title }}</td></tr>
      <tr><td class="label">部门</td><td>{{ formData.department }}</td></tr>
      <tr><td class="label">职务</td><td>{{ formData.position }}</td></tr>    
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
              <span class="contact-label ">电话</span>
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
      <tr>
        <td class="label">个人网站</td>
        <td style="text-align: center;">
          <a :href="formData.personalWebsite" target="_blank" class="link">{{ formData.personalWebsite || '-' }}</a>
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
import { getBasicInformation } from "@/api/basicInformation"; // 导入接口
import { getInfo } from "@/api/login";
import { parseStrEmpty } from "@/utils/ruoyi"; // 复用工具函数（处理空值）
import { get } from "@vueuse/core";

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
        department: '', 
        position: '',  
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
    // console.log('路由参数',this.$route.query)
    getInfo().then(response => {
      // console.log('登录用户信息', response.user.userName);  
      this.getBasicInfoData( response.user.userName); // 调用接口加载数据

    })
    // this.getBasicInfoData(this.$route.query.username); // 调用接口加载数据

  },
  methods: {
    /**
     * 调用接口获取人员详情
     * 
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
              department: data.department || "",   
              position: data.position || "",    
              currentPosition: data.currentPosition || "",
              undergradSchool: data.undergradSchool || "",
              undergradTime: data.undergradTime || "",
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
        // .catch(error => {
        //   console.error("接口调用失败：", error);
        //   this.$message.error("网络异常，请稍后重试");
        // });
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
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

</style>