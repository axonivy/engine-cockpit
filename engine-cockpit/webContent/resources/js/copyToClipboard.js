function copyToClipboard(content) {
  if (navigator.clipboard) {
    navigator.clipboard.writeText(content);
  }
  alert(content);
}
