package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;

public class DateStringFormatterTag extends NativeTagExecutor{
          protected static final String FORMAT = "format";
          protected static final String GMT = "gmt";
          
	@Override
	public Object executeTag(org.w3c.dom.Element dateString) throws Exception{
               String format;
        SimpleDateFormat formatter;
        Object date;
        
        format=this.engine.evaluateString(dateString.getAttribute(FORMAT),IEngine.EngineStringType.ATTRIBUTE);
        formatter=new SimpleDateFormat(format);

        if(Boolean.parseBoolean(dateString.getAttribute(GMT))){
           Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
           formatter.setCalendar(cal);
        }
        
        date=this.executeChildTag(DOMUtil.getFirstChild(dateString));
        
        return formatter.format(date).toString();
    }

   

}
