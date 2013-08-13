package com.xgame.server.objects;

import java.nio.channels.AsynchronousSocketChannel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.enums.Direction;
import com.xgame.server.enums.Action;
import com.xgame.server.enums.PlayerStatus;
import com.xgame.server.game.map.Map;
import com.xgame.server.network.WorldSession;

public class Player extends WorldObject
{
	public long accountId = Long.MIN_VALUE;
    public int level = 0;
    public String name = "";
    public float speed = 0;
    public long accountCash = Long.MIN_VALUE;
    public int direction = Direction.DOWN;
    public int action = Action.STOP;
    public int healthMax = Integer.MIN_VALUE;
    public int health = Integer.MIN_VALUE;
    public int manaMax = Integer.MIN_VALUE;
    public int mana = Integer.MIN_VALUE;
    public int energyMax = Integer.MIN_VALUE;
    public int energy = Integer.MIN_VALUE;
    public PlayerStatus status = PlayerStatus.PENDING;
    
    private Motion motion;
    private AsynchronousSocketChannel channel;
	private WorldSession session;
	
	private static Log log = LogFactory.getLog(Player.class);
	
	public Player()
	{
		motion = new Motion(this);
	}
	
	public boolean loadFromDatabase()
	{
		if(accountId == Long.MIN_VALUE)
		{
			log.error("loadFromDatabase() accountId没有初始化");
			return false;
		}
		try
		{
			String sql = "SELECT * FROM `game_account` WHERE `account_id`=" + accountId;
			PreparedStatement st = DatabaseRouter.getInstance().getConnection("gamedb").prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			if(rs.first())
			{
				setMapId(rs.getInt("map_id"));
				level = rs.getInt("level");
				name = rs.getString("nick_name");
				accountCash = rs.getLong("account_cash");
				direction = rs.getInt("direction");
				action = rs.getInt("action");
				speed = rs.getInt("speed") / 100;
				health = rs.getInt("current_health");
				healthMax = rs.getInt("max_health");
				mana = rs.getInt("current_mana");
				manaMax = rs.getInt("max_mana");
				energy = rs.getInt("current_energy");
				energyMax = rs.getInt("max_energy");
				setX(rs.getDouble("current_x"));
				setY(rs.getDouble("current_y"));
			}
			else
			{
				log.error("loadFromDatabase() 没有找到对应的角色数据 accountId=" + accountId);
				return false;
			}
			long accountGuid = rs.getLong("account_guid");
			if(accountGuid != session.getAccountId())
			{
				log.error("loadFromDatabase() accountId与WorldSession使用的accountId不匹配");
				return false;
			}
			String gameGuid = rs.getString("game_guid");
			String guidSql = "";
			if(!gameGuid.isEmpty())
			{
				setGuid(UUID.fromString(gameGuid));
			}
			else
			{
				guidSql = "`game_guid`='" + getGuid().toString().toUpperCase() + "', ";
			}
			
			getMap();
			
			sql = "UPDATE `game_account` SET " + guidSql + " `account_lastlogin`=" + new Date().getTime() + " WHERE `account_id`=" + accountId;
			st.executeUpdate(sql);
			
			rs.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public void killPlayer()
	{
		//TODO 死亡处理
		action = Action.CORPSE;
	}

	public WorldSession getSession()
	{
		return session;
	}

	public void setSession(WorldSession session)
	{
		this.session = session;
	}

	public AsynchronousSocketChannel getChannel()
	{
		return channel;
	}

	public void setChannel(AsynchronousSocketChannel channel)
	{
		this.channel = channel;
	}

	public Motion getMotion()
	{
		return motion;
	}
}
