package com.xgame.server.game;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.xgame.server.common.CoordinatePair;
import com.xgame.server.objects.Player;

public class Map
{
	protected int id;
	protected int unloadTimer;
	private Grid[][] gridContainer;
	private int gridX;
	private int gridY;
	private MapConfig config;
	
	private static final int GRID_WIDTH = 1028;
	private static final int GRID_HEIGHT = 600;
	private static Log log = LogFactory.getLog(Map.class);
	
	public Map(int id, MapConfig config)
	{
		this.id = id;
		this.config = config;
		
		int gridCountX = (int)Math.floor(config.width / GRID_WIDTH);
		int gridCountY = (int)Math.floor(config.height / GRID_HEIGHT);
		
		for(int i = 0; i < gridCountX; i++)
		{
			for(int j = 0; j < gridCountY; j++)
			{
				setGrid(null, i, j);
			}
		}
	}
	
	public static CoordinatePair getCoordinatePair(double x, double y)
	{
		return new CoordinatePair(Math.ceil(x / GRID_WIDTH), Math.ceil(y / GRID_HEIGHT));
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
	
	public boolean add(Player p)
	{
		CoordinatePair coordinate = getCoordinatePair(p.getX(), p.getY());
		Grid g = new Grid();
		g.addWorldObject(p);
		setGrid(g, (int)coordinate.getX(), (int)coordinate.getY());
		return true;
	}
}
