export function createEvidenceFileOpeningController({
  openLoading,
  setTimer = globalThis.setTimeout,
  clearTimer = globalThis.clearTimeout,
  timeoutMs = 15000,
  onFinish = () => {}
}) {
  let loadingInstance = null;
  let timeoutId = null;

  function finish() {
    if (!loadingInstance) {
      return;
    }

    if (timeoutId !== null) {
      clearTimer(timeoutId);
      timeoutId = null;
    }

    const currentLoading = loadingInstance;
    loadingInstance = null;
    currentLoading.close();
    onFinish();
  }

  function begin() {
    if (loadingInstance) {
      return false;
    }

    loadingInstance = openLoading();
    timeoutId = setTimer(finish, timeoutMs);
    return true;
  }

  return {
    begin,
    finish,
    destroy: finish,
    isOpening() {
      return Boolean(loadingInstance);
    }
  };
}
