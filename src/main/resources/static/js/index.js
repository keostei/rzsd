$(document).ready(function(){
//    loadBooks('1');
});

$(function(){
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
    
    $('.rzsd-reject').click(function(){
        var obj = $(this);
        if(confirm("本次操作不可取消，确认要否认这条发货申请吗？")){
            doAjax({
                url : 'rest/invoice/reject',
                type : 'POST',
                dataType : "json",
                data : {'invoiceId': $(this).parent().children('.rzsd-invoiceid').val()},
                onSuccess : function(json) {
                    $(obj).parent().parent().parent().removeClass('panel-warning');
                    $(obj).parent().parent().parent().addClass('panel-danger');
                    $(obj).parent().parent().parent().children('.panel-heading').html('已否认');
                    $(obj).parent().children('.rzsd-confirm').hide();
                    $(obj).parent().children('.rzsd-reject').hide();
                }
            });
        }
        return false;
    });
});