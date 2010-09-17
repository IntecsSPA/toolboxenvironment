package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.db.TimerInstance;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.toolbox.timers.TimerManager;
import it.intecs.pisa.toolbox.util.ScriptUtil;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.datetime.TimeInterval;

public class TimerTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element timer) throws Exception {
        String delay = this.engine.evaluateString(timer.getAttribute(DELAY),IEngine.EngineStringType.ATTRIBUTE);
        long interval=TimeInterval.getIntervalAsLong(delay);
        long service_instance_id=0;
        long script_id=0;
        long due_date=DateUtil.getFutureDate(interval).getTime();
        TimerInstance instance;

        service_instance_id=getServiceInstanceId();
        script_id=ScriptUtil.createScriptFromLinkedList(DOMUtil.getChildren(timer),engine.getVariablesStore());

        String idName;
        idName=this.engine.evaluateString(timer.getAttribute(NAME),IEngine.EngineStringType.ATTRIBUTE);

        TimerManager timerMan;

        timerMan=TimerManager.getInstance();
        timerMan.addTimerInstance(service_instance_id,script_id,due_date, idName);

        return null;
    }

    private long getServiceInstanceId() {

        IVariableStore confVarStore = engine.getConfigurationVariablesStore();
        return Long.valueOf((String)confVarStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_ID));

    }

}
