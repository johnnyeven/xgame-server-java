package com.xgame.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xgame.server.common.PackageItem;
import com.xgame.server.common.ServerPackage;
import com.xgame.server.common.protocol.EnumProtocol;
import com.xgame.server.pool.BufferPool;

public class CommandCenter
{
	private static Log	log	= LogFactory.getLog( CommandCenter.class );

	// private static Future<Integer> f;
	// private static ByteBuffer cache = BufferPool.getInstance().getBuffer();

	public static void send( AsynchronousSocketChannel channel,
			ServerPackage pack )
	{
		int dataLength = 0;
		ByteBuffer buffer = BufferPool.getInstance().getBuffer();
		buffer.position( 4 );

		buffer.putShort( pack.protocolId );
		buffer.put( (byte) ( pack.success ) );
		dataLength += 3;

		String strVal;
		Integer intVal;
		Long longVal;
		Double doubleVal;
		for ( int i = 0; i < pack.parameter.size(); i++ )
		{
			PackageItem item = pack.parameter.get( i );
			if ( item.item instanceof String )
			{
				byte[] str = ( (String) item.item ).getBytes( Charset
						.forName( "UTF-8" ) );
				buffer.putInt( str.length );
			}
			else
			{
				buffer.putInt( item.length );
			}
			dataLength += 4;

			if ( item.item instanceof String )
			{
				buffer.put( (byte) EnumProtocol.TYPE_STRING );
				dataLength += 1;

				strVal = (String) item.item;
				byte[] strBytes = strVal.getBytes( Charset.forName( "UTF-8" ) );
				buffer.put( strBytes, 0, strBytes.length );
				dataLength += strBytes.length;
			}
			else if ( item.item instanceof Integer )
			{
				buffer.put( (byte) EnumProtocol.TYPE_INT );
				dataLength += 1;

				intVal = (int) item.item;
				buffer.putInt( intVal );
				dataLength += 4;
			}
			else if ( item.item instanceof Long )
			{
				buffer.put( (byte) EnumProtocol.TYPE_LONG );
				dataLength += 1;

				longVal = (long) item.item;
				buffer.putLong( longVal );
				dataLength += 8;
			}
			else if ( item.item instanceof Double )
			{
				buffer.put( (byte) EnumProtocol.TYPE_DOUBLE );
				dataLength += 1;

				doubleVal = (double) item.item;
				buffer.putDouble( doubleVal );
				dataLength += 8;
			}
		}
		buffer.putInt( 0, dataLength );
		dataLength += 4;
		buffer.flip();
		buffer.limit( dataLength );

		Future< Integer > f = channel.write( buffer );

		try
		{
			int length = f.get();
			BufferPool.getInstance().releaseBuffer( buffer );
			log.debug( "send() Length=" + length );
		}
		catch ( InterruptedException | ExecutionException e )
		{
			e.printStackTrace();
		}
		// if(f.isDone() || f.isCancelled())
		// {
		// f = channel.write(buffer);
		//
		// try
		// {
		// int length = f.get();
		// BufferPool.getInstance().releaseBuffer(buffer);
		// log.debug("send() Length=" + length);
		// }
		// catch (InterruptedException | ExecutionException e)
		// {
		// e.printStackTrace();
		// }
		// }
		// else
		// {
		// spliceBuffer(buffer);
		// }
	}

	// private static void spliceBuffer(ByteBuffer b)
	// {
	// if(cache == null)
	// {
	// cache = BufferPool.getInstance().getBuffer();
	// }
	// cache.put(b.array());
	// }

}
