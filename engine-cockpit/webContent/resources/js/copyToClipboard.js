function copyToClipboard(content, button) {
  try {
    navigator.clipboard.writeText(content);
    if (button) {
      const icon = $(button).find('.ui-icon');
      icon.removeClass('ti-copy')
      icon.addClass('ti-check');
      setTimeout(() => {
        icon.removeClass('ti-check');
        icon.addClass('ti-copy')
      }, 1000);
    }
  } catch {
    alert(content);
  }
}
