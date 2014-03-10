<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- <%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%> --%>

<c:forEach var="v" items="${page.items}">
<c:choose>
	<c:when test="${v.recoverState==0}">
		<tr bgcolor="#FFFFFF" id="${v.msgID}">
	</c:when>
	<c:otherwise>
		<tr bgcolor="#E6E6FA" id="${v.msgID}">
	</c:otherwise>
</c:choose>
		<td>
			<div align="center">
				<input type="checkbox" value="${v.msgID}" name="msgIDBox" 
					<c:if test="${v.recoverState==1}">class="unRecoverable"</c:if> />
			</div>
		</td>
		<td>
			<div align="center">${v.msgID}</div>
		</td>
		<td>
			<div align="center">${v.recoverState == 1 ? "恢复中" : "待恢复"}</div>
		</td>
		<td>
			<div align="center">${v.retryQueueName}</div>
		</td>
		<td>
			<div align="center"><%-- ${v.businessMsg} --%></div>
		</td>
		<td>
			<div align="center">${v.retryInterval}</div>
		</td>
		<td>
			<div align="center">${v.retriedTimes}</div>
		</td>
		<td>
			<div align="center">${v.maxRetryTimes}</div>
		</td>
		<td>
			<div align="center">${v.checkoutAt}</div>
		</td>
		<td>
			<div align="center">${v.updatedAt}</div>
		</td>
		<td>
			<div align="center">${v.createdAt}</div>
		</td>
		<td>
			<div align="center">
			 <c:if test="${v.recoverState==0}">
			 	<a href="javascript:void(0);" onClick="recoverRecord(${v.msgID})">恢复</a>
				/
			 </c:if>
				<a href="javascript:void(0);" onClick="deleteRecord(${v.msgID})">删除</a>
			</div>
		</td>
	</tr>
</c:forEach>
<input type="hidden" id="recordTotalCount" value="${page.totalCount}" />