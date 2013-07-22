package com.xgame.server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.xgame.server.common.protocol.ProtocolRegisterAccountRole;
import com.xgame.server.common.protocol.ProtocolRequestAccountRole;
import com.xgame.server.common.protocol.ProtocolRequestHotkey;
import com.xgame.server.common.protocol.ProtocolRouter;
import com.xgame.server.game.ProtocolParam;

public class AIOSocketMgr
{
	private ProtocolRouter router;
    private AsynchronousServerSocketChannel server;
    public final static String HOST = "127.0.0.1";
    public final static int PORT = 9050;
    
    private static AIOSocketMgr instance = null;
    private static boolean allowInstance = false;
    
	public AIOSocketMgr() throws Exception
	{
		if(!allowInstance)
		{
			throw new Exception();
		}
		try
		{
			server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(HOST,PORT), 100);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		router = new ProtocolRouter();
        router.Bind((short)0x0040, ProtocolRequestAccountRole.class);
        router.Bind((short)0x0050, ProtocolRegisterAccountRole.class);
        router.Bind((short)0x0060, ProtocolRequestHotkey.class);
	}
	
	public static AIOSocketMgr getInstance()
	{
		if(instance == null)
		{
			allowInstance = true;
			try
			{
				instance = new AIOSocketMgr();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			allowInstance = false;
		}
		return instance;
	}
	
	public void startCompletionPort()
    {
    	System.out.println("服务器已启动");
    	
    	server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
    		
    		ByteBuffer buffer = ByteBuffer.allocate(65535);
    		public void completed(AsynchronousSocketChannel result,Object attachment)
    		{
                final AsynchronousSocketChannel finnalResult=result;
    			
    			try
    			{
    				buffer.clear();
    				result.read(buffer, null, new CompletionHandler<Integer, Object>() {
    					public void completed(Integer result1, Object result2)
    					{
    						if(result1 > 0)
    						{
	    						//处理buffer
	    						buffer.flip();
	    						int packageLength = buffer.getInt();
	    						short protocolId = buffer.getShort();
	    						
	    						ProtocolParam parameter = new ProtocolParam();
	    						parameter.client = finnalResult;
	    						parameter.receiveDataLength = result1;
	    						parameter.receiveData = buffer;
	    						parameter.offset = 6;
	    						router.Trigger(protocolId, parameter);
	    						
	    						buffer.clear();
	    						finnalResult.read(buffer, null, this);
    						}
    						else
    						{
    							try
    							{
    								finnalResult.close();
    							}
    							catch(IOException e)
    							{
    								e.printStackTrace();
    							}
    						}
    					}
    					
    					public void failed(Throwable exc, Object result2)
    					{
    						exc.printStackTrace();
    						buffer.clear();
    					}
    				});
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    			finally
    			{
        			server.accept(null, this);
    			}
    		}
    		
    		public void failed(Throwable exc, Object attachment)
    		{
    			exc.printStackTrace();
    			server.accept(null, this);
    		}
    	});
    	
    	try
    	{
    		System.in.read();
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    }
}
