package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import java.sql.Connection;
import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.sql.DriverManager;
import java.util.Iterator;
import org.w3c.dom.Element;

public class DbConnectTag extends NativeTagExecutor{
          
	@Override
	public Object executeTag(org.w3c.dom.Element dbConnect) throws Exception{
                    String driver = this.engine.evaluateString(dbConnect.getAttribute(DRIVER),IEngine.EngineStringType.ATTRIBUTE);
        if (driver.length() > 0) {
            Class.forName(driver);
        }
        Iterator children = DOMUtil.getChildren(dbConnect).iterator();
        Connection connection = (Connection) DriverManager.getConnection(
                (String) this.executeChildTag((Element) children.next()),
                (String) this.executeChildTag((Element) children.next()),
                (String) this.executeChildTag((Element) children.next()));
        if (!getBool(dbConnect.getAttribute(AUTO_COMMIT))) {
            connection.setAutoCommit(false);
        }
        return connection;
    }

   

}
