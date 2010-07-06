/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptVariable.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.7 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:05:45 $
 * File ID: $Id: TscriptVariable.java,v 1.7 2007/01/22 14:05:45 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.model.debug;

import it.intecs.pisa.communication.messages.VariableDescriptionMessage;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

// TODO: Auto-generated Javadoc
/**
 * The Class TscriptVariable.
 */
public class TscriptVariable extends PlatformObject implements IVariable {

	private static final String TYPE_SHORT = "java.lang.Short";
	private static final String TYPE_LONG = "java.lang.Long";
	private static final String TYPE_INTEGER = "java.lang.Integer";
	private static final String TYPE_FLOAT = "java.lang.Float";
	private static final String TYPE_DOUBLE = "java.lang.Double";
	private static final String TYPE_CHARACTER = "java.lang.Character";
	private static final String TYPE_BYTE = "java.lang.Byte";
	private static final String TYPE_BOOLEAN = "java.lang.Boolean";
	private static final String TYPE_STRING = "java.lang.String";
	private static final String TYPE_XML = "org.w3c.dom.Document";

	/**
	 * The Constructor.
	 * 
	 * @param name
	 *            the name
	 * @param debugTarget
	 *            the debug target
	 */
	public TscriptVariable(String name, IDebugTarget debugTarget) {
		VariableDescriptionMessage descrMsg;

		this.name = name;
		this.target = (TscriptDebugTarget) debugTarget;
		this.valueChanged = false;
		this.type = null;

		try {
			descrMsg = target.getVariableDescription(name);
			this.type=descrMsg.getType();
			this.value = new TscriptValue(descrMsg.getValue(),type, target);

		} catch (Exception e) {
			System.err.println(e);
			this.value = new TscriptValue(null, null, target);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	public String getName() throws DebugException {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	public IValue getValue() throws DebugException {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	public boolean hasValueChanged() throws DebugException {
		return this.valueChanged;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		return this.target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return target.getLaunch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(java.lang.String)
	 */
	public void setValue(String expression) throws DebugException {
		target.setVariablesValue(name, type, expression);
		this.value = new TscriptValue(expression, type, target);
		this.valueChanged = true;

		DebugEvent event = new DebugEvent(this, DebugEvent.CHANGE);
		fireEvent(event);
	}

	/**
	 * Fire event.
	 * 
	 * @param event
	 *            the event
	 */
	private void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(org.eclipse.debug.core.model.IValue)
	 */
	public void setValue(IValue value) throws DebugException {
		if (value instanceof TscriptValue) {
			this.valueChanged = true;
			this.value = (TscriptValue) value;
			this.valueChanged = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
	 */
	public boolean supportsValueModification() {
		if (type.equals(TYPE_STRING) || type.equals(TYPE_BOOLEAN)
				|| type.equals(TYPE_BYTE) || type.equals(TYPE_CHARACTER)
				|| type.equals(TYPE_DOUBLE) || type.equals(TYPE_FLOAT)
				|| type.equals(TYPE_INTEGER) || type.equals(TYPE_LONG)
				|| type.equals(TYPE_SHORT) || type.equals(TYPE_XML))
			return true;
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(java.lang.String)
	 */
	public boolean verifyValue(String expression) throws DebugException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(org.eclipse.debug.core.model.IValue)
	 */
	public boolean verifyValue(IValue value) throws DebugException {
		if ((value instanceof TscriptValue)
				&& (((TscriptValue) value).getType().equals(type))) {
			return true;
		} else
			return false;
	}

	/**
	 * The name.
	 */
	private String name;

	/**
	 * The target.
	 */
	private TscriptDebugTarget target;

	/**
	 * The value.
	 */
	private TscriptValue value;

	/**
	 * The type.
	 */
	private String type;

	/**
	 * The value changed.
	 */
	private boolean valueChanged;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		// TODO Auto-generated method stub
		return "com.intecs.toolboxscript.debug";
	}

}
