package com.xgame.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import com.xgame.server.common.ServerPackage;

public class CommandCenter
{

	public static void send(AsynchronousSocketChannel channel, ServerPackage pack)
	{
		int dataLength = 0;
		ByteBuffer buffer = ByteBuffer.allocate(1024);
	}

}
