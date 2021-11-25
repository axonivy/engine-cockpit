console.log("    ______            _               ______           __         _ __ \r\n   \/ ____\/___  ____ _(_)___  ___     \/ ____\/___  _____\/ \/______  (_) \/_\r\n  \/ __\/ \/ __ \\\/ __ `\/ \/ __ \\\/ _ \\   \/ \/   \/ __ \\\/ ___\/ \/\/_\/ __ \\\/ \/ __\/\r\n \/ \/___\/ \/ \/ \/ \/_\/ \/ \/ \/ \/ \/  __\/  \/ \/___\/ \/_\/ \/ \/__\/ ,< \/ \/_\/ \/ \/ \/_  \r\n\/_____\/_\/ \/_\/\\__, \/_\/_\/ \/_\/\\___\/   \\____\/\\____\/\\___\/_\/|_\/ .___\/_\/\\__\/  \r\n   \/ \/_  __ \/____\/\/   |  _  ______  ____    (_)   ____ \/_\/             \r\n  \/ __ \\\/ \/ \/ \/  \/ \/| | | |\/_\/ __ \\\/ __ \\  \/ \/ | \/ \/ \/ \/ \/             \r\n \/ \/_\/ \/ \/_\/ \/  \/ ___ |_>  <\/ \/_\/ \/ \/ \/ \/ \/ \/| |\/ \/ \/_\/ \/              \r\n\/_.___\/\\__, \/  \/_\/  |_\/_\/|_|\\____\/_\/ \/_(_)_\/ |___\/\\__, \/               \r\n      \/____\/                                     \/____\/                \n\
https://developer.axonivy.com/team");

function buttonAddSpinner(button) {
  $(button).addClass('ui-state-disabled');
  var icon = $(button).find('.ui-icon');
  icon.removeClass(function (index, css) {
    return (css.match(/(^|\s)si-\S+/g) || []).join(' '); // removes anything that starts with "si-"
  });
  $(icon).addClass('si-button-refresh-arrows si-is-spinning');
}

function buttonRemoveSpinner(button, defaultIcon) {
  var icon = $(button).find('.ui-icon')
  $(icon).removeClass('si-button-refresh-arrows si-is-spinning');
  $(icon).addClass(defaultIcon);
  $(button).removeClass('ui-state-disabled');
}

function checkGravatarImg(img)
{
  img.style.display = "block";
  img.nextElementSibling.style.display = "none";
}
