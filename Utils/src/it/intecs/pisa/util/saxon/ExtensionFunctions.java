
package it.intecs.pisa.util.saxon;


import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */
public class ExtensionFunctions {

    public static String getInfo (){


        return ("GET INFO: " + "addInfo");
    }


    public static String getTimeRangeList (String list, String start, String end){
        String rangeList="";
        String [] arrayList=list.split(",");
        Long temp;

        Long startNum=Long.parseLong( start);
        Long endNum=Long.parseLong( end);
        for(int i=0; i<arrayList.length; i++){
            if(arrayList[i] == null ? "" != null : !arrayList[i].equals("")){
                temp=Long.parseLong( arrayList[i] );
                temp=temp/1000;

                if( temp<=endNum && temp>=startNum)
                  if(!rangeList.contains(","+temp+","))
                    rangeList+=temp+",";
            }
        }

        return rangeList.substring(0,rangeList.length()-1);
    }

    public static String getDepthRangeList (String list, String start, String end){

        String rangeList="";
        String [] arrayList=list.split(",");
        Double temp;

        Double startNum=Double.parseDouble(start);
        Double endNum=Double.parseDouble(end);
        for(int i=0; i<arrayList.length; i++){
            if(arrayList[i] == null ? "" != null : !arrayList[i].equals("")){
                temp=Double.parseDouble( arrayList[i] );
                if( temp<=endNum && temp>=startNum)
                   if(!rangeList.contains(","+temp+","))
                    rangeList+=temp+",";
            }
        }

        return rangeList.substring(0,rangeList.length()-1);
    }

    public static String getFirstValueList (String list){
        String [] arrayList=list.split(",");
        return arrayList[0];

    }

    public static String getLastValueList (String list){
        String [] arrayList=list.split(",");
        return arrayList[arrayList.length-1];

    }


    public static Element getListElements(Element docEl, String list){
        String [] arrayList=list.split(",");
       // Document doc=docEl.getOwnerDocument();
        DOMUtil dUtil= new DOMUtil();
        Document doc=dUtil.newDocument();
        Element result=doc.createElement("ElementList");
        Element temp=null;
        for(int i=0; i<arrayList.length; i++){
            temp=doc.createElement("el");
            temp.setTextContent(arrayList[i]);
            result.appendChild(temp);

        }
        return result;

    }

    public static boolean isValueOfList (String list, String value){
        String newList=","+list+",";

        return newList.contains(","+value+",");
    }

    public static boolean isTimeOfList (String list, String value){
        String newList=","+list+",";

        return newList.contains(","+(Long.parseLong(value))*1000+",");
    }
}
