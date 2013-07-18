package com.xgame.server.common.protocol;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.xgame.server.CommandCenter;
import com.xgame.server.common.CharacterProperty;
import com.xgame.server.common.PackageItem;
import com.xgame.server.common.ServerPackage;
import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.game.ProtocolParam;

public class ProtocolRequestAccountRole implements IProtocol
{

	@Override
	public void Execute(Object param)
	{
		ProtocolParam parameter = (ProtocolParam)param;
		
		long guid = Long.MIN_VALUE;
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
			}
			i += (length + 5);
		}
		System.out.println("[RequestAccountRole] Guid=" + guid);
		
		if(guid != Long.MIN_VALUE)
		{
			try
			{
				String sql = "SELECT *  FROM `game_account` WHERE `account_guid` = " + guid;
				PreparedStatement st = DatabaseRouter.getInstance().getDbConnection().prepareStatement(sql);
				ResultSet rs = st.executeQuery();
				ServerPackage pack = new ServerPackage();
				pack.success = EnumProtocol.ACK_CONFIRM;
				pack.protocolId = 0x0040;
				if(rs.first())
				{
					long accountId = rs.getLong("account_id");
					pack.parameter.add(new PackageItem(8, accountId));
					
					String nickName = rs.getString("nick_name");
					pack.parameter.add(new PackageItem(nickName.length(), nickName));
					
					long accountCash = rs.getLong("account_cash");
					pack.parameter.add(new PackageItem(8, accountCash));
					
					int direction = rs.getInt("direction");
					pack.parameter.add(new PackageItem(4, direction));
					
					int currentHealth = rs.getInt("current_health");
					pack.parameter.add(new PackageItem(4, currentHealth));

					int maxHealth = rs.getInt("max_health");
					pack.parameter.add(new PackageItem(4, maxHealth));

					int currentMana = rs.getInt("current_mana");
					pack.parameter.add(new PackageItem(4, currentMana));

					int maxMana = rs.getInt("max_mana");
					pack.parameter.add(new PackageItem(4, maxMana));

					int currentEnergy = rs.getInt("current_energy");
					pack.parameter.add(new PackageItem(4, currentEnergy));

					int maxEnergy = rs.getInt("max_energy");
					pack.parameter.add(new PackageItem(4, maxEnergy));

					int currentX = rs.getInt("current_x");
					pack.parameter.add(new PackageItem(4, currentX));

					int currentY = rs.getInt("current_y");
					pack.parameter.add(new PackageItem(4, currentY));
					
					CharacterProperty property = new CharacterProperty();
				}
				else
				{
					pack.parameter.add(new PackageItem(8, (long)(-1)));
				}
				rs.close();
				
				CommandCenter.send(parameter.client, pack);
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

}
