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
package fr.paris.lutece.plugins.pagelinkservice.business;

import fr.paris.lutece.util.sql.DAOUtil;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for PageLibrary objects
 */
@ApplicationScoped
public class PageLinkServiceDAO
{
    private static final String SQL_QUERY_SELECTALL = " SELECT id_page , name ,description FROM core_page";
    private static final String SQL_QUERY_SELECT_BY_NAME = SQL_QUERY_SELECTALL + " WHERE name LIKE ?";
    private static final String LIKE_WILDCARD = "%";

    /**
     * The collection of page
     * @param strPageName the name of the page
     * @return The collection of field
     */
    List<PageLinkService> selectPageListbyName( String strPageName )
    {
        List<PageLinkService> list = new ArrayList<>(  );
        boolean bFilter = !"".equals( strPageName );

        try ( DAOUtil daoUtil = new DAOUtil( bFilter ? SQL_QUERY_SELECT_BY_NAME : SQL_QUERY_SELECTALL ) )
        {
            if ( bFilter )
            {
                daoUtil.setString( 1, LIKE_WILDCARD + strPageName + LIKE_WILDCARD );
            }

            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                PageLinkService page = new PageLinkService(  );
                page.setIdPage( daoUtil.getInt( 1 ) );
                page.setLabelPage( daoUtil.getString( 2 ) );
                page.setDescriptionPage( daoUtil.getString( 3 ) );
                list.add( page );
            }
        }

        return list;
    }
}
