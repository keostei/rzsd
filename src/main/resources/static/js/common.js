var doAjax = function(ajaxParam){
    var method = 'POST';
    if(ajaxParam.type) {
        method = ajaxParam.type;
    }
    var dataType = 'json';
    if(ajaxParam.dataType){
        dataType = ajaxParam.dataType;
    }
    $.ajax(ajaxParam.url, {
        type : method,
        dataType : dataType,
        data : ajaxParam.data
    }).done(function(json) {
        if(json.isFail){
            alert('服务器响应失败，请稍候再试。');
            return;
        }
        if(ajaxParam.onSuccess){
            ajaxParam.onSuccess(json);
        }
    }).fail(function(xhr) {
        var messages = "";
        if(401 == xhr.status){
        	window.location.href = '/login';
        	return;
        } else if(400 <= xhr.status && xhr.status <= 499){
            var contentType = xhr.getResponseHeader('Content-Type');
            if (contentType != null && contentType.indexOf("json") != -1) {
                json = $.parseJSON(xhr.responseText);
                messages = json.message;
            } else {
                messages = (xhr.statusText);
            }
        }else{
            messages = ("System error occurred.");
        }
        alert(messages);
    });
}