package com.xgame.server.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

import com.xgame.server.game.ProtocolPackage;

public class ReadCompletionHandler implements
		CompletionHandler<Integer, WorldSession>
{
	private AIOSocketMgr server;
	
    public ReadCompletionHandler(AIOSocketMgr server)
	{
		this.server = server;
	}
	
	@Override
	public void completed(Integer result,
			WorldSession attachment)
	{
		if(result > 0)
		{
			//¥¶¿Ìbuffer
			ByteBuffer buffer = attachment.getReadBuffer();
			buffer.flip();
			int packageLength = buffer.getInt();
			short protocolId = buffer.getShort();
			
			ProtocolPackage parameter = new ProtocolPackage();
			parameter.protocolId = protocolId;
			parameter.client = attachment.getChannel();
			parameter.receiveDataLength = result;
			parameter.receiveData = buffer.duplicate();
			parameter.offset = 6;
			
			attachment.addParameterQueue(parameter);
			
			buffer.clear();
			attachment.startRecv();
//			finnalResult.read(buffer, null, this);
		}
		else
		{
			try
			{
				attachment.getChannel().close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, WorldSession attachment)
	{
		
	}

}
