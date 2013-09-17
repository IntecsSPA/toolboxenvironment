package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.util.LinkedList;
import org.w3c.dom.Element;

public class CopyFileTag extends NativeTagExecutor{
        
	@Override
	public Object executeTag(org.w3c.dom.Element loadFile) {
            String from;
            String to;
        
            try
            {
                LinkedList children = DOMUtil.getChildren(loadFile);
                from=(String) this.executeChildTag((Element) children.get(0));
                to=(String) this.executeChildTag((Element) children.get(1));

                IOUtil.copyFile(new File(from), new File(to));
                return true;
            }
            catch(Exception e)
            {
                return false;
        }
        
    }

   

}
