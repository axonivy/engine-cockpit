var file;
var directUpload = false;
var uploadUrl = "";
var uploadErrorField = "#uploadError";
var headers = { "X-Requested-By": "engine-cockpit" };

function initFileUpload() {
  $(document).ready(function (e) {
    handleFile(document.getElementById('fileInput').files[0]);
  });

  $("#fileInput").on("change", function (e) {
    handleFile(e.target.files[0]);
  });

  $('#drop_zone').on('drag dragstart dragend dragover dragenter dragleave drop', function (e) {
    e.preventDefault();
    e.stopPropagation();
  }).on('dragover dragenter', function () {
    $('#drop_zone').addClass('is-dragover');
  }).on('dragleave dragend drop', function () {
    $('#drop_zone').removeClass('is-dragover');
  }).on('drop', function (e) {
    if (e.originalEvent.dataTransfer && e.originalEvent.dataTransfer.files.length) {
      handleFile(e.originalEvent.dataTransfer.files[0]);
    }
  });
}

function handleFile(file) {
  if (checkFileInput(file)) {
    this.file = file;
    $('#selectedFileOutput').text(file.name + " (" + file.size / 1000 + "kB)");
    if (directUpload) {
      upload();
    }
  } else {
    $('#selectedFileOutput').text("Choose or drop a file which ends with: " + accepts);
    this.file = null;
  }
}

function checkFileInput(file) {
  if (file != null && endsWithAnyAccepts(file.name)) {
    return true;
  }
  return false;
}

function endsWithAnyAccepts(fileName) {
  return accepts.some(function (accept) {
    return fileName.endsWith(accept);
  });
}

function upload() {
  if (!file) {
    $(uploadErrorField).html("<i class='si si-alert-circle'></i> Choose a valid file before upload.");
    return;
  }

  $.ajax({
  
  xhr: function() {
    var xhr = new window.XMLHttpRequest();
    xhr.upload.addEventListener("progress", function(evt) {
      if (evt.lengthComputable) {
        var percentComplete = evt.loaded / evt.total;
        percentComplete = parseInt(percentComplete * 100);
        uploadProgress(percentComplete);
      }}, false);
      return xhr;
    },
    url: uploadUrl,
    mimeType: 'multipart/form-data',
    cache: false,
    contentType: false,
    processData: false,
    data: buildFormData(),
    method: 'POST',
    headers: headers,
    async: true,
    crossDomain: false,
    beforeSend: function (xhr) {
      beforeUpload();
    }
  }).done(function (response) {
    $('#uploadLog').text(response);
    $('#uploadStatus').html("<i class='si si-check-circle-1'></i> Success").css("color", "green");
    uploadDone();
  }).fail(function (request, status, error) {
    $('#uploadLog').text(request.responseText);
    $('#uploadStatus').html("<i class='si si-alert-circle'></i> Error").css("color", "red");
  }).always(function () {
    uploadedAlways();
  });
}

function buildFormData() {
  var form_data = new FormData();
  if (!uploadUrl.endsWith('licence')) {
    form_data.append('fileToDeploy', file, file.name);
    addDeployOptions(form_data);
  } else {
    form_data.append('licence', file, file.name);
  }
  return form_data;
}

function beforeUpload() {
  //can be overwritten
}

function uploadDone() {
  //can be overwritten
}

function uploadedAlways() {
  //can be overwritten
}

function uploadProgress(percent) {
  //can be overwritten
  console.log("uploaded: "+percent+"%");
}

