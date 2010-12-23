/**
 * 
 */
package it.intecs.pisa.common.tbx;

import it.intecs.pisa.common.tbx.exceptions.zipIsNotATBXService;
import it.intecs.pisa.util.Zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author Massimiliano
 *
 */
public class servicePackage {
	
	public static final String FILE_SERVICE_DESCRIPTOR = "serviceDescriptor.xml";
	protected String serviceName=null;
	private String packagePath=null;
	
	private servicePackage()
	{
		
	}
	
	private servicePackage(String rootDir)
	{
		
	}

	public String getServiceName() {
		return serviceName;
	}
	
	public static servicePackage loadFromFile(String filePath) throws zipIsNotATBXService
	{
		File file=null;
		
		file=new File(filePath);
		if(file.exists())
			return loadFromFile(file);
		else return null;
	}
	
	public static servicePackage loadFromFile(File file) throws zipIsNotATBXService
	{
		ZipFile zipFile=null;
		ZipEntry descriptor=null;
		InputStream descriptorStream=null;
		Service descrClass=null;
		
		servicePackage pack=null;
		
		try {
			pack=new servicePackage();
			
			zipFile=new ZipFile(file);
			descriptor=zipFile.getEntry(FILE_SERVICE_DESCRIPTOR);
			if(descriptor==null)
				throw new zipIsNotATBXService();
			
			descriptorStream=zipFile.getInputStream(descriptor);
			
			descrClass=new Service();
			descrClass.loadFromStream(descriptorStream);
			
			pack.serviceName=descrClass.getServiceName();
			pack.packagePath=file.getAbsolutePath();
			
			return pack;
		} catch (zipIsNotATBXService zip) {
			throw zip;
		}
		
		catch (Exception e) {
			return null;
		}
	}
	
	public void extractFromPackage(String destination, boolean createServiceRootDirectory) throws Exception
	{
		File destinationFile=null;
		File serviceDir=null;
		
		if(createServiceRootDirectory==true &&
		   (this.serviceName==null || this.serviceName.equals("")))
				throw new Exception("Cannot create service directory");
			
		if(this.packagePath== null ||this.packagePath.equals(""))
				throw new Exception("Zip file not available");
		
		destinationFile=new File(destination);
		if(destinationFile.exists()==false || destinationFile.isDirectory()==false)
			throw new Exception("Destination directory doesn't exists");
		
		if(createServiceRootDirectory==true)
		{
			//creating service directory
			serviceDir=new File(destination,this.serviceName);
			serviceDir.mkdir();
			
			Zip.extractZipFile(this.packagePath,serviceDir.getAbsolutePath());
		}
		else Zip.extractZipFile(this.packagePath,destinationFile.getAbsolutePath());
	}
	
}
