window.addEventListener("load", (event) => {
  window.parent.postMessage({ type: 'frame-onload', url: window.location.href }, '*');
});
