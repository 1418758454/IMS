import assert from 'node:assert/strict';

import { createEvidenceFileOpeningController } from './evidenceFileOpeningController.js';

function test(name, callback) {
  callback();
  console.log(`PASS ${name}`);
}

function createController(overrides = {}) {
  const state = {
    closeCount: 0,
    clearCount: 0,
    timeoutCallback: null
  };

  const controller = createEvidenceFileOpeningController({
    openLoading: () => ({
      close() {
        state.closeCount += 1;
      }
    }),
    setTimer(callback) {
      state.timeoutCallback = callback;
      return 1;
    },
    clearTimer() {
      state.clearCount += 1;
    },
    timeoutMs: 15000,
    ...overrides
  });

  return { controller, state };
}

test('begin opens one loading mask and blocks repeated starts', () => {
  let openCount = 0;
  const { controller } = createController({
    openLoading: () => {
      openCount += 1;
      return { close() {} };
    }
  });

  assert.equal(controller.begin(), true);
  assert.equal(controller.begin(), false);
  assert.equal(openCount, 1);
  assert.equal(controller.isOpening(), true);
});

test('finish closes the mask once and clears the timeout', () => {
  const { controller, state } = createController();

  controller.begin();
  controller.finish();
  controller.finish();

  assert.equal(state.closeCount, 1);
  assert.equal(state.clearCount, 1);
  assert.equal(controller.isOpening(), false);
});

test('timeout and destroy both leave the controller closed', () => {
  let finishCount = 0;
  const { controller, state } = createController({
    onFinish() {
      finishCount += 1;
    }
  });

  controller.begin();
  state.timeoutCallback();
  assert.equal(state.closeCount, 1);
  assert.equal(controller.isOpening(), false);

  controller.begin();
  controller.destroy();
  assert.equal(state.closeCount, 2);
  assert.equal(finishCount, 2);
  assert.equal(controller.isOpening(), false);
});
