package com.xgame.server.login;

import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.common.protocol.ProtocolRouter;

public class LoginServer
{
	private static ProtocolRouter router;
	
	public static void main(String[] args)
	{
		router = new ProtocolRouter();
		
		DatabaseRouter.getInstance();
		
	}

}
