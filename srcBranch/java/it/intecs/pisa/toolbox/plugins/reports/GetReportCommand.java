/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.reports;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.plugins.managerNativePlugins.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class GetReportCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JasperReport report;
        InputStream reportStream;
        OutputStream outstream;

        reportStream = new FileInputStream(new File(Toolbox.getInstance().getRootDir(), "WEB-INF/reports/instancesreport.jrxml"));
        report = JasperCompileManager.compileReport(reportStream);

        Hashtable params = new Hashtable();
        JasperPrint jasPrint = JasperFillManager.fillReport(report, params, new JRResultSetDataSource(getReportRS()));

        outstream = resp.getOutputStream();
        resp.setHeader("content-disposition", "attachment; filename=report.pdf");
        JasperExportManager.exportReportToPdfStream(jasPrint, outstream);
    }

    protected ResultSet getReportRS() throws Exception {
        Statement stm = null;
        ResultSet rs = null;


        stm = ToolboxInternalDatabase.getInstance().getStatement();

        rs = stm.executeQuery("SELECT KEY AS T_STATISTICS_KEY,VALUE AS T_STATISTICS_VALUE FROM T_STATISTICS");

        return rs;


    }
}
