/**
 * 
 */
package it.intecs.pisa.develenv.model.project;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.SchemaSetRelocator;
import it.intecs.pisa.util.ServiceFoldersFilter;
import it.intecs.pisa.util.Zip;

import java.io.File;
import java.util.Vector;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * This class contains some static method that are used to perform some common,
 * stateless activities over a Toolbox project placed under Eclipse
 * 
 * @author Massimiliano Fanciulli
 * 
 */
public class ToolboxEclipseProjectUtil {

	private static final String TOOLBOX_EXPORT_TEMP_DIR = "ToolboxExportTempDir";

	public static enum PackageType {
		EMPTY, FULL, FULL_DEPLOY, DEPLOY_MODIFIED
	};

	/**
	 * This method creates the export package of the provided project at the
	 * packageFullPath path. The content of the export package depends on the
	 * type value. IMPORTANT: Currently only FULL type is supported
	 * 
	 * @param project
	 *            Project to be exported to a zip file
	 * @param type
	 *            Type of the export content
	 * @param packageFullPath
	 *            Path at which the export package shall be stores
	 * @return true or false, depending on the export procedure success
	 */
	public static boolean createExportpackage(IProject project,
			PackageType type, String packageFullPath) {
		// copying service directory and performing some settings (schema paths
		// etc..)
		File exportTempDir;
		File serviceTempDir;
		IFileStore source;
		IFileStore destination;
		IFileStore schemaDirStore;
		IFileStore sourceSchemaDirStore;
		boolean schemaRelocationResult = false;
		try {
			/*
			 * The following code copies the project into a temporary directory
			 * We are assuming that the destination directory is on the local
			 * disk, so we can use the Java IO API
			 */
			exportTempDir = new File(System.getProperty("user.home"),
					TOOLBOX_EXPORT_TEMP_DIR);
			if (exportTempDir.exists() == false)
				exportTempDir.mkdir();

			serviceTempDir = new File(exportTempDir, project.getName());

			if (serviceTempDir.exists())
				IOUtil.rmdir(serviceTempDir);

			serviceTempDir.mkdir();

			source = EFS.getStore(project.getLocationURI());
			destination = EFS.getStore(serviceTempDir.toURI());

			source.copy(destination, EFS.OVERWRITE, null);

			schemaDirStore = destination.getChild("Schemas");
			sourceSchemaDirStore = source.getChild("Schemas");

			schemaRelocationResult = SchemaSetRelocator
					.updateSchemaLocationToRelative(schemaDirStore.toLocalFile(
							0, null), sourceSchemaDirStore.toURI());
			if (schemaRelocationResult) {
				switch (type) {
				case FULL:
					return createFullPackage(serviceTempDir, packageFullPath);

				case FULL_DEPLOY:
					return createFullDeployPackage(serviceTempDir,
							packageFullPath);

				default:
					return true;
				}
			} else
				return false;
		} catch (Exception ex) {
			// report erro
			return false;
		}
	}

	private static boolean createFullDeployPackage(File serviceTempDir,
			String packageFullPath) {
		try {
			String[] allowedPaths = {
					ToolboxEclipseProject.FOLDER_ADDITIONAL_RESOURCES,
					ToolboxEclipseProject.FOLDER_COMMON_SCRIPTS,
					ToolboxEclipseProject.FOLDER_INFO,
					ToolboxEclipseProject.FOLDER_JARS,
					ToolboxEclipseProject.FOLDER_OPERATIONS,
					ToolboxEclipseProject.FOLDER_RESOURCES,
					ToolboxEclipseProject.FOLDER_SCHEMAS,
					ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR };

			Zip.zipDirectory(packageFullPath, serviceTempDir.getAbsolutePath(),
					false, new ServiceFoldersFilter(allowedPaths));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * This method creates an export package with all project content inside
	 * 
	 * @param serviceTempDir
	 *            Project to be exported to a zip file
	 * @param packageFullPath
	 *            Path at which the export package shall be stores
	 * @return true or false, depending on the export procedure success
	 */
	private static boolean createFullPackage(File serviceTempDir,
			String packageFullPath) {
		try {
			String[] allowedPaths = {
					ToolboxEclipseProject.FOLDER_ADDITIONAL_RESOURCES,
					ToolboxEclipseProject.FOLDER_COMMON_SCRIPTS,
					ToolboxEclipseProject.FOLDER_INFO,
					ToolboxEclipseProject.FOLDER_JARS,
					ToolboxEclipseProject.FOLDER_OPERATIONS,
					ToolboxEclipseProject.FOLDER_RESOURCES,
					ToolboxEclipseProject.FOLDER_SCHEMAS,
					ToolboxEclipseProject.FOLDER_TEST_DIR,
					ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR };

			Zip.zipDirectory(packageFullPath, serviceTempDir.getAbsolutePath(),
					false, new ServiceFoldersFilter(allowedPaths));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isToolboxProject(IProject project) {
		Service descriptor = null;
		IFile descriptorFile = null;

		descriptor = new Service();

		descriptorFile = project
				.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);

		return descriptor.loadFromFile(descriptorFile.getLocationURI()
				.getPath());
	}

	public static boolean isToolboxProject(String projectName) {
		IProject project = null;
		IWorkspaceRoot root = null;
		IWorkspace workspace = null;

		try {
			workspace = ResourcesPlugin.getWorkspace();
			root = workspace.getRoot();
			project = root.getProject(projectName);
		} catch (Exception e) {
			return false;
		}

		return isToolboxProject(project);
	}

	static public IProject getProject(String name) {
		// Getting workspace root
		IWorkspace wrkSpace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = wrkSpace.getRoot();
		IProject project = root.getProject(name);
		return project;
	}

	public static String[] getProjectNames() {
		IProject[] projects;
		String[] projectsNames;
		Vector<IProject> projectsVector;
		int tbxProjectsCount;
		int i = 0;
		IWorkspace wrkSpace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = wrkSpace.getRoot();

		projects = root.getProjects();
		projectsVector = new Vector<IProject>();

		for (IProject prj : projects) {
			if (isToolboxProject(prj))
				projectsVector.add(prj);
		}

		tbxProjectsCount = projectsVector.size();
		projectsNames = new String[tbxProjectsCount];

		i = 0;
		for (IProject prj : projectsVector) {
			projectsNames[i] = prj.getName();
			i++;
		}

		return projectsNames;

	}

	public static String[] getOperationNames(String serviceName) {
		String[] operations;
		Service descriptor;
		File descriptorFile;
		IProject project;
		IFile descriptorIFile;
		Interface implInterface;
		Operation[] implOperations;
		int i = 0;
		try {
			project = getProject(serviceName);
			descriptorIFile = project
					.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
			descriptorFile = descriptorIFile.getLocation().toFile();

			descriptor = new Service();
			descriptor.loadFromFile(descriptorFile);

			implInterface = descriptor.getImplementedInterface();
			implOperations = implInterface.getOperations();

			operations = new String[implOperations.length];
			for (Operation oper : implOperations) {
				operations[i] = implOperations[i].getName();
				i++;
			}

			return operations;
		} catch (Exception ecc) {
			return new String[0];
		}
	}

	public static Operation getOperation(String serviceName,
			String operationName) {
		Service descriptor;
		File descriptorFile;
		IProject project;
		IFile descriptorIFile;
		Interface implInterface;

		try {
			project = getProject(serviceName);
			descriptorIFile = project
					.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
			descriptorFile = descriptorIFile.getLocation().toFile();

			descriptor = new Service();
			descriptor.loadFromFile(descriptorFile);

			implInterface = descriptor.getImplementedInterface();

			return implInterface.getOperationByName(operationName);
		} catch (Exception ecc) {
			return null;
		}
	}

}
