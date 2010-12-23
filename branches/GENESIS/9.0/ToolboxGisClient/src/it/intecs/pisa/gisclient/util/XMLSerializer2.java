/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.gisclient.util;

import java.io.OutputStream;
import java.io.Writer;
import org.w3c.dom.Node;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLSerializer2 {

  private static Transformer serializer = null;

  static {
    try {
      serializer = TransformerFactory.newInstance().newTransformer();
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    }
  }

  private final StreamResult out;

  public XMLSerializer2(OutputStream out) {
    this.out = new StreamResult(out);
  }

  public XMLSerializer2(Writer out) {
    this.out = new StreamResult(out);
  }

  public void serialize(Node node) throws TransformerException {
   if (serializer != null)
      synchronized (serializer) {
        serializer.transform(new DOMSource(node), out);
      }
  }

}