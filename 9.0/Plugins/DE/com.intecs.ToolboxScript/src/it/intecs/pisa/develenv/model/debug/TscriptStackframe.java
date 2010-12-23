/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptStackframe.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.9 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:59:22 $
 * File ID: $Id: TscriptStackframe.java,v 1.9 2007/01/22 14:59:22 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.model.debug;

import it.intecs.pisa.develenv.model.debug.TscriptVariable;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

// TODO: Auto-generated Javadoc
/**
 * The Class TscriptStackframe.
 */
public class TscriptStackframe extends PlatformObject implements IStackFrame {
	/**
	 * The Constructor.
	 * @param variables the variables
	 * @param target TODO
	 * @param String TODO
	 * @param target the target
	 * @param path the path
	 */
	public TscriptStackframe(String filePath,int lineNumber,String[] variables, TscriptDebugTarget target ) {
		int count=0;
		
		this.path=filePath;
		this.lineNumber=lineNumber;
		
		count=variables.length;
		vars=new TscriptVariable[count];
		for(int i=0;i<count;i++)
		{
			vars[i]=new TscriptVariable(variables[i],target);
		
		}
	}

	public int getCharEnd() throws DebugException {
		return -1;
	}

	public int getCharStart() throws DebugException {
		return -1;
	}

	public int getLineNumber() throws DebugException {
		return this.lineNumber;
	}

	public String getName() throws DebugException {
		return this.path;
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[0];
	}

	public IThread getThread() {
		try {
			return target.getThreads()[0];
		} catch (DebugException e) {
			return null;
		}
	}
	

	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	public boolean hasVariables() throws DebugException {
		if (this.vars != null)
			return true;
		else
			return false;
	}
	
	public IVariable[] getVariables() throws DebugException {
		return vars;
	}

	public IDebugTarget getDebugTarget() {
		return this.target;
	}

	public ILaunch getLaunch() {
		return target.getLaunch();
	}


	public String getModelIdentifier() {
		return "com.intecs.toolboxscript.debug";
	}

	public boolean canStepInto() {
		try {
			return target.canStepInto();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean canStepOver() {
		// TODO Auto-generated method stub
		try {
			return target.canStepOver();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean canStepReturn() {
		// TODO Auto-generated method stub
		try {
			return target.canStepReturn();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isStepping() {
		try {
		return target.isStepping();
		} catch (Exception e) {
			return false;
		}
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
		this.target.resume();
	}

	public void suspend() throws DebugException {
		this.target.suspend();
	}

	
	public boolean canTerminate() {
		return target.canTerminate();
	}

	public boolean isTerminated() {
		return target.isTerminated();
	}

	public void terminate() throws DebugException {
		this.target.terminate();
	}


	public void setXPath(String xpath) throws DebugException {
		this.path = xpath;
	}
		
	public void setDebugTarget(TscriptDebugTarget target)
	{
		this.target= target;
	}
	
	private String path;
	private TscriptDebugTarget target;
	private IVariable[] vars;
	private int lineNumber;

}
