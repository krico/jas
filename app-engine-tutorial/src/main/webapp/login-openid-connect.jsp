<%@ page import="ch.findmyslot.tutorial.appengine.Constants" %>
<%@ page import="com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl" %>
<%@ page import="com.google.api.client.http.GenericUrl" %>
<%@ page import="java.math.BigInteger" %>
<%@ page import="java.security.SecureRandom" %>
<%@ page import="java.util.Arrays" %>
<%--
  From this file you initiate your authentication request
  User: krico
  Date: 30/10/14
  Time: 21:18
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>OpenID Connect Login with JSP</title>
</head>
<body>
<h1>OpenID Connect Login with JSP</h1>
<%

    String state = new BigInteger(130, new SecureRandom()).toString(32);
    session.setAttribute(Constants.Session.OpenIDState, state);

    GenericUrl redirectUrl = new GenericUrl(request.getRequestURL().toString());
    redirectUrl.setRawPath("/login-openid-connect-callback.jsp");
    String clientRequestUrl = new AuthorizationCodeRequestUrl(Constants.Endpoint.Authorization.Google,
            Constants.OpenID.Credentials.ClientID)
            .setState(state)
            .setRedirectUri(redirectUrl.build())
            .setScopes(Arrays.asList(Constants.OpenID.Scope.OpenID, Constants.OpenID.Scope.Email))
            .build();
%>
<a href="<%= clientRequestUrl %>">Login using Google with OpenID Connect</a><br/>
Callback: <i><%=redirectUrl.build()%>
</i><br/>
Url: <i><%= clientRequestUrl %>
</i>

</body>
</html>
