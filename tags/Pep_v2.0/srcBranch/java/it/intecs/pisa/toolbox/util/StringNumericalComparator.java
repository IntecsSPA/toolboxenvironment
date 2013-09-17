
package it.intecs.pisa.toolbox.util;

import java.util.Comparator;

/**
 *
 * @author Andrea Marongiu
 */
public class StringNumericalComparator implements Comparator {

    public int compare(Object t, Object t1) {
        if(Double.parseDouble((String) t) < Double.parseDouble((String) t1))
            return -1;
        else
           if(Double.parseDouble((String) t) == Double.parseDouble((String) t1))
              return 0;
           else
              return 1;
    }

}
