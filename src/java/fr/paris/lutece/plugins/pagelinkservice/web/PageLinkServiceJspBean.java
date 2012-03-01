/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.insert.InsertServiceJspBean;
import fr.paris.lutece.portal.web.insert.InsertServiceSelectionBean;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage PageLibrary features
 */
public class PageLinkServiceJspBean extends InsertServiceJspBean implements InsertServiceSelectionBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String REGEX_PAGE_ID = "^[\\d]+$";

    //Parameters
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";
    private static final String PARAMETER_PAGE_NAME = "page_name";
    private static final String PARAMETER_PAGE_ID = "page";
    private static final String PARAMETER_PAGE_ID_URL = "page_id";
    private static final String PARAMETER_ALT = "alt";
    private static final String PARAMETER_TARGET = "target";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_INPUT = "input";

    //Properties
    private static final String PROPERTY_SEARCH_MESSAGE = "message.warning.resulsearch.empty";

    //Markers
    private static final String MARK_PLUGIN_NAME = "plugin_name";
    private static final String MARK_SEARCH_MESSAGE = "search_message";
    private static final String MARK_PAGES_LIST = "pages_list";
    private static final String MARK_LIST_PAGE = "list_page";
    private static final String MARK_URL = "url";
    private static final String MARK_TARGET = "target";
    private static final String MARK_ALT = "alt";
    private static final String MARK_NAME = "name";
    private static final String MARK_INPUT = "input";

    //Templates
    private static final String TEMPLATE_SELECTOR_PAGE = "admin/plugins/pagelinkservice/pagelinkservice_selector.html";
    private static final String TEMPLATE_LINK = "admin/plugins/pagelinkservice/pagelinkservice_link.html";
    private AdminUser _user;
    private Plugin _plugin;
    private String _input;
    private IPageService _pageService = (IPageService) SpringContextService.getBean("pageService");

    ////////////////////////////////////////////////////////////////////////////
    // Methods

    /**
     * Return the html form for image selection.
     *
     * @param request The Http Request
     * @return The html form.
     */
    public String getInsertServiceSelectorUI( HttpServletRequest request )
    {
        init( request );

        AdminUser user = AdminUserService.getAdminUser( request );
        String strSearch = request.getParameter( PARAMETER_PAGE_NAME );
        String strResultSearch = ( strSearch == null ) ? "" : strSearch;
        String strPageName = replaceString( strResultSearch, "'", "''" );

        HashMap model = getDefaultModel(  );

        Collection<PageLinkService> listPages = PageLinkServiceHome.getPageListbyName( strPageName );
        Collection<PageLinkService> listPagesAuthorized = new ArrayList<PageLinkService>(  );

        for ( PageLinkService pageLinkService : listPages )
        {
            if ( _pageService.isAuthorizedAdminPage( pageLinkService.getIdPage(  ),
                        PageResourceIdService.PERMISSION_VIEW, user ) )
            {
                listPagesAuthorized.add( pageLinkService );
            }
        }

        model.put( MARK_PAGES_LIST, listPagesAuthorized );

        StringBuilder strListPage = new StringBuilder(  );

        if ( listPages.isEmpty() )
        {
            model.put( MARK_SEARCH_MESSAGE, AppPropertiesService.getProperty( PROPERTY_SEARCH_MESSAGE ) );
        }

        String strBaseUrl = AppPathService.getBaseUrl( request );

        // Search Message
        model.put( MARK_URL, strBaseUrl );
        model.put( MARK_LIST_PAGE, strListPage.toString(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECTOR_PAGE, _user.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Replaces a string in an initial string by another string
     *
     * @param str the initial string
     * @param oldStr the string to replace
     * @param newStr the string which will replace the old string
     * @return the cleaned string
     */
    private String replaceString( String str, String oldStr, String newStr )
    {
        // Temporary string to avoid assignment of parameter str
        String cleanString = str;

        int index = 0;

        while ( true )
        {
            index = cleanString.lastIndexOf( oldStr );

            if ( ( index == 2 ) || ( index == -1 ) )
            {
                break;
            }

            cleanString = cleanString.substring( 0, index ) + newStr +
                cleanString.substring( index + oldStr.length(  ) );
        }

        return cleanString;
    }

    /**
     * Insert the specified url into HTML content
     *
     * @param request The http request
     * @return String The url
     */
    public String doInsertUrl( HttpServletRequest request )
    {
        init( request );

        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strTarget = request.getParameter( PARAMETER_TARGET );
        String strAlt = request.getParameter( PARAMETER_ALT );
        String strName = request.getParameter( PARAMETER_NAME );
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        Page page = null;

        if ( ( strPageId == null ) || !strPageId.matches( REGEX_PAGE_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        page = PageHome.findByPrimaryKey( Integer.parseInt( strPageId ) );

        UrlItem url = new UrlItem( AppPathService.getPortalUrl(  ) );
        url.addParameter( PARAMETER_PAGE_ID_URL, page.getId(  ) );
        model.put( MARK_URL, url.getUrl(  ) );
        model.put( MARK_TARGET, strTarget );
        model.put( MARK_ALT, strAlt );
        model.put( MARK_NAME, ( strName.length(  ) == 0 ) ? page.getName(  ) : strName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_LINK, null, model );

        return insertUrl( request, _input, StringEscapeUtils.escapeJavaScript( template.getHtml(  ) ) );
    }

    private void init( HttpServletRequest request )
    {
        String strPluginName = request.getParameter( PARAMETER_PLUGIN_NAME );
        _user = AdminUserService.getAdminUser( request );
        _plugin = PluginService.getPlugin( strPluginName );
        _input = request.getParameter( PARAMETER_INPUT );
    }

    private HashMap getDefaultModel(  )
    {
        HashMap model = new HashMap(  );
        model.put( MARK_PLUGIN_NAME, _plugin.getName(  ) );
        model.put( MARK_INPUT, _input );

        return model;
    }
}
