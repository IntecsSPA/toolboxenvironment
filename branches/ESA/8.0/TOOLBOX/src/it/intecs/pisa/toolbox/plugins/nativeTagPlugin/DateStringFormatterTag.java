package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.text.SimpleDateFormat;

public class DateStringFormatterTag extends NativeTagExecutor{
          protected static final String FORMAT = "format";
          
	@Override
	public Object executeTag(org.w3c.dom.Element dateString) throws Exception{
               String format;
        SimpleDateFormat formatter;
        Object date;
        
        format=this.engine.evaluateString(dateString.getAttribute(FORMAT),IEngine.EngineStringType.ATTRIBUTE);
        formatter=new SimpleDateFormat(format);
        
        date=this.executeChildTag(DOMUtil.getFirstChild(dateString));
        
        return formatter.format(date).toString();
    }

   

}
