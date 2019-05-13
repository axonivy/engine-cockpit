PF('sidebar_menu').saveMenuState = function(){
  if(this.wrapper.hasClass('layout-wrapper-static'))
    $.cookie('serenity_menu_static', 'serenity_menu_static', {path: '/'});
  else
    $.removeCookie('serenity_menu_static', {path: '/'});
};

PF('sidebar_menu').restoreMenuState = function(){
  var sidebarCookie = $.cookie('serenity_menu_static');
  if(sidebarCookie) {
    this.wrapper.addClass('layout-wrapper-static layout-wrapper-static-restore');
  }
}

$(document).ready(function(){
  $.removeCookie('serenity_expandeditems', {path: '/'});
  var page = window.location.pathname.substr(window.location.pathname.lastIndexOf('/') + 1);
  page = searchCorrectMenuItemPage(page);
  $('#menuform>ul>li>a').each(function() {
	var menuItemPage = $(this).attr('href');
	var menuItemId = $(this).parent().attr('id');
	if (menuItemPage != "#") {
	  if (menuItemPage.startsWith(page)) {
	    activateMenuItem(menuItemId);
      }
    }
	else {
      $(this).parent().find('ul>li>a').each(function() {
        var subMenuItemPage = $(this).attr('href');
        var subMenuItemId = $(this).parent().attr('id');
        if (subMenuItemPage.startsWith(page)) {
          activateMenuItem(menuItemId);
          activateMenuItem(subMenuItemId);
        }
      });
    }
  });

  function searchCorrectMenuItemPage(value) {
    var map = {
      'security-detail.xhtml': 'securitysystem.xhtml',
      'userdetail.xhtml': 'users.xhtml',
      'roledetail.xhtml': 'roles.xhtml',
      'application-detail.xhtml': 'applications.xhtml'
    };
    return map[value] == null ? value : map[value];
  }

  function activateMenuItem(id) {
    var menuitem = $("#" + id.replace(/:/g, "\\:"));
    menuitem.addClass('active-menuitem');
    var submenu = menuitem.children('ul');
    if(submenu.length) {
      submenu.show();
    }
  }
});
