package com.xgame.server.game;

import java.util.Date;

public class WorldThread implements Runnable
{
	private final int WORLD_SLEEP_TIME = 100;
	
	@Override
	public void run()
	{
		long currentTime = 0;
		long prevTime = new Date().getTime();
		long timeDiff = 0;
		long prevSleepTime = 0;
		
		while(!World.stop)
		{
			World.loopCounter++;
			currentTime = new Date().getTime();
			timeDiff = currentTime - prevTime;
			
			World.getInstance().update(timeDiff);
			prevTime = currentTime;
			
			if(timeDiff <= WORLD_SLEEP_TIME + prevSleepTime)
			{
				prevSleepTime = WORLD_SLEEP_TIME + prevSleepTime - timeDiff;
				try
				{
					Thread.sleep(prevSleepTime);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				prevSleepTime = 0;
			}
		}
	}

}
