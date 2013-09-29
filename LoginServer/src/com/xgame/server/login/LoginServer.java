package com.xgame.server.login;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.common.protocol.*;

public class LoginServer
{
	private ProtocolRouter router;
    private AsynchronousServerSocketChannel server;
    public Map<String, List<AsynchronousSocketChannel>> map= new HashMap<String, List<AsynchronousSocketChannel>>(); 
    public final static int PORT = 9040;
    
    public LoginServer()
    {
		router = new ProtocolRouter();
		router.Bind(EnumProtocol.QUICK_START, ProtocolRequestQuickStart.class);
		
		DatabaseRouter.getInstance();
		
		try
		{
			server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("192.168.0.107", PORT), 100);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
    }
    
    public void startCompletionPort()
    {
		if(server.isOpen())
		{
			System.out.println("open");
		}
		else
		{
			System.out.println("close");
		}
    	server.accept(server, new CompletionHandler<AsynchronousSocketChannel, Object>() {
    		
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
	    						buffer.getInt();
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
    		}
    	});
    	System.out.println("服务器已启动");
    	
    	try
    	{
    		System.in.read();
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    }
	
	public static void main(String[] args)
	{
		new LoginServer().startCompletionPort();
	}
	
}
