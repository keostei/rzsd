$(document).ready(function(){
//    loadBooks('1');
});

$(function(){

    $('#btnConfirmApply').click(function(){
        window.location.href = '/confirm';
        false;
    });
    $('#btnDownloadCustomInfo').click(function(){
        doAjax({
            url : 'rest/system/invoice_output',
            type : 'POST',
            dataType : "json",
            onSuccess : function(json) {
            	var form=$("<form>");//定义一个form表单  
                form.attr("style","display:none");  
                form.attr("target","");  
                form.attr("method","post");  
                form.attr("action","system/invoice_download");  
                $("body").append(form);//将表单放置在web中  
                form.submit();//表单提交   
                console.log('数据已成功下载。');
            }
        });
        return false;
    });

    $('#btnImport').click(function(){
    	if($('#uploadFile').val() === ''){
    		alert('请选择回传单号文件。');
    		return;
    	}
    	var formData = new FormData($("#uploadForm")[0]);
    	$.ajax('rest/system/import', {
    		type: 'POST',  
            data: formData,  
            async: false,  
            cache: false,  
            contentType: false,  
            processData: false
        }).done(function(json) {
            var form=$("<form>");//定义一个form表单  
            form.attr("style","display:none");  
            form.attr("target","");  
            form.attr("method","post");  
            form.attr("action","system/invoice_download?path=" + json.optStr);  
            $("body").append(form);//将表单放置在web中  
            form.submit();//表单提交   
            if(json.fail){
                alert('数据导入失败，请查看错误结果!');
            } else {
            	alert('数据导入成功。');
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
        return false;
    });
    
    $('#btnDataModify').click(function(){
    	window.location.href = '/data';
    });

    $('#btnOnekeyClear').click(function(){
        doAjax({
            url : 'rest/system/onekey_update',
            type : 'POST',
            dataType : "json",
            data : {'lotNo': $('#txtLotNo').val()},
            onSuccess : function(json) {
            	alert('一键清关成功。');
            }
        });
        return false;
    });
    
    $('#btnSetRate').click(function(){
        doAjax({
            url : 'rest/system/setrate',
            type : 'POST',
            dataType : "json",
            data : {'value': $('#txtRate').val()},
            onSuccess : function(json) {
            	alert('汇率设置成功。');
            }
        });
        return false;
    });
});