package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import java.util.Random;

public class RandomIntTag extends NativeTagExecutor {

    protected static final String MAX_EXCLUSIVE = "maxExclusive";

    @Override
    public Object executeTag(org.w3c.dom.Element randomInt) throws Exception {
        Random random = new Random();
        Object result;
        String seed;

        if (randomInt.hasAttribute(MAX_EXCLUSIVE)) {
            seed = this.engine.evaluateString(randomInt.getAttribute(MAX_EXCLUSIVE), IEngine.EngineStringType.ATTRIBUTE);
            result = new Integer(random.nextInt(Integer.parseInt(seed)));
        } else {
            result = new Integer(random.nextInt());
        }
        return result;
    }
}
