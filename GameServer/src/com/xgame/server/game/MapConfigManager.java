package com.xgame.server.game;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MapConfigManager
{
	private static MapConfigManager instance = new MapConfigManager();
	private HashMap<Integer, MapConfig> configContainer = new HashMap<Integer, MapConfig>();
	
	private MapConfigManager()
	{
		
	}

	public static MapConfigManager getInstance()
	{
		if(instance == null)
		{
			instance = new MapConfigManager();
		}
		return instance;
	}
	
	public MapConfig getConfig(int id)
	{
		if(configContainer.containsKey(id))
		{
			return configContainer.get(id);
		}
		else
		{
			try
			{
				MapConfig c = loadMapConfig(id);
				configContainer.put(id, c);
				return c;
			}
			catch (ParserConfigurationException | SAXException | IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private MapConfig loadMapConfig(int id) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
		Document doc = dbBuilder.parse("data/map/" + id + ".xml");
		
		MapConfig c = new MapConfig();
		c.id = Integer.parseInt(doc.getElementsByTagName("id").item(0).getTextContent());
		c.width = Integer.parseInt(doc.getElementsByTagName("width").item(0).getTextContent());
		c.height = Integer.parseInt(doc.getElementsByTagName("height").item(0).getTextContent());
		return c;
	}
}
