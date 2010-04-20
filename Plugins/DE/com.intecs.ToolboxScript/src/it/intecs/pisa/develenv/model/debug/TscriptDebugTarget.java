package it.intecs.pisa.develenv.model.debug;

import it.intecs.pisa.common.communication.ClientDebugConsole;
import it.intecs.pisa.common.communication.IMessageReceptionListener;
import it.intecs.pisa.common.communication.messages.BooleanValueMessage;
import it.intecs.pisa.common.communication.messages.BreakpointHitMessage;
import it.intecs.pisa.common.communication.messages.CanStepIntoMessage;
import it.intecs.pisa.common.communication.messages.CanStepOverMessage;
import it.intecs.pisa.common.communication.messages.CanStepReturnMessage;
import it.intecs.pisa.common.communication.messages.DescribeVariableMessage;
import it.intecs.pisa.common.communication.messages.ExecutionCompletedMessage;
import it.intecs.pisa.common.communication.messages.ExecutionResumedMessage;
import it.intecs.pisa.common.communication.messages.ExecutionStartedMessage;
import it.intecs.pisa.common.communication.messages.ExecutionTreeMessage;
import it.intecs.pisa.common.communication.messages.GetExecutionTreeMessage;
import it.intecs.pisa.common.communication.messages.GetVariablesMessage;
import it.intecs.pisa.common.communication.messages.RemoveBreakpointMessage;
import it.intecs.pisa.common.communication.messages.SetBreakpointMessage;
import it.intecs.pisa.common.communication.messages.SetVariableValueMessage;
import it.intecs.pisa.common.communication.messages.StepIntoMessage;
import it.intecs.pisa.common.communication.messages.StepOverMessage;
import it.intecs.pisa.common.communication.messages.StepReturnMessage;
import it.intecs.pisa.common.communication.messages.StructuredMessage;
import it.intecs.pisa.common.communication.messages.TerminateMessage;
import it.intecs.pisa.common.communication.messages.VariableDescriptionMessage;
import it.intecs.pisa.common.communication.messages.VariablesListMessage;
import it.intecs.pisa.develenv.model.interfaces.ContentChangeNotifier;
import it.intecs.pisa.util.DOMUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;

public class TscriptDebugTarget extends PlatformObject implements IDebugTarget,
		IMessageReceptionListener {

	public static final int STEP_INTO = 0;

	public static final Object CANNOT_STEP = null;

	public static final int STEP_OVER = 0;

	public static final int STEP_RETURN = 0;

	protected ClientDebugConsole console;

	protected ILaunch launch;

	protected IThread[] threads;

	protected TscriptThread thread;

	private EventDispatchJob fEventDispatch;

	private boolean isTerminated;

	private boolean isSuspended;

	protected boolean isStepping;

	protected String[] variables;

	private boolean canStepInto = false;

	private boolean canStepOver = false;

	private boolean canStepReturn = false;

	protected IFolder testFolder = null;

	public TscriptDebugTarget(ILaunch launch, ClientDebugConsole console,
			IFolder testResultFolder) throws Exception {

		thread = new TscriptThread(this);
		threads = new IThread[] { thread };

		this.console = console;
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(
				this);

		fEventDispatch = new EventDispatchJob();
		fEventDispatch.schedule();

		isTerminated = false;
		isSuspended = false;
		isStepping = false;

		variables = null;

		testFolder = testResultFolder;
		
		this.launch=launch;
		
	}

	public boolean canTerminate() {
		boolean canTerminate = true;

		if ((this.isSuspended == true) && (this.isTerminated == false))
			canTerminate = true;
		else
			canTerminate = false;

		return canTerminate;
	}

	public boolean isTerminated() {
		return this.isTerminated;
	}

	public void terminate() throws DebugException {
		try {
			TerminateMessage msg;

			msg = new TerminateMessage();
			console.sendCommand(msg);
		} catch (Exception e) {
			throw new DebugException(null);
		} finally {
			terminated();
		}
	}

	private void terminated() {
		isSuspended = false;
		isTerminated = true;

		threads = new IThread[0];
		thread.clearStackFrames();

		//this.setExecutionTreeToView("");
		fireTerminateEvent();
	}

	private void fireTerminateEvent() {
		DebugEvent event = new DebugEvent(this, DebugEvent.TERMINATE);
		fireEvent(event);

		event = new DebugEvent(thread, DebugEvent.TERMINATE);
		fireEvent(event);

		/*
		 * event = new DebugEvent(this, DebugEvent.CHANGE); fireEvent(event);
		 */

	}

	public boolean canResume() {
		boolean canResume = false;

		if ((isSuspended == true) && (this.isTerminated == false))
			canResume = true;
		else
			canResume = false;
		return canResume;
	}

	public boolean canSuspend() {
		return false;
	}

	public boolean isSuspended() {
		return this.isSuspended;
	}

	public void resume() throws DebugException {

		try {
			console.sendCommand(new ExecutionResumedMessage());
			resumed();
		} catch (Exception e) {
			terminate();
		}

	}

	private void resumed() {
		isTerminated = false;
		isSuspended = false;

		fireResumedEvent();
	}

	private void fireResumedEvent() {
		DebugEvent event = new DebugEvent(this, DebugEvent.RESUME);
		fireEvent(event);

		event = new DebugEvent(thread, DebugEvent.RESUME);
		fireEvent(event);
	}

	public void suspend() throws DebugException {
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
	}

	public void addBreakpoint(String breakpoint) {

	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
	}

	public void removeBreakpoint(String breakpoint) {
	}

	public boolean canDisconnect() {
		return false;
	}

	public void disconnect() throws DebugException {
	}

	public boolean isDisconnected() {
		return false;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException {
		return null;
	}

	public boolean supportsStorageRetrieval() {
		return false;
	}

	/**
	 * Started.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	protected void started() throws Exception {
		DebugEvent event = new DebugEvent(this, DebugEvent.CREATE);
		fireEvent(event);

		event = new DebugEvent(this, DebugEvent.RESUME,
				DebugEvent.CLIENT_REQUEST);
		fireEvent(event);

		this.isSuspended = false;
		this.isTerminated = false;
	}

	/**
	 * Breakpoint hitted.
	 * 
	 * @param xpath
	 *            the xpath
	 * @param filePath
	 *            TODO
	 */
	public void breakpointHit(BreakpointHitMessage brkptMsg) {
		try {
			thread.setBreakpointHitted(brkptMsg);

			fireBreakpointHit();

			this.isSuspended = true;
			this.isTerminated = false;
		} catch (Exception e) {
			terminated();
		}
	}

	protected void fireBreakpointHit() {
		DebugEvent event;

		/*event = new DebugEvent(this, DebugEvent.SUSPEND, DebugEvent.BREAKPOINT);

		fireEvent(event);*/

		event = new DebugEvent(thread, DebugEvent.SUSPEND,
				DebugEvent.BREAKPOINT);

		fireEvent(event);
		
	}

	private void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}

	public String[] getVariablesName() {
		return variables;
	}

	public VariableDescriptionMessage getVariableDescription(String varName) {
		DescribeVariableMessage descrMsg;

		descrMsg = new DescribeVariableMessage(varName);
		console.sendCommand(descrMsg);
		return (VariableDescriptionMessage) console.readCommand();
	}

	public void setVariablesValue(String name, String type, String value) {
		SetVariableValueMessage msg = null;
		DOMUtil util;

		if (type.equals("org.w3c.dom.Document")) {
			util = new DOMUtil();

			try {
				msg = new SetVariableValueMessage(name, util
						.stringToDocument(value));
			} catch (IOException e) {
			} catch (SAXException e) {
			}
		} else {
			msg = new SetVariableValueMessage(name, type, value);
		}

		console.sendCommand(msg);
	}

	protected void retrieveVariablesInformation() {
		GetVariablesMessage getVarMsg;
		VariablesListMessage varListMsg;

		getVarMsg = new GetVariablesMessage();
		console.sendCommand(getVarMsg);
		varListMsg = (VariablesListMessage) console.readCommand();

		variables = varListMsg.getVariables();
	}

	protected void retrieveSteppingInformation() {
		CanStepIntoMessage stepIntoMsg;
		CanStepOverMessage stepOverMsg;
		CanStepReturnMessage stepReturnMsg;
		BooleanValueMessage canStep;

		stepIntoMsg = new CanStepIntoMessage();
		console.sendCommand(stepIntoMsg);
		canStep = (BooleanValueMessage) console.readCommand();
		canStepInto = canStep.getValue();

		stepOverMsg = new CanStepOverMessage();
		console.sendCommand(stepOverMsg);
		canStep = (BooleanValueMessage) console.readCommand();
		canStepOver = canStep.getValue();

		stepReturnMsg = new CanStepReturnMessage();
		console.sendCommand(stepReturnMsg);
		canStep = (BooleanValueMessage) console.readCommand();
		canStepReturn = canStep.getValue();
	}

	protected void retrieveAndSetExecutionTree() {
		GetExecutionTreeMessage getMsg;
		ExecutionTreeMessage execMsg = null;
		org.eclipse.jface.text.Document viewDoc;
		String treeStr = "";
		ByteArrayOutputStream outStream;

		try {
			getMsg = new GetExecutionTreeMessage();
			console.sendCommand(getMsg);
			execMsg = (ExecutionTreeMessage) console.readCommand();

			outStream=new ByteArrayOutputStream();
			DOMUtil.dumpXML(execMsg.getExecutionTree(),outStream,true);
			
			treeStr=new String(outStream.toByteArray());
			//removing xml header
			treeStr=treeStr.substring(treeStr.indexOf('>')+1);
		} catch (Exception e) {
			treeStr = "Cannot retrieve execution tree";
		}

		if (execMsg != null) {
			if (execMsg.getFileName().equals("")==false) {
				try {
					File resourceFile;
					resourceFile = new File(testFolder.getFile(
							execMsg.getFileName()).getLocationURI());
					DOMUtil.dumpXML(execMsg.getExecutionTree(), resourceFile,true);
					testFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				setExecutionTreeToView(treeStr);
		}

	}

	protected void setExecutionTreeToView(String treeStr) {
		IWorkbench workbench;
		;
		IViewReference[] refs;
		IViewPart part;

		workbench = PlatformUI.getWorkbench();
		
		for(IWorkbenchWindow window: workbench.getWorkbenchWindows())
		{
			for(IWorkbenchPage page:window.getPages())
			{
				refs = page.getViewReferences();

				for (IViewReference ref : refs) {
					if (ref.getId().equals(
							"it.intecs.pisa.develenv.ui.views.ExecutionTreeView")) {
						part = ref.getView(true);
						if(part instanceof ContentChangeNotifier)
							((ContentChangeNotifier)part).contentChanged(treeStr);
					}
				}
			}
		}
		
	}

	private void handleExecutionTree(ExecutionTreeMessage msg) {
		IFile resource;
		String filename;
		File resourceFile;

		try {		
			filename = msg.getFileName();
			if (filename != null && filename.equals("") == false) {		
				resource = testFolder.getFile(filename);

				resourceFile = new File(resource.getLocationURI());
				DOMUtil.dumpXML(msg.getExecutionTree(), resourceFile,true);

				testFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * The Class EventDispatchJob.
	 */
	class EventDispatchJob extends Job {

		/**
		 * The Constructor.
		 */
		public EventDispatchJob() {
			super("PDA Event Dispatch");
			setSystem(true);
		}

		protected IStatus run(IProgressMonitor monitor) {
			StructuredMessage msg;
			BreakpointHitMessage brkptMsg;
			
			System.out.println("Starting TscriptDebugTarget");
			
			while (!isTerminated()) {
				try {
					System.out.println(" ");
					msg = console.readCommand();
					
					//System.out.println("Got Message");
					
					if (msg != null) {
						//System.out.println("Readed message "+msg.getClass().getCanonicalName());
												
						if (msg instanceof TerminateMessage
								|| msg instanceof ExecutionCompletedMessage) {
							terminated();
						} else if (msg instanceof ExecutionStartedMessage)
							started();
						else if (msg instanceof BreakpointHitMessage) {
							brkptMsg = (BreakpointHitMessage) msg;

							retrieveSteppingInformation();
							retrieveVariablesInformation();

							breakpointHit(brkptMsg);

							//retrieveAndSetExecutionTree();
						} else if (msg instanceof ExecutionTreeMessage)
							handleExecutionTree((ExecutionTreeMessage) msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					isTerminated = true;
				}
			}

			return Status.OK_STATUS;
		}

	}

	/**
	 * Sets the stepping.
	 * 
	 * @param stepping
	 *            the stepping
	 */
	public void setStepping(boolean stepping) {
		this.isStepping = stepping;
	}

	/**
	 * Checks if is stepping.
	 * 
	 * @return true, if is stepping
	 */
	public boolean isStepping() {
		return this.isStepping;
	}

	public void MessageReceived(StructuredMessage msg) {

	}

	public String getName() throws DebugException {
		return "Toolbox DE";
	}

	public IProcess getProcess() {
		return null;
	}

	public IThread[] getThreads() throws DebugException {
		return threads;
	}

	public boolean hasThreads() throws DebugException {
		return true;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return true;
	}

	public IDebugTarget getDebugTarget() {
		return this;
	}

	public ILaunch getLaunch() {
		return this.launch;
	}

	public String getModelIdentifier() {
		return "com.intecs.ToolboxScript.model";
	}

	/*
	 * These methods are not coming from interfaces. They are convenience
	 * methods
	 * 
	 */
	public boolean canStepInto() {
		return false;
		//return this.canStepInto;
	}

	public boolean canStepOver() {
		return false;
		//return this.canStepOver;
	}

	public boolean canStepReturn() {
		return false;
		//return this.canStepReturn;
	}

	public void stepInto() {
		StepIntoMessage stepMsg;

		stepMsg = new StepIntoMessage();
		console.sendCommand(stepMsg);
	}

	public void stepOver() {
		StepOverMessage stepMsg;

		stepMsg = new StepOverMessage();
		console.sendCommand(stepMsg);
	}

	public void stepReturn() {
		StepReturnMessage stepMsg;

		stepMsg = new StepReturnMessage();
		console.sendCommand(stepMsg);
	}
}
