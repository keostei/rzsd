$(document).ready(function(){
//    loadBooks('1');
});

$(function(){
    $('#btnDownloadCustomInfo').click(function(){
        doAjax({
            url : 'rest/download_invoice_output',
            type : 'POST',
            dataType : "json",
            onSuccess : function(json) {
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