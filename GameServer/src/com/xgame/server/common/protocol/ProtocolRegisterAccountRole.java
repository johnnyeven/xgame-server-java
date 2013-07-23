package com.xgame.server.common.protocol;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.xgame.server.CommandCenter;
import com.xgame.server.common.PackageItem;
import com.xgame.server.common.ServerPackage;
import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.game.ProtocolPackage;

public class ProtocolRegisterAccountRole implements IProtocol
{

	@Override
	public void Execute(Object param)
	{
		ProtocolPackage parameter = (ProtocolPackage)param;
		
		long guid = Long.MIN_VALUE;
		String nickName = null;
		
		for(int i = parameter.offset; i < parameter.receiveDataLength; )
		{
			int length = parameter.receiveData.getInt();
			int type = parameter.receiveData.get();
			switch(type)
			{
				case EnumProtocol.TYPE_LONG:
					if(guid == Long.MIN_VALUE)
					{
						guid = parameter.receiveData.getLong();
					}
					break;
				case EnumProtocol.TYPE_STRING:
					if(nickName == null)
					{
						length = parameter.receiveData.getShort();
						byte[] dst = new byte[length];
						parameter.receiveData.get(dst);
						length += 2;
						try
						{
							nickName = new String(dst, "UTF-8");
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
					}
			}
			i += (length + 5);
		}
		System.out.println("[RegisterAccountRole] Guid=" + guid + ", NickName=" + nickName);
		
		if(guid != Integer.MIN_VALUE && nickName != null)
		{
			try
			{
				String sql = "INSERT INTO game_account(account_guid, nick_name, current_health, max_health, current_mana, max_mana, current_energy, max_energy, current_x, current_y)values";
				sql += "(" + guid + ", '" + nickName + "', 200, 200, 85, 85, 100, 100, 700, 700)";
				PreparedStatement st = DatabaseRouter.getInstance().getDbConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				st.executeUpdate();
				
				ResultSet rs = st.getGeneratedKeys();
				rs.first();
				long lastInsertId = rs.getLong(1);

				ServerPackage pack = new ServerPackage();
				pack.success = EnumProtocol.ACK_CONFIRM;
				pack.protocolId = 0x0050;

				pack.parameter.add(new PackageItem(8, lastInsertId));
				pack.parameter.add(new PackageItem(nickName.length(), nickName));
				pack.parameter.add(new PackageItem(8, (long)0));
				pack.parameter.add(new PackageItem(4, 0));
				pack.parameter.add(new PackageItem(4, 200));
				pack.parameter.add(new PackageItem(4, 200));
				pack.parameter.add(new PackageItem(4, 85));
				pack.parameter.add(new PackageItem(4, 85));
				pack.parameter.add(new PackageItem(4, 100));
				pack.parameter.add(new PackageItem(4, 100));
				pack.parameter.add(new PackageItem(4, 700));
				pack.parameter.add(new PackageItem(4, 700));
				CommandCenter.send(parameter.client, pack);
				
				rs.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

}
