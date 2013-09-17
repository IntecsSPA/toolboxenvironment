package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;


public class NowTag extends NativeTagExecutor {
      protected static final String FORMAT = "format";
      protected static final String GMT = "gmt";
      
    @Override
    public Object executeTag(org.w3c.dom.Element nowElement) throws Exception {
        String format;
        SimpleDateFormat formatter;
        Date date = new Date();

        format = engine.evaluateString(nowElement.getAttribute(FORMAT),IEngine.EngineStringType.ATTRIBUTE);
        if (format != null && format.equals("") == false) {
            formatter = new SimpleDateFormat(format);
            if(Boolean.parseBoolean(nowElement.getAttribute(GMT))){
                Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
                formatter.setCalendar(cal);
            }
            return formatter.format(new Date()).toString();
        } else {
            return new Date();
        }

    }
}
