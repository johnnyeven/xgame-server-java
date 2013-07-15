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

import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.common.protocol.ProtocolRouter;

public class LoginServer
{
	private ProtocolRouter router;
    private AsynchronousServerSocketChannel server;
    public Map<String, List<AsynchronousSocketChannel>> map= new HashMap<String, List<AsynchronousSocketChannel>>(); 
    public final static int PORT = 9040;
    
    public LoginServer()
    {
		router = new ProtocolRouter();
		
		DatabaseRouter.getInstance();
		
		try
		{
			server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("127.0.0.1",PORT), 100);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
    }
    
    public void startCompletionPort()
    {
    	System.out.println("服务器已启动");
    	
    	server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
    		
    		ByteBuffer buffer = ByteBuffer.allocate(65535);
    		public void completed(AsynchronousSocketChannel result,Object attachment)
    		{
                final AsynchronousSocketChannel finnalResult=result;
    			server.accept(null, this);
    			
    			try
    			{
    				buffer.clear();
    				result.read(buffer, null, new CompletionHandler<Integer, Object>() {
    					
    					public void completed(Integer result1, Object result2)
    					{
    						//处理buffer
    						buffer.flip();
    						
    						
    						buffer.clear();
    						finnalResult.read(buffer, null, this);
    					}
    					
    					public void failed(Throwable exc, Object result2)
    					{
    						exc.printStackTrace();
    						buffer.clear();
    						finnalResult.read(buffer, null, this);
    					}
    				});
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
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
	
	public static void main(String[] args)
	{
		new LoginServer().startCompletionPort();
	}
	
}
