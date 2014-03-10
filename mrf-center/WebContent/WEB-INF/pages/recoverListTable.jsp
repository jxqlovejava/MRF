<%@page import="java.lang.reflect.Array"%>
<%@page import="com.nali.lang.ToStringBuilder"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.nali.mrfcenter.domain.Recover,org.springframework.amqp.core.Message,
	com.nali.mrfcore.message.MessageSerializer,com.google.gson.Gson" %>
<%-- <%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%> --%>

<%!
Gson gson = new Gson();
public static boolean isChinese(char c) {  
    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
        || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
        || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
        || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
        || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
      return true;  
    }  
    return false;  
}

// 判断一个字符串是否包含乱码
public static boolean isMessyCode(String str) {  
	java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\s*|\t*|\r*|\n*");  
    java.util.regex.Matcher m = p.matcher(str);  
    String after = m.replaceAll("");  
    String temp = after.replaceAll("\\p{P}", "");  
    char[] ch = temp.trim().toCharArray();  
    float chLength = ch.length;  
    float count = 0;  
    for (int i = 0; i < ch.length; i++) {  
      char c = ch[i];  
      if (!Character.isLetterOrDigit(c)) {  
  
        if (!isChinese(c)) {  
          count = count + 1;  
          System.out.print(c);  
        }  
      }  
    }  
    float result = count / chLength;  
    if (result > 0.4) {  
      return true;  
    } else {  
      return false;  
    }  
}
%>
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
		<td class="businessMsgCol">
			<div align="center">
			<%
				String humanReadableStr = null;
			
			    String businessMsgJsonStr = ((Recover) pageContext.getAttribute("v")).getBusinessMsg();
				Message businessMessage = null;
				try {
					businessMessage = MessageSerializer.deserialize(businessMsgJsonStr);
				}
				catch(Exception e) {
					// swallow it
				}
				
				if(businessMessage != null) {
					String messageContentType = businessMessage.getMessageProperties().getContentType();
					String contentEncoding = businessMessage.getMessageProperties().getContentEncoding();
					contentEncoding = contentEncoding == null ? "UTF-8" : contentEncoding;
					byte[] messageBody = businessMessage.getBody();
					if(messageContentType.contains("json") || messageContentType.contains("text")) {
						humanReadableStr = new String(messageBody, contentEncoding);
					}
 				 	else if(messageContentType.equals("application/x-java-serialized-object")) {
						byte[] temp = new byte[messageBody.length - 9];
						System.arraycopy(messageBody, 8, temp, 0, temp.length);
 				 		humanReadableStr = "x-java-serialized-object: " + new String(temp, contentEncoding);
					}
					else if(messageContentType.equals("application/octet-stream")) {
						String byteStr = new String(messageBody, contentEncoding);   // 先尝试简单转成字符串
						humanReadableStr = isMessyCode(byteStr) ? gson.toJson(businessMessage.getMessageProperties()) : byteStr;
					}
					else {   // use MessageProperties to track the record
						humanReadableStr = gson.toJson(businessMessage.getMessageProperties());
					}
				}
		    %>
		    	<c:out value="<%=humanReadableStr%>">Empty Message String</c:out>
			</div>
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