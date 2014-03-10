<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Recover List Page</title>
<link href="css/pagination.css" rel="stylesheet" type="text/css" />
<script src="js/jquery.js" type="text/javascript"></script>
<script src="js/jquery.pagination.js" type="text/javascript"></script>
<script type="text/javascript">
	var pageSize = 15;
	var getPageRecoversUrl = "getPageRecovers";
	var recoverRecordUrl = "recoverRecord";
	var batchRecoverRecordsUrl = "batchRecoverRecords";
	var deleteRecordUrl = "deleteRecord";
	var batchDeleteRecordsUrl = "batchDeleteRecords";

	$(function() {
		// 默认加载第一页数据
		initData(0, getPageRecoversUrl);
		
		// 全选和反全选
		$("#allBox").click(function() {
			$('input[name="msgIDBox"]').attr("checked",this.checked); 
		});
		var $msgIDBox = $("input[name='msgIDBox']");
		$msgIDBox.live("click", function(){
        	$("#allBox").attr("checked", $msgIDBox.length == $("input[name='msgIDBox']:checked").length ? true : false);
        });
		
		// 批量删除、批量恢复事件
		$("#batchRecoverBt").click(function() {
			batchRecoverRecords();
		});
		$("#batchDeleteBt").click(function() {
			batchDeleteRecords();
		});
	});
	
	function initData(pageindx, url) {
		var pageCount = "";
		// var arg = $("#fn").val();
		var datas = "pageIdx=" + (pageindx) + "&pageSize=" + (pageSize);
/* 		if (arg != "") {
			datas += "&&conditions=" + (arg);
		} */
		$.ajax({
			type : "GET",
			url : url,
			async : false,
			// dataType : "json",
			data : datas,
			success : function(tableBodyHtml) {
				$("#pageeee").html("").append(tableBodyHtml);
				pageCount = $("#recordTotalCount").val(); // total record count
			}
		});
		
		if (pageCount > 0) {
			$("#Pagination").pagination(pageCount,
										{
											callback: pageselectCallback,
											prev_text: '<< 上一页',
					                		next_text: '下一页 >>',
											items_per_page: 4,
											num_display_entries: 2,
											current_page: pageindx,
											num_edge_entries: 3
										});
		}
	}
	
	function pageselectCallback(index) {
		initData(index, getPageRecoversUrl);
	}
	
	// 人工恢复
	function recoverRecord(msgID) {
	 	$.ajax({
			type : "POST",
			url : recoverRecordUrl,
			async : false,
			data : "msgID=" + msgID,
			success : function(result) {
				if(result.successFlag == true) {
					reloadCurPage();
				}
				else {
					alert("人工恢复失败，错误：" + JSON.stringify(result));
				}
			}
		});
	}
	
	// 批量人工恢复
	function batchRecoverRecords() {
		var msgIDs = [];
		var selectedMsgIDBoxs = $("input[name='msgIDBox']:checked");
		if(selectedMsgIDBoxs.hasClass("unRecoverable")) {
			alert("有的记录不可恢复，请确认！");
			return;
		}
		selectedMsgIDBoxs.each(function(index, item) {
			msgIDs.push($(item).val());
		});
		
		$.ajax({
			type : "POST",
			url : batchRecoverRecordsUrl,
			async : false,
			dataType : "json",
			data : { "msgIDs": msgIDs },
			success : function(result) {
				if(result.successFlag == false) {
					alert("部分或全部记录人工恢复失败，错误：" + JSON.stringify(result));
				}
				reloadCurPage();
			}
		});
	}
	
	// 直接删除
	function deleteRecord(msgID) {
		$.ajax({
			type : "POST",
			url : deleteRecordUrl,
			async : false,
			data : "msgID=" + msgID,
			success : function(result) {
	 			if(result.successFlag == true) {
	 				reloadCurPage();
				}
				else {
					alert("删除失败，错误：" + JSON.stringify(result));
				}
			}
		});
	}
	
	// 批量删除
	function batchDeleteRecords() {
		var msgIDs = [];
		$("input[name='msgIDBox']:checked").each(function(index, item) {
			msgIDs.push($(item).val());
		});
		
		$.ajax({
			type : "POST",
			url : batchDeleteRecordsUrl,
			async : false,
			dataType : "json",
			data : { "msgIDs": msgIDs },
			success : function(result) {
				if(result.successFlag == false) {
					alert("部分或全部记录人工恢复失败，错误：" + JSON.stringify(result));
				}
				reloadCurPage();
			}
		});
	}
	
	// 重新加载当前页
	function reloadCurPage() {
		$("#allBox").attr("checked", false);
		
		var curPageIndx = parseInt($("span.current").text(), 10) - 1;
		initData(curPageIndx, getPageRecoversUrl);
	}
</script>
<style type="text/css">
	.info {
		color: brown;
	}
	#recoverTable, #Pagination, #operatePanel {
		margin-top: 8px;
	}
	.businessMsgCol {
		max-width: 290px;
		background-color: #f5f5f5;
	}
	.businessMsgCol div {
		width: 288px;
		overflow: hidden;
		word-wrap:break-word;
		font-size: small;
		color: #ff0084;
	}
	.shortCol {
		max-width: 60px;
	}
</style>
</head>
<body>
	<div>
		&nbsp;<span class="info">注意：删除操作将忽略这条记录，并直接从数据库删除！</span>
	</div>
	<table id="recoverTable" style="width:100%">
		<tr class="tableHeader">
			<th nowrap align="middle">
				<input id="allBox" type="checkbox">
			</th>
			<th nowrap align="middle">
				<a>消息ID</a>
			</th>
			<th nowrap align="middle">
				<a>恢复状态</a>
			</th>
			<th nowrap align="middle">
				<a>重试队列</a>
			</th>
			<th nowrap align="middle" class="businessMsgCol">
				<a>业务消息</a>
			</th>
			<th nowrap align="middle">
				<a>重试间隔(ms)</a>
			</th>
			<th nowrap align="middle">
				<a>已重试次数</a>
			</th>
			<th nowrap align="middle">
				<a>最大重试次数</a>
			</th>
			<th nowrap align="middle">
				<a>检出时间</a>
			</th>
			<th nowrap align="middle">
				<a>更新时间</a>
			</th>
			<th nowrap align="middle">
				<a>创建时间</a>
			</th>
			<th nowrap align="middle">
				<a style="cursor: pointer;">操作</a>
			</th>
		</tr>
		<tbody id="pageeee"></tbody>
	</table>
	<div id="Pagination" class="flickr" style="text-align:left"></div>
	<div id="operatePanel">
		<input type="button" id="batchRecoverBt" value="批量恢复" />
		<input type="button" id="batchDeleteBt" value="批量删除" />
	</div>
</body>
</html>