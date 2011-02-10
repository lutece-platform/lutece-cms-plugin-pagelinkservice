<jsp:include page="../../insert/InsertServiceHeader.jsp" />
<jsp:useBean id="pageLinkService" scope="session" class="fr.paris.lutece.plugins.pagelinkservice.web.PageLinkServiceJspBean" />

<% response.sendRedirect( pageLinkService.doInsertUrl( request ) );%>
