$(document).ready(function () {
  function initFix() {
    if (PF('sidebar_menu') == null) {
      var timeoutID = window.setTimeout(initFix, 10);
    } else {
      $.removeCookie('serenity_expandeditems', { path: '/' });
      var page = window.location.pathname.substr(window.location.pathname.lastIndexOf('/') + 1);
      page = searchCorrectMenuItemPage(page);
      $('#menuform>ul>li>a').each(function () {
        var menuItemPage = $(this).attr('href');
        var menuItemId = $(this).parent().attr('id');
        if (menuItemPage != "#") {
          if (menuItemPage.startsWith(page)) {
            activateMenuItem(menuItemId);
          }
        }
        else {
          $(this).parent().find('ul>li>a').each(function () {
            var subMenuItemPage = $(this).attr('href');
            var subMenuItemId = $(this).parent().attr('id');
            if (subMenuItemPage != "#") {
              if (subMenuItemPage.startsWith(page)) {
                activateMenuItem(menuItemId);
                activateMenuItem(subMenuItemId);
              }
            }
            else {
              $(this).parent().find('ul>li>a').each(function () {
                var subSubMenuItemPage = $(this).attr('href');
                var subSubMenuItemId = $(this).parent().attr('id');
                if (subSubMenuItemPage.startsWith(page)) {
                  activateMenuItem(menuItemId);
                  activateMenuItem(subMenuItemId);
                  activateMenuItem(subSubMenuItemId);
                }
              });
            }
          });
        }
      });

      function searchCorrectMenuItemPage(value) {
        var map = {
          'security-detail.xhtml': 'securitysystem.xhtml',
          'userdetail.xhtml': 'users.xhtml',
          'roledetail.xhtml': 'roles.xhtml',
          'application-detail.xhtml': 'applications.xhtml',
          'pmv-detail.xhtml': 'applications.xhtml',
          'externaldatabasedetail.xhtml': 'externaldatabases.xhtml',
          'restclientdetail.xhtml': 'restclients.xhtml',
          'webservicedetail.xhtml': 'webservices.xhtml',
          'businesscalendar-detail.xhtml': 'businesscalendar.xhtml'
        };
        return map[value] == null ? value : map[value];
      }

      function activateMenuItem(id) {
        var menuitem = $("#" + id.replace(/:/g, "\\:"));
        menuitem.addClass('active-menuitem');
        var submenu = menuitem.children('ul');
        if (submenu.length) {
          submenu.show();
        }
      }
    }
  }
  initFix();
});

console.log("    ______            _               ______           __         _ __ \r\n   \/ ____\/___  ____ _(_)___  ___     \/ ____\/___  _____\/ \/______  (_) \/_\r\n  \/ __\/ \/ __ \\\/ __ `\/ \/ __ \\\/ _ \\   \/ \/   \/ __ \\\/ ___\/ \/\/_\/ __ \\\/ \/ __\/\r\n \/ \/___\/ \/ \/ \/ \/_\/ \/ \/ \/ \/ \/  __\/  \/ \/___\/ \/_\/ \/ \/__\/ ,< \/ \/_\/ \/ \/ \/_  \r\n\/_____\/_\/ \/_\/\\__, \/_\/_\/ \/_\/\\___\/   \\____\/\\____\/\\___\/_\/|_\/ .___\/_\/\\__\/  \r\n   \/ \/_  __ \/____\/\/   |  _  ______  ____    (_)   ____ \/_\/             \r\n  \/ __ \\\/ \/ \/ \/  \/ \/| | | |\/_\/ __ \\\/ __ \\  \/ \/ | \/ \/ \/ \/ \/             \r\n \/ \/_\/ \/ \/_\/ \/  \/ ___ |_>  <\/ \/_\/ \/ \/ \/ \/ \/ \/| |\/ \/ \/_\/ \/              \r\n\/_.___\/\\__, \/  \/_\/  |_\/_\/|_|\\____\/_\/ \/_(_)_\/ |___\/\\__, \/               \r\n      \/____\/                                     \/____\/                \n\
https://developer.axonivy.com/team");

function buttonAddSpinner(button) {
  $(button).addClass('ui-state-disabled');
  var icon = $(button).find('.ui-icon');
  icon.removeClass(function (index, css) {
    return (css.match(/\si-\S+/g) || []).join(' '); // removes anything that starts with "si-"
  });
  $(icon).addClass('si-button-refresh-arrows si-is-spinning');
  window.onblur = function () {

  };
}

function buttonRemoveSpinner(button, defaultIcon) {
  var icon = $(button).find('.ui-icon')
  $(icon).removeClass('si-button-refresh-arrows si-is-spinning');
  $(icon).addClass(defaultIcon);
  $(button).removeClass('ui-state-disabled');
}
