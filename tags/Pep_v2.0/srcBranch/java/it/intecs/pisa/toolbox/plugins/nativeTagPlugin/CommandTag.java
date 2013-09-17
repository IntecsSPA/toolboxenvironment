package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;

public class CommandTag extends NativeTagExecutor {

    private static final String ASYNCHRONOUS = "asynchronous";

    @Override
    public Object executeTag(org.w3c.dom.Element command) throws Exception {
        Runtime runtime = null;
        Process process = null;
        String commandLine = null;

        runtime = Runtime.getRuntime();
        commandLine = (String) this.executeChildTag(DOMUtil.getFirstChild(command));
        process = runtime.exec(commandLine);

        if (!getBool(command.getAttribute(ASYNCHRONOUS))) {
            process.waitFor();
             return process.exitValue();
        }

        return null;
    }
}
