package com.xgame.server.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import com.xgame.server.game.map.Grid;

public class InteractiveObject extends WorldObject
{
	private ArrayList< WorldObject >	lastInteractiveList;
	private ArrayList< WorldObject >	interactiveList;
	private ArrayList< WorldObject >	dropedInteractive;
	private ArrayList< WorldObject >	newInteractive;

	public InteractiveObject()
	{
		super();
		lastInteractiveList = new ArrayList< WorldObject >();
		interactiveList = new ArrayList< WorldObject >();
		dropedInteractive = new ArrayList< WorldObject >();
		newInteractive = new ArrayList< WorldObject >();
	}

	public void update( long timeDiff )
	{
		interactiveList.clear();
		Grid g = getCurrentGrid();

		ArrayList< Grid > glist = getMap().getViewGrid( g );
		Iterator< Grid > itg = glist.iterator();
		Grid currentGrid;
		Iterator< Entry< UUID, WorldObject >> iten;
		Entry< UUID, WorldObject > en;
		WorldObject obj;
		while ( itg.hasNext() )
		{
			currentGrid = itg.next();
			if ( currentGrid == null )
			{
				continue;
			}
			iten = currentGrid.getWorldObjectIterator();
			while ( iten.hasNext() )
			{
				en = iten.next();
				obj = en.getValue();
				if ( obj != this )
				{
					interactiveList.add( en.getValue() );
				}
			}
		}
		compareInteracting();
	}

	private void compareInteracting()
	{
		diff( lastInteractiveList, interactiveList, dropedInteractive );
		diff( interactiveList, lastInteractiveList, newInteractive );
	}

	private static ArrayList< WorldObject > diff( ArrayList< WorldObject > ls,
			ArrayList< WorldObject > ls2 )
	{
		ArrayList< WorldObject > list = new ArrayList< WorldObject >();
		Collections.copy( list, ls );
		list.removeAll( ls2 );
		return list;
	}

	private static ArrayList< WorldObject > diff( ArrayList< WorldObject > ls,
			ArrayList< WorldObject > ls2, ArrayList< WorldObject > target )
	{
		if ( target != null )
		{
			target.clear();
		}
		else
		{
			target = new ArrayList< WorldObject >();
		}
		Collections.copy( target, ls );
		target.removeAll( ls2 );
		return target;
	}

	public ArrayList< WorldObject > getDropedInteractive()
	{
		return dropedInteractive;
	}

	public ArrayList< WorldObject > getNewInteractive()
	{
		return newInteractive;
	}

}
