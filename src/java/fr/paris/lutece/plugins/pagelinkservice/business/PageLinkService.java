/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;


/**
 * This class represents the business object PageLibrary.
 */
public class PageLinkService implements AdminWorkgroupResource
{
    private int _nIdPage;
    private String _sLabelPage;
    private String _sDescriptionPage;
    private String _strWorkgroup;

    /**
     * @return _nIdPage
     */
    public int getIdPage(  )
    {
        return _nIdPage;
    }

    /**
     * Set _nIdPage
     *
     * @param nIdPage The Page ID
     */
    public void setIdPage( int nIdPage )
    {
        _nIdPage = nIdPage;
    }

    /**
     * @return Label Page
     */
    public String getLabelPage(  )
    {
        return _sLabelPage;
    }

    /**
     * Set Label Page
     *
     * @param sLabelPage The string label Page
     */
    public void setLabelPage( String sLabelPage )
    {
        _sLabelPage = sLabelPage;
    }

    /**
     * @return Description Page
     */
    public String getDescriptionPage(  )
    {
        return _sDescriptionPage;
    }

    /**
     * Set _sDescriptionPage
     *
     * @param sDescriptionPage The description page
     */
    public void setDescriptionPage( String sDescriptionPage )
    {
        _sDescriptionPage = sDescriptionPage;
    }

    /**
    *
    * @return the work group associate to the page
    */
    public String getWorkgroup(  )
    {
        return _strWorkgroup;
    }

    /**
     * set  the work group associate to the page
     * @param workGroup  the work group associate to the page
     */
    public void setWorkgroup( String workGroup )
    {
        _strWorkgroup = workGroup;
    }
}
