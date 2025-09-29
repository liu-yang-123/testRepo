<template>
  <span>{{ codeToText }}</span>
</template>

<script>
export default {
  name: 'Dictionary',
  props: {
    dic: {
      required: true,
      type: Array,
      default() {
        return {}
      }
    },
    code: {
      required: true
    }
  },
  computed: {
    codeToText() {
      if (this.code != null || this.code !== '') {
        const elm = this.dic.find((val) => val.code === this.code)
        if (!elm) { return '' }
        return elm.content
      } else {
        return ''
      }
    }
  }
}
</script>

<style scoped>

</style>
