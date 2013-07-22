package com.xgame.server.game;

import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.network.AIOSocketMgr;

public class GameServer
{
    public final static int PORT = 9050;

	public GameServer()
	{
		
		DatabaseRouter.getInstance();
	}
	
	public void run()
	{
	    System.out.println( "MM   MM         MM   MM  MMMMM   MMMM   MMMMM");
	    System.out.println( "MM   MM         MM   MM MMM MMM MM  MM MMM MMM");
	    System.out.println( "MMM MMM         MMM  MM MMM MMM MM  MM MMM");
	    System.out.println( "MM M MM         MMMM MM MMM     MM  MM  MMM");
	    System.out.println( "MM M MM  MMMMM  MM MMMM MMM     MM  MM   MMM");
	    System.out.println( "MM M MM M   MMM MM  MMM MMMMMMM MM  MM    MMM");
	    System.out.println( "MM   MM     MMM MM   MM MM  MMM MM  MM     MMM");
	    System.out.println( "MM   MM MMMMMMM MM   MM MMM MMM MM  MM MMM MMM");
	    System.out.println( "MM   MM MM  MMM MM   MM  MMMMMM  MMMM   MMMMM");
	    System.out.println( "        MM  MMM");
	    System.out.println( "        MMMMMM\n\n");
	    
	    World.getInstance().setInitialWorldSettings();
	    
	    Thread wt = new Thread(new WorldThread());
	    wt.setPriority(10);
	    wt.start();
	    
		AIOSocketMgr.getInstance().startCompletionPort();
	}

	public static void main(String[] args)
	{
		new GameServer().run();
	}

}
