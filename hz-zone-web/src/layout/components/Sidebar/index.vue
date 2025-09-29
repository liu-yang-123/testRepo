<template>
  <div :class="{'has-logo':showLogo}">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :unique-opened="true"
        :active-text-color="variables.menuActiveText"
        :collapse-transition="false"
        mode="vertical"
        @select="handleSelect"
      >
        <sidebar-item v-for="route in permission_routes" :key="route.path" :item="route" :base-path="route.path" />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/styles/variables.scss'
import { getFinToken, getSafeToken, getSafeReceiveToken, getProductToken, getProductReceiveToken } from '@/api/form/index'

export default {
  components: { SidebarItem, Logo },
  computed: {
    ...mapGetters([
      'permission_routes',
      'sidebar'
    ]),
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    variables() {
      return variables
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  },
  methods: {
    handleSelect(key) {
      if (key === '/form/fill-in') {
        getFinToken().then(res => {
          window.open(`http://zccc.cbpm-xinda.net:22880/#/stat/form?token=${res.data}`, '', 'location=no')
        }).catch(() => {
          // this.$message.error(res.msg)
        })
      } else if (key === '/form/data-list') {
        getFinToken().then(res => {
          window.open(`http://zccc.cbpm-xinda.net:22880/#/stat/form-data-list?token=${res.data}`, '', 'location=no')
        }).catch(() => {
          // this.$message.error(res.msg)
        })
      } else if (key === '/form/document') {
        getFinToken().then(res => {
          window.open(`http://zccc.cbpm-xinda.net:22880/#/stat/document?token=${res.data}`, '', 'location=no')
        }).catch(() => {
          // this.$message.error(res.msg)
        })
      } else if (key === '/form/information') {
        getFinToken().then(res => {
          window.open(`http://zccc.cbpm-xinda.net:22880/#/stat/information?token=${res.data}`, '', 'location=no')
        }).catch(() => {
          // this.$message.error(res.msg)
        })
      } else if (key === '/file/safe/safe-issue') {
        getSafeToken().then(res => {
          window.open(`http://zccc.cbpm-xinda.net:22880/#/safe-area?token=${res.data}`, '', 'location=no')
        }).catch(() => {
          // this.$message.error(res.msg)
        })
      } else if (key === '/file/safe/safe-accept') {
        getSafeReceiveToken().then(res => {
          window.open(`http://zccc.cbpm-xinda.net:22880/#/safe-information-area?token=${res.data}`, '', 'location=no')
        }).catch(() => {
          // this.$message.error(res.msg)
        })
      } else if (key === '/file/product/product-issue') {
        getProductToken().then(res => {
          window.open(`http://zccc.cbpm-xinda.net:22880/#/produce-area?token=${res.data}`, '', 'location=no')
        }).catch(() => {
          // this.$message.error(res.msg)
        })
      } else if (key === '/file/product/product-accept') {
        getProductReceiveToken().then(res => {
          window.open(`http://zccc.cbpm-xinda.net:22880/#/produce-information-area?token=${res.data}`, '', 'location=no')
        }).catch(() => {
          // this.$message.error(res.msg)
        })
      }
    }
  }
}
</script>
