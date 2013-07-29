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
    private AsynchronousServerSocketChannel server;
    private AcceptCompletionHandler acceptHandler;
    private ReadCompletionHandler readHandler;
    private AuthSessionCompletionHandler authHandler;
    public final static String HOST = "127.0.0.1";
    public final static int PORT = 9050;
    public static int counter = 0;
    
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
			authHandler = new AuthSessionCompletionHandler();
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

	public AuthSessionCompletionHandler getAuthHandler()
	{
		return authHandler;
	}
}
