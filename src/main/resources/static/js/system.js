$(document).ready(function(){
//    loadBooks('1');
});

$(function(){
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