

package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */
public class ArrayUnionTag extends NativeTagExecutor {


    @Override
    public Object executeTag(org.w3c.dom.Element arrayUnionElement) throws Exception {

        LinkedList arrayUnionParams= DOMUtil.getChildren(arrayUnionElement);
     
        Object [] a1=(Object[]) this.executeChildTag((Element) arrayUnionParams.get(0));
        Object [] a2=(Object[]) this.executeChildTag((Element)arrayUnionParams.get(1));

        Set sArray = new HashSet();

        String [] unionArray=(String[]) a1;
        
	//Set<String> sDepth = new HashSet<String>();
        if(a1 instanceof String[] && a2 instanceof String[]){
            sArray.addAll(Arrays.asList((String[]) a1));
            sArray.addAll(Arrays.asList((String[]) a2));
            unionArray=(String[]) sArray.toArray(new String[sArray.size()]);
            Arrays.sort(unionArray);
        }
       /* }else
           if(a1 instanceof Integer[] && a2 instanceof Integer[]){
            sArray.addAll(Arrays.asList((Integer[]) a1));
            sArray.addAll(Arrays.asList((Integer[]) a2));
            Integer [] unionArray=(Integer[]) sArray.toArray(new Integer[sArray.size()]);
            Arrays.sort(unionArray);
        }else
           if(a1 instanceof Float[] && a2 instanceof Float[]){
            sArray.addAll(Arrays.asList((Float[]) a1));
            sArray.addAll(Arrays.asList((Float[]) a2));
            Float [] unionArray=(Float[]) sArray.toArray(new Float[sArray.size()]);
            Arrays.sort(unionArray);
        }*/


        Arrays.sort(unionArray);
      
        return unionArray;
    }

}
