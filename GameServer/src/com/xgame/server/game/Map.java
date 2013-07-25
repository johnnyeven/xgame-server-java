package com.xgame.server.game;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Map
{
	protected int id;
	protected int unloadTimer;
	private Grid[][] gridContainer;
	private int gridX;
	private int gridY;
	private MapConfig config;
	
	private static final int GRID_WIDTH = 1028;
	private static final int GRID_HEIGHT = 1028;
	private static Log log = LogFactory.getLog(Map.class);
	
	public Map(int id, MapConfig config)
	{
		this.id = id;
		this.config = config;
		loadMap();
	}
	
	private void loadMap()
	{
		
	}
	
	private void setGrid(Grid g, int x, int y)
	{
		if(x >= gridX || y >= gridY)
		{
			log.error("setGrid() ´íÎóµÄGrid×ø±ê x=" + x + ", y=" + y);
			return;
		}
		gridContainer[x][y] = g;
	}
}
