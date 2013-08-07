package com.xgame.server.common.protocol;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xgame.server.CommandCenter;
import com.xgame.server.common.PackageItem;
import com.xgame.server.common.ServerPackage;
import com.xgame.server.game.ProtocolPackage;
import com.xgame.server.game.astar.Node;
import com.xgame.server.game.map.Map;
import com.xgame.server.network.WorldSession;
import com.xgame.server.pool.ServerPackagePool;

public class ProtocolRequestFindPath implements IProtocol
{
	private static Log log = LogFactory.getLog(ProtocolUpdatePlayerStatus.class);

	@Override
	public void Execute(Object param1, Object param2)
	{
		ProtocolPackage parameter = (ProtocolPackage)param1;
		WorldSession session = (WorldSession)param2;
		
		int startX = Integer.MIN_VALUE;
		int startY = Integer.MIN_VALUE;
		int endX = Integer.MIN_VALUE;
		int endY = Integer.MIN_VALUE;

		for(int i = parameter.offset; i < parameter.receiveDataLength; )
		{
			int length = parameter.receiveData.getInt();
			int type = parameter.receiveData.get();
			switch(type)
			{
				case EnumProtocol.TYPE_INT:
					if(startX == Integer.MIN_VALUE)
					{
						startX = parameter.receiveData.getInt();
						break;
					}
					if(startY == Integer.MIN_VALUE)
					{
						startY = parameter.receiveData.getInt();
						break;
					}
					if(endX == Integer.MIN_VALUE)
					{
						endX = parameter.receiveData.getInt();
						break;
					}
					if(endY == Integer.MIN_VALUE)
					{
						endY = parameter.receiveData.getInt();
						break;
					}
			}
			i += (length + 5);
		}
		
		if(startX != Integer.MIN_VALUE && startY != Integer.MIN_VALUE && endX != Integer.MIN_VALUE && endY != Integer.MIN_VALUE)
		{
			Map m = session.getPlayer().getMap();
			ArrayList<Node> path = m.getAstar().find(startX, startY, endX, endY);
			log.info("[RequestFindPath] start x=" + startX + ", y=" + startY + "; end x=" + endX + ", y=" + endY + "; path=" + path);
			
			if(path != null)
			{
				ServerPackage pack = ServerPackagePool.getInstance().getObject();
				pack.success = EnumProtocol.ACK_CONFIRM;
				pack.protocolId = EnumProtocol.REGISTER_ACCOUNT_ROLE;
				
				Iterator<Node> it = path.iterator();
				Node n;
				while(it.hasNext())
				{
					n = it.next();
					pack.parameter.add(new PackageItem(4, n.x));
					pack.parameter.add(new PackageItem(4, n.y));
				}
				
				CommandCenter.send(parameter.client, pack);
			}
		}
	}

}
