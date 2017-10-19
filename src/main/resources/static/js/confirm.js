$(document).ready(function(){
//    loadBooks('1');
});

$(function(){
    $('.rzsd-confirm').click(function(){
        var obj = $(this);
        doAjax({
            url : 'rest/invoice/confirm',
            type : 'POST',
            dataType : "json",
            data : {'invoiceId': $(this).parent().children('.rzsd-invoiceid').val()},
            onSuccess : function(json) {
                $(obj).parent().parent().parent().removeClass('panel-warning');
                $(obj).parent().parent().parent().addClass('panel-success');
                $(obj).parent().parent().parent().children('.panel-heading').html('已确认');
                $(obj).parent().children('.rzsd-confirm').hide();
                $(obj).parent().children('.rzsd-reject').hide();
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