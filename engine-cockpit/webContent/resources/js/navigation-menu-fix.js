if (!String.prototype.startsWith) {
  Object.defineProperty(String.prototype, 'startsWith', {
    value: function(search, pos) {
      pos = !pos || pos < 0 ? 0 : +pos;
      return this.substring(pos, pos + search.length) === search;
    }
  });
}

if (!String.prototype.endsWith) {
  String.prototype.endsWith = function(search, this_len) {
	if (this_len === undefined || this_len > this.length) {
	  this_len = this.length;
	}
	return this.substring(this_len - search.length, this_len) === search;
  };
}

$(document).ready(function() {
  function initFix() {
    if (PF('sidebar_menu') == null) {
      var timeoutID = window.setTimeout(initFix, 10);
    } else {
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
          'application-detail.xhtml': 'applications.xhtml',
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
        if(submenu.length) {
          submenu.show();
        }
      }
    }
  }
  initFix();
});
