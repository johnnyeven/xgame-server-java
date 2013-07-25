package com.xgame.server.objects;

public class WorldObject
{
	private long guid;
	private int mapId;
	private int x;
	private int y;
	
	public WorldObject()
	{
		
	}
	public long getGuid()
	{
		return guid;
	}

	public void setGuid(long guid)
	{
		this.guid = guid;
	}
	public int getMapId()
	{
		return mapId;
	}
	public void setMapId(int mapId)
	{
		this.mapId = mapId;
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

}
