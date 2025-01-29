function copyToClipboard(content, button) {
  try {
    navigator.clipboard.writeText(content);
    if (button) {
      const icon = $(button).find('.ui-icon');
      icon.removeClass('si-copy-paste')
      icon.addClass('si-check-1');
      setTimeout(() => {
        icon.removeClass('si-check-1');
        icon.addClass('si-copy-paste')
      }, 1000);
    }
  } catch {
    alert(content);
  }
}
