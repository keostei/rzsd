$(document).ready(function(){
	var mydata = [];
	jQuery("#jqGrid").jqGrid({
        datatype: "local",
        colNames:['流水号','客户编码','取件日期','收件人', '地址', '状态（隐藏）','当前状态','总重量','运费总计','内部编号（隐藏）','内部编号','物流单号（隐藏）','物流单号','重量'],
        colModel:[
            {name:'invoiceId',width:50, sortable:false, hidden:true},
            {name:'customCd',index:'customCd', width:65, align:"center"},
            {name:'invoiceDate',index:'invoiceDate', width:80, align:"center", sortable:false},
            {name:'name',index:'name',align:"center", width:60},
            {name:'address',index:'address', width:180},
            {name:'invoiceStatus',index:'invoiceStatus', width:100,align:"center", sortable:false, hidden:true},
            {name:'invoiceStatusName',index:'invoiceStatusName', align:"center",width:65},
            {name:'totalWeight', width:55,align:"right",sortable:false},
            {name:'invoiceAmountJpy',align:"right", width:65,sortable:false},
            {name:'lotNo',index:'lotNo', width:100, hidden:true},
            {name:'dispLotNo',index:'dispLotNo', width:135},
            {name:'trackingNo',width:100, sortable:false,hidden:true},
            {name:'dispTrackingNo',index:'dispTrackingNo', width:135},
            {name:'dispWeight',index:'dispWeight', width:80, sortable:false}
        ],
        rowNum:10,
        rowList:[10],
        pager: '#jqGridPager',
        sortname: 'id',
        viewrecords: true,
        invoiceDate: "asc",
        width:window.screen.availWidth-300,
        autowidth: false,
        height:211,
        shrinkToFit: true,
        data : mydata,
        onSelectRow:function (rowid, status) {
            var rowData=$('#jqGrid').jqGrid('getRowData',rowid);
            $('#hidInvoiceId').val(rowData.invoiceId);
            $('#txtCustomCd').val(rowData.customCd);
            $('#txtAddress').val(rowData.address);
            $('#txtName').val(rowData.name);
            $('#selInvoiceStatus').selectpicker('val', rowData.invoiceStatus);
            $('#txtTotalWeight').val(rowData.totalWeight);
            $('#txtInvoiceAmountJpy').val(rowData.invoiceAmountJpy);
            $('#txtDispLotNo').val(rowData.dispLotNo);
            $('#txtDispTrackingNo').val(rowData.dispTrackingNo);
            $('#txtDispWeight').val(rowData.dispWeight);
        }
    });
    jQuery("#jqGrid").jqGrid('navGrid','#jqGridPager',{edit:false,add:false,del:false,refresh:false});
    doSearch();
});


$(function(){
    $('#btnModify').click(function(){
        doAjax({
            url : 'rest/system/edit_data',
            type : 'POST',
            dataType : "json",
            data : {'invoiceId': $('#hidInvoiceId').val(),
            	'customCd': $('#txtCustomCd').val(),
            	'name': $('#txtName').val(),
            	'address': $('#txtAddress').val(),
            	'invoiceStatus': $('#selInvoiceStatus').val(),
            	'totalWeight': $('#txtTotalWeight').val(),
            	'invoiceAmountJpy': $('#txtInvoiceAmountJpy').val(),
            	'dispLotNo': $('#txtDispLotNo').val(),
            	'dispTrackingNo': $('#txtDispTrackingNo').val(),
            	'dispWeight': $('#txtDispWeight').val()
            	},
            onSuccess : function(json) {
                alert('数据更新成功。');
            }
        });
        return false;
    });
    
    $('#btnSearch').click(function(){
    	doSearch();
    });
});

function doSearch(){
    doAjax({
        url : 'rest/system/search_data',
        type : 'POST',
        dataType : "json",
        data : {'condCustomCd': $('#txtCondCustomCd').val(),
        	'condName': $('#txtCondName').val(),
        	'condInvoiceStatus': $('#txtCondInvoiceStatus').val(),
        	'condInvoiceDateFrom': $('#txtCondInvoiceDateFrom').val(),
        	'condInvoiceDateTo': $('#txtCondInvoiceDateTo').val()
        	},
        onSuccess : function(json) {
            $("#jqGrid").jqGrid('clearGridData');
            $("#jqGrid").jqGrid('setGridParam',{
                  data : json, 
            }).trigger("reloadGrid");
        }
    });
}
