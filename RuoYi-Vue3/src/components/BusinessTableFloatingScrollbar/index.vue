<template>
  <teleport to="body">
    <div
      v-show="visible"
      ref="floatingScrollbarRef"
      class="business-table-floating-scrollbar"
      :style="{ left: `${left}px`, width: `${width}px` }"
      @scroll.passive="handleFloatingScroll"
      @wheel="handleWheel"
    >
      <div
        class="business-table-floating-scrollbar__spacer"
        :style="{ width: `${contentWidth}px` }"
      ></div>
    </div>
  </teleport>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const floatingScrollbarRef = ref(null)
const visible = ref(false)
const left = ref(0)
const width = ref(0)
const contentWidth = ref(0)

let activeTable = null
let activeScroller = null
let animationFrameId = 0
let mutationObserver = null
let resizeObserver = null

function getTableScroller(table) {
  return table.querySelector('.el-table__body-wrapper .el-scrollbar__wrap')
    || table.querySelector('.el-table__body-wrapper')
}

function handleTableScroll() {
  const floatingScroller = floatingScrollbarRef.value
  if (!floatingScroller || !activeScroller) return

  if (Math.abs(floatingScroller.scrollLeft - activeScroller.scrollLeft) > 1) {
    floatingScroller.scrollLeft = activeScroller.scrollLeft
  }
}

function handleFloatingScroll() {
  const floatingScroller = floatingScrollbarRef.value
  if (!floatingScroller || !activeScroller) return

  if (Math.abs(activeScroller.scrollLeft - floatingScroller.scrollLeft) > 1) {
    activeScroller.scrollLeft = floatingScroller.scrollLeft
  }
}

function handleWheel(event) {
  const floatingScroller = floatingScrollbarRef.value
  if (!floatingScroller || !activeScroller) return

  const delta = Math.abs(event.deltaX) > Math.abs(event.deltaY)
    ? event.deltaX
    : event.deltaY
  if (!delta) return

  event.preventDefault()
  floatingScroller.scrollLeft += delta
  activeScroller.scrollLeft = floatingScroller.scrollLeft
}

function detachActiveTable() {
  if (activeScroller) {
    activeScroller.removeEventListener('scroll', handleTableScroll)
  }
  if (resizeObserver) {
    if (activeTable) resizeObserver.unobserve(activeTable)
    if (activeScroller) resizeObserver.unobserve(activeScroller)
  }
  activeTable = null
  activeScroller = null
}

function bindActiveTable(table, scroller) {
  if (activeTable === table && activeScroller === scroller) return

  detachActiveTable()
  activeTable = table
  activeScroller = scroller
  activeScroller.addEventListener('scroll', handleTableScroll, { passive: true })
  if (resizeObserver) {
    resizeObserver.observe(activeTable)
    resizeObserver.observe(activeScroller)
  }
}

function hideFloatingScrollbar() {
  visible.value = false
  detachActiveTable()
}

function findActiveTable() {
  if (document.body.classList.contains('el-popup-parent--hidden')) return null

  const viewportBottom = window.innerHeight
  return Array.from(document.querySelectorAll('.business-table-scroll-scope .el-table'))
    .filter((table) => !table.closest('.el-dialog'))
    .map((table) => {
      const scroller = getTableScroller(table)
      return { table, scroller, rect: table.getBoundingClientRect() }
    })
    .filter(({ scroller, rect }) => (
      scroller
      && scroller.clientWidth > 0
      && scroller.scrollWidth > scroller.clientWidth + 1
      && rect.top < viewportBottom - 16
      && rect.bottom > viewportBottom
    ))
    .sort((first, second) => second.rect.top - first.rect.top)[0] || null
}

function refreshFloatingScrollbar() {
  animationFrameId = 0
  const candidate = findActiveTable()
  if (!candidate) {
    hideFloatingScrollbar()
    return
  }

  const visibleLeft = Math.max(candidate.rect.left, 0)
  const visibleRight = Math.min(candidate.rect.right, window.innerWidth)
  const visibleWidth = visibleRight - visibleLeft
  if (visibleWidth < 80) {
    hideFloatingScrollbar()
    return
  }

  bindActiveTable(candidate.table, candidate.scroller)
  left.value = Math.round(visibleLeft)
  width.value = Math.round(visibleWidth)
  contentWidth.value = candidate.scroller.scrollWidth
  visible.value = true

  nextTick(() => {
    if (floatingScrollbarRef.value && activeScroller) {
      floatingScrollbarRef.value.scrollLeft = activeScroller.scrollLeft
    }
  })
}

function scheduleRefresh() {
  if (animationFrameId) return
  animationFrameId = window.requestAnimationFrame(refreshFloatingScrollbar)
}

const stopRouteWatch = watch(
  () => route.fullPath,
  async () => {
    hideFloatingScrollbar()
    await nextTick()
    scheduleRefresh()
  }
)

onMounted(() => {
  if (typeof ResizeObserver !== 'undefined') {
    resizeObserver = new ResizeObserver(scheduleRefresh)
  }

  if (typeof MutationObserver !== 'undefined') {
    const appMain = document.querySelector('.app-main')
    if (appMain) {
      mutationObserver = new MutationObserver(scheduleRefresh)
      mutationObserver.observe(appMain, { childList: true, subtree: true })
    }
  }

  window.addEventListener('scroll', scheduleRefresh, true)
  window.addEventListener('resize', scheduleRefresh)
  document.addEventListener('fullscreenchange', scheduleRefresh)
  nextTick(scheduleRefresh)
})

onBeforeUnmount(() => {
  stopRouteWatch()
  hideFloatingScrollbar()
  if (animationFrameId) window.cancelAnimationFrame(animationFrameId)
  if (mutationObserver) mutationObserver.disconnect()
  if (resizeObserver) resizeObserver.disconnect()
  window.removeEventListener('scroll', scheduleRefresh, true)
  window.removeEventListener('resize', scheduleRefresh)
  document.removeEventListener('fullscreenchange', scheduleRefresh)
})
</script>

<style scoped lang="scss">
.business-table-floating-scrollbar {
  position: fixed;
  bottom: 8px;
  z-index: 1200;
  height: 16px;
  overflow-x: scroll;
  overflow-y: hidden;
  background: #dfe7ee;
  border-radius: 3px;
  box-shadow: 0 0 0 1px rgba(62, 82, 102, 0.35), 0 3px 10px rgba(31, 45, 61, 0.22);
  scrollbar-color: #526b80 #dfe7ee;
  scrollbar-width: auto;
}

.business-table-floating-scrollbar::-webkit-scrollbar {
  height: 12px;
}

.business-table-floating-scrollbar::-webkit-scrollbar-track {
  background: #dfe7ee;
  border-radius: 3px;
}

.business-table-floating-scrollbar::-webkit-scrollbar-thumb {
  background: #526b80;
  border: 2px solid #dfe7ee;
  border-radius: 4px;
}

.business-table-floating-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #2f536f;
}

.business-table-floating-scrollbar__spacer {
  height: 1px;
}
</style>
