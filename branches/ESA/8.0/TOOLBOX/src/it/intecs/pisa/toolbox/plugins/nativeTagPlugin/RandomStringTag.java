package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import java.util.Random;

public class RandomStringTag extends NativeTagExecutor {

   protected static final String LENGTH = "length";

    @Override
    public Object executeTag(org.w3c.dom.Element randomString) throws Exception {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        
        int length = Integer.parseInt(this.engine.evaluateString(randomString.getAttribute(LENGTH),IEngine.EngineStringType.ATTRIBUTE));
        int i;
        while (length-- > 0) {
            i = random.nextInt(62);
            buffer.append((char) (i +
                    (i < 10 ? '0' : (i < 36 ? 'A' - 10 : 'a' - 36))));
        }
        String result = buffer.toString();
        
        return result;
    }
}
