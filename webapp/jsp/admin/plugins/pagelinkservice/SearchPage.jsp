<jsp:include page="../../insert/InsertServiceHeader.jsp" />

<jsp:useBean id="pagelinkservice" scope="session" class="fr.paris.lutece.plugins.pagelinkservice.web.PageLinkServiceJspBean" />

<%= pagelinkservice.getInsertServiceSelectorUI( request ) %>
