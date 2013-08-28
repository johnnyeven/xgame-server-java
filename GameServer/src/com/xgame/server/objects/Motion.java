package com.xgame.server.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xgame.server.common.Angle;
import com.xgame.server.common.Point;
import com.xgame.server.enums.Action;
import com.xgame.server.enums.PlayerStatus;
import com.xgame.server.game.WorldThread;
import com.xgame.server.game.astar.Node;

public class Motion
{
	private Player			p;
	private List< Point >	path;
	private int				currentStep;
	private Point			nextPoint;
	private Point			endPoint;
	private static Log		log	= LogFactory.getLog( Motion.class );

	public Motion( Player p )
	{
		if ( p != null )
		{
			this.p = p;
		}
		path = new ArrayList< Point >();
	}

	public void update( long timeDiff )
	{
		if ( p.status != PlayerStatus.NORMAL )
		{
			return;
		}

		if ( path == null || path.isEmpty() || path.get( currentStep ) == null )
		{
			return;
		}

		if ( p.action != Action.MOVE )
		{
			p.action = Action.MOVE;
		}

		if ( currentStep == path.size() )
		{
			nextPoint = endPoint;
		}
		else
		{
			Point pos = path.get( currentStep );
			nextPoint = p.getMap().block2WorldPosition( (int) pos.getX(),
					(int) pos.getY() );
		}

		double radian = Angle.getAngle( nextPoint.getX() - p.getX(),
				nextPoint.getY() - p.getY() );

		boolean xEnd = false;
		boolean yEnd = false;

		double xSpeed = p.getMoveSpeed() * Math.cos( radian );
		double ySpeed = p.getMoveSpeed() * Math.sin( radian );

		if ( Math.abs( p.getX() - nextPoint.getX() ) <= xSpeed )
		{
			xEnd = true;
			xSpeed = 0;
		}
		if ( Math.abs( p.getY() - nextPoint.getY() ) <= ySpeed )
		{
			yEnd = true;
			ySpeed = 0;
		}

		if ( xEnd && yEnd )
		{
			currentStep++;
			if ( currentStep >= path.size() )
			{
				clearPath();
				return;
			}
		}
		moveTo( p.getX() + xSpeed, p.getY() + ySpeed );
	}

	public void clearPath()
	{
		this.path.clear();
		currentStep = 1;
	}

	public void move( ArrayList< Node > path )
	{
		clearPath();
		Node en = path.get( path.size() - 1 );
		endPoint = new Point( en.x, en.y );

		Iterator< Node > it = path.iterator();
		while ( it.hasNext() )
		{
			en = it.next();
			this.path.add( new Point( en.x, en.y ) );
		}
	}

	private void moveTo( double x, double y )
	{
		if ( p.action != Action.DIE )
		{
			p.setX( x );
			p.setY( y );
			p.action = Action.MOVE;
			log.debug( p.name + " �ƶ��� x=" + x + ", y=" + y );
		}
	}
}
