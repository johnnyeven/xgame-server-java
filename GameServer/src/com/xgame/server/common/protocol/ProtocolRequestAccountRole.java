package com.xgame.server.common.protocol;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xgame.server.CommandCenter;
import com.xgame.server.common.PackageItem;
import com.xgame.server.common.ServerPackage;
import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.game.GameServer;
import com.xgame.server.game.ProtocolPackage;
import com.xgame.server.network.WorldSession;
import com.xgame.server.objects.Player;

public class ProtocolRequestAccountRole implements IProtocol
{
	private static Log log = LogFactory.getLog(ProtocolRequestAccountRole.class);

	@Override
	public void Execute(Object param1, Object param2)
	{
		ProtocolPackage parameter = (ProtocolPackage)param1;
		WorldSession session = (WorldSession)param2;
		
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
		log.info("[RequestAccountRole] Guid=" + guid);
		
		if(guid != Long.MIN_VALUE)
		{
			try
			{
				String sql = "SELECT *  FROM `game_account` WHERE `account_guid` = " + guid;
				PreparedStatement st = DatabaseRouter.getInstance().getConnection("gamedb").prepareStatement(sql);
				ResultSet rs = st.executeQuery();
				ServerPackage pack = new ServerPackage();
				pack.success = EnumProtocol.ACK_CONFIRM;
				pack.protocolId = 0x0040;
				if(rs.first())
				{
					long accountId = rs.getLong("account_id");
					pack.parameter.add(new PackageItem(8, accountId));
					
					int level = rs.getInt("level");
					
					String nickName = rs.getString("nick_name");
					pack.parameter.add(new PackageItem(nickName.length(), nickName));
					
					long accountCash = rs.getLong("account_cash");
					pack.parameter.add(new PackageItem(8, accountCash));
					
					int direction = rs.getInt("direction");
					pack.parameter.add(new PackageItem(4, direction));
					
					int action = rs.getInt("action");
					
					int speed = rs.getInt("speed");
					
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

					double currentX = rs.getDouble("current_x");
					pack.parameter.add(new PackageItem(4, currentX));

					double currentY = rs.getDouble("current_y");
					pack.parameter.add(new PackageItem(4, currentY));
					
					int mapId = rs.getInt("map_id");
					
					Player property = new Player();
					property.accountId = accountId;
					property.level = level;
					property.name = nickName;
					property.speed = speed;
					property.accountCash = accountCash;
					property.direction = direction;
					property.action = action;
					property.setMapId(mapId);
					property.setX(currentX);
					property.setX(currentY);
					property.healthMax = maxHealth;
					property.health = currentHealth;
					property.manaMax = maxMana;
					property.mana = currentMana;
					property.energyMax = maxEnergy;
					property.energy = currentEnergy;
					property.setChannel(parameter.client);
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
