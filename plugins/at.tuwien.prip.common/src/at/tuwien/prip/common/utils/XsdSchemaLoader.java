package at.tuwien.prip.common.utils;
/*
 * XsdSchemaLoader.java
 * Copyright (c) 2007 by Dr. Herong Yang. All rights reserved.
 */
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.XMLConstants;
import java.io.File;

class XsdSchemaLoader 
{
	
	public static void main(String[] a) 
	{
		String name = null;
		if (a.length==0)
		{
			name = "/home/max/Downloads/competition1.xsd";
		}
		else 
		{
			name = a[0];
		}
		
		//load the schema
		loadSchema(name);
	}
	
	/**
	 * Load a XSD schema from file.
	 * @param name
	 * @return
	 */
	public static Schema loadSchema(String name)
	{
		Schema schema = null;
		try
		{
			// getting the default implementation of XML Schema factory
			String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			SchemaFactory factory = SchemaFactory.newInstance(language);
			System.out.println();
			System.out.println("Schema Language: "+language);
			System.out.println("Factory Class: "
					+ factory.getClass().getName());

			// parsing the schema file      
			schema = factory.newSchema(new File(name));
			System.out.println();
			System.out.println("Schema File: "+name);
			System.out.println("Schema Class: "
					+ schema.getClass().getName());

		} catch (Exception e) {
			// catching all exceptions
			System.out.println();
			System.out.println(e.toString());
		}
		return schema;
	}
}