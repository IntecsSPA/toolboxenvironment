/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptThread.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.12 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:05:45 $
 * File ID: $Id: TscriptThread.java,v 1.12 2007/01/22 14:05:45 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.model.debug;

import java.util.StringTokenizer;

import it.intecs.pisa.common.communication.messages.BreakpointHitMessage;
import it.intecs.pisa.develenv.model.debug.TscriptDebugTarget;
import it.intecs.pisa.develenv.model.debug.TscriptStackframe;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

/**
 * The Class TscriptThread.
 */
public class TscriptThread extends PlatformObject implements IThread {

	/**
	 * The Constructor.
	 * 
	 * @param target
	 *            the target
	 */
	public TscriptThread(IDebugTarget target) {
		this.target = (TscriptDebugTarget) target;
		this.stackFrames = new TscriptStackframe[0];
		breakpointHit=new IBreakpoint[0];
	}

	public void setBreakpointHitted(BreakpointHitMessage brkptMsg) {
		String filePath;
		String xpath;
		TscriptLineBreakpoint scriptBrkpt;
		
		this.breakpointHitted = brkptMsg;
		
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints();

		for(IBreakpoint brkpt: breakpoints)
		{
			if(brkpt instanceof TscriptLineBreakpoint)
			{
				scriptBrkpt=(TscriptLineBreakpoint) brkpt;
				
				filePath=scriptBrkpt.getFilePath();
				xpath=scriptBrkpt.getXPath();
				
				if(filePath.equals(brkptMsg.getBreakFilePath()) &&
						xpath.equals(brkptMsg.getBreakXpath()))
				{
					this.breakpointHit=new IBreakpoint[]{brkpt};
				}

			}
		}
		
		calculateStackframes();
	}

	public IBreakpoint[] getBreakpoints() {	
		return breakpointHit;
	}
	

	public String getName() throws DebugException {
		return new String("Toolbox RE");
	}

	public int getPriority() throws DebugException {
		return 0;
	}

	public IStackFrame[] getStackFrames() throws DebugException {
		return stackFrames;
	}

	private void calculateStackframes() {
		StringTokenizer tokenizer = null;
		TscriptStackframe[] stacks=null;
		int count = 0;

		if (breakpointHitted != null) {
			tokenizer = new StringTokenizer(
					breakpointHitted.getBreakFilePath(), "/");
			count = tokenizer.countTokens();

			stacks = new TscriptStackframe[count];

			for (int i = count - 1; i >= 0; i--) {
				stacks[i] = new TscriptStackframe((String) tokenizer
						.nextToken(), breakpointHitted.getLineNumber(), target
						.getVariablesName(), target);
				stacks[i].setDebugTarget(target);
			}
			
			stackFrames=stacks;
		}
	}

	public IStackFrame getTopStackFrame() throws DebugException {
		if ((stackFrames != null) && (stackFrames.length > 0))
			return stackFrames[0];
		else
			return null;
	}

	public boolean hasStackFrames() throws DebugException {
		return stackFrames!=null && stackFrames.length>0;
	}

	public IDebugTarget getDebugTarget() {
		return target;
	}

	public ILaunch getLaunch() {
		return target.getLaunch();
	}

	public String getModelIdentifier() {
		return "com.intecs.toolboxscript.debug";
	}

	public boolean canResume() {
		return target.canResume();
	}

	public boolean canSuspend() {
		return target.canSuspend();
	}

	public boolean isSuspended() {
		return target.isSuspended();
	}

	public void resume() throws DebugException {
		this.stackFrames = null;

		target.setStepping(false);
		target.resume();
	}

	public void suspend() throws DebugException {
		target.suspend();
	}

	public boolean canStepInto() {
		return target.canStepInto();
	}

	public boolean canStepOver() {
		return target.canStepOver();
	}

	public boolean canStepReturn() {
		return target.canStepReturn();
	}

	public boolean isStepping() {
		return target.isStepping();
	}

	public void stepInto() throws DebugException {
		target.stepInto();
	}

	

	public void stepOver() throws DebugException {
		target.stepOver();
	}

	public void stepReturn() throws DebugException {
		target.stepReturn();
	}

	public boolean canTerminate() {
		return target.canTerminate();
	}

	public boolean isTerminated() {
		return target.isTerminated();
	}

	public void terminate() throws DebugException {
		fireTerminateEvent();
		
		this.stackFrames = null;
		
		target.terminate();
	}

	private void fireTerminateEvent() {
		DebugEvent event = new DebugEvent(this, DebugEvent.TERMINATE);
		fireEvent(event);
	}

	private void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}
	
	public void clearStackFrames()
	{
		this.stackFrames=new TscriptStackframe[0];
	}

	protected TscriptDebugTarget target;
	private TscriptStackframe[] stackFrames;
	private BreakpointHitMessage breakpointHitted;
	protected IBreakpoint[] breakpointHit;
	
}
