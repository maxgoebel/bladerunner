/*******************************************************************************
 * Copyright (c) 2008 nexB Inc. and EasyEclipse.org. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     nexB Inc. and EasyEclipse.org - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.debug.testplugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FacetDataModelMap;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.core.JavaProject;
import org.eclipse.wst.jsdt.launching.JavaRuntime;
import org.eclipse.wst.project.facet.SimpleWebFacetProjectCreationDataModelProvider;

public class ATFProjectHelper {

	public static IProject createATFProject(String projectName)
			throws CoreException {

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);

		if (!project.exists()) {
			project.create(null);
		} else {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}

		if (!project.isOpen()) {
			project.open(null);
		}

		// Must add faceted nature before actually attempting to install any
		// facet.
		addNature(project, FacetedProjectNature.NATURE_ID);

		configureJavaScriptProject(project);

		// Installs the static web project facet.
		IFacetedProject faceted = ProjectFacetsManager.create(project);
		IFacetedProjectTemplate tpl = ProjectFacetsManager
				.getTemplate("template.wst.web");
		Set<IProjectFacet> facets = tpl.getFixedProjectFacets();

		// There should be only one fixed facet. If there's more, this code will
		// need to be reviewed.
		Assert.assertEquals(1, facets.size());
		IProjectFacet facet = facets.iterator().next();

		// Creates the data model for configuring the facet.
		IDataModel config = DataModelFactory
				.createDataModel(new SimpleWebFacetProjectCreationDataModelProvider());

		config = initConfig(config, facet.getDefaultVersion(),
				IFacetedProject.Action.Type.INSTALL, projectName);

		faceted.installProjectFacet(facet.getDefaultVersion(), config,
				new NullProgressMonitor());

		return project;
	}

	private static void configureJavaScriptProject(IProject project)
			throws CoreException {

		addNature(project, JavaScriptCore.NATURE_ID);

		List<IIncludePathEntry> rwList = new ArrayList<IIncludePathEntry>();

		rwList.add(JavaScriptCore.newContainerEntry(new Path(
				JavaRuntime.JRE_CONTAINER)));

		JavaProject javaProject = (JavaProject) JavaScriptCore.create(project);
		javaProject.setRawIncludepath(rwList.toArray(new IIncludePathEntry[0]),
				new NullProgressMonitor());
	}

	private static void addNature(IProject project, String natureId)
			throws CoreException {
		IProjectDescription description = project.getDescription();
		if (description.hasNature(natureId)) {
			return;
		}
		String[] natures = description.getNatureIds();

		// Add the nature
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = natureId;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

	private static IDataModel initConfig(IDataModel model,
			IProjectFacetVersion fv, Type type, String pjname)
			throws CoreException {
		FacetDataModelMap map = (FacetDataModelMap) model
				.getProperty(IFacetProjectCreationDataModelProperties.FACET_DM_MAP);
		IDataModel configDM = (IDataModel) map
				.get(fv.getProjectFacet().getId());
		if (configDM == null) {
			final Object config = fv.createActionConfig(type, pjname);
			if (config == null || !(config instanceof IDataModel)) {
				return null;
			}
			configDM = (IDataModel) config;
			map.add(configDM);
		}

		configDM.setProperty(IFacetDataModelProperties.FACET_VERSION, fv);

		return configDM;
	}

	/**
	 * Imports files form the specified root dir into the specified path
	 * 
	 * @param rootDir
	 * @param destPath
	 * @param monitor
	 * @throws InvocationTargetException
	 * @throws IOException
	 */
	public static void importFilesFromDirectory(File rootDir, IPath destPath)
			throws InvocationTargetException, IOException {
		IImportStructureProvider structureProvider = FileSystemStructureProvider.INSTANCE;
		IProgressMonitor monitor = new NullProgressMonitor();

		List<File> files = new ArrayList<File>(100);
		addFiles(rootDir, files);

		try {
			ImportOperation op = new ImportOperation(destPath, rootDir,
					structureProvider, new ImportOverwriteQuery(), files);
			op.setCreateContainerStructure(false);
			op.run(monitor);
		} catch (InterruptedException e) {
			// should not happen
		}
	}

	/**
	 * Recursively adds files from the specified dir to the provided list
	 * 
	 * @param dir
	 * @param collection
	 * @throws IOException
	 */
	private static void addFiles(File dir, List<File> collection)
			throws IOException {

		File[] files = dir.listFiles();
		List<File> subDirs = new ArrayList<File>(2);
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				collection.add(files[i]);
			} else if (files[i].isDirectory()) {
				subDirs.add(files[i]);
			}
		}

		// I am not sure why JDT does this... it just
		// changes the order in which things are added,
		// at the expense of extra some code.
		Iterator<File> iter = subDirs.iterator();
		while (iter.hasNext()) {
			File subDir = iter.next();
			addFiles(subDir, collection);
		}
	}

	/**
	 * Static class for an <code>IOverwriteQuery</code> implementation
	 */
	private static class ImportOverwriteQuery implements IOverwriteQuery {
		/**
		 * @see org.eclipse.ui.dialogs.IOverwriteQuery#queryOverwrite(java.lang.String)
		 */
		public String queryOverwrite(String file) {
			return ALL;
		}
	}
}
