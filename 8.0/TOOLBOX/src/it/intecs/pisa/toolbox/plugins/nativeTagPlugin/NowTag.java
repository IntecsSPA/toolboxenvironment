package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NowTag extends NativeTagExecutor {
      protected static final String FORMAT = "format";
      
    @Override
    public Object executeTag(org.w3c.dom.Element nowElement) throws Exception {
        String format;
        SimpleDateFormat formatter;

        format = engine.evaluateString(nowElement.getAttribute(FORMAT),IEngine.EngineStringType.ATTRIBUTE);
        if (format != null && format.equals("") == false) {
            formatter = new SimpleDateFormat(format);

            return formatter.format(new Date()).toString();
        } else {
            return new Date();
        }
    }
}
