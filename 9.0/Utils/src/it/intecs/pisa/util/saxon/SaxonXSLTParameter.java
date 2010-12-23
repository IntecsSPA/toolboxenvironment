

package it.intecs.pisa.util.saxon;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmAtomicValue;

/**
 *
 * @author Andrea Marongiu
 */
public class SaxonXSLTParameter {
    private QName paramName;
    private XdmAtomicValue paramValue;

    
   public SaxonXSLTParameter(String parameterName, Object parmaterValue){
       this.paramName=new QName(parameterName);
       this.paramValue=new XdmAtomicValue((String)parmaterValue);
   }


    /**
     * @return the paramName
     */
    public QName getParamName() {
        return paramName;
    }

    /**
     * @return the paramValue
     */
    public XdmAtomicValue getParamValue() {
        return paramValue;
    }
}
