<%@ include file="include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
    <meta content='width=device-width, initial-scale=1' name='viewport'/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/phone-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/internal/css/css-menu/menu-style.css" type='text/css' media="screen"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static2/external/jquery/css/jquery-ui.css"/>
</head>
<body>
<form:form method="post"
        action="${pageContext.request.contextPath}/open/chat/conversation.htm"
        modelAttribute="chatConversationForm">

    <form:hidden path="chatMessagesAsString" value="${chatConversationForm.chatMessagesAsString}" />
    <form:hidden path="chatNounsAsString" value="${chatConversationForm.chatNounsAsString}" />

    <div class="admin-content">
        <div class="add-new">
            <ul class="list-form">
                <li>
                    <div class="col-lable3">
                        <form:label path="conversation" cssErrorClass="lb_error">Chat</form:label>
                    </div>
                    <div class="col-fields">
                        <form:input path="conversation" cssClass="form-field-admin" cssErrorClass="form-field-admin error-field" />
                    </div>
                    <div class="clearFix"></div>
                </li>
            </ul>

            <div class="col-lable3"></div>
            <div class="col-fields">
                <div class="button-btn">
                    <button name="search" class="ladda-button next-btn" style="width:48%; float: left">Send</button>
                </div>
                <div class="clearFix"></div>
            </div>
            <div class="clearFix"></div>
        </div>
    </div>

    <c:choose>
        <c:when test="${fn:length(chatConversationForm.chatMessages) != 0}">
        <div class="add-store">
            <div class="store-table">
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <th width="4%">&nbsp;</th>
                        <th width="96%">Conversation</th>
                    </tr>
                    <c:forEach items="${chatConversationForm.chatMessages}" var="chatMessage" varStatus="status">
                    <tr>
                        <td>${status.count}&nbsp;</td>
                        <td>${chatMessage}</td>
                    </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
        </c:when>
        <c:otherwise>
            <div class="alert-info">
                <div class="no-approve">Welcome!</div>
            </div>
        </c:otherwise>
    </c:choose>


    <c:if test="${fn:length(chatConversationForm.chatNouns) != 0}">
        <div class="add-store">
            <div class="store-table">
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <th width="4%">&nbsp;</th>
                        <th width="96%">Context</th>
                    </tr>
                    <c:forEach items="${chatConversationForm.chatNouns}" var="chatNoun" varStatus="status">
                    <tr>
                        <td>${status.count}&nbsp;</td>
                        <td>${chatNoun}</td>
                    </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
    </c:if>
</form:form>
</body>
</html>
