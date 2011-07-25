package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import org.w3c.dom.Element;
import it.intecs.pisa.util.DOMUtil;
import java.util.Date;
import java.util.Iterator;

public class MinusTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element minus) throws Exception {
        Iterator values = DOMUtil.getChildren(minus).iterator();
        int result = 0;
        Object firstValue = null;
        Object res = null;

        if (values.hasNext() == false) {
            res = new Integer(result);
            return res;
        }

        firstValue = this.executeChildTag((Element) values.next());

        if (firstValue instanceof Date) {
            //Modifying the date
            Date date;
            Object delta;
            long newDateLongValue = 0;

            newDateLongValue = ((Date) firstValue).getTime();

            while (values.hasNext()) {
                delta =  this.executeChildTag((Element) values.next());

                newDateLongValue -= Long.parseLong(delta.toString());
            }
            res = new Date(newDateLongValue);
        } else {
            result =((Number)firstValue).intValue(); 
            while (values.hasNext()) {
                result -= ((Number) this.executeChildTag((Element) values.next())).intValue();
            }
            res = new Integer(result);
        }

      
        return res;
    }
}
