package com.xgame.server.game.map;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.xgame.server.CommandCenter;
import com.xgame.server.common.CoordinatePair;
import com.xgame.server.common.PackageItem;
import com.xgame.server.common.ServerPackage;
import com.xgame.server.common.protocol.EnumProtocol;
import com.xgame.server.game.astar.Grid;
import com.xgame.server.objects.Player;
import com.xgame.server.objects.WorldObject;
import com.xgame.server.pool.ServerPackagePool;

public class Map
{
	protected int id;
	protected int unloadTimer;
	private Grid[][] gridContainer;
	private boolean[][] negativePath;
	private int gridX;
	private int gridY;
	private MapConfig config;
	
	private static final int GRID_WIDTH = 500;
	private static final int GRID_HEIGHT = 500;
	private static Log log = LogFactory.getLog(Map.class);
	
	public Map(int id, MapConfig config)
	{
		this.id = id;
		this.config = config;
		
		gridX = (int)Math.floor(config.width / GRID_WIDTH);
		gridY = (int)Math.floor(config.height / GRID_HEIGHT);
		
		gridContainer = new Grid[gridX][gridY];
		negativePath = new boolean[config.blockNumHeight][config.blockNumWidth];
		loadMap();
	}
	
	private void loadMap()
	{
		try
		{
			BufferedImage img = ImageIO.read(new FileInputStream("data/map/" + id + "/road.png"));
			double roadScale = (double)img.getWidth() / config.width;
			long color;
			long alpha;
			
			for(int x = 0; x < config.blockNumWidth; x++)
			{
				for(int y = 0; y < config.blockNumHeight; y++)
				{
					color = img.getRGB((int)(config.blockSizeWidth * x * roadScale), (int)(config.blockSizeHeight * y * roadScale));
					alpha = color >> 24;
					if(alpha != 0)
					{
						negativePath[y][x] = true;
					}
				}
			}
//			String logString;
//			for(int i = 0; i < negativePath.length; i++)
//			{
//				logString = "";
//				for(int j = 0; j < negativePath[i].length; j++)
//				{
//					if(negativePath[i][j])
//					{
//						logString += "■";
//					}
//					else
//					{
//						logString += "□";
//					}
//				}
//				log.debug(logString);
//			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
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
		return true;
	}
	
	public boolean updatePlayerStatus(Player p)
	{
		if(p.getMapId() != id)
		{
			log.error("updatePlayerStatus() Player所在地图ID与本地图ID不符合");
			return false;
		}
		CoordinatePair coordinate = getCoordinatePair(p.getX(), p.getY());
		Grid g = getGrid((int)coordinate.getX(), (int)coordinate.getY());
		if(g == null)
		{
			return false;
		}
		
		//同屏其他玩家可见
		updateVisibility(p, g);
		//可见同屏其他玩家
//		updateOtherVisibility(p, g);
		
		return true;
	}
	
	private void sendPlayerData(Player p)
	{
		ServerPackage verifyMap = ServerPackagePool.getInstance().getObject();
		verifyMap.success = EnumProtocol.ACK_CONFIRM;
		verifyMap.protocolId = EnumProtocol.BASE_VERIFY_MAP;
		verifyMap.parameter.add(new PackageItem(4, p.getMapId()));
		verifyMap.parameter.add(new PackageItem(4, p.direction));
		CommandCenter.send(p.getChannel(), verifyMap);

		ServerPackage pack = ServerPackagePool.getInstance().getObject();
		pack.success = EnumProtocol.ACK_CONFIRM;
		pack.protocolId = EnumProtocol.REGISTER_ACCOUNT_ROLE;
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
		ArrayList<Grid> list = getViewGrid(g);
		Iterator<Grid> it = list.iterator();
		Grid currentGrid;
		Entry<UUID, WorldObject> en;
		Player currentPlayer;
		while(it.hasNext())
		{
			currentGrid = it.next();
			
			if(currentGrid == null)
			{
				continue;
			}
			
			Iterator<Entry<UUID, WorldObject>> gridIt = currentGrid.getWorldObjectIterator();
			while(gridIt.hasNext())
			{
				en = gridIt.next();
				if(en.getValue() instanceof Player && en.getValue() != p)
				{
					currentPlayer = (Player)en.getValue();
					if(!currentPlayer.getChannel().isOpen())
					{
						continue;
					}
					
					ServerPackage pack = ServerPackagePool.getInstance().getObject();
					pack.success = EnumProtocol.ACK_CONFIRM;
					pack.protocolId = EnumProtocol.SCENE_SHOW_PLAYER;
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
					CommandCenter.send(currentPlayer.getChannel(), pack);
					ServerPackagePool.getInstance().returnObject(pack);

					pack = ServerPackagePool.getInstance().getObject();
					pack.success = EnumProtocol.ACK_CONFIRM;
					pack.protocolId = EnumProtocol.SCENE_SHOW_PLAYER;
					pack.parameter.add(new PackageItem(8, currentPlayer.accountId));
					pack.parameter.add(new PackageItem(currentPlayer.name.length(), currentPlayer.name));
					pack.parameter.add(new PackageItem(8, currentPlayer.accountCash));
					pack.parameter.add(new PackageItem(4, currentPlayer.direction));
					pack.parameter.add(new PackageItem(4, currentPlayer.health));
					pack.parameter.add(new PackageItem(4, currentPlayer.healthMax));
					pack.parameter.add(new PackageItem(4, currentPlayer.mana));
					pack.parameter.add(new PackageItem(4, currentPlayer.manaMax));
					pack.parameter.add(new PackageItem(4, currentPlayer.energy));
					pack.parameter.add(new PackageItem(4, currentPlayer.energyMax));
					pack.parameter.add(new PackageItem(8, currentPlayer.getX()));
					pack.parameter.add(new PackageItem(8, currentPlayer.getY()));
					CommandCenter.send(p.getChannel(), pack);
					ServerPackagePool.getInstance().returnObject(pack);
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void updateOtherVisibility(Player p, Grid g)
	{
		ArrayList<Grid> list = getViewGrid(g);
		Iterator<Grid> it = list.iterator();
		Grid currentGrid;
		Entry<UUID, WorldObject> en;
		Player currentPlayer;
		while(it.hasNext())
		{
			currentGrid = it.next();
			
			if(currentGrid == null)
			{
				continue;
			}
			
			Iterator<Entry<UUID, WorldObject>> gridIt = currentGrid.getWorldObjectIterator();
			while(gridIt.hasNext())
			{
				en = gridIt.next();
				if(en.getValue() instanceof Player)
				{
					currentPlayer = (Player)en.getValue();
					
					if(currentPlayer == p)
					{
						continue;
					}
					if(!p.getChannel().isOpen())
					{
						continue;
					}

					ServerPackage pack = ServerPackagePool.getInstance().getObject();
					pack.success = EnumProtocol.ACK_CONFIRM;
					pack.protocolId = EnumProtocol.SCENE_SHOW_PLAYER;
					pack.parameter.add(new PackageItem(8, currentPlayer.accountId));
					pack.parameter.add(new PackageItem(currentPlayer.name.length(), currentPlayer.name));
					pack.parameter.add(new PackageItem(8, currentPlayer.accountCash));
					pack.parameter.add(new PackageItem(4, currentPlayer.direction));
					pack.parameter.add(new PackageItem(4, currentPlayer.health));
					pack.parameter.add(new PackageItem(4, currentPlayer.healthMax));
					pack.parameter.add(new PackageItem(4, currentPlayer.mana));
					pack.parameter.add(new PackageItem(4, currentPlayer.manaMax));
					pack.parameter.add(new PackageItem(4, currentPlayer.energy));
					pack.parameter.add(new PackageItem(4, currentPlayer.energyMax));
					pack.parameter.add(new PackageItem(8, currentPlayer.getX()));
					pack.parameter.add(new PackageItem(8, currentPlayer.getY()));
					CommandCenter.send(p.getChannel(), pack);
				}
			}
		}
	}
	
	private ArrayList<Grid> getViewGrid(Grid g)
	{
		int x = g.getX();
		int y = g.getY();
		int startX = x-1 < 0 ? 0 : x-1;
		int startY = y-1 < 0 ? 0 : y-1;
		int endX = startX+3 > gridX ? gridX : startX+3;
		int endY = startY+3 > gridY ? gridY : startY+3;
		ArrayList<Grid> list = new ArrayList<Grid>();
		
		for(x = startX; x < endX; x++)
		{
			for(y = startY; y < endY; y++)
			{
				list.add(gridContainer[x][y]);
			}
		}
		return list;
	}
}
