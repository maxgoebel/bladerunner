/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.dbai.bladeRunner.editors.annotator;

import java.awt.*;
import java.awt.geom.*;

import java.io.*;

import java.nio.*;
import java.nio.channels.*;

import javax.swing.*;

import com.sun.pdfview.*;

/**
 * PDFViewer.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date Sep 26, 2011
 */
public class PDFViewer2 extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1386083934199322617L;

	static Image image;

	private String fileName = "";
	private int currentPage;

	public PDFViewer2(String title, String fileName, int pageNum) {
		// super (title);
		setDocument(fileName, pageNum);

	}

	public void setDocument(String fileName, int pageNum) {
		// clear display
		removeAll();

		this.fileName = fileName;
		this.currentPage = pageNum;

		init();

		JLabel label = new JLabel(new ImageIcon(image));
		label.setVerticalAlignment(JLabel.TOP);

		add(label);
		setVisible(true);
	}

	/**
	 * Initialize the viewer.
	 * 
	 */
	private void init() {
		if (currentPage < 1)
			currentPage = 1;

		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(new File(fileName), "r");

			FileChannel fc = raf.getChannel();
			ByteBuffer buf = fc
					.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			PDFFile pdfFile = new PDFFile(buf);

			int numpages = pdfFile.getNumPages();
			System.out.println("Number of pages = " + numpages);
			if (currentPage > numpages)
				currentPage = numpages;

			PDFPage page = pdfFile.getPage(currentPage);

			Rectangle2D r2d = page.getBBox();

			double width = r2d.getWidth();
			double height = r2d.getHeight();
			width /= 72.0;
			height /= 72.0;
			int res = Toolkit.getDefaultToolkit().getScreenResolution();
			width *= res;
			height *= res;

			image = page.getImage((int) width, (int) height, r2d, null, true,
					true);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}// PDFViewer
