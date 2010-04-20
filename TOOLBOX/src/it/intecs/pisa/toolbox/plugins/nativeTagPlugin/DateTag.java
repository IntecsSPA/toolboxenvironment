package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class DateTag extends NativeTagExecutor{
          protected static final String FORMAT = "format";
          
	@Override
	public Object executeTag(org.w3c.dom.Element date) throws Exception{
               String format;
                SimpleDateFormat formatter;
                Object dateToConvert;

                format=engine.evaluateString(date.getAttribute(FORMAT),IEngine.EngineStringType.ATTRIBUTE);

                formatter=new SimpleDateFormat(format);

                dateToConvert=this.executeChildTag(DOMUtil.getFirstChild(date));

                return formatter.parse((String) dateToConvert, new ParsePosition(0));
    }

   

}
