package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IToolboxEngineConstants;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.TimerManager;
import org.w3c.dom.Element;

public class TimerTag extends NativeTagExecutor {

     protected static final String DELAY = "delay";

    @Override
    public Object executeTag(org.w3c.dom.Element timer) throws Exception {
        Element clone=null;
        IVariableStore configStore=null;
        TimerManager timerManager;
        
        configStore=this.engine.getConfigurationVariablesStore();
        timerManager=(TimerManager)configStore.getVariable(IToolboxEngineConstants.CONFIGURATION_TIMER_MANAGER);
        
        clone= (Element) timer.cloneNode(true);

        String delay = this.engine.evaluateString(timer.getAttribute(DELAY),IEngine.EngineStringType.ATTRIBUTE);

        clone.setAttribute(DELAY, delay);

        timerManager.addTimer(this.engine.getVariablesStore().getVariables(), clone);

        return null;
    }
}
