<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // La página inicial lleva directo al Servlet que muestra la central.
    response.sendRedirect(request.getContextPath() + "/pacientes");
%>
