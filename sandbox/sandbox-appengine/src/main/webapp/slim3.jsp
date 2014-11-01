<%@ page import="com.jasify.sandbox.appengine.meta.UserMeta" %>
<%@ page import="com.jasify.sandbox.appengine.model.Group" %>
<%@ page import="com.jasify.sandbox.appengine.model.User" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.slim3.datastore.Datastore" %>
<%@ page import="org.slim3.datastore.EntityNotFoundRuntimeException" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.UUID" %>
<%--
  Created by IntelliJ IDEA.
  User: krico
  Date: 01/11/14
  Time: 00:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create Slim3 objects</title>
</head>
<body>
<%
    if ("yes".equals(request.getParameter("add"))) {
        String userName = request.getParameter("user");
        String groupName = request.getParameter("group");
        if (StringUtils.isEmpty(groupName)) groupName = "default";
        if (StringUtils.isEmpty(userName)) userName = "Guest" + UUID.randomUUID().toString();
        Key groupKey = Datastore.createKey(Group.class, groupName);
        Group group;
        try {
            group = Datastore.get(Group.class, groupKey);
%>
Using existing group: <%= groupName %><br/>
<%
} catch (EntityNotFoundRuntimeException enf) {
%>
Creating new group: <%=groupName %><br/>
<%
        group = new Group();
        group.setCreated(new Date());
        group.setName(groupKey);
    }
    group.setModified(new Date());
    UserMeta u = UserMeta.get();
    User user = Datastore.query(User.class).filter(u.name.equal(userName)).asSingle();
    if (user != null) {
%>
User: <%= userName %> already exists
<%
} else {
    Key userKey = Datastore.allocateId(groupKey, User.class);
    user = new User();
    user.setCreationDate(new Date());
    user.setName(userName);
    user.setKey(userKey);
    Datastore.put(group, user);
%>
User: <%= userName %> was created
<%
        }
    }
%>

<form action="slim3.jsp" method="post">
    <input type="hidden" name="add" value="yes"/>
    <label>User:
        <input type="text" name="user"/>
    </label>
    <label>Group:
        <input name="group" type="text"/>
    </label>
    <input type="submit"/>
</form>
<h5>Listing existing users</h5>
<%
    for (Group group : Datastore.query(Group.class).asIterable()) {
%>
<%=group%><br/>
<ul><%
    for (User user : Datastore.query(User.class, group.getName()).asIterable()) {
%>
    <li><%= user %>
    </li>
    <%
        }
    %></ul>
<%
    }
%>
</body>
</html>
