package com.xgame.server.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

import com.xgame.server.common.AuthSessionPackage;
import com.xgame.server.map.ProtocolPackage;
import com.xgame.server.pool.BufferPool;

public class ReadCompletionHandler implements
		CompletionHandler<Integer, AuthSessionPackage>
{
	private AIOSocketMgr server;
	
    public ReadCompletionHandler(AIOSocketMgr server)
	{
		this.server = server;
	}
	
	@Override
	public void completed(Integer result,
			AuthSessionPackage attachment)
	{
		if(result > 0)
		{
			//¥¶¿Ìbuffer
			ByteBuffer buffer = attachment.buffer;
			buffer.flip();
			buffer.getInt();
			short protocolId = buffer.getShort();
			
			ProtocolPackage parameter = new ProtocolPackage();
			parameter.protocolId = protocolId;
			parameter.client = attachment.channel;
			parameter.receiveDataLength = result;
			parameter.receiveData = buffer.duplicate();
			parameter.offset = 6;
			
			BufferPool.getInstance().releaseBuffer(buffer);
        	buffer = BufferPool.getInstance().getBuffer();
			attachment.channel.read(buffer, new AuthSessionPackage(buffer, attachment.channel), this);
		}
		else
		{
			try
			{
				attachment.channel.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				BufferPool.getInstance().releaseBuffer(attachment.buffer);
			}
		}
	}

	@Override
	public void failed(Throwable exc, AuthSessionPackage attachment)
	{
		BufferPool.getInstance().releaseBuffer(attachment.buffer);
	}

}
