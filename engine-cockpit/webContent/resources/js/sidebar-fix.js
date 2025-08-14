jQuery(function () {
  function initFix() {
    if (FreyaEnvironment?.isInitialized !== true) {
      window.setTimeout(initFix, 10);
      return;
    }
    $.removeCookie('freya_expandeditems', { path: '/' });
    let page = window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1);
    page = searchCorrectMenuItemPage(page);
    $('#menuform>ul>li>a').each(function () {
      const menuItemPage = $(this).attr('href');
      const menuItemId = $(this).parent().attr('id');
      if (menuItemPage != "#") {
        if (menuItemPage.startsWith(page)) {
          activateMenuItem(menuItemId);
        }
      }
      else {
        $(this).parent().find('ul>li>a').each(function () {
          const subMenuItemPage = $(this).attr('href');
          const subMenuItemId = $(this).parent().attr('id');
          if (subMenuItemPage != "#") {
            if (subMenuItemPage.startsWith(page)) {
              activateMenuItem(menuItemId);
              activateMenuItem(subMenuItemId);
            }
          }
          else {
            $(this).parent().find('ul>li>a').each(function () {
              const subSubMenuItemPage = $(this).attr('href');
              const subSubMenuItemId = $(this).parent().attr('id');
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
  }

  function searchCorrectMenuItemPage(value) {
    const map = {
      'security-detail.xhtml': 'securitysystem.xhtml',
      'identity-provider.xhtml': 'securitysystem.xhtml',
      'notificationDeliveries.xhtml': 'notifications.xhtml',
      'userdetail.xhtml': 'users.xhtml',
      'roledetail.xhtml': 'roles.xhtml',
      'application-detail.xhtml': 'applications.xhtml',
      'pmv-detail.xhtml': 'applications.xhtml',
      'notification-channel-detail.xhtml': 'notification-channels.xhtml',
      'databasedetail.xhtml': 'databases.xhtml',
      'restclientdetail.xhtml': 'restclients.xhtml',
      'webservicedetail.xhtml': 'webservices.xhtml',
      'businesscalendar-detail.xhtml': 'businesscalendar.xhtml',
      'monitorTraceDetail.xhtml': 'monitorTraces.xhtml',
      'searchindex.xhtml': 'searchengine.xhtml',
      'monitorStartEventDetails.xhtml': 'monitorStartEvents.xhtml',
      'monitorIntermediateEventDetails.xhtml': 'monitorIntermediateEvents.xhtml',
      'securitysystem-merge.xhtml': 'securitysystem.xhtml',
      'monitor-health-checks.xhtml': 'monitor-health.xhtml',
      'systemdb-info.xhtml': 'systemdb.xhtml'
    };
    return map[value] == null ? value : map[value];
  }

  function activateMenuItem(id) {
    const menuitem = $("#" + id.replace(/\\/g, "\\\\").replace(/:/g, "\\:"));;
    menuitem.addClass('active-menuitem');
    const submenu = menuitem.children('ul');
    if (submenu.length) {
      submenu.show();
    }
  }
  
  initFix();
});
