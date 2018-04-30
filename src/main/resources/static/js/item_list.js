$(document).ready(function(){
    // do nothing.
});
$(function(){
    $('.item-display').click(function(){
        var obj = $(this);
        $(obj).hide();
        $(obj).parent().children('.item-edit').show();
        $(obj).parent().children('.item-edit-submit').show();
        return false;
    });
    
    $('.item-edit-submit').click(function(){
    	var obj = $(this);
    	doAjax({
            url : '/rest/shop/update_item_name',
            type : 'POST',
            dataType : "json",
            data : {'shopId': $('#hidShopId').val(),
                'barCode': $(obj).prev().val(),
                'itemName': $(obj).parent().children('.item-edit').val()},
            onSuccess : function(json) {
            	$(obj).hide();
                $(obj).parent().children('.item-edit').hide();
                $(obj).parent().children('.item-display').text($(obj).parent().children('.item-edit').val());
                $(obj).parent().children('.item-display').show();
            }
        });
    	return false;
    });
    
});