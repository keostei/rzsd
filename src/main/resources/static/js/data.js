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
                alert('update success.');
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

function onDaoruClick(){
    var mydata = [
        {khbm:"AOBNS-01",id1:"rz.0001",nicheng:"日中小二",shoujianren:"张三",dizhi:"北京市海定区长安街103号A区3栋1108",quhuoriqi:"2017-09-27",zongzhongliang:"39",bgzl:"20,19",yunfeijiage:"37440",dangqianzhuangtai:"出库完成",nbbh:"RZ-A-01,RZ-A-02",kddh:"992518271,992518000",daorujieguo:"成功",dqzt:"4"},
        {khbm:"AOBNS-02",id1:"rz.0001",nicheng:"日中小二",shoujianren:"李四",dizhi:"上海市闵行区谈中路76弄107室",quhuoriqi:"2017-09-27",zongzhongliang:"19",bgzl:"19",yunfeijiage:"18240",dangqianzhuangtai:"出库完成",nbbh:"RZ-A-03",kddh:"992518273",daorujieguo:"成功",dqzt:"4"},
        {khbm:"AOMXN-01",id1:"rz.0002",nicheng:"4C太阳",shoujianren:"王超",dizhi:"江苏省南京市奥体大街68号新城科技园",quhuoriqi:"2017-09-28",zongzhongliang:"76",bgzl:"20,19,18,19",yunfeijiage:"71440",dangqianzhuangtai:"出库完成",nbbh:"RZ-A-04,RZ-A-05,RZ-A-06,RZ-A-07",kddh:"992518274,992518001,992518275,992518004,",daorujieguo:"成功",dqzt:"4"},
        {khbm:"AOPOA-01",id1:"rz.0003",nicheng:"多多",shoujianren:"李小风",dizhi:"江苏省南京市雨花区小行里丽景花园2栋",quhuoriqi:"2017-09-28",zongzhongliang:"34",bgzl:"17,17",yunfeijiage:"32640",dangqianzhuangtai:"出库完成",nbbh:"RZ-A-07,RZ-A-08",kddh:"992518279,992518098",daorujieguo:"成功",dqzt:"4"},
        {khbm:"AOLPO-01",id1:"rz.0004",nicheng:"大耳垂",shoujianren:"张晓晓",dizhi:"上海市虹口区教场路27号金砖大厦13楼",quhuoriqi:"2017-09-28",zongzhongliang:"39",bgzl:"20,19",yunfeijiage:"37440",dangqianzhuangtai:"出库完成",nbbh:"RZ-A-09,RZ-A-10",kddh:"992518280,992518990",daorujieguo:"成功",dqzt:"4"},
        {khbm:"AOIJK-01",id1:"rz.0005",nicheng:"Hujin",shoujianren:"胡一刀",dizhi:"浙江省杭州市溪湖区静安路3号",quhuoriqi:"2017-09-28",zongzhongliang:"17",bgzl:"17",yunfeijiage:"16320",dangqianzhuangtai:"出库完成",nbbh:"RZ-A-11",kddh:"992518281",daorujieguo:"成功",dqzt:"4"},
        {khbm:"AOBPL-01",id1:"rz.0006",nicheng:"蛋卷",shoujianren:"李寻欢",dizhi:"江苏省常州市天宁区夏城路117号-2",quhuoriqi:"2017-09-29",zongzhongliang:"18",bgzl:"18",yunfeijiage:"17280",dangqianzhuangtai:"打包完成",nbbh:"RZ-A-12",kddh:"992518282",daorujieguo:"成功",dqzt:"3"},
        {khbm:"AXPOS-01",id1:"rz.0007",nicheng:"都宝马",shoujianren:"晓峰",dizhi:"上海市黄浦区河南南路16号中汇大厦2楼",quhuoriqi:"2017-09-29",zongzhongliang:"18",bgzl:"18",yunfeijiage:"17280",dangqianzhuangtai:"打包完成",nbbh:"RZ-A-13",kddh:"992518283",daorujieguo:"成功",dqzt:"3"},
        {khbm:"ALMSA-01",id1:"rz.0008",nicheng:"小C",shoujianren:"段玉",dizhi:"江苏省南京市江宁区诚信大道68号精良彩云居12栋",quhuoriqi:"2017-09-29",zongzhongliang:"18",bgzl:"18",yunfeijiage:"17280",dangqianzhuangtai:"打包完成",nbbh:"RZ-A-14",kddh:"992518284",daorujieguo:"成功",dqzt:"3"},
        {khbm:"ALSPA-01",id1:"rz.0009",nicheng:"蛋卷",shoujianren:"李莫愁",dizhi:"江苏省常州市钟楼区广电西路2号",quhuoriqi:"2017-09-30",zongzhongliang:"0",yunfeijiage:"0",dangqianzhuangtai:"已取件",nbbh:"",kddh:"",daorujieguo:"成功",dqzt:"2"},
        {khbm:"ALMKA-01",id1:"rz.0010",nicheng:"豆饼",shoujianren:"王洋",dizhi:"上海市永和路118弄23号楼",quhuoriqi:"2017-09-30",zongzhongliang:"0",yunfeijiage:"0",dangqianzhuangtai:"已取件",nbbh:"",kddh:"",daorujieguo:"成功",dqzt:"2"},
        {khbm:"ALPSM-01",id1:"rz.0011",nicheng:"A龙祥",shoujianren:"李玉明",dizhi:"浙江省宁波市新城区柳东路28号威尼新城11-2-211",quhuoriqi:"2017-09-30",zongzhongliang:"0",yunfeijiage:"0",dangqianzhuangtai:"已取件",nbbh:"",kddh:"",daorujieguo:"成功",dqzt:"2"},
        {khbm:"MSKAL-01",id1:"rz.0012",nicheng:"wangwei",shoujianren:"王长苏",dizhi:"江苏省苏州市姑苏区寒山路27号创业大厦8楼",quhuoriqi:"2017-10-01",zongzhongliang:"0",yunfeijiage:"0",dangqianzhuangtai:"已取件",nbbh:"",kddh:"",daorujieguo:"成功",dqzt:"2"},
        {khbm:"AOPLS-01",id1:"rz.0013",nicheng:"KonfuMao",shoujianren:"荀彧",dizhi:"江苏省泰州市靖江市新福路幸福小区21号A区综合楼107室",quhuoriqi:"2017-10-01",zongzhongliang:"0",yunfeijiage:"0",dangqianzhuangtai:"已预约",nbbh:"",kddh:"",daorujieguo:"成功",dqzt:"1"},
        {khbm:"AOPLA-01",id1:"rz.0014",nicheng:"MiyakoJima",shoujianren:"曹操",dizhi:"江苏省南京市浦口区沿山大道23号",quhuoriqi:"2017-10-01",zongzhongliang:"0",yunfeijiage:"0",dangqianzhuangtai:"已预约",nbbh:"",kddh:"",daorujieguo:"成功",dqzt:"1"}
        ]
    jQuery("#jqGrid").jqGrid({
        //url:'server.php?q=2',
        datatype: "local",
        colNames:['客户编码','用户ID','昵称','收件人', '地址', '取货日期','重量','运费总计','当前状态','内部编号','物流单号','包裹重量','导入结果（隐藏）','当前状态（隐藏）'],
        colModel:[
            {name:'khbm',index:'khbm', width:55, sortable:false},
            {name:'id1',index:'id1', width:55, sortable:false,hidden:true},
            {name:'nicheng',index:'nicheng', width:90, sortable:false,hidden:true},
            {name:'shoujianren',index:'shoujianren',align:"center", width:70},
            {name:'dizhi',index:'dizhi', width:180},
            {name:'quhuoriqi',index:'quhuoriqi', width:100,align:"center",hidden:true},
            {name:'zongzhongliang',index:'zongzhongliang', width:50,align:"right"},
            {name:'yunfeijiage',index:'yunfeijiage', width:70,align:"right"},
            {name:'dangqianzhuangtai',index:'dangqianzhuangtai', align:"center", width:100},
            {name:'nbbh',index:'nbbh', width:100, sortable:false},
            {name:'kddh',index:'kddh', width:100, sortable:false},
            {name:'bgzl',index:'bgzl', width:100, sortable:false},
            {name:'daorujieguo',index:'daorujieguo', width:150, sortable:false,hidden:true},
            {name:'dqzt',index:'dqzt', width:150, sortable:false,hidden:true}
        ],
        rowNum:10,
        rowList:[10],
        pager: '#jqGridPager',
        sortname: 'id',
        viewrecords: true,
        quhuoriqi: "asc",
        width:window.screen.availWidth-300,
        autowidth: false,
        shrinkToFit: true,
        data : mydata,
        onSelectRow:function (rowid, status) {
            var rowData=$('#jqGrid').jqGrid('getRowData',rowid);
            $('#txtKhbm').val(rowData.khbm);
            $('#txtUserId').val(rowData.id1);
            $('#txtWxnc').val(rowData.nicheng);
            $('#txtQjrq').val(rowData.quhuoriqi);
            $('#txtSjr').val(rowData.shoujianren);
            $('#txtAddress').val(rowData.dizhi);
            $('#txtBgzl').val(rowData.bgzl);
            $('#txtZzl').val(rowData.zongzhongliang);
            $('#txtYfjg').val(rowData.yunfeijiage);
            $('#selStatus').selectpicker('val', rowData.dqzt);
            $('#txtNbbh').val(rowData.nbbh);
            $('#txtKddh').val(rowData.kddh);
        }
    });
    jQuery("#jqGrid").jqGrid('navGrid','#jqGridPager',{edit:false,add:false,del:false,refresh:false});
}
