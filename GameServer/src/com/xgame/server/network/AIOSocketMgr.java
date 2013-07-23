package com.xgame.server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xgame.server.common.protocol.ProtocolRegisterAccountRole;
import com.xgame.server.common.protocol.ProtocolRequestAccountRole;
import com.xgame.server.common.protocol.ProtocolRequestHotkey;
import com.xgame.server.common.protocol.ProtocolRouter;
import com.xgame.server.game.ProtocolPackage;
import com.xgame.server.game.World;

public class AIOSocketMgr
{
	private ProtocolRouter router;
    private AsynchronousServerSocketChannel server;
    private AcceptCompletionHandler acceptHandler;
    private ReadCompletionHandler readHandler;
    public final static String HOST = "127.0.0.1";
    public final static int PORT = 9050;
    
    private static AIOSocketMgr instance = null;
    private static boolean allowInstance = false;
    private static Log log = LogFactory.getLog(AIOSocketMgr.class);
    
	public AIOSocketMgr() throws Exception
	{
		if(!allowInstance)
		{
			throw new Exception();
		}
		try
		{
			AsynchronousChannelGroup resourceGroup = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 8);  
			server = AsynchronousServerSocketChannel.open(resourceGroup);
			server.bind(new InetSocketAddress(HOST, PORT), 100);
			
			acceptHandler = new AcceptCompletionHandler();
			readHandler = new ReadCompletionHandler(this);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		ProtocolRouter.getInstance().Bind((short)0x0040, ProtocolRequestAccountRole.class);
		ProtocolRouter.getInstance().Bind((short)0x0050, ProtocolRegisterAccountRole.class);
		ProtocolRouter.getInstance().Bind((short)0x0060, ProtocolRequestHotkey.class);
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
    	server.accept(this, acceptHandler);
    	log.info("服务器已启动");
    	
//    	server.accept(this, new CompletionHandler<AsynchronousSocketChannel, Object>() {
//    		
//    		ByteBuffer buffer = ByteBuffer.allocate(65535);
//    		public void completed(AsynchronousSocketChannel result,Object attachment)
//    		{
//				final WorldSession s = new WorldSession(1, result, new Date().getTime());
//                final AsynchronousSocketChannel finnalResult=result;
//
//				World.getInstance().addSession(s);
//    			try
//    			{
//    				buffer.clear();
//    				result.read(buffer, null, new CompletionHandler<Integer, Object>() {
//    					public void completed(Integer result1, Object result2)
//    					{
//    						if(result1 > 0)
//    						{
//	    						//处理buffer
//	    						buffer.flip();
//	    						int packageLength = buffer.getInt();
//	    						short protocolId = buffer.getShort();
//	    						
//	    						ProtocolPackage parameter = new ProtocolPackage();
//	    						parameter.protocolId = protocolId;
//	    						parameter.client = finnalResult;
//	    						parameter.receiveDataLength = result1;
//	    						parameter.receiveData = buffer.duplicate();
//	    						parameter.offset = 6;
//	    						
//	    						s.addParameterQueue(parameter);
//	    						
//	    						buffer.clear();
//	    						finnalResult.read(buffer, null, this);
//    						}
//    						else
//    						{
//    							try
//    							{
//    								finnalResult.close();
//    							}
//    							catch(IOException e)
//    							{
//    								e.printStackTrace();
//    							}
//    						}
//    					}
//    					
//    					public void failed(Throwable exc, Object result2)
//    					{
//    						exc.printStackTrace();
//    						buffer.clear();
//    					}
//    				});
//    			}
//    			catch(Exception e)
//    			{
//    				e.printStackTrace();
//    			}
//    			finally
//    			{
//        			server.accept(null, this);
//    			}
//    		}
//    		
//    		public void failed(Throwable exc, Object attachment)
//    		{
//    			exc.printStackTrace();
//    			server.accept(null, this);
//    		}
//    	});
    	
    	try
    	{
    		System.in.read();
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    }

	public AsynchronousServerSocketChannel getServer()
	{
		return server;
	}

	public void setServer(AsynchronousServerSocketChannel server)
	{
		this.server = server;
	}

	public ReadCompletionHandler getReadHandler()
	{
		return readHandler;
	}
}
