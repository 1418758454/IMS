import { ElLoading } from 'element-plus';

import { createEvidenceFileOpeningController } from './evidenceFileOpeningController.js';

const OPENING_TIMEOUT_MS = 15000;
const FOCUS_CLOSE_DELAY_MS = 250;

function openDefaultLoading() {
  return ElLoading.service({
    lock: true,
    fullscreen: true,
    text: '正在打开文件选择窗口，请稍候……',
    background: 'rgba(255, 255, 255, 0.86)',
    customClass: 'evidence-file-opening-loading'
  });
}

export function createEvidenceFileOpeningDirective({
  openLoading = openDefaultLoading,
  windowTarget = typeof window === 'undefined' ? null : window,
  documentTarget = typeof document === 'undefined' ? null : document,
  setTimer = globalThis.setTimeout,
  clearTimer = globalThis.clearTimeout
} = {}) {
  const states = new WeakMap();

  return {
    mounted(el) {
      let activeInput = null;
      let focusTimerId = null;
      let controller;

      const clearActiveInput = () => {
        if (activeInput) {
          activeInput.removeEventListener('change', finishOpening);
          activeInput = null;
        }
        if (focusTimerId !== null) {
          clearTimer(focusTimerId);
          focusTimerId = null;
        }
      };

      controller = createEvidenceFileOpeningController({
        openLoading,
        setTimer,
        clearTimer,
        timeoutMs: OPENING_TIMEOUT_MS,
        onFinish: clearActiveInput
      });

      function finishOpening() {
        controller.finish();
      }

      const handleClick = event => {
        const trigger = event.target?.closest?.('.el-upload');
        if (!trigger || (typeof el.contains === 'function' && !el.contains(trigger))) {
          return;
        }

        const input = trigger.querySelector?.('input[type="file"]')
          || el.querySelector?.('input[type="file"]');
        if (!input || !controller.begin()) {
          return;
        }

        activeInput = input;
        activeInput.addEventListener('change', finishOpening);
      };

      const handleFocusReturn = () => {
        if (!controller.isOpening()) {
          return;
        }

        if (focusTimerId !== null) {
          clearTimer(focusTimerId);
        }
        focusTimerId = setTimer(() => {
          focusTimerId = null;
          finishOpening();
        }, FOCUS_CLOSE_DELAY_MS);
      };

      const handleVisibilityChange = () => {
        if (documentTarget?.visibilityState === 'visible') {
          handleFocusReturn();
        }
      };

      el.addEventListener('click', handleClick, true);
      windowTarget?.addEventListener('focus', handleFocusReturn);
      documentTarget?.addEventListener('visibilitychange', handleVisibilityChange);

      states.set(el, {
        destroy() {
          el.removeEventListener('click', handleClick, true);
          windowTarget?.removeEventListener('focus', handleFocusReturn);
          documentTarget?.removeEventListener('visibilitychange', handleVisibilityChange);
          controller.destroy();
          clearActiveInput();
        }
      });
    },
    unmounted(el) {
      states.get(el)?.destroy();
      states.delete(el);
    }
  };
}

export default createEvidenceFileOpeningDirective();
