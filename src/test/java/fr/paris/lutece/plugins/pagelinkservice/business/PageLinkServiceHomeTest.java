/*
 * Copyright (c) 2002-2026, Mairie de Paris
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
package fr.paris.lutece.plugins.pagelinkservice.business;

import java.util.List;
import java.util.UUID;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.style.PageTemplateHome;
import fr.paris.lutece.portal.service.page.IPageService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Tests the search of portal pages by name.
 */
public class PageLinkServiceHomeTest extends LuteceTestCase
{
    @Inject
    private IPageService _pageService;

    private String _strToken;
    private Page _page;

    /**
     * Creates a page whose name carries an apostrophe.
     *
     * @throws Exception
     *             if the test context fails to start
     */
    @BeforeEach
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        _strToken = UUID.randomUUID( ).toString( );
        _page = new Page( );
        _page.setParentPageId( PortalService.getRootPageId( ) );
        _page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( ).get( 0 ).getId( ) );
        _page.setName( "O'Brien " + _strToken );
        _page.setDescription( _strToken );
        _pageService.createPage( _page );
    }

    /**
     * Removes the test page.
     *
     * @throws Exception
     *             if the test context fails to stop
     */
    @AfterEach
    protected void tearDown( ) throws Exception
    {
        _pageService.removePage( _page.getId( ) );
        super.tearDown( );
    }

    /**
     * A name with an apostrophe finds its page, as a value and not as SQL.
     */
    @Test
    public void testSearchByNameWithApostrophe( )
    {
        List<PageLinkService> list = PageLinkServiceHome.getPageListbyName( "O'Brien " + _strToken );

        assertEquals( 1, list.size( ) );
        assertEquals( _page.getId( ), list.get( 0 ).getIdPage( ) );
        assertEquals( _strToken, list.get( 0 ).getDescriptionPage( ) );
    }

    /**
     * A part of the name finds the page, an unknown name finds nothing, an empty name lists every page.
     */
    @Test
    public void testSearchByPartOfName( )
    {
        assertEquals( 1, PageLinkServiceHome.getPageListbyName( _strToken.substring( 4, 20 ) ).size( ) );
        assertTrue( PageLinkServiceHome.getPageListbyName( "' OR '1'='1" ).isEmpty( ) );
        assertTrue( PageLinkServiceHome.getPageListbyName( "" ).stream( ).anyMatch( p -> p.getIdPage( ) == _page.getId( ) ) );
    }
}
