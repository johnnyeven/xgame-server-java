package com.xgame.server.common.protocol;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.xgame.server.common.database.DatabaseRouter;
import com.xgame.server.login.ProtocolParam;
import com.xgame.server.common.protocol.EnumProtocol;

public class ProtocolRequestQuickStart implements IProtocol
{

	@Override
	public void Execute(Object param)
	{
		ProtocolParam parameter = (ProtocolParam)param;
		
		int gameId = Integer.MIN_VALUE;
		for(int i = parameter.offset; i < parameter.receiveDataLength; )
		{
			int length = parameter.receiveData.getInt();
			int type = parameter.receiveData.get();
			switch(type)
			{
				case EnumProtocol.TYPE_INT:
					if(gameId == Integer.MIN_VALUE)
					{
						gameId = parameter.receiveData.getInt();
					}
					break;
			}
			i += (length + 5);
		}
		System.out.println("[QuickStart] GameId=" + gameId);
		
		if(gameId != Integer.MIN_VALUE)
		{
			String guid = getGuid().substring(0, 8);
			String name = "G" + guid;
			String pass = encode("MD5", guid);
			System.out.println("[QuickStart] Name=" + name + ", Pass=" + pass);
			
			try
			{
				Statement st = DatabaseRouter.getInstance().getDbConnection().createStatement();
				String sql = "insert into pulse_account(account_name, account_pass) values ('" + name + "', '" + pass + "')";
				st.executeUpdate(sql);
				ResultSet rs = st.getGeneratedKeys();
				rs.next();
				int insertId = rs.getInt(1);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private String getGuid()
	{
		String guid = UUID.randomUUID().toString();
		return guid.substring(0,8)+guid.substring(9,13)+guid.substring(14,18)+guid.substring(19,23)+guid.substring(24);
	}
	
	private String encode(String algorithm, String str)
	{
        if (str == null)
        {
            return null;
        }
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(str.getBytes());
            return getFormattedText(messageDigest.digest());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
	
	private String getFormattedText(byte[] digest)
	{
		StringBuffer md5StrBuff = new StringBuffer();  
		
		for (int i = 0; i < digest.length; i++)
		{              
		    if (Integer.toHexString(0xFF & digest[i]).length() == 1)
		    {
		        md5StrBuff.append("0").append(Integer.toHexString(0xFF & digest[i]));
		    }
		    else
		    {
		        md5StrBuff.append(Integer.toHexString(0xFF & digest[i]));
		    }
		} 
		return md5StrBuff.toString();
	}

}
