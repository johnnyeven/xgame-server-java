package com.xgame.server.network;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xgame.server.CommandCenter;
import com.xgame.server.common.AuthSessionPackage;
import com.xgame.server.common.PackageItem;
import com.xgame.server.common.ServerPackage;
import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.common.protocol.EnumProtocol;
import com.xgame.server.game.ProtocolPackage;
import com.xgame.server.game.World;
import com.xgame.server.pool.BufferPool;

public class AuthSessionCompletionHandler implements
		CompletionHandler<Integer, AuthSessionPackage>
{
    private static Log log = LogFactory.getLog(AuthSessionCompletionHandler.class);

	public AuthSessionCompletionHandler()
	{
		
	}

	@Override
	public void completed(Integer arg0, AuthSessionPackage arg1)
	{
		ByteBuffer buffer = arg1.buffer;
		buffer.flip();
		int packageLength = buffer.getInt();
		short protocolId = buffer.getShort();
		
		if(protocolId == 0x0070)
		{
			String accountName = null;
			for(int i = 6; i < arg0; )
			{
				int length = buffer.getInt();
				int type = buffer.get();
				switch(type)
				{
					case EnumProtocol.TYPE_STRING:
						if(accountName == null)
						{
							length = buffer.getShort();
							byte[] dst = new byte[length];
							buffer.get(dst);
							length += 2;
							try
							{
								accountName = new String(dst, "UTF-8");
							} catch (UnsupportedEncodingException e)
							{
								e.printStackTrace();
							}
						}
				}
				i += (length + 5);
			}
			log.info("[AuthSession] AccountName=" + accountName);
			

			if(accountName != null)
			{
				try
				{
					String sql = "SELECT * FROM `pulse_account` WHERE `account_name`='" + accountName + "'";
					PreparedStatement st = DatabaseRouter.getInstance().getConnection("accountdb").prepareStatement(sql);
					ResultSet rs = st.executeQuery();
					long guid = Long.MIN_VALUE;
					if(rs.first())
					{
						guid = rs.getLong("GUID");
					}
					
					WorldSession s = new WorldSession(guid, arg1.channel, new Date().getTime());
					World.getInstance().addSessionQueue(s);
					s.startRecv();

					ServerPackage pack = new ServerPackage();
					pack.success = EnumProtocol.ACK_CONFIRM;
					pack.protocolId = 0x0070;
					pack.parameter.add(new PackageItem(4, 1));
					CommandCenter.send(arg1.channel, pack);
					
					rs.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
			
			BufferPool.getInstance().releaseBuffer(buffer);
		}
		else
		{
			log.error("��SessionЭ��Ŵ����յ���Э���Ϊ" + protocolId + ", Ӧ��Ϊ" + 0x0070);
		}
	}

	@Override
	public void failed(Throwable arg0, AuthSessionPackage arg1)
	{
		log.error(arg0.getMessage());
		ByteBuffer buffer = arg1.buffer;
		BufferPool.getInstance().releaseBuffer(buffer);
	}

}
