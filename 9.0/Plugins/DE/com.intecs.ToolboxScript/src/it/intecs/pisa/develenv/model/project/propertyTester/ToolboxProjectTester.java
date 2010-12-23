package it.intecs.pisa.develenv.model.project.propertyTester;

import org.eclipse.core.expressions.PropertyTester;

public class ToolboxProjectTester extends PropertyTester {

	public ToolboxProjectTester()
	{
		
	}
	
	@Override
	public boolean test(Object arg0, String arg1, Object[] arg2, Object arg3) {
		if(arg1.equals("Operations"))
		return true;
		else return false;
		
	}

}
