var restartStartTimestamp;

function redirectToHomeAfterRestart() {
  restartStartTimestamp = new Date();
  retryRedirectToHome();
}

function retryRedirectToHome() {
  window.setTimeout(redirectToHome, 500)
}

var restartError;

function redirectToHome() {
  let url = window.location.protocol + "//" + window.location.hostname+ ":" + window.location.port + "/";
  $.ajax(url)
    .done(function (response) {
      let timeSinceRestart = new Date().getTime() - restartStartTimestamp.getTime();
      if(restartError != null || timeSinceRestart > 30000) {
        window.location.href = url;
      } else {
        retryRedirectToHome();
      }
    })
    .fail(function (request, status, error) {
      restartError = error;
      if (request.status == 503) {
        window.location.href = url;
      } else {
        retryRedirectToHome();
      }
    });
}

