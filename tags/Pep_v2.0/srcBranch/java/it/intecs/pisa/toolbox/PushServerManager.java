/* 
 *
 * ****************************************************************************
 *  Copyright 2003*2004 Intecs
 ****************************************************************************
 *  This file is part of TOOLBOX.
 *
 *  TOOLBOX is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TOOLBOX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TOOLBOX; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ****************************************************************************
 */

package it.intecs.pisa.toolbox;

import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.XMLSerializer2;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class is used to process all requests received by the Push Server on its doGet entry point
 * @author Massimiliano
 */
public class PushServerManager {
     protected static final String REQUEST_PARAMETER_COMMAND = "cmd";
    protected static final String REQUEST_PARAMETER_GET_PUSHED_MESSAGE = "GetPushedMessage";
    protected static final String REQUEST_PARAMETER_MESSAGE_ID = "MessageId";
     
    protected PushServer server;
    /**
     * Default constructor
     */
    public PushServerManager(PushServer server)
    {
        this.server=server;
    }
    
    /**
     *  This method execute Push server operation based on the input parameters in the request header
     * @param request
     * @param response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String command;
        
        try
        {
        command = request.getParameter(REQUEST_PARAMETER_COMMAND);
        
        if(command.equalsIgnoreCase(REQUEST_PARAMETER_GET_PUSHED_MESSAGE))
        {
                executeGetMessage( request, response);
            
        }
        else
        {
            //returning error message to caller
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
        }
        catch(Exception e)
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void executeGetMessage(HttpServletRequest request, HttpServletResponse response) throws TransformerException, IOException, SAXException, IOException {
        DOMUtil util;
        File pushedMessage;
        PrintWriter writer;
        Document pushedMessageDoc;
        String messageId;
        
        messageId = request.getParameter(REQUEST_PARAMETER_MESSAGE_ID);
        pushedMessage = server.getPushedMessage(messageId);

        if (pushedMessage.exists()) {
            util = new DOMUtil();

            response.setContentType("text/xml");
            writer = response.getWriter();

            //sending back pushed message
            pushedMessageDoc = util.fileToDocument(pushedMessage);
            new XMLSerializer2(writer).serialize(pushedMessageDoc);
            writer.close();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
