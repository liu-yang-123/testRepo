<template>
  <div class="app-container">
    <div v-if="user">
      <el-card>
        <el-row :gutter="24">
          <el-col :span="12" :offset="6">
            <el-form ref="profileForm" :model="user" label-width="80px" :rules="rules">
              <el-form-item label="登录名">
                <el-input v-model.trim="user.name" disabled />
              </el-form-item>
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="user.oldPassword" placeholder="请输入原密码" show-password />
              </el-form-item>
              <el-form-item label="新密码" prop="password">
                <el-input v-model.trim="user.password" placeholder="请输入密码" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model.trim="user.confirmPassword" placeholder="请输入密码" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submit">修改</el-button>
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script>
import { profile } from '@/api/user'
import { mapGetters } from 'vuex'
export default {
  name: 'Profile',
  components: { },
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码不能少于6位数'))
      } else {
        callback()
      }
    }
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.user.password) {
        callback(new Error('两次密码不相同'))
      } else {
        callback()
      }
    }
    return {
      user: {},
      rules: {
        oldPassword: [
          { required: true, message: '旧密码不能少于6位数', trigger: 'blur', validator: validatePassword }
        ],
        password: [
          { required: true, message: '新密码不能少于6位数', trigger: 'blur', validator: validatePassword }
        ],
        confirmPassword: [
          { required: true, message: '确认密码不能少于6位数', trigger: 'blur', validator: validatePassword },
          { required: true, message: '两次密码不相同,请检查', trigger: 'blur', validator: validateConfirmPassword }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'name'
    ])
  },
  created() {
    this.getUser()
  },
  methods: {
    submit() {
      // 提交数据
      this.$refs.profileForm.validate(valid => {
        console.log(valid)
        if (valid) {
          // 提交数据
          profile(this.user).then(res => {
            if (res.code === 0) {
              this.$message.success(res.msg)
              this.user = {
                oldPassword: '',
                password: '',
                confirmPassword: ''
              }
            } else {
              this.$message.error(res.msg)
            }
          })
        } else {
          return false
        }
      })
    },
    getUser() {
      this.user = {
        name: this.name,
        oldPassword: '',
        password: '',
        confirmPassword: ''
      }
    }
  }
}
</script>
