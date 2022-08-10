jQuery(function () {
  function initFix() {
    try {
      PF('sidebar_menu');
      $.removeCookie('freya_expandeditems', { path: '/' });
      var page = window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1);
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
    } catch (ex) {
      var timeoutID = window.setTimeout(initFix, 10);
    }
  }

  function searchCorrectMenuItemPage(value) {
    var map = {
      'security-detail.xhtml': 'securitysystem.xhtml',
      'security-ldap.xhtml': 'securitysystem.xhtml',
      'security-provider.xhtml': 'securitysystem.xhtml',
      'userdetail.xhtml': 'users.xhtml',
      'roledetail.xhtml': 'roles.xhtml',
      'application-detail.xhtml': 'applications.xhtml',
      'pmv-detail.xhtml': 'applications.xhtml',
      'databasedetail.xhtml': 'databases.xhtml',
      'restclientdetail.xhtml': 'restclients.xhtml',
      'webservicedetail.xhtml': 'webservices.xhtml',
      'businesscalendar-detail.xhtml': 'businesscalendar.xhtml',
      'monitorTraceDetail.xhtml': 'monitorTraces.xhtml'
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
  
  initFix();
});
