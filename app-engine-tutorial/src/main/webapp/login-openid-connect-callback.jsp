<%@ page import="ch.findmyslot.tutorial.appengine.Constants" %>
<%@ page import="com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl" %>
<%@ page import="com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest" %>
<%@ page import="com.google.api.client.auth.oauth2.TokenResponse" %>
<%@ page import="com.google.api.client.auth.oauth2.TokenResponseException" %>
<%@ page import="com.google.api.client.http.GenericUrl" %>
<%@ page import="com.google.api.client.http.javanet.NetHttpTransport" %>
<%@ page import="com.google.api.client.json.jackson2.JacksonFactory" %>
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

<h3>Retreived access token</h3>
<%
    TokenResponse tokenResponse = null;
    String exception = null;
    try {

        GenericUrl redirectUrl = new GenericUrl(request.getRequestURL().toString());
        redirectUrl.setRawPath("/login-openid-connect-callback.jsp");

        tokenResponse = new AuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                new GenericUrl(Constants.Endpoint.Token.Google), authResponse.getCode())
                .setRedirectUri(redirectUrl.build())
                .set(Constants.OpenID.Fields.ClientId, Constants.OpenID.Credentials.ClientID)
                .set(Constants.OpenID.Fields.ClientSecret, Constants.OpenID.Credentials.ClientSecret)
                .execute();
    } catch (TokenResponseException e) {
        if (e.getDetails() != null) {
            exception = "<b>error</b>: " + e.getDetails().getError();
            if (e.getDetails().getErrorDescription() != null) {
                exception += " <b>errorDescription</b>: " + e.getDetails().getErrorDescription();
            }
            if (e.getDetails().getErrorUri() != null) {
                exception += " <b>errorUri</b>: " + e.getDetails().getErrorUri();
            }
        } else {
            exception = "<b>message</>: " + e.getMessage();
        }
    }
%>
<p>
    <b>Token response:</b> <%=tokenResponse%><br>
</p>
<%if (StringUtils.isNoneBlank(exception)) {%>
<p>
    <b style="color: red; ">Exception:</b> <%=exception%>
</p>
<%}%>
<a href="login-openid-connect.jsp">Login once more</a>
<% }%>
</body>
</html>
