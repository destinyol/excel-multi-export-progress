<template>
  <span>
    <el-button :style="myStyle" type="primary" size="medium" @click="show" >{{title}}</el-button>
    <el-dialog :title="title" :visible.sync="dialogVisible" top="6vh" width="780px"
               :before-close="close" destroy-on-close custom-class="role-mask" :close-on-click-modal="false">
      <div style="padding: 0px 10px">
        <div style="display: flex">
          <el-button type="success" size="medium" @click="exportTable" :loading="exportLoading">导出文件</el-button>
        </div>
        <div>
          <div style="margin: 15px 0px 0px 10px">
            <span>
              <span>数据处理中：</span>
              <el-tag v-if="progressStatus===0" type="info">未开始</el-tag>
              <el-tag v-if="progressStatus===1">进行中</el-tag>
              <el-tag v-if="progressStatus===2" type="success">已完成</el-tag>
              <el-tag v-if="progressStatus===3" type="danger">错误</el-tag>
            </span>
          </div>
          <div style="margin: 15px 10px">
            <el-progress :status="progressStatus===2?'success':''" :percentage="progressNum"></el-progress>
          </div>
        </div>
        <div>
          <div style="margin: 15px 0px 0px 10px">
            <span>
              <span>文件下载：</span>
            </span>
          </div>
          <div style="margin: 15px 10px">
            <el-progress :status="downloadProgress===100.0?'success':''" :percentage="downloadProgress"></el-progress>
          </div>
        </div>


      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="close">关 闭</el-button>
      </div>
    </el-dialog>
  </span>
</template>

<script>
import { cookieUser } from "@/components/common/partten.js";
import {httpGet, httpPost, httpJson} from "@/api";
import departDialog from "@/components/page/drawer/departDialog.vue";
import bus from "@/assembly/bus";
import Vue from "vue";
import axios from "axios";
import VueCookies from "vue-cookies";
export default {
  props:{
    myStyle:{},
    title:{
      type: String,
      default: "Excel导出"
    },
    pageBean:{},
    url:"",
  },
  data() {
    return {
      exportLoading:false,
      accept: '.xls,.xlsx',
      dialogVisible:false,

      progressKey:null,
      fileName:null,
      progressNum:0.0,
      progressStatus:0,
      progressController:true,  // true就轮询，false就终止轮询

      downloadProgress: 0.0,
    }
  },
  methods: {
    getProgress(){
      httpGet(this.url+"/getExportExpenseExtraInfoProgress", {processKey:this.progressKey}).then(res => {
        if (res.ret ===0){
          this.progressNum = Number((res.datas.progress*100).toFixed(2))
          this.progressStatus = res.datas.status  // 处理状态  0是未开始   1是处理中   2是处理完毕     3是报错了

          if (res.datas.status!==2 && res.datas.status!==3){
            setTimeout(()=>{
              if (this.progressController){
                this.getProgress()
              }
            },1000)
          }else{
            this.exportLoading=false
            if (res.datas.status===2){
              this.progressNum = 100.0
              this.$message.success("导出成功")
              this.downloadExcel()
            }
          }
        }else{
          this.exportLoading=false
          this.progressStatus = 3
          this.$message.error(res.msg);
        }
      })
    },
    downloadExcel(){
      let cookie_user = VueCookies.get(cookieUser);
      let header = {
        'userId': cookie_user.userId,
        'token': cookie_user.token,
        'timestamp': cookie_user.timestamp,
      }
      axios({
        url: process.env.VUE_APP_BASEURL+this.url+"/downloadExportExcel"+"?fileName="+this.fileName,
        method: 'GET',
        responseType: 'blob', // 重要：设置响应类型为blob
        headers: header,
        onDownloadProgress: (progressEvent) => { // 配置下载进度事件
          if (progressEvent.lengthComputable) {
            this.downloadProgress = parseFloat(Number((progressEvent.loaded / progressEvent.total) * 100).toFixed(2));
          }
        }
      }).then((response) => {
        // 创建一个Blob对象，使用响应数据
        const blob = new Blob([response.data], { type: 'application/octet-stream' });
        // 创建一个链接元素
        const link = document.createElement('a');
        const url = window.URL.createObjectURL(blob);
        // 设置下载文件名
        link.setAttribute('href', url);
        link.setAttribute('download', response.headers['content-disposition'].split('filename=')[1]); // 从响应头中获取文件名

        // 模拟点击链接下载文件
        link.click();
        // 释放URL对象
        window.URL.revokeObjectURL(url);
      }).catch((error) => {
        // 处理错误
        console.error('Download error:', error);
      });
    },
    exportTable(){
      this.downloadProgress = 0.0
      this.progressController = true
      this.progressKey = null;
      this.fileName = null;
      this.progressStatus = 0
      this.progressNum = 0.0
      this.exportLoading=true
      httpJson(this.url+"/exportExpenseExtraInfo", this.pageBean).then(res => {
        if (res.ret ===0){
          this.progressKey = res.datas.key
          this.fileName = res.datas.fileName
          this.getProgress()
        }else{
          this.$message.error(res.msg);
        }
      })
    },
    show(){
      this.dialogVisible = true
    },
    close(){
      this.downloadProgress = 0.0
      this.exportLoading = false
      this.progressController = false
      this.progressKey = null;
      this.fileName = null;
      this.progressStatus = 0
      this.progressNum = 0.0
      this.dialogVisible = false
    },
  },
  computed: {

  },
  watch: {

  },
  mounted() {
  },
  created() {

  }
}

</script>
<style scoped>

/deep/ .el-progress-bar{
  width: 95%;
}
</style>
