package com.xgame.server.objects;

import com.xgame.server.objects.hashmap.PlayerMap;

public class ObjectManager
{

	public ObjectManager()
	{
		
	}
	
	public void addObject( WorldObject target )
	{
		if( target instanceof Player )
		{
			PlayerMap.getInstance().add( ( Player )target );;
		}
	}
}
