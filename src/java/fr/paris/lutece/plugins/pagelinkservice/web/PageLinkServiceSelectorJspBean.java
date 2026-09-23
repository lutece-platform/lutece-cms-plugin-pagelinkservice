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

import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.insert.InsertServiceSelectorJspBean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;


/**
 * Controller of the page selector popup opened by the rich text editor: page search and link insertion.
 */
@RequestScoped
@Named
@Controller( controllerJsp = "SearchPage.jsp", controllerPath = "jsp/admin/plugins/pagelinkservice/", right = InsertServiceSelectorJspBean.RIGHT_MANAGE_LINK_SERVICE, securityTokenEnabled = true )
public class PageLinkServiceSelectorJspBean extends MVCAdminJspBean
{
    public static final String ACTION_INSERT_PAGE_LINK = "insertPageLink";

    private static final long serialVersionUID = 1L;

    private static final String VIEW_SEARCH_PAGE = "searchPage";

    @Inject
    private PageLinkServiceJspBean _insertService;

    /**
     * Displays the page search and the pages found.
     *
     * @param request The Http Request
     * @return The html of the selector
     */
    @View( value = VIEW_SEARCH_PAGE, defaultView = true )
    public String getSearchPage( HttpServletRequest request )
    {
        return _insertService.getInsertServiceSelectorUI( request );
    }

    /**
     * Inserts the link to the selected page into the calling editor field.
     *
     * @param request The http request
     * @return The redirection
     */
    @Action( ACTION_INSERT_PAGE_LINK )
    public String doInsertPageLink( HttpServletRequest request )
    {
        return redirect( request, _insertService.getInsertLinkUrl( request ) );
    }
}
