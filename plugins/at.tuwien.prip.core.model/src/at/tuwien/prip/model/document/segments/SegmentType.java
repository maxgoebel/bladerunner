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
package at.tuwien.prip.model.document.segments;


/**
 * SegmentType.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Jul 4, 2011
 */
public enum SegmentType 
{
	Attribute(1), 
	Image(2),
	Comment(3), 
	Input(4), 
	Link(5),
	Element(6), 
	Form(7), 
	Menu(8),
	Table(9), 
	TablePart(10), 
	TableCell(11), 
	TableRow(12),
	TableColumn(13), 
	TableAccessor(14),
	List(15), 
	ListAccessor(16),
	Section(17), 
	SectionHeader(18),
	Semantic(19), 
	Visual(20),
	Footer(21),
	Header(22),
	KeyValue(23), 
	DomainValue(24),
	DomainKey(25),
	Block(26), 
	Word(27), 
	Textline(28), 
	Paragraph(29),
	Rectangle(30), 
	Region(31),
	Heading(32), 
	Composite(33),
	Leaf(34), 
	Root(35);

	private int code;
	private SegmentType(int code) 
	{
		this.code = code;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public static SegmentType valueOf(int i){
		for (SegmentType s : values()){
			if (s.code == i){
				return s;
			}
		}
		throw new IllegalArgumentException("No matching constant for " + i);
	}
}
