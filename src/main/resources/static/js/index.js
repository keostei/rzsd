$(document).ready(function(){
//    loadBooks('1');
});

$(function(){
	$('#selCustomId').change(function(){
		alert($('#selCustomId').val());
	});
    $('.save-adderss').click(function(){
        var obj = $(this);
        doAjax({
            url : 'rest/custom/edit',
            type : 'POST',
            dataType : "json",
            data : {'customId': $(obj).next().val(),
            	'rowNo': $(obj).next().next().val(),
            	'name': $(obj).parent().prev().prev().children('.form-control').val(),
            	'telNo': $(obj).parent().prev().children('.form-control').val(),
            	'address': $(obj).parent().prev().prev().prev().children('.form-control').val()},
            onSuccess : function(json) {
                alert('地址修改成功！');
            }
        });
        return false;
    });
});