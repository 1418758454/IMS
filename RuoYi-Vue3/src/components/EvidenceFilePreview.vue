<template>
  <div v-if="url" class="evidence-file-preview">
    <el-image
      v-if="isImageEvidenceFile(url)"
      class="evidence-image-thumb"
      :style="thumbnailStyle"
      :src="url"
      :preview-src-list="[url]"
      preview-teleported
      fit="cover"
      :alt="'证明材料图片'"
    >
      <template #error>
        <span class="evidence-image-error">图片加载失败</span>
      </template>
    </el-image>
    <span v-if="isImageEvidenceFile(url)" class="evidence-image-hint">点击放大</span>
    <el-link
      v-else
      type="primary"
      icon="Document"
      :href="url"
      target="_blank"
      :underline="false"
    >
      {{ pdfLabel }}
    </el-link>
  </div>
  <span v-else>-</span>
</template>

<script>
import { isImageEvidenceFile } from '@/utils/evidenceFile';

export default {
  name: 'EvidenceFilePreview',
  props: {
    url: {
      type: String,
      default: ''
    },
    pdfLabel: {
      type: String,
      default: '查看PDF'
    },
    thumbnailSize: {
      type: Number,
      default: 48
    }
  },
  computed: {
    thumbnailStyle() {
      return {
        width: `${this.thumbnailSize}px`,
        height: `${this.thumbnailSize}px`
      };
    }
  },
  methods: {
    isImageEvidenceFile
  }
};
</script>

<style scoped>
.evidence-file-preview {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  min-width: 54px;
  min-height: 34px;
}

.evidence-image-thumb {
  width: 48px;
  height: 48px;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  cursor: zoom-in;
}

.evidence-image-hint,
.evidence-image-error {
  color: #909399;
  font-size: 11px;
  line-height: 1.2;
}
</style>
