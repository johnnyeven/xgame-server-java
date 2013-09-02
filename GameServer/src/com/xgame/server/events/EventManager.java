package com.xgame.server.events;

import java.util.ArrayList;
import java.util.HashMap;
import com.xgame.server.objects.WorldObject;

public class EventManager
{
	private HashMap< WorldObject, HashMap< String, ArrayList< IEventCallback > >>	eventContainer;
	private static EventManager														instance;

	private EventManager()
	{
		eventContainer = new HashMap< WorldObject, HashMap< String, ArrayList< IEventCallback > >>();
	}

	public static EventManager getInstance()
	{
		if ( instance == null )
		{
			instance = new EventManager();
		}
		return instance;
	}

	public void addEventListener( WorldObject target, String type,
			IEventCallback callback )
	{
		if ( eventContainer.containsKey( target ) )
		{
			HashMap< String, ArrayList< IEventCallback > > map = eventContainer
					.get( target );
			if ( map.containsKey( type ) )
			{
				map.get( type ).add( callback );
			}
			else
			{
				ArrayList< IEventCallback > list = new ArrayList< IEventCallback >();
				list.add( callback );
				map.put( type, list );
			}
		}
		else
		{
			HashMap< String, ArrayList< IEventCallback > > map = new HashMap< String, ArrayList< IEventCallback > >();
			ArrayList< IEventCallback > list = new ArrayList< IEventCallback >();
			list.add( callback );
			map.put( type, list );
			eventContainer.put( target, map );
		}
	}

	public void removeEventListener( WorldObject target, String type,
			IEventCallback callback )
	{
		if ( eventContainer.containsKey( target ) )
		{
			HashMap< String, ArrayList< IEventCallback > > map = eventContainer
					.get( target );
			if ( map.containsKey( type ) )
			{
				map.get( type ).remove( callback );
			}
		}
	}

	public void removeEvent( WorldObject target, String type )
	{
		if ( eventContainer.containsKey( target ) )
		{
			HashMap< String, ArrayList< IEventCallback > > map = eventContainer
					.get( target );
			if ( map.containsKey( type ) )
			{
				map.remove( type );
			}
		}
	}

	public void dispatchEvent( WorldObject target, Event evt )
	{
		if ( eventContainer.containsKey( target ) )
		{
			HashMap< String, ArrayList< IEventCallback > > map = eventContainer
					.get( target );
			if ( map.containsKey( evt.getName() ) )
			{
				ArrayList< IEventCallback > list = map.get( evt.getName() );
				evt.setSender( target );
				for ( int i = 0; i < list.size(); i++ )
				{
					list.get( i ).execute( evt );
				}
			}
		}
	}
}
