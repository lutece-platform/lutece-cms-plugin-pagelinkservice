/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.pagelinkservice.business.PageLinkService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Renders the templates of the page link service with HTML auto-escaping (LUT-33153): the names of the pages and the
 * texts typed in the selector are escaped once, and the markup of the templates is kept.
 */
public class PageLinkServiceTemplatesRenderTest extends LuteceTestCase
{
    private static final String TEMPLATE_SELECTOR = "admin/plugins/pagelinkservice/pagelinkservice_selector.html";
    private static final String TEMPLATE_LINK = "admin/plugins/pagelinkservice/pagelinkservice_link.html";

    /**
     * The link inserted into the editor writes its name and its caption as text, escaped once.
     */
    @Test
    public void testLinkEscapesItsTextOnce( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "url", "jsp/site/Portal.jsp?page_id=3" );
        model.put( "target", "_blank" );
        model.put( "alt", "Plan \"accès\" <b>" );
        model.put( "name", "O'Brien & <i>fils</i>" );

        String strHtml = AppTemplateService.getTemplate( TEMPLATE_LINK, Locale.FRENCH, model ).getHtml( );

        assertEquals( "<a href=\"jsp/site/Portal.jsp?page_id=3\" target=\"_blank\" title=\"Plan &quot;accès&quot; &lt;b&gt;\">"
                + "O&#39;Brien &amp; &lt;i&gt;fils&lt;/i&gt;</a>", strHtml );
    }

    /**
     * The selector writes the name of each page in bold, as text, and its identifier as a number.
     */
    @Test
    public void testSelectorWritesThePageNameAsText( )
    {
        PageLinkService page = new PageLinkService( );
        page.setIdPage( 1234 );
        page.setLabelPage( "Accueil <b>\"jeunes\"</b>" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "plugin_name", "pagelinkservice" );
        model.put( "input", "content" );
        model.put( "pages_list", List.of( page ) );
        model.put( "url", "http://localhost/lutece/" );

        String strHtml = AppTemplateService.getTemplate( TEMPLATE_SELECTOR, Locale.FRENCH, model ).getHtml( );

        assertTrue( strHtml.contains( "<strong>Accueil &lt;b&gt;&quot;jeunes&quot;&lt;/b&gt;</strong> [1234]" ), strHtml );
        assertTrue( strHtml.contains( "id=\"page_radio_1234\"" ), strHtml );
        assertTrue( strHtml.contains( "value=\"Accueil &lt;b&gt;&quot;jeunes&quot;&lt;/b&gt;\"" ), strHtml );
        assertTrue( strHtml.contains( "data-lutece-action=\"back\"" ), strHtml );
        assertFalse( strHtml.contains( "&lt;strong&gt;" ), strHtml );
        assertFalse( strHtml.contains( "&amp;lt;" ), strHtml );
        assertFalse( strHtml.contains( "onclick" ), strHtml );
    }

    /**
     * The selector has a page heading, groups its radio buttons under a legend, requires a page, and names the link
     * that opens each page in a new window.
     */
    @Test
    public void testSelectorIsAccessible( )
    {
        PageLinkService page = new PageLinkService( );
        page.setIdPage( 7 );
        page.setLabelPage( "Plan" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "plugin_name", "pagelinkservice" );
        model.put( "pages_list", List.of( page ) );
        model.put( "url", "http://localhost/lutece/" );

        String strHtml = AppTemplateService.getTemplate( TEMPLATE_SELECTOR, Locale.ENGLISH, model ).getHtml( );

        assertTrue( strHtml.contains( "<h1 class=\"visually-hidden\">Insert a link to a Page of the site</h1>" ), strHtml );
        assertTrue( strHtml.contains( "<legend class=\"visually-hidden\">Page choice</legend>" ), strHtml );
        assertTrue( strHtml.matches( "(?s).*id=\"page_radio_7\"[^>]*required.*" ), strHtml );
        assertTrue( strHtml.contains( "<span class=\"visually-hidden\">Show this page Plan (new window)</span>" ), strHtml );
    }
}
