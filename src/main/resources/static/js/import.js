$(document).ready(function(){
//    loadBooks('1');
});

$(function(){
    $('#btnImport').click(function(){
    	var formData = new FormData($("#uploadForm")[0]);
    	$.ajax('rest/system/import', {
    		type: 'POST',  
            data: formData,  
            async: false,  
            cache: false,  
            contentType: false,  
            processData: false
        }).done(function(json) {
            if(json.isFail){
                alert('服务器响应失败，请稍候再试。');
                return;
            }
            alert('成功');
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
        return false;
    });
});