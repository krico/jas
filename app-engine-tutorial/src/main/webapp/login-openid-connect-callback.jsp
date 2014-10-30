<%@ page import="ch.findmyslot.tutorial.appengine.Constants" %>
<%@ page import="com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl" %>
<%@ page import="java.util.Objects" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%--
  Created by IntelliJ IDEA.
  User: krico
  Date: 30/10/14
  Time: 22:31
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>OpenID Connect Login with JSP - Callback</title>
</head>
<body>
<h1>OpenID Connect Login with JSP - Callback</h1>
<%
    String state = (String) session.getAttribute(Constants.Session.OpenIDState);

    StringBuffer fullUrlBuf = request.getRequestURL();
    if (request.getQueryString() != null) {
        fullUrlBuf.append('?').append(request.getQueryString());
    }
    AuthorizationCodeResponseUrl authResponse = new AuthorizationCodeResponseUrl(fullUrlBuf.toString());

%>

<% if (authResponse.getError() != null) {%>
<h2>
    <span style="color: red; ">Denied: </span> <%=authResponse.getError()%>
</h2>
<%if (StringUtils.isNoneBlank(authResponse.getErrorDescription())) {%>
<a href="<%=authResponse.getErrorUri()%>">
    <%= authResponse.getErrorDescription()%>
</a>
<%}%>
<a href="login-openid-connect.jsp">Try again</a>
<%} else if (StringUtils.isBlank(state) || !StringUtils.equals(state, authResponse.getState())) {%>
<h2>
    <span style="color: red; ">Invalid state!</span>
</h2>
<a href="login-openid-connect.jsp">Try again</a>
<%} else {%>
<h2>
    <span style="color: green; ">Success!</span>
</h2>

<p>
    <b>Code: </b><%=authResponse.getCode()%>
</p>
<a href="login-openid-connect.jsp">Login once more</a>
<% }%>
</body>
</html>
