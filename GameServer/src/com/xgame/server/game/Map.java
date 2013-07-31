package com.xgame.server.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.xgame.server.CommandCenter;
import com.xgame.server.common.CoordinatePair;
import com.xgame.server.common.PackageItem;
import com.xgame.server.common.ServerPackage;
import com.xgame.server.common.protocol.EnumProtocol;
import com.xgame.server.objects.Player;
import com.xgame.server.objects.WorldObject;

public class Map
{
	protected int id;
	protected int unloadTimer;
	private Grid[][] gridContainer;
	private int gridX;
	private int gridY;
	private MapConfig config;
	
	private static final int GRID_WIDTH = 1028;
	private static final int GRID_HEIGHT = 600;
	private static Log log = LogFactory.getLog(Map.class);
	
	public Map(int id, MapConfig config)
	{
		this.id = id;
		this.config = config;
		
		gridX = (int)Math.floor(config.width / GRID_WIDTH);
		gridY = (int)Math.floor(config.height / GRID_HEIGHT);
		
		gridContainer = new Grid[gridX][gridY];
	}
	
	public static CoordinatePair getCoordinatePair(double x, double y)
	{
		return new CoordinatePair(Math.ceil(x / GRID_WIDTH), Math.ceil(y / GRID_HEIGHT));
	}
	
	private void setGrid(Grid g, int x, int y)
	{
		if(x >= gridX || y >= gridY)
		{
			log.error("setGrid() 错误的Grid坐标 x=" + x + ", y=" + y);
			return;
		}
		gridContainer[x][y] = g;
	}
	
	private Grid getGrid(int x, int y)
	{
		if(x >= gridX || y >= gridY)
		{
			log.error("setGrid() 错误的Grid坐标 x=" + x + ", y=" + y);
			return null;
		}
		return gridContainer[x][y];
	}
	
	public boolean add(Player p)
	{
		CoordinatePair coordinate = getCoordinatePair(p.getX(), p.getY());
		
		Grid g = getGrid((int)coordinate.getX(), (int)coordinate.getY());
		if(g == null)
		{
			g = new Grid((int)coordinate.getX(), (int)coordinate.getY());
			setGrid(g, (int)coordinate.getX(), (int)coordinate.getY());
		}
		g.addWorldObject(p);
		
		//发送玩家初始化数据
		sendPlayerData(p);
		//同屏其他玩家可见
		updateVisibility(p, g);
		//可见同屏其他玩家
		updateOtherVisibility(p, g);
		return true;
	}
	
	private void sendPlayerData(Player p)
	{
		ServerPackage verifyMap = new ServerPackage();
		verifyMap.success = EnumProtocol.ACK_CONFIRM;
		verifyMap.protocolId = EnumProtocol.ACTION_VERIFY_MAP << 8 | EnumProtocol.CONTROLLER_BASE;
		verifyMap.parameter.add(new PackageItem(4, p.getMapId()));
		verifyMap.parameter.add(new PackageItem(4, p.direction));
		CommandCenter.send(p.getChannel(), verifyMap);

		ServerPackage pack = new ServerPackage();
		pack.success = EnumProtocol.ACK_CONFIRM;
		pack.protocolId = 0x0050;
		pack.parameter.add(new PackageItem(8, p.accountId));
		pack.parameter.add(new PackageItem(p.name.length(), p.name));
		pack.parameter.add(new PackageItem(8, (long)0));
		pack.parameter.add(new PackageItem(4, 0));
		pack.parameter.add(new PackageItem(4, 200));
		pack.parameter.add(new PackageItem(4, 200));
		pack.parameter.add(new PackageItem(4, 85));
		pack.parameter.add(new PackageItem(4, 85));
		pack.parameter.add(new PackageItem(4, 100));
		pack.parameter.add(new PackageItem(4, 100));
		pack.parameter.add(new PackageItem(8, (double)700));
		pack.parameter.add(new PackageItem(8, (double)700));
		CommandCenter.send(p.getChannel(), pack);
	}
	
	private void updateVisibility(Player p, Grid g)
	{
		Iterator<Entry<UUID, WorldObject>> it = g.getWorldObjectIterator();
		Entry<UUID, WorldObject> en;
		Player other;
		while(it.hasNext())
		{
			en = it.next();
			if(en.getValue() instanceof Player && en.getValue() != p)
			{
				other = (Player)en.getValue();
				if(!other.getChannel().isOpen())
				{
					continue;
				}
				
				ServerPackage pack = new ServerPackage();
				pack.success = EnumProtocol.ACK_CONFIRM;
				pack.protocolId = EnumProtocol.ACTION_SHOW_PLAYER << 8 | EnumProtocol.CONTROLLER_SCENE;
				pack.parameter.add(new PackageItem(8, p.accountId));
				pack.parameter.add(new PackageItem(p.name.length(), p.name));
				pack.parameter.add(new PackageItem(8, p.accountCash));
				pack.parameter.add(new PackageItem(4, p.direction));
				pack.parameter.add(new PackageItem(4, p.health));
				pack.parameter.add(new PackageItem(4, p.healthMax));
				pack.parameter.add(new PackageItem(4, p.mana));
				pack.parameter.add(new PackageItem(4, p.manaMax));
				pack.parameter.add(new PackageItem(4, p.energy));
				pack.parameter.add(new PackageItem(4, p.energyMax));
				pack.parameter.add(new PackageItem(8, p.getX()));
				pack.parameter.add(new PackageItem(8, p.getY()));
				CommandCenter.send(other.getChannel(), pack);
			}
		}
	}
	
	private void updateOtherVisibility(Player p, Grid g)
	{
		
	}
}
