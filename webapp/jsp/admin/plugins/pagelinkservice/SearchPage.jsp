<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../insert/InsertServiceHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.pagelinkservice.web.PageLinkServiceJspBean"%>

${ pageLinkServiceJspBean.getInsertServiceSelectorUI( pageContext.request ) }
