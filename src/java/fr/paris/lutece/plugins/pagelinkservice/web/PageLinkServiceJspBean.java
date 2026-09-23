/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.pagelinkservice.web;

import fr.paris.lutece.plugins.pagelinkservice.business.PageLinkService;
import fr.paris.lutece.plugins.pagelinkservice.business.PageLinkServiceHome;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.page.IPageService;
import fr.paris.lutece.portal.service.page.PageResourceIdService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.insert.InsertServiceJspBean;
import fr.paris.lutece.portal.web.insert.InsertServiceSelectionBean;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage PageLibrary features
 */
@RequestScoped
public class PageLinkServiceJspBean extends InsertServiceJspBean implements InsertServiceSelectionBean
{
    private static final long serialVersionUID = 1L;

    private static final String REGEX_PAGE_ID = "^[\\d]+$";
    private static final List<String> LIST_TARGETS = List.of( "_self", "_blank", "_parent", "_top" );

    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";
    private static final String PARAMETER_PAGE_NAME = "page_name";
    private static final String PARAMETER_PAGE_ID = "id_page";
    private static final String PARAMETER_PAGE_ID_URL = "page_id";
    private static final String PARAMETER_ALT = "alt";
    private static final String PARAMETER_TARGET = "target";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_INPUT = "input";

    private static final String MESSAGE_PAGE_NOT_FOUND = "pagelinkservice.message.error.pageNotFound";

    private static final String MARK_PLUGIN_NAME = "plugin_name";
    private static final String MARK_PAGES_LIST = "pages_list";
    private static final String MARK_URL = "url";
    private static final String MARK_TARGET = "target";
    private static final String MARK_ALT = "alt";
    private static final String MARK_NAME = "name";
    private static final String MARK_INPUT = "input";

    private static final String TEMPLATE_SELECTOR_PAGE = "admin/plugins/pagelinkservice/pagelinkservice_selector.html";
    private static final String TEMPLATE_LINK = "admin/plugins/pagelinkservice/pagelinkservice_link.html";

    private IPageService _pageService = CDI.current( ).select( IPageService.class ).get( );

    /**
     * Return the html form for page selection, with the pages matching the searched name that the user may view.
     *
     * @param request The Http Request
     * @return The html form.
     */
    @Override
    public String getInsertServiceSelectorUI( HttpServletRequest request )
    {
        AdminUser user = AdminUserService.getAdminUser( request );
        Plugin plugin = PluginService.getPlugin( request.getParameter( PARAMETER_PLUGIN_NAME ) );

        List<PageLinkService> listPagesAuthorized = PageLinkServiceHome
                .getPageListbyName( StringUtils.defaultString( request.getParameter( PARAMETER_PAGE_NAME ) ) ).stream( )
                .filter( page -> _pageService.isAuthorizedAdminPage( page.getIdPage( ), PageResourceIdService.PERMISSION_VIEW, user ) )
                .collect( Collectors.toList( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_PLUGIN_NAME, ( plugin == null ) ? StringUtils.EMPTY : plugin.getName( ) );
        model.put( MARK_INPUT, request.getParameter( PARAMETER_INPUT ) );
        model.put( MARK_PAGES_LIST, listPagesAuthorized );
        model.put( MARK_URL, AppPathService.getBaseUrl( request ) );
        model.put( SecurityTokenHandler.MARK_CSRF_TOKEN, CDI.current( ).select( ISecurityTokenService.class ).get( )
                .getToken( request, PageLinkServiceSelectorJspBean.ACTION_INSERT_PAGE_LINK ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECTOR_PAGE, user.getLocale( ), model );

        return template.getHtml( );
    }

    /**
     * Builds the link to the selected page and returns the url that inserts it into the calling editor field.
     *
     * @param request The http request
     * @return the insertion url, or the url of an error message when the page is missing or not viewable
     */
    public String getInsertLinkUrl( HttpServletRequest request )
    {
        AdminUser user = AdminUserService.getAdminUser( request );
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );

        if ( ( strPageId == null ) || !strPageId.matches( REGEX_PAGE_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nPageId = NumberUtils.toInt( strPageId, 0 );
        Page page = PageHome.findByPrimaryKey( nPageId );

        if ( ( page.getId( ) != nPageId ) || !_pageService.isAuthorizedAdminPage( nPageId, PageResourceIdService.PERMISSION_VIEW, user ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PAGE_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        String strTarget = request.getParameter( PARAMETER_TARGET );
        String strName = request.getParameter( PARAMETER_NAME );

        UrlItem url = new UrlItem( AppPathService.getPortalUrl( ) );
        url.addParameter( PARAMETER_PAGE_ID_URL, page.getId( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_URL, url.getUrl( ) );
        model.put( MARK_TARGET, LIST_TARGETS.contains( strTarget ) ? strTarget : StringUtils.EMPTY );
        model.put( MARK_ALT, toHtmlText( StringUtils.defaultString( request.getParameter( PARAMETER_ALT ) ) ) );
        model.put( MARK_NAME, toHtmlText( StringUtils.isBlank( strName ) ? page.getName( ) : strName ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_LINK, user.getLocale( ), model );

        return insertUrl( request, request.getParameter( PARAMETER_INPUT ), StringEscapeUtils.escapeEcmaScript( template.getHtml( ) ) );
    }

    /**
     * Escapes a text for HTML once, whether or not the core XSS filter already encoded some of its characters.
     *
     * @param strText the text
     * @return the escaped text
     */
    private static String toHtmlText( String strText )
    {
        return StringEscapeUtils.escapeHtml4( StringEscapeUtils.unescapeHtml4( strText ) );
    }
}
