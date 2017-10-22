$(document).ready(function(){
//    loadBooks('1');
});

$(function(){
    $('#btnRegist').click(function(){
        doAjax({
            url : 'rest/login/regist',
            type : 'POST',
            dataType : "json",
            data : {'userName': $('#txtUserName').val(),
            	'password' : md5($('#txtPassword').val()),
            	'customId' : $('#txtCustomId').val(),
            	'name' : $('#txtName').val(),
            	'address' : $('#txtAddress').val(),
            	'telNo' : $('#txtTelNo').val()},
            onSuccess : function(json) {
            	window.location.href = '/';
            }
        });
        return false;
    });
});