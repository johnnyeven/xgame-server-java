package com.xgame.server.game;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xgame.server.objects.WorldObject;

public class Grid
{
	private int x;
	private int y;
	private HashMap<Long, WorldObject> objectMap;
	
	private static Log log = LogFactory.getLog(Grid.class);
	
	public Grid(int x, int y)
	{
		this.x = x;
		this.y = y;
		objectMap = new HashMap<Long, WorldObject>();
	}
	
	public Grid()
	{
		x = 0;
		y = 0;
		objectMap = new HashMap<Long, WorldObject>();
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}
	
	public void addWorldObject(WorldObject o)
	{
		if(objectMap.containsKey(o.getGuid()))
		{
			log.warn("addWorldObject() Grid[x=" + x + ", y=" + y + "]Key重复，未添加成功"); 
			return;
		}
		objectMap.put(o.getGuid(), o);
	}

}
