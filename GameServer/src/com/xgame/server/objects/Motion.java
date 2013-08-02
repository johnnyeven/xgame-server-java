package com.xgame.server.objects;

import java.util.ArrayList;
import java.util.List;

import com.xgame.server.common.CoordinatePair;
import com.xgame.server.enums.Action;
import com.xgame.server.enums.PlayerStatus;

public class Motion
{
	private Player p;
	private List<CoordinatePair> path;
	private int currentStep;

	public Motion(Player p)
	{
		if(p != null)
		{
			this.p = p;
		}
		path = new ArrayList<CoordinatePair>();
	}
	
	public void update(long timeDiff)
	{
		if(p.status != PlayerStatus.NORMAL)
		{
			return;
		}
		
		if(path == null || path.isEmpty() || path.get(currentStep) == null)
		{
			return;
		}
		
		if(p.action != Action.MOVE)
		{
			p.action = Action.MOVE;
		}
	}
}
