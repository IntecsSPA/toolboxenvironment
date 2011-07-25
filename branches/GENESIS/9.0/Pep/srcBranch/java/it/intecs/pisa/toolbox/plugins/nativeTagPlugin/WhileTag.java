package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class WhileTag extends NativeTagExecutor {
    private static final String CONTINUE = "continue";
    private static final String BREAK = "break";
    
    @Override
    public Object executeTag(org.w3c.dom.Element whileElement) throws Exception {
         Object result = null;
      
        Element conditionElement = DOMUtil.getFirstChild(whileElement);
        while (getBool(conditionElement)) {
            removeLastChildNode();
            
            Iterator elementsOfBody = DOMUtil.getChildren(whileElement).
                    iterator();
            elementsOfBody.next();
            while (elementsOfBody.hasNext()) {
                Element elementOfBody = (Element) elementsOfBody.next();
                if (elementOfBody.getLocalName().equals(CONTINUE)) {
                    Iterator elementsOfContinue = DOMUtil.getChildren(
                            elementOfBody).iterator();
                    Element continueCondition = (Element) elementsOfContinue.
                            next();
                    if (getBool(continueCondition)) {
                        while (elementsOfContinue.hasNext()) {
                            
                            result = this.executeChildTag((Element) elementsOfContinue.
                                    next());
                        }
                        break;
                    } else {
                        continue;
                    }
                }
                if (elementOfBody.getLocalName().equals(BREAK)) {
                    Iterator elementsOfBreak = DOMUtil.getChildren(
                            elementOfBody).iterator();
                    Element breakCondition = (Element) elementsOfBreak.next();
                    if (getBool(breakCondition)) {
                        while (elementsOfBreak.hasNext()) {
                            result = this.executeChildTag((Element) elementsOfBreak.next());
                        }
                        return result;
                    } else {
                        continue;
                    }
                }
                removeLastChildNode();
                result = this.executeChildTag(elementOfBody);
            }
        }
        return result;
    }
}
