package com.xgame.server.objects;

import java.nio.channels.AsynchronousSocketChannel;

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
}
