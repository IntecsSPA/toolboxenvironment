package it.intecs.pisa.develenv.ui.wizards;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import it.intecs.pisa.common.tbx.servicePackage;
import it.intecs.pisa.common.tbx.exceptions.serviceAlreadyExists;
import it.intecs.pisa.common.tbx.exceptions.zipIsNotATBXService;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.SchemaSetRelocator;
import it.intecs.pisa.util.XSLT;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class TBX_ProjectImportOldDescriptorWizard extends Wizard implements IImportWizard {

	private TBX_ProjectImportOldDescriptorWizardPage page;

	public TBX_ProjectImportOldDescriptorWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		String descriptorPath=null;
		String projectName=null;
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root = null;
		IProject project =null;
		servicePackage pack=null;
		File descriptorFile;
		Document doc;
		DOMUtil util;
		Element rootEl,serviceEl;
				
		try {
			descriptorPath=page.getDescriptorPath();
			wrkSpace=ResourcesPlugin.getWorkspace();
			root=wrkSpace.getRoot();
			
			//creating project....
			util=new DOMUtil();
			
			descriptorFile=new File(descriptorPath);
			doc=util.fileToDocument(descriptorFile);
			rootEl=doc.getDocumentElement();
			serviceEl=DOMUtil.getFirstChild(rootEl);
			
			project=root.getProject(serviceEl.getAttribute("serviceName"));
			if(project.exists())
				throw new serviceAlreadyExists();
			
			project.create(null);
					
			createServiceStructureFromDescriptor(serviceEl, project);
			createServiceDescriptor(doc,project);
			
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			
		} catch (Exception e) {
			final Shell s = new Shell();
			MessageBox alert=new MessageBox(s,SWT.ICON_ERROR|SWT.OK);
			
			alert.setText("Error importing the TOOLBOX service");
			
			if(e instanceof serviceAlreadyExists)
				alert.setMessage("A service with the same name already exists!");
			else if(e instanceof zipIsNotATBXService)
				alert.setMessage("The Zip file doesn't contains a TOOLBOX service!");
			
			alert.open();
		}
		
		
		return true;
	}

	private void createServiceDescriptor(Document doc, IProject project) throws Exception {
		StreamSource xslSource;
		DOMSource docSource;
		StreamResult output;
		FileOutputStream outputStream;
		IFile serviceDescriptor;
		Bundle editorFilesPlugin=null;
		URL entry=null;
		
		serviceDescriptor=project.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
		
		editorFilesPlugin=Platform.getBundle("com.intecs.ToolboxScript.editorFiles");
		entry=editorFilesPlugin.getEntry("xsl/oldDescriptorImport.xsl");
		
		xslSource=new StreamSource(entry.openStream());
		docSource=new DOMSource(doc);
		
		outputStream=new FileOutputStream(serviceDescriptor.getLocation().toFile());
		output=new StreamResult(outputStream);
		
		XSLT.transform(xslSource,docSource,output);
	}

	private void createServiceStructureFromDescriptor(Element serviceEl, IProject project) {
		Element rootEl,schemaDocumentEl;
		IFolder infoFolder;
		IFolder schemasFolder;
		IFolder operationFolder;
		IFolder resourcesFolder;
		IFolder testFilesFolder;
		
		try
		{
			if (!project.isOpen()) {
				project.open(null);
			}
				
			infoFolder=project.getFolder(ToolboxEclipseProject.FOLDER_INFO);
			infoFolder.create(IResource.NONE, true, null);		
			createAbstractAndDescriptionFiles(infoFolder,serviceEl);
			
			rootEl=(Element) serviceEl.getParentNode();
			schemaDocumentEl=DOMUtil.getChildByTagName(rootEl, "schemaDocuments");
			
			schemasFolder=project.getFolder(ToolboxEclipseProject.FOLDER_SCHEMAS);
			schemasFolder.create(IResource.NONE, true, null);
			createSchemasFolder(schemasFolder,schemaDocumentEl);
			
			operationFolder=project.getFolder(ToolboxEclipseProject.FOLDER_OPERATIONS);
			operationFolder.create(IResource.NONE, true, null);
			createOperationFolder(operationFolder,serviceEl);
			
			resourcesFolder=project.getFolder(ToolboxEclipseProject.FOLDER_RESOURCES);
			resourcesFolder.create(IResource.NONE, true, null);
			createResourcesFolder(resourcesFolder,serviceEl);
			
			testFilesFolder=project.getFolder(ToolboxEclipseProject.FOLDER_TEST_DIR);
			testFilesFolder.create(IResource.NONE, true, null);
			createTestFolder(testFilesFolder,serviceEl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	private void createResourcesFolder(IFolder resourcesFolder, Element serviceEl) {
		IFolder commonScripts,jars;
		
		try
		{
			commonScripts=resourcesFolder.getFolder(ToolboxEclipseProject.FOLDER_COMMON_SCRIPTS);
			commonScripts.create(IResource.NONE, true, null);
			
			jars=resourcesFolder.getFolder(ToolboxEclipseProject.FOLDER_JARS);
			jars.create(IResource.NONE, true, null);
		}
		catch(Exception e)
		{
			
		}
		
		
	}

	private void createTestFolder(IFolder testFilesFolder, Element serviceEl) {
		// TODO Auto-generated method stub
		
	}

	private void createOperationFolder(IFolder operationFolder, Element serviceEl) {
		Element operationsEl;
		LinkedList operationList;
		LinkedList scriptList;
		Iterator iter,scriptIter;
		Element operationEl,scriptEl;
		String operationName;
		IFolder ithOperationFolder;
		IFile scriptFile;
		Document tempDoc;
		int i=0;

		try
		{
			
			operationsEl=DOMUtil.getChildByTagName(serviceEl, "operations");
			operationList=DOMUtil.getChildrenByTagName(operationsEl,"operation");
			
			iter=operationList.iterator();
			while(iter.hasNext())
			{
				operationEl=(Element) iter.next();
				operationName=operationEl.getAttribute("name");
				
				ithOperationFolder=operationFolder.getFolder(operationName);
				ithOperationFolder.create(IResource.NONE, true, null);
				
				scriptList=DOMUtil.getChildrenByTagName(operationEl,"script");
				scriptIter=scriptList.iterator();
				
				i=1;
				while(scriptIter.hasNext())
				{
					scriptEl=(Element) scriptIter.next();
					scriptFile=ithOperationFolder.getFile("script_"+i+".tscript");
					
					tempDoc = createDocumentFromElement(DOMUtil.getFirstChild(scriptEl));
					
					DOMUtil.dumpXML(tempDoc, scriptFile.getLocation().toFile(),true);
					i++;
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	private Document createDocumentFromElement(Element scriptEl) {
		Document tempDoc;
		Node importedNode;
		DOMUtil util;
		
		util=new DOMUtil();
		
		tempDoc=util.newDocument();
		importedNode=tempDoc.importNode(scriptEl, true);
		tempDoc.appendChild(importedNode);
		return tempDoc;
	}

	private void createSchemasFolder(IFolder schemasFolder, Element schemaDocumentEl) {
		LinkedList schemaList;
		Iterator iter;
		Element schemaEl;
		String schemaName;
		Document tempDoc;
		IFile schemaFile;
		
		try
		{
			schemaList=DOMUtil.getChildrenByTagName(schemaDocumentEl,"schema");
			iter=schemaList.iterator();
			
			while(iter.hasNext())
			{
				schemaEl=(Element) iter.next();
				schemaName=schemaEl.getAttribute("name");
				
				tempDoc = createDocumentFromElement(DOMUtil.getFirstChild(schemaEl));
				
				schemaFile=schemasFolder.getFile(schemaName);
				
				DOMUtil.dumpXML(tempDoc,schemaFile.getLocation().toFile(),true);
			}
			
			SchemaSetRelocator.updateSchemaLocationToOnlySchemaName(schemasFolder.getLocation().toFile());
			SchemaSetRelocator.updateSchemaLocationToAbsolute(schemasFolder.getLocation().toFile(), schemasFolder.getLocationURI());
		}
		catch(Exception e)
		{
			
		}
		
	}

	private void createAbstractAndDescriptionFiles(IFolder infoFolder, Element serviceEl) {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		page=new TBX_ProjectImportOldDescriptorWizardPage("Select service to import");
		
	}
	
	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		this.addPage(page);
	}

}
