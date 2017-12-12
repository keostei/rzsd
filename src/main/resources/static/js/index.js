$(document).ready(function(){
    initSelectBox();
});

function initSelectBox(){
    var strOpt = '';
    strOpt += getOptStr(1)
    strOpt += getOptStr(2);
    strOpt += getOptStr(3);
    $('#selCustomId').html(strOpt);
    setCustomInfo(1);
}
function getOptStr(rowNo){
    var strOpt = '';
    var address = $('#dizhi').children().eq(1).children().eq(1).children('.rzsd-address').children('.form-control').val();
    if(address == ''){
        return strOpt;
    }
    strOpt += '<option value="';
    strOpt += $('#dizhi').children().eq(rowNo).children().eq(1).children('.rzsd-customcd').children('.save-address-customid').val();
    strOpt += $('#dizhi').children().eq(rowNo).children().eq(1).children('.rzsd-customcd').children('.save-address-rowno').val();
    strOpt += '" '
    if(rowNo == 1){
        strOpt += ' selected="selected" ';
    }
    strOpt += ' >';
    strOpt += $('#dizhi').children().eq(rowNo).children().eq(1).children('.rzsd-customcd').children('.save-address-customid').val();
    strOpt += $('#dizhi').children().eq(rowNo).children().eq(1).children('.rzsd-customcd').children('.save-address-rowno').val();
    strOpt += '</option>';
    return strOpt;
}

function setCustomInfo(rowNo){
    $('#txtName').val($('#dizhi').children().eq(rowNo).children().eq(1).children('.rzsd-name').children('.form-control').val());
    $('#txtTelNo').val($('#dizhi').children().eq(rowNo).children().eq(1).children('.rzsd-telno').children('.form-control').val());
    $('#txtAddress').val($('#dizhi').children().eq(rowNo).children().eq(1).children('.rzsd-address').children('.form-control').val());

}
$(function(){
    $('#selCustomId').change(function(){
        setCustomInfo(Number($('#selCustomId').val().substring(4)));
    });
    $('.save-adderss').click(function(){
        var obj = $(this);
        if($(obj).parent().prev().prev().children('.form-control').val() == ""){
            alert('请填写收件人姓名！');
            return false;
        }
        if($(obj).parent().prev().children('.form-control').val() == ""){
            alert('请填写收件人电话号码！');
            return false;
        }
        if($(obj).parent().prev().prev().prev().children('.form-control').val() == ""){
            alert('请填写收件人地址！');
            return false;
        }
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
                initSelectBox();
                alert('地址修改成功！');
            }
        });
        return false;
    });
    $('#btnAppointment').click(function(){
        if($('#txtInvoiceDate').val() == ""){
            alert('请填写期望取件日期！');
            return false;
        }
        if($('#txtInvoideAddress').val() == ""){
            alert('请填写取件地址！');
            return false;
        }
        if($('#txtWeight').val() == ""){
            alert('请填写预估取件重量！');
            return false;
        }
        if($('#txtName').val() == ""){
            alert('客户编码对应的收件人信息未设置，请前往【收件地址】中进行设置！');
            return false;
        }
        doAjax({
            url : 'rest/custom/appoint',
            type : 'POST',
            dataType : "json",
            data : {'invoiceDate': $('#txtInvoiceDate').val(),
                'invoiceTimeCd': $('#txtInvoiceTimeCd').val(),
                'invoideAddress': $('#txtInvoideAddress').val(),
                'weight': $('#txtWeight').val(),
                'customCd': $('#selCustomId').val(),
                'name': $('#txtName').val(),
                'telNo': $('#txtTelNo').val(),
                'address': $('#txtAddress').val(),
                'invoiceRequirement': $('#txtInvoiceRequirement').val()},
            onSuccess : function(json) {
                alert('预约申请已提交！\n为了更好的解约您的时间，建议添加工作人员微信号确定取货时间，谢谢！');
            }
        });
        return false;
    });
});