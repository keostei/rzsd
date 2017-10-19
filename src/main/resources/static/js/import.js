$(document).ready(function(){
//    loadBooks('1');
});

$(function(){
    $('#btnImport').click(function(){
        doAjax({
            url : 'rest//system/import',
            type : 'POST',
            dataType : "json",
//            data : {'userName': $('#txtUserName').val(), 'password' : md5($('#txtPassword').val())},
            onSuccess : function(json) {
            	alert('done.');
            }
        });
        return false;
    });
});