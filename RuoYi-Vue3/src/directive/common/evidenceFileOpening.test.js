import assert from 'node:assert/strict';

import { createEvidenceFileOpeningDirective } from './evidenceFileOpening.js';

function test(name, callback) {
  callback();
  console.log(`PASS ${name}`);
}

function createEventTarget() {
  const listeners = new Map();

  return {
    addEventListener(type, listener) {
      const typeListeners = listeners.get(type) || new Set();
      typeListeners.add(listener);
      listeners.set(type, typeListeners);
    },
    removeEventListener(type, listener) {
      listeners.get(type)?.delete(listener);
    },
    dispatch(type, event = {}) {
      for (const listener of listeners.get(type) || []) {
        listener(event);
      }
    },
    listenerCount() {
      return [...listeners.values()].reduce((total, items) => total + items.size, 0);
    }
  };
}

function createFixture() {
  const root = createEventTarget();
  const input = createEventTarget();
  const windowTarget = createEventTarget();
  const documentTarget = createEventTarget();
  const timers = new Map();
  let nextTimerId = 1;
  let openCount = 0;
  let closeCount = 0;

  root.querySelector = () => input;
  root.contains = target => target === root;
  documentTarget.visibilityState = 'visible';

  const directive = createEvidenceFileOpeningDirective({
    openLoading: () => {
      openCount += 1;
      return {
        close() {
          closeCount += 1;
        }
      };
    },
    windowTarget,
    documentTarget,
    setTimer(callback) {
      const timerId = nextTimerId;
      nextTimerId += 1;
      timers.set(timerId, callback);
      return timerId;
    },
    clearTimer(timerId) {
      timers.delete(timerId);
    }
  });

  return {
    directive,
    root,
    input,
    windowTarget,
    documentTarget,
    timers,
    counts: {
      open: () => openCount,
      close: () => closeCount
    }
  };
}

test('upload click opens one mask and file change closes it', () => {
  const fixture = createFixture();
  fixture.directive.mounted(fixture.root);
  const triggerEvent = { target: { closest: () => fixture.root } };

  fixture.root.dispatch('click', triggerEvent);
  fixture.root.dispatch('click', triggerEvent);
  assert.equal(fixture.counts.open(), 1);

  fixture.input.dispatch('change');
  assert.equal(fixture.counts.close(), 1);

  fixture.directive.unmounted(fixture.root);
  assert.equal(fixture.root.listenerCount(), 0);
  assert.equal(fixture.windowTarget.listenerCount(), 0);
  assert.equal(fixture.documentTarget.listenerCount(), 0);
});

test('focus return schedules mask closing after file dialog cancellation', () => {
  const fixture = createFixture();
  fixture.directive.mounted(fixture.root);
  fixture.root.dispatch('click', { target: { closest: () => fixture.root } });

  fixture.windowTarget.dispatch('focus');
  const focusTimer = [...fixture.timers.values()].at(-1);
  focusTimer();

  assert.equal(fixture.counts.close(), 1);
  fixture.directive.unmounted(fixture.root);
});

test('unmount closes an active mask', () => {
  const fixture = createFixture();
  fixture.directive.mounted(fixture.root);
  fixture.root.dispatch('click', { target: { closest: () => fixture.root } });

  fixture.directive.unmounted(fixture.root);

  assert.equal(fixture.counts.close(), 1);
});
