package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;

public class DateTag extends NativeTagExecutor{
          protected static final String FORMAT = "format";
          protected static final String GMT = "gmt";
          
	@Override
	public Object executeTag(org.w3c.dom.Element date) throws Exception{
               String format;
                SimpleDateFormat formatter;
                Object dateToConvert;

                format=engine.evaluateString(date.getAttribute(FORMAT),IEngine.EngineStringType.ATTRIBUTE);

                formatter=new SimpleDateFormat(format);

                if(Boolean.parseBoolean(date.getAttribute(GMT))){
                   Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
                   formatter.setCalendar(cal);
                }

                dateToConvert=this.executeChildTag(DOMUtil.getFirstChild(date));

                return formatter.parse((String) dateToConvert, new ParsePosition(0));
    }

   

}
