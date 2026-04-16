function copyToClipboard(content, button) {
  try {
    navigator.clipboard.writeText(content);
    if (button) {
      const icon = $(button).find('.ui-icon');
      icon.removeClass('ti ti-copy')
      icon.addClass('ti ti-check');
      setTimeout(() => {
        icon.removeClass('ti ti-check');
        icon.addClass('ti ti-copy')
      }, 1000);
    }
  } catch {
    alert(content);
  }
}
