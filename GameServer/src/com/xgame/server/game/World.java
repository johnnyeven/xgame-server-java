package com.xgame.server.game;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.xgame.server.network.WorldSession;

public class World
{
	public static Map<Integer, WorldSession> sessionMap = new HashMap<Integer, WorldSession>();
	public static boolean stop = false;
	public static long loopCounter = 0;
	
	private static World instance = null;
	private static boolean allowInstance = false;
	
	private int playerLimit;
	private long serverStartTime;
	
	public World() throws Exception
	{
		if(!allowInstance)
		{
			throw new Exception();
		}
		playerLimit = 100;
		serverStartTime = new Date().getTime();
	}
	
	public static World getInstance()
	{
		if(instance == null)
		{
			allowInstance = true;
			try
			{
				instance = new World();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			allowInstance = false;
		}
		return instance;
	}
	
	public void addSession(WorldSession session)
	{
		
	}
	
	public void removeSession(int id)
	{
		
	}
	
	public WorldSession getSession(int id)
	{
		return null;
	}
	
	public void setInitialWorldSettings()
	{
		
	}
	
	public boolean update(long timeDiff)
	{
		System.out.println(timeDiff);
		return true;
	}
}
