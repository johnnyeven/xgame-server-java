package com.xgame.server.common.protocol;

import java.util.HashMap;

public class ProtocolRouter
{
	private HashMap<Short, Class<?>> protocolList;
	
	public ProtocolRouter()
	{
		protocolList = new HashMap<Short, Class<?>>();
	}
	
	public void Bind(Short key, Class<?> value)
	{
		protocolList.put(key, value);
	}
	
	public void UnBind(Short key)
	{
		if(protocolList.containsKey(key))
		{
			protocolList.remove(key);
		}
	}
	
	public boolean HasBind(Short key)
	{
		return protocolList.containsKey(key);
	}
	
	public void Trigger(Short key, Object param)
	{
		if(protocolList.containsKey(key))
		{
			Class<?> reflection = protocolList.get(key);
			try
			{
				IProtocol protocol = (IProtocol)reflection.newInstance();
				protocol.Execute(param);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("收到一个不认识的协议号, id=" + key);
		}
	}
}
