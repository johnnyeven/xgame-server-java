package com.xgame.server.game;

import java.util.HashMap;

public class MapManager
{
	private static MapManager instance = new MapManager();
	private HashMap<Integer, Map> mapContainer = new HashMap<Integer, Map>();

	private MapManager()
	{
		
	}
	
	public static MapManager getInstance()
	{
		if(instance == null)
		{
			instance = new MapManager();
		}
		return instance;
	}
	
	public Map getMap(int id)
	{
		Map m = getBaseMap(id);
		return m;
	}
	
	private Map getBaseMap(int id)
	{
		Map m = findMap(id);
		if(m == null)
		{
			MapConfig config = MapConfigManager.getInstance().getConfig(id);
			if(config != null)
			{
				m = new Map(id, config);
			}
			mapContainer.put(id, m);
		}
		return m;
	}
	
	private Map findMap(int id)
	{
		if(mapContainer.containsKey(id))
		{
			return mapContainer.get(id);
		}
		return null;
	}
}
