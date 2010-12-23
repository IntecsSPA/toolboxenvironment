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
 *  File Name:         $RCSfile: ToolboxException.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:26 $
 *
 */
package it.intecs.pisa.soap.toolbox.exceptions;

import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ToolboxException extends it.intecs.pisa.toolbox.util.CustomException {

  public ToolboxException(String message) {
    super(message);
    
    Document doc;
    DOMUtil util;
    Element root;
    
    util=new DOMUtil();
    doc=util.newDocument();
    root=doc.createElement("details");
    
    DOMUtil.setTextToElement(doc, root, message);
    doc.appendChild(root);
    
    this.detailsDOM=root;
  }
  
   public ToolboxException(String message,Element details) {
    super(message);
    
    detailsDOM=(Element)details.cloneNode(true);
  }
   
   public ToolboxException(String message,Element details,File logDir) {
    this(message,details);
    
    instaceLogDir=logDir;
  }
   
   public Element getDetailsXML()
   {
       return detailsDOM;
   }
   
   public File getInstanceLogDir()
   {
       return instaceLogDir;
   }
   
    public void setInstanceLogDir(File dir)
   {
       instaceLogDir=dir;
   }

   private Element detailsDOM;
   private File instaceLogDir;
}
