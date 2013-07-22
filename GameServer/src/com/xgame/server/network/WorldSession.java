package com.xgame.server.network;

import java.nio.channels.AsynchronousSocketChannel;

import com.xgame.server.objects.Player;

public class WorldSession
{
	public Player player;
	public AsynchronousSocketChannel channel;
	
	public WorldSession()
	{
		
	}

}
