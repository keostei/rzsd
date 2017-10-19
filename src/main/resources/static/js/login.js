$(document).ready(function(){
//    loadBooks('1');
});

$(function(){
    $('#btnLogin').click(function(){
        doAjax({
            url : 'rest/login/auth',
            type : 'POST',
            dataType : "json",
            data : {'userName': $('#txtUserName').val(), 'password' : md5($('#txtPassword').val())},
            onSuccess : function(json) {
            	window.location.href = '/';
            }
        });
        return false;
    });
});