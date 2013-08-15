package com.xgame.server.objects;

import com.xgame.server.objects.hashmap.PlayerMap;

public class ObjectManager
{
	private static ObjectManager	instance;

	private ObjectManager()
	{

	}

	public static ObjectManager getInstance()
	{
		if ( instance == null )
		{
			instance = new ObjectManager();
		}
		return instance;
	}

	public void addObject( WorldObject target )
	{
		if ( target instanceof Player )
		{
			PlayerMap.getInstance().add( (Player) target );
			;
		}
	}
}
