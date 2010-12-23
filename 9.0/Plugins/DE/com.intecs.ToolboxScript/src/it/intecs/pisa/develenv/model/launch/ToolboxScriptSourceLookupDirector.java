package it.intecs.pisa.develenv.model.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;

public class ToolboxScriptSourceLookupDirector extends
		AbstractSourceLookupDirector{

	public void initializeParticipants() {
		addParticipants(new ISourceLookupParticipant[]{new TscriptLookupParticipant()});

	}
}
