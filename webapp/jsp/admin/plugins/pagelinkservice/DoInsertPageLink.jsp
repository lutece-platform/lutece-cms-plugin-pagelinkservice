<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.pagelinkservice.web.PageLinkServiceJspBean"%>

${ pageContext.response.sendRedirect( pageLinkServiceJspBean.doInsertUrl( pageContext.request ) ) }
