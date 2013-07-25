package com.xgame.server.game;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Map
{
	protected int id;
	protected int unloadTimer;
	private Grid[][] gridContainer;
	private int gridX;
	private int gridY;
	
	private static final int GRID_WIDTH = 1028;
	private static final int GRID_HEIGHT = 1028;
	private static Log log = LogFactory.getLog(Map.class);
	
	public Map(int id)
	{
		this.id = id;
		
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
