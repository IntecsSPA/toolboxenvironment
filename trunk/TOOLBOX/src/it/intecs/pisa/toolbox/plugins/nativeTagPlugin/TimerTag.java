package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.db.TimerInstance;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import it.intecs.pisa.toolbox.timers.TimerManager;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.datetime.TimeInterval;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TimerTag extends NativeTagExecutor {

     protected static final String DELAY = "delay";

    @Override
    public Object executeTag(org.w3c.dom.Element timer) throws Exception {
        Element clone=null;
        /*IVariableStore configStore=null;
        TimerManager timerManager;
        
        configStore=this.engine.getConfigurationVariablesStore();
        timerManager=(TimerManager)configStore.getVariable(IToolboxEngineConstants.CONFIGURATION_TIMER_MANAGER);
        */

        String delay = this.engine.evaluateString(timer.getAttribute(DELAY),IEngine.EngineStringType.ATTRIBUTE);
        long interval=TimeInterval.getIntervalAsLong(delay);
        long service_instance_id=0;
        long script_id=0;
        long due_date=DateUtil.getFutureDate(interval).getTime();
        TimerInstance instance;

        service_instance_id=getServiceInstanceId();
        script_id=storeScriptFile(timer);

        instance=new TimerInstance(0);
        instance.setType(TimerInstance.TYPE_TIMER);
        instance.setService_instance_id(service_instance_id);
        instance.setScript_id(script_id);
        instance.setDue_date(due_date);
        instance.store();

        TimerManager timerMan;

        timerMan=TimerManager.getInstance();
        timerMan.addTimerInstance(service_instance_id,script_id,due_date);

        return null;
    }

    private long storeScriptFile(Element timer) throws Exception {
        Document doc;
        DOMUtil util;

        util=new DOMUtil();
        doc=util.newDocument();

        Element sequenceEl;
        sequenceEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","sequence");
        doc.appendChild(sequenceEl);

        IVariableStore varStore = engine.getVariablesStore();
        Hashtable variables = varStore.getVariables();

        Set keySet = variables.keySet();
        String[] keyArray = (String[]) keySet.toArray(new String[0]);

        for(String key:keyArray)
        {
            Element setVariableEl;

            setVariableEl=getSetVariable(doc,key,variables.get(key));
            if(setVariableEl!=null)
                sequenceEl.appendChild(setVariableEl);
        }

        LinkedList children = DOMUtil.getChildren(timer);
        Iterator iter=children.iterator();
        while(iter.hasNext())
        {
            Element child=(Element) iter.next();
            Element importedEl=(Element) doc.importNode(child, true);
            sequenceEl.appendChild(importedEl);
        }

        XMLResourcesPersistence xmlPers;
        xmlPers=XMLResourcesPersistence.getInstance();
        
        String idStr=xmlPers.storeXML(doc);
        return Long.valueOf(idStr);
    }

    protected Element getSetVariable(Document doc, String key, Object value)
    {
        Element setVariableEl = doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","setVariable");
        setVariableEl.setAttribute("name", key);

        Element bodyEl=null;
        if(value instanceof Byte)
            bodyEl=getByteEl(doc,(Byte)value);
        if(value instanceof Short)
            bodyEl=getShortEl(doc,(Short)value);
        if(value instanceof Long)
            bodyEl=getLongEl(doc,(Long)value);
        if(value instanceof Float)
            bodyEl=getFloatEl(doc,(Float)value);
        if(value instanceof Double)
            bodyEl=getDoubleEl(doc,(Double)value);
        if(value instanceof Character)
            bodyEl=getStringEl(doc,((Character)value).toString());
        if(value instanceof Boolean)
            bodyEl=getBooleaneEl(doc,(Boolean)value);
        if(value instanceof String)
            bodyEl=getStringEl(doc,(String)value);
        if(value instanceof Document)
            bodyEl=getDocumentEl(doc,(Document)value);

        if(bodyEl!=null)
            setVariableEl.appendChild(bodyEl);
        return setVariableEl;
    }

    private Element getStringEl(Document doc, String string) {
        Element stringEl;

        stringEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","string");
        stringEl.setTextContent(string);
        return stringEl;
    }

    private Element getByteEl(Document doc, Byte value) {
        Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "byte");
        valueEl.setAttribute("value",Byte.toString(value));
        return valueEl;
    }

    private Element getShortEl(Document doc, Short value) {
         Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "short");
        valueEl.setAttribute("value",Short.toString(value));
        return valueEl;
    }

    private Element getLongEl(Document doc, Long value) {
        Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "long");
        valueEl.setAttribute("value",Long.toString(value));
        return valueEl;
    }

    private Element getFloatEl(Document doc, Float value) {
         Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "float");
        valueEl.setAttribute("value",Float.toString(value));
        return valueEl;
    }

    private Element getDoubleEl(Document doc, Double value) {
         Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "double");
        valueEl.setAttribute("value",Double.toString(value));
        return valueEl;
    }

    private Element getBooleaneEl(Document doc, Boolean value) {
         Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "boolean");
        valueEl.setAttribute("value",Boolean.toString(value));
        return valueEl;
    }

    private Element getDocumentEl(Document doc, Document document) {
        Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","xml");
        valueEl.setAttribute("textTag", "evaluate");
        valueEl.setAttribute("attributePrefix","x");

        Element importedEl=(Element) doc.importNode(document.getDocumentElement(), true);
        valueEl.appendChild(importedEl);
        return valueEl;
    }

    private long getServiceInstanceId() {

        IVariableStore confVarStore = engine.getConfigurationVariablesStore();
        return Long.valueOf((String)confVarStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_ID));

    }


}
