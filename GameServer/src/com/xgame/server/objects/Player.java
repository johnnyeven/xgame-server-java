package com.xgame.server.objects;

import java.nio.channels.AsynchronousSocketChannel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.enums.Direction;
import com.xgame.server.enums.Action;
import com.xgame.server.network.WorldSession;

public class Player extends WorldObject
{
	public long accountId = Long.MIN_VALUE;
    public int level = 0;
    public String name = "";
    public int speed = 0;
    public long accountCash = Long.MIN_VALUE;
    public int direction = Direction.DOWN;
    public int action = Action.STOP;
    public int mapId = Integer.MIN_VALUE;
    public int currentX = Integer.MIN_VALUE;
    public int currentY = Integer.MIN_VALUE;
    public int healthMax = Integer.MIN_VALUE;
    public int health = Integer.MIN_VALUE;
    public int manaMax = Integer.MIN_VALUE;
    public int mana = Integer.MIN_VALUE;
    public int energyMax = Integer.MIN_VALUE;
    public int energy = Integer.MIN_VALUE;
    
    public AsynchronousSocketChannel channel;
	public WorldSession session;
	
	public boolean loadFromDatabase()
	{
		if(accountId == Long.MIN_VALUE)
		{
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
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return true;
	}
}
