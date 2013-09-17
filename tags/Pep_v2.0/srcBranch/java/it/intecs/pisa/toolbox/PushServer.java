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
 *  File Name:         $RCSfile: PushServer.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:26 $
 *
 */
package it.intecs.pisa.toolbox;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.w3c.dom.*;


import it.intecs.pisa.util.*;
import java.util.Date;
import java.util.StringTokenizer;

public class PushServer extends HttpServlet {

    private static DOMUtil domUtil = new DOMUtil(true);
    private static final String ROOT = "/";
    private static final String PUSH = "Push";
    private static File root;
    private PushServerManager serverManager;

    /**
     * This method overrides its parent method. It has been introduced in order to implement some information retrieval operation,
     * accessed both from Toolbox Development Environment  and from Toolbox Runtime Environment interface
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        //parsing request parameters in order to match operation to implement

        this.serverManager.execute(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        String relatesTo = null;
        Document soapRequestDocument = null;

        try {
            BufferedReader buf = request.getReader();
            soapRequestDocument = domUtil.readerToDocument(buf);

            String requestURI = request.getRequestURI();
            StringTokenizer tokenizer;

            if (requestURI.contains("Push/instance/")) {
                try {
                    tokenizer = new StringTokenizer(requestURI, "/");
                    while (tokenizer.hasMoreTokens()) {
                        String token;
                        token = tokenizer.nextToken();

                        if (token.equals("instance")) {
                            break;
                        }
                    }

                    relatesTo = tokenizer.nextToken();
                } catch (Exception e) {
                    relatesTo = null;
                }
            }


            try {
                if (relatesTo == null) {
                    relatesTo = soapRequestDocument.getElementsByTagNameNS("http://schemas.xmlsoap.org/ws/2003/03/addressing", "RelatesTo").item(0).getFirstChild().getNodeValue();
                }
            } catch (Exception e) {
                relatesTo = null;
            }

            try {
                if (relatesTo == null) {
                    relatesTo = soapRequestDocument.getElementsByTagNameNS("http://www.w3.org/2005/08/addressing", "RelatesTo").item(0).getFirstChild().getNodeValue();
                }
            } catch (Exception ecc) {
                relatesTo = null;
            }

            if(relatesTo==null)
                relatesTo = "noRelatesTo";

            Date messageDate= new Date();
            relatesTo=getModifiedRelatesTo(relatesTo+"_"+messageDate.toString()); 
            response.setContentType("text/xml");
            response.setCharacterEncoding(request.getCharacterEncoding());
            
            PrintWriter writer = response.getWriter();

            new XMLSerializer2(writer).serialize(soapRequestDocument);
            writer.close();

            dumpMessage(soapRequestDocument, new File(new File(root, PUSH), relatesTo + ".xml"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            root = new File(getServletContext().getRealPath(ROOT));

            serverManager = new PushServerManager(this);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void dumpMessage(Document message, String fileName) {
        try {
            domUtil.dumpXML(message, new File(fileName));
        } catch (Exception e) {
        }
    }

    private void dumpMessage(Document message, File absoluteFile) {
        try {
            domUtil.dumpXML(message, absoluteFile);
        } catch (Exception e) {
        }
    }

    private String getModifiedRelatesTo(String messageId) {
        return messageId.replaceAll("[/\\\\:*?<>| ]", "_");
    }

    /**
     * This method return a File class that point to the push message requested through its messageId. You shall check for file existance before opeining it because this method doesn't perform any check
     * @param messageId
     * @return Reference to pushed message
     */
    public File getPushedMessage(String messageId) {
        String modifiedMesssageId;
        File pushedMessage;

        modifiedMesssageId = getModifiedRelatesTo(messageId);

        pushedMessage = new File(new File(root, PUSH), modifiedMesssageId);
        return pushedMessage;
    }
}
