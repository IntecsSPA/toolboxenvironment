package it.intecs.pisa.develenv.model.project;

import it.intecs.pisa.common.stream.XmlDirectivesFilterStream;
import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.InterfacesDescription;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.utils.BundleUtil;
import it.intecs.pisa.develenv.model.utils.FileSystemUtil;
import it.intecs.pisa.develenv.model.utils.InterfaceDescriptionLoader;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.XmlRootSchemaUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.net.URI;
import java.net.URL;
import java.util.Vector;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class is used to create and manage a TOOLBOX project under Eclipse
 * 
 * @author Massimiliano
 */
public class ToolboxEclipseProject {

	public static final String NAMESPACE_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

	public static final String FOLDER_TEST_DIR = "Test Files";

	public static final String FOLDER_RESOURCES = "Resources";

	public static final String FOLDER_SCHEMAS = "Schemas";

	public static final String FOLDER_INFO = "Info";

	public static final String FOLDER_OPERATIONS = "Operations";

	private static final String FOLDER_USER_CONFIGURATIONS = "TOOLBOXUserConfigurations";

	public static final String FOLDER_COMMON_SCRIPTS = "Common Scripts";

	public static final String FOLDER_JARS = "External Jars";

	private static final String ATTRIBUTE_SCHEMA_LOCATION = "schemaLocation";

	private static final String FILE_DESCRIPTION_TXT = "description.txt";

	private static final String FILE_ABSTRACT_TXT = "abstract.txt";

	private static final String BUNDLE_INTERFACES = "com.intecs.ToolboxScript.interfaces";

	private static final String BUNDLE_EDITOR_FILES = "com.intecs.ToolboxScript.editorFiles";

	public static final String FILE_SERVICE_DESCRIPTOR = "serviceDescriptor.xml";

	public static final String FOLDER_ADDITIONAL_RESOURCES = "AdditionalResources";

	private String tbxScriptPath = null;

	private Service serviceDescr = null;

	private DOMUtil domutil = null;

	/**
	 * Default constructor
	 * 
	 * @param descr
	 *            ServiceDescriptor used to initialize the Eclipse project
	 */
	public ToolboxEclipseProject(Service descr) {
		Bundle interfacesPlugin = null;
		URL entry = null;

		serviceDescr = descr;
		this.domutil = new DOMUtil();

		interfacesPlugin = Platform.getBundle(BUNDLE_EDITOR_FILES);
		entry = interfacesPlugin.getEntry("schemas/xmlScript.xsd");

		try {
			tbxScriptPath = FileLocator.toFileURL(entry).toURI().toString();
		} catch (Exception e) {
			tbxScriptPath = "";
		}
	}

	/**
	 * This method is used to create the project on disk
	 */
	public void createProject() {
		IProject project = ToolboxEclipseProjectUtil.getProject(serviceDescr
				.getServiceName());

		try {
			// creating project
			if (!project.exists()) {
				project.create(null);
			} else
				return;

			if (!project.isOpen()) {
				project.open(null);
			}

			createServiceInfoDirectoryAndFiles(project);

			final IFolder schemasFolder = project.getFolder(FOLDER_SCHEMAS);

			if (!schemasFolder.exists()) {
				schemasFolder.create(IResource.NONE, true, null);
			}

			copySchemaSet(schemasFolder);

			final IFolder resourcesFolder = project.getFolder(FOLDER_RESOURCES);

			if (!resourcesFolder.exists()) {
				resourcesFolder.create(IResource.NONE, true, null);
			}

			createResources(resourcesFolder);

			final IFolder scriptFolder = project.getFolder(FOLDER_OPERATIONS);

			if (!scriptFolder.exists()) {
				scriptFolder.create(IResource.NONE, true, null);
			}

			createOperations(scriptFolder);

			createDescriptor(project);

			final IFolder testFolder = project.getFolder(FOLDER_TEST_DIR);

			if (!testFolder.exists()) {
				testFolder.create(IResource.NONE, true, null);
			}
			createTestFiles(testFolder);

			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Exception ecc) {

		}
	}

	/**
	 * This method is used to update an already existing TOOLBOX project under
	 * Eclipse
	 * 
	 * @param newDescr
	 *            Service used as reference for determining deltas over project
	 */
	public void updateProject(Service newDescr) {
		Interface implementedInterf = null;
		Interface updateInterf = null;
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root = null;
		IProject project = null;
		IFolder operations = null;
		IFolder testDirectory = null;

		try {
			implementedInterf = serviceDescr.getImplementedInterface();
			updateInterf = newDescr.getImplementedInterface();

			serviceDescr.setImplementedInterface(newDescr
					.getImplementedInterface());

			wrkSpace = ResourcesPlugin.getWorkspace();
			root = wrkSpace.getRoot();
			project = root.getProject(serviceDescr.getServiceName());
			operations = project.getFolder(FOLDER_OPERATIONS);
			testDirectory = project.getFolder(FOLDER_TEST_DIR);

			// add check here! must check for interface type change
			// removeOperation(implementedOper, newOper, operations);
			// adding operations
			addOperations(operations, implementedInterf, updateInterf);

			// removeTestDirectory(implementedOper, newOper, testDirectory);
			addTestFiles(testDirectory, implementedInterf, updateInterf);

			// dumping descriptor
			// serviceDescr.setImplementedInterface(newDescr.getImplementedInterface());
			createDescriptor(project);

			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Exception e) {

		}

	}

	public void addOperation(Operation newOp) throws Exception
	{
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root = null;
		IProject project = null;
		IFolder operations = null;
		IFolder operationFolder;
		IFolder testDirectory = null;
		IFolder testFolder = null;
		
		wrkSpace = ResourcesPlugin.getWorkspace();
		root = wrkSpace.getRoot();
		project = root.getProject(serviceDescr.getServiceName());
		operations = project.getFolder(FOLDER_OPERATIONS);
		operationFolder=operations.getFolder(newOp.getName());
		testDirectory = project.getFolder(FOLDER_TEST_DIR);
		
		serviceDescr.getImplementedInterface().addOperations(newOp);
		
		if(operationFolder.exists()==false)
			operationFolder.create(true, true, null);
		
		for (Script s : newOp.getScripts()) {
			IPath scriptPath = copyScriptFileFromRepository(s.getType(),operationFolder, newOp);

			String p;
			p=scriptPath.removeFirstSegments(1).toString();
			
			s.setPath(p);
		}
		
		setRootSchemaIntoScripts(operationFolder);

		testFolder=testDirectory.getFolder(newOp.getName());
		if(testFolder.exists()==false)
			testFolder.create(true, true, null);
		
		InputStream fileStream = BundleUtil.getEntryAsInputStream("outOfRepository/testFiles/genericTest.xml",BUNDLE_INTERFACES);
		IOUtil.copy(fileStream, new FileOutputStream(new File(testFolder.getFile("test.xml").getLocationURI())));

		createDescriptor(project);
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}
	
	private void createResources(IFolder resourcesFolder) {
		IFolder commonFolder = null;
		IFolder jarsFolder = null;
		try {
			commonFolder = resourcesFolder.getFolder(FOLDER_COMMON_SCRIPTS);

			if (!commonFolder.exists()) {
				commonFolder.create(IResource.NONE, true, null);
			}

			jarsFolder = resourcesFolder.getFolder(FOLDER_JARS);
			if (!jarsFolder.exists()) {
				jarsFolder.create(IResource.NONE, true, null);
			}
		} catch (Exception e) {

		}

	}

	/**
	 * This method is used to create test files for operations
	 * 
	 * prerequisite: Service.xml already created under project directory
	 * 
	 * @param folder
	 *            Folder containing the Test Files
	 * @param updateInterf
	 * @param implementedInterf2
	 */
	private void addTestFiles(IFolder folder, Interface implementedInterf,
			Interface updateInterf) {
		String testFilePath = null;
		URI destPath = null;
		InputStream stream = null;
		DOMUtil util = null;
		Document doc = null;
		File destFile = null;
		String operationName = null;
		Operation[] newOperation = null;
		IFolder operFolder = null;

		try {
			util = new DOMUtil();

			newOperation = updateInterf.getOperations();

			for (Operation oper : newOperation) {

				if (implementedInterf.isOperationImplemented(oper.getName()) == false) {
					testFilePath = oper.getTestFile();
					if (testFilePath != null
							&& testFilePath.equals("") == false) {
						operationName = oper.getName();

						operFolder = folder.getFolder(operationName);

						if (operFolder.exists() == false)
							operFolder.create(IResource.NONE, true, null);

						destPath = operFolder.getFile(operationName + ".xml")
								.getLocationURI();

						stream = ToolboxEclipseProject.getTestFileStream(
								serviceDescr.getServiceName(),
								implementedInterf, operationName, false);

						doc = util.inputStreamToDocument(stream);
						destFile = new File(destPath);
						DOMUtil.dumpXML(doc, destFile);

						folder.refreshLocal(IResource.DEPTH_INFINITE, null);
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method is used to create test files for operations
	 * 
	 * prerequisite: Service.xml already created under project directory
	 * 
	 * @param folder
	 *            Folder containing the Test Files
	 */
	private void createTestFiles(IFolder folder) {
		String testFilePath = null;
		URI destPath = null;
		InputStream stream = null;
		DOMUtil util = null;
		Document doc = null;
		File destFile = null;
		File schemaFile = null;
		String operationName = null;
		String serviceName = null;
		try {
			util = new DOMUtil();

			Interface implementedInterf = serviceDescr
					.getImplementedInterface();
			serviceName = serviceDescr.getServiceName();

			if (implementedInterf != null) {
				for (Operation oper : implementedInterf.getOperations()) {
					testFilePath = oper.getTestFile();
					if (testFilePath != null
							&& testFilePath.equals("") == false) {
						operationName = oper.getName();
						IFolder operFolder = folder.getFolder(operationName);

						if (operFolder.exists() == false) {
							operFolder.create(IResource.NONE, true, null);
						}

						destPath = operFolder.getFile(operationName + ".xml")
								.getLocationURI();
						schemaFile = new File(implementedInterf.getSchemaDir(),
								implementedInterf.getSchemaRoot());

						stream = ToolboxEclipseProject.getTestFileStream(
								serviceName, implementedInterf, operationName,
								false);

						doc = util.inputStreamToDocument(stream);
						destFile = new File(destPath);
						DOMUtil.dumpXML(doc, destFile);

						folder.refreshLocal(IResource.DEPTH_INFINITE, null);
					}

				}
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	private void createDescriptor(final IProject project) throws CoreException {
		// Whe are now using the information stored into the Service Descriptor
		// in order
		// to create a Service.xml (TOOLBOX RE like)
		Document descriptor = null;
		OutputStream output = null;
		IFile descrFile = null;
		IFileStore descrFileStore = null;

		try {
			descriptor = serviceDescr.createServiceDescriptor();

			descrFile = project.getFile(FILE_SERVICE_DESCRIPTOR);
			descrFileStore = EFS.getStore(descrFile.getLocationURI());

			output = descrFileStore.openOutputStream(0, null);
			DOMUtil.dumpXML(descriptor, output, true);
			output.close();

		} catch (final Exception e) {
			throw new CoreException(null);
		}
	}

	private void copySchemaSet(final IFolder folder) throws CoreException {
		URI sourceFolderURI = null;
		URI destFolderURI = null;
		IFileStore destStore = null;
		String schemaPath = null;

		try {
			schemaPath = serviceDescr.getSchemaPath();

			if (schemaPath != null) {
				sourceFolderURI = parsePathForURI(schemaPath);
				destFolderURI = folder.getLocationURI();

				FileSystemUtil.copyDirectory(sourceFolderURI, destFolderURI);

				folder.refreshLocal(IResource.DEPTH_INFINITE, null);

				// relocating import for each schema
				destStore = EFS.getStore(destFolderURI);

				relocateSchemas(destStore, folder.getParent().getLocation()
						.toOSString());
			}

		} catch (final Exception e) {

		}
	}

	private void relocateSchemas(IFileStore schemaDirStore, String startPath)
			throws CoreException {
		IFileStore[] subDirs = null;
		InputStream input = null;
		OutputStream output = null;
		Document doc = null;

		if (schemaDirStore.fetchInfo().isDirectory()) {
			subDirs = schemaDirStore.childStores(0, null);
			for (final IFileStore st : subDirs) {
				final File newSubPath = new File(startPath, schemaDirStore
						.fetchInfo().getName());
				this.relocateSchemas(st, newSubPath.getAbsolutePath());
			}

		} else {
			// it is a file, must relocate
			try {
				input = schemaDirStore.openInputStream(0, null);
				doc = this.domutil.inputStreamToDocument(input);

				this.relocateSchema(doc, startPath);

				output = schemaDirStore.openOutputStream(0, null);
				DOMUtil.dumpXML(doc, output, true);

				output.close();

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void relocateSchema(final Document doc, final String path) {
		Element root = null;
		NodeList importNodes = null;
		String schemaLocation = null;
		String newSchemaLocation = null;
		Element importNode = null;
		File filePath = null;

		root = doc.getDocumentElement();
		importNodes = root.getElementsByTagNameNS(
				"http://www.w3.org/2001/XMLSchema", "import");
		// importNodes=root.getElementsByTagName("import");
		for (int i = 0; i < importNodes.getLength(); i++) {
			importNode = (Element) importNodes.item(i);

			schemaLocation = importNode.getAttribute(ATTRIBUTE_SCHEMA_LOCATION);

			filePath = new File(path, schemaLocation);
			newSchemaLocation = filePath.getAbsolutePath();

			// TODO: Modify with URI
			importNode
					.setAttribute(ATTRIBUTE_SCHEMA_LOCATION, filePath.toURI()
							.toString()/* "file:/"+newSchemaLocation.replace('\\','/') */);
		}

		importNodes = root.getElementsByTagNameNS(
				"http://www.w3.org/2001/XMLSchema", "include");
		// importNodes=root.getElementsByTagName("include");
		for (int i = 0; i < importNodes.getLength(); i++) {
			importNode = (Element) importNodes.item(i);

			schemaLocation = importNode.getAttribute(ATTRIBUTE_SCHEMA_LOCATION);

			filePath = new File(path, schemaLocation);
			newSchemaLocation = filePath.getAbsolutePath();

			importNode
					.setAttribute(ATTRIBUTE_SCHEMA_LOCATION, filePath.toURI()
							.toString()/* "file:/"+newSchemaLocation.replace('\\','/') */);
		}

	}

	private void addOperations(IFolder folder, Interface implementedInterf,Interface updateInterf) throws CoreException {
		Operation[] operations = null;
		IFolder operationFolder = null;
		IPath scriptPath;

		try {
			operations = updateInterf.getOperations();

			for (Operation op : operations) {

				if (implementedInterf.isOperationImplemented(op.getName()) == false) {

					operationFolder = folder.getFolder(op.getName());
					operationFolder.create(true, true, null);

					Script[] scripts;
					Operation newOperation;

					newOperation = (Operation) op.clone();

					scripts = newOperation.getScripts();
					for (Script s : scripts) {
						scriptPath = copyScriptFileFromRepository(s.getType(),operationFolder, op);

						String p;
						p=scriptPath.removeFirstSegments(1).toString();
						
						s.setPath(p);
					}
					
					implementedInterf.addOperations(newOperation);
				}

				folder.refreshLocal(IResource.DEPTH_INFINITE, null);
				setRootSchemaIntoScripts(operationFolder);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void createOperations(IFolder folder) throws CoreException {
		Operation[] operations = null;
		Interface interf = null;
		IFolder operationFolder = null;
		IPath scriptPath;

		try {
			interf = serviceDescr.getImplementedInterface();
			if (interf != null) {
				operations = interf.getOperations();

				for (Operation op : operations) {
					operationFolder = folder.getFolder(op.getName());

					if (operationFolder.exists() == false) {
						operationFolder.create(true, true, null);

						for(Script s:op.getScripts())
						{
							scriptPath = copyScriptFileFromRepository(
									s.getType(),operationFolder, op);
							
							op.getScript(s.getType()).setPath(
									scriptPath.removeFirstSegments(1).toString());
						}
						
						folder.refreshLocal(IResource.DEPTH_INFINITE, null);
						setRootSchemaIntoScripts(operationFolder);
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private IPath copyScriptFileFromRepository(String type,
			IFolder operationFolder, Operation op) {
		URI destFileURI;
		String path;
		String fileName;
		File file;
		URI sourceURI;
		IFile destFile;

		path = op.getScript(type).getPath();
		sourceURI = parsePathForURI(path);

		file = new File(sourceURI);

		fileName = file.getName();

		destFile = operationFolder.getFile(fileName);
		destFileURI = destFile.getLocationURI();

		FileSystemUtil.copyFile(sourceURI, destFileURI);

		return destFile.getFullPath();
	}

	private void setRootSchemaIntoScripts(IFolder operationFolder) {
		// This method sets the schema location to all tscript files
		IResource[] children = null;
		String path = null;

		try {
			children = operationFolder.members(false);
			for (IResource resource : children) {
				if (resource instanceof IFolder)
					setRootSchemaIntoScripts((IFolder) resource);
				else if (resource instanceof IFile) {
					path = resource.getLocationURI().getPath();
					if (path.endsWith(".tscript")) {
						setScriptSchema(path);
					}
				}
			}
		} catch (Exception ecc) {

		}
	}

	private void setScriptSchema(String path) {
		Document doc = null;
		DOMUtil util = null;

		try {
			util = new DOMUtil(true);
			doc = util.fileToDocument(path);

			XmlRootSchemaUtil.updateSchemaLocation(doc, "http://pisa.intecs.it/mass/toolbox/xmlScript",tbxScriptPath);
			
			DOMUtil.dumpXML(doc, new File(path));
		} catch (Exception e) {

		}

	}

	private URI parsePathForURI(String path) {
		Bundle interfacesPlugin = null;
		URL entry = null;
		String userHome = null;
		File dir = null;
		File file = null;

		try {
			interfacesPlugin = Platform.getBundle(BUNDLE_INTERFACES);
			entry = interfacesPlugin.getEntry(path);
			return FileLocator.toFileURL(entry).toURI();
		} catch (Exception e) {
			try {
				userHome = System.getProperty("user.home");
				dir = new File(userHome, FOLDER_USER_CONFIGURATIONS);
				file = new File(dir, path);
				return file.toURI();
			} catch (Exception e1) {
				return null;
			}
		}
	}

	private void createServiceInfoDirectoryAndFiles(IProject project)
			throws CoreException {
		try {
			final IFolder infoFolder = project.getFolder(FOLDER_INFO);

			InputStream abstractStream, descriptionStream;

			if (!infoFolder.exists()) {
				infoFolder.create(IResource.NONE, true, null);
			}

			abstractStream = serviceDescr.getServiceAbstract();
			final IFile abstractFile = infoFolder.getFile(FILE_ABSTRACT_TXT);

			abstractFile.create(abstractStream, true, null);

			descriptionStream = serviceDescr.getServiceDescription();
			final IFile descriptionFile = infoFolder
					.getFile(FILE_DESCRIPTION_TXT);

			descriptionFile.create(descriptionStream, true, null);
		} catch (final Exception ecc) {

		}
	}

	public static InputStream getTestFileStream(String serviceName,
			Interface interf, String operation, boolean addHeaderFooter)
			throws Exception {
		Interface repInterf = null;
		InterfacesDescription interfDescr = null;
		InputStream schemaLocationUpdatedStream = null;
		InputStream fileStream = null;
		Bundle bundle = null;
		URL url = null;
		Operation op = null;

		try {
			bundle = Platform.getBundle(BUNDLE_INTERFACES);
			url = bundle.getEntry("interfacesDefinition.xml");

			interfDescr = new InterfacesDescription();
			interfDescr.loadFromStream(url.openStream(), true);
			repInterf = interfDescr.getInterface(interf.getName(), interf
					.getVersion());

			if (repInterf != null) {
				op = repInterf.getOperationByName(operation);
				url = bundle.getEntry(op.getTestFile());
			} else
				url = bundle
						.getEntry("outOfRepository/testFiles/genericTest.xml");

			fileStream = url.openStream();
		} catch (Exception e) {

		}

		try {
			schemaLocationUpdatedStream = XmlRootSchemaUtil.updateSchemaLocation(fileStream, ToolboxEclipseProject
							.getServiceSchemaFullURI(serviceName));

		} catch (Exception ecc) {
			schemaLocationUpdatedStream = url.openStream();
		}

		try {
			return addHeaderFooterStreams(schemaLocationUpdatedStream,
					addHeaderFooter);
		} catch (Exception ioe) {
			return null;
		}
	}

	private static InputStream addHeaderFooterStreams(InputStream body,
			boolean addHeader) {
		InputStream header = null;
		InputStream footer = null;
		InputStream headeredStream = null;
		InputStream soapSchemaUpdateStream = null;
		Document doc = null;
		DOMUtil util = null;
		Element root = null;
		Vector<InputStream> streams = null;
		URL url = null;
		String schemaLocationStr = null;

		try {
			if (addHeader == false) {
				return body;
			} else {
				util = new DOMUtil();

				header = BundleUtil.getEntryAsInputStream("templates/SOAP_header.xml",BUNDLE_EDITOR_FILES);
				footer = BundleUtil.getEntryAsInputStream("templates/SOAP_footer.xml",BUNDLE_EDITOR_FILES);

				streams = new Vector<InputStream>();
				streams.add(header);
				streams.add(new XmlDirectivesFilterStream(body));
				streams.add(footer);

				headeredStream = new SequenceInputStream(streams.elements());

				doc = util.inputStreamToDocument(headeredStream);
				root = doc.getDocumentElement();

				url = BundleUtil.getEntryFromBundle("schemas/envelope.xsd",
						BUNDLE_EDITOR_FILES);
				schemaLocationStr = "http://schemas.xmlsoap.org/soap/envelope/ "
						+ url.toExternalForm();

				url = BundleUtil.getEntryFromBundle("schemas/addressing.xsd",
						BUNDLE_EDITOR_FILES);
				schemaLocationStr += " http://schemas.xmlsoap.org/ws/2004/08/addressing "
						+ url.toExternalForm();

				root.setAttribute("xsi:schemaLocation", schemaLocationStr);

				soapSchemaUpdateStream = DOMUtil.getDocumentAsInputStream(doc);
			}
		} catch (Exception ecc) {
			return body;
		}
		return soapSchemaUpdateStream;
	}

	public static void updateSchemaLocation(File fileToUpdate,
			URI schemaLocation) {
		DOMUtil util = null;
		Document xml = null;

		try {
			// getting xml
			xml = util.fileToDocument(fileToUpdate);

			XmlRootSchemaUtil.updateSchemaLocation(xml, schemaLocation);

			DOMUtil.dumpXML(xml, fileToUpdate);

		} catch (Exception e) {

		}
	}

	

	private static URI getServiceSchemaFullURI(String serviceName) {
		Service descr = null;
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root = null;
		IProject project = null;
		IFile descriptorFile = null;
		IFile schemaFile = null;

		wrkSpace = ResourcesPlugin.getWorkspace();
		root = wrkSpace.getRoot();
		project = root.getProject(serviceName);
		descriptorFile = project
				.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);

		descr = new Service();
		descr.loadFromFile(descriptorFile.getLocationURI().getPath());

		schemaFile = project.getFile(descr.getFullSchemaPath());
		return schemaFile.getLocationURI();
	}

	public boolean convert20To21(IProject project) {
		Interface interf;
		Interface repoInterf;
		InterfacesDescription interfDescr;
		String interfName, interfVersion;

		interfDescr = InterfaceDescriptionLoader.load();
		interf = serviceDescr.getImplementedInterface();

		interfName = interf.getName();
		interfVersion = interf.getVersion();
		if (interfName.equals("") && interfVersion.equals("")) {

			for (Operation oper : interf.getOperations()) {
				copyNoInterfaceOperationScriptFromRepository(project, oper,
						Script.SCRIPT_TYPE_FIRST_SCRIPT);
				copyNoInterfaceOperationScriptFromRepository(project, oper,
						Script.SCRIPT_TYPE_GLOBAL_ERROR);

				if (oper.getType()
						.equals(Operation.OPERATION_TYPE_ASYNCHRONOUS)) {
					copyNoInterfaceOperationScriptFromRepository(project, oper,
							Script.SCRIPT_TYPE_RESPONSE_BUILDER);
					copyNoInterfaceOperationScriptFromRepository(project, oper,
							Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER);
					copyNoInterfaceOperationScriptFromRepository(project, oper,
							Script.SCRIPT_TYPE_SECOND_SCRIPT);
					copyNoInterfaceOperationScriptFromRepository(project, oper,
							Script.SCRIPT_TYPE_THIRD_SCRIPT);

				}
			}
			return true;
		} else {
			repoInterf = interfDescr.getInterface(interfName, interfVersion);
			if (repoInterf != null) {
				for (Operation oper : interf.getOperations()) {
					copyOperationScriptFromRepository(project, repoInterf,
							oper, Script.SCRIPT_TYPE_FIRST_SCRIPT);
					copyOperationScriptFromRepository(project, repoInterf,
							oper, Script.SCRIPT_TYPE_GLOBAL_ERROR);

					if (oper.getType().equals(
							Operation.OPERATION_TYPE_ASYNCHRONOUS)) {
						copyOperationScriptFromRepository(project, repoInterf,
								oper, Script.SCRIPT_TYPE_RESPONSE_BUILDER);
						copyOperationScriptFromRepository(project, repoInterf,
								oper,
								Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER);
						copyOperationScriptFromRepository(project, repoInterf,
								oper, Script.SCRIPT_TYPE_SECOND_SCRIPT);
						copyOperationScriptFromRepository(project, repoInterf,
								oper, Script.SCRIPT_TYPE_THIRD_SCRIPT);

					}
				}

				return true;
			} else
				return false;
		}
	}

	private void copyNoInterfaceOperationScriptFromRepository(IProject project,
			Operation oper, String scriptType) {
		IFolder operationFolder;
		IFile repoFile;
		IFile prjFile;
		IPath scriptPath;
		String scriptFile;
		String repoPath;
		String fileName;
		URI from;
		URI to;

		scriptFile = oper.getScript(scriptType).getPath();
		if (scriptFile == null || scriptFile.equals("")) {
			if (scriptType.equals(Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER))
				repoPath = "outOfRepository/templates/asynchronous/response_builder_error.tscript";
			else if (scriptType.equals(Script.SCRIPT_TYPE_FIRST_SCRIPT)
					&& oper.getType().equals(
							Operation.OPERATION_TYPE_ASYNCHRONOUS))
				repoPath = "outOfRepository/templates/asynchronous/first_script.tscript";
			else if (scriptType.equals(Script.SCRIPT_TYPE_FIRST_SCRIPT)
					&& oper.getType().equals(
							Operation.OPERATION_TYPE_SYNCHRONOUS))
				repoPath = "outOfRepository/templates/synchronous/script.tscript";
			else if (scriptType.equals(Script.SCRIPT_TYPE_GLOBAL_ERROR)
					&& oper.getType().equals(
							Operation.OPERATION_TYPE_ASYNCHRONOUS))
				repoPath = "outOfRepository/templates/asynchronous/globalError.tscript";
			else if (scriptType.equals(Script.SCRIPT_TYPE_GLOBAL_ERROR)
					&& oper.getType().equals(
							Operation.OPERATION_TYPE_SYNCHRONOUS))
				repoPath = "outOfRepository/templates/synchronous/globalError.tscript";
			else if (scriptType.equals(Script.SCRIPT_TYPE_RESP_ACK))
				repoPath = "outOfRepository/templates/asynchronous/response_builder.tscript";
			else if (scriptType.equals(Script.SCRIPT_TYPE_RESPONSE_BUILDER))
				repoPath = "outOfRepository/templates/asynchronous/response_builder.tscript";
			else if (scriptType.equals(Script.SCRIPT_TYPE_SECOND_SCRIPT))
				repoPath = "outOfRepository/templates/asynchronous/second_script.tscript";
			else
				repoPath = "outOfRepository/templates/asynchronous/third_script.tscript";

			from = parsePathForURI(repoPath);

			fileName = repoPath.substring(repoPath.lastIndexOf("/") + 1);

			operationFolder = project.getFolder("Operations/" + oper.getName());
			prjFile = operationFolder.getFile(fileName);
			to = prjFile.getLocationURI();

			FileSystemUtil.copyFile(from, to);

			oper.getScript(scriptType).setPath(prjFile.getFullPath()
					.removeFirstSegments(1).toString());
		}

	}

	private void copyOperationScriptFromRepository(IProject project,
			Interface repoInterf, Operation oper, String scriptType) {
		IFolder operationFolder;
		IPath scriptPath;
		String scriptFile;

		scriptFile = oper.getScript(scriptType).getPath();
		if (scriptFile == null || scriptFile.equals("")) {
			operationFolder = project.getFolder("Operations/" + oper.getName());
			scriptPath = copyScriptFileFromRepository(scriptType,
					operationFolder, repoInterf.getOperationByName(oper.getName()));
			oper.getScript(scriptType).setPath(scriptPath.removeFirstSegments(1)
					.toString());
		}
	}

}
