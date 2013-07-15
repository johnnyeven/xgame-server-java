package com.xgame.server.common.protocol;

import com.xgame.server.login.ProtocolParam;

public class ProtocolRequestQuickStart implements IProtocol
{

	@Override
	public void Execute(Object param)
	{
		ProtocolParam parameter = (ProtocolParam)param;
		
		int gameId = Integer.MIN_VALUE;
		for(int i = parameter.offset; i < parameter.receiveDataLength; )
		{
			int length = parameter.receiveData.getInt();
			int type = parameter.receiveData.get();
			switch(type)
			{
				case 0:
					if(gameId == Integer.MIN_VALUE)
					{
						gameId = parameter.receiveData.getInt();
					}
					break;
			}
			i += (length + 5);
		}
		System.out.println("[QuickStart] GameId=" + gameId);
	}

}
