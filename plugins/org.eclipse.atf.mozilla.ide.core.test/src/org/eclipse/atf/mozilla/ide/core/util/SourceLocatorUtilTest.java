package org.eclipse.atf.mozilla.ide.core.util;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

public class SourceLocatorUtilTest extends TestCase {

	public void testGetSourceElementURL() throws CoreException, MalformedURLException, URISyntaxException {
		SourceLocatorUtil locator = SourceLocatorUtil.getInstance();

		IFile file = createFile("xyz/abc");

		URL url = new URL(file.getLocationURI().toString());

		IStorage storage = locator.getSourceElement(url, null, null);
		assertEquals(file, storage);

		// hint should not break the locator result
		storage = locator.getSourceElement(url, null, file.getProject());
		assertEquals(file, storage);
	}

	private IFile createFile(String name) throws CoreException {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(name));
		IProject project = file.getProject();
		if (!project.exists()) {
			project.create(null);
			project.open(null);
		}
		file.create(new ByteArrayInputStream(new byte[0]), true, null);
		assertTrue(file.exists());
		return file;
	}
}
