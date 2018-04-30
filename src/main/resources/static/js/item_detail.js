$(document).ready(function(){
    // do nothing.
});
$(function(){
    $('.invoice-del').click(function(){
    	var obj = $(this);
    	doAjax({
            url : '/rest/shop/del_shop_item_invoice',
            type : 'POST',
            dataType : "json",
            data : {'shopInvoiceId': $(obj).next().val()},
            onSuccess : function(json) {
            	$(obj).parent().parent().hide();
            }
        });
    	return false;
    });
    
});