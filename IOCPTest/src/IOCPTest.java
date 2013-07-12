import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class IOCPTest {
     public final static int PORT = 9040;   //端口号
     private AsynchronousServerSocketChannel server; //此类相当一一个服务器的SOCKET，不过它的实现是异步通信形式
     public Map<String, List<AsynchronousSocketChannel>> map=
                                         new HashMap<String, List<AsynchronousSocketChannel>>(); 

 

    public IOCPTest() throws IOException {
            //   AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 1);
//         server = AsynchronousServerSocketChannel.open(group);
//         //通过setOption配置Socket
//         server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
//         server.setOption(StandardSocketOptions.SO_RCVBUF, 16 * 1024);
//         //绑定到指定的主机，端口
//         server.bind(new InetSocketAddress("写一个Ip地址", PORT)); 

           //上面注释掉的写法,经我测试,会报IO异常

           server = AsynchronousServerSocketChannel.open().bind(
                          new InetSocketAddress("127.0.0.1",PORT)); 

     }
       
    public void startWithCompletionHandler() throws InterruptedException,
                         ExecutionException, TimeoutException, IOException {
               System.out.println("Server listen on " + PORT);


                /*****************************************************
                 * 主要功能:注册事件和事件完成后的处理器
                 * 实现原理:当一有用户连接到服务器时,会调用accept方法,
                 * 此方法是一个非阻塞方法并和一个完成端口(即处理器-CompletionHandler)进行绑定的方法
                 * 当用户连接成功时,完成端口会自动调用completed方法,这步由操作系统完成
                 * 要实现能连接多用户,必须在completed方法中在循环调用一次accept方法
                 * 代码如下:server.accept(null, this);
                 * 关于IOCP的详细实现原理参见C++中的IOCP
                 *****************************************************/              

               server.accept(null,new CompletionHandler<AsynchronousSocketChannel, Object>() {

                          //ByteBuffer:接收数据的缓冲区,这里初始化大小为65535
                          ByteBuffer buffer = ByteBuffer.allocate(65535);

                          
                          @Override
                          public void completed(AsynchronousSocketChannel result,Object attachment) {

                                       //AsynchronousSocketChannel相当唯一标示客户的socket
                                       //这里需要加个final关键字才能让SocketChannel对象在方法read中可见
                                       final AsynchronousSocketChannel SocketChannel=result;

                                      //再次向处理器投递一个连接请求
                                       server.accept(null, this);
      
                                try {

                                        //清空缓冲区,这步不能省
                                        buffer.clear();

                                        result.read(buffer, null, new CompletionHandler<Integer, Object>() {

                                                @Override
                                                public void completed(Integer result1,Object result2) {
                                                               System.out.println("result1:"+result1);
            
                                                                 if(result1==-1){

                                                                       //这里面可进行对失去连接的客户端进行删除操作
                                                                  } 
                                                 
                                                                 if(result1!=-1){
                                                                         buffer.flip();

                                                                         //接收到的数据缓冲区转字节数,此后可对这个数组进行操作
                                                                 }

                                                   //完成接收操作之后,必须清空缓冲区,不然会出现死循环
                                                  buffer.clear();

                                                  //再次向处理器投递一个接收数据请求
                                                  SocketChannel.read(buffer, null, this);
                         
                                                 }
         

                                                @Override
                                                public void failed(Throwable exc,Object result2) {
                                                         exc.printStackTrace();
                                                         System.out.println("failed: " + exc);

                                                             //完成接收操作之后,必须清空缓冲区,不然会出现死循环
                                                             buffer.clear();

                                                            //再次向处理器投递一个接收数据请求
                                                            SocketChannel.read(buffer, null, this);

                                                 }
         
                                        });
        
       
        
                             }catch (Exception e) {
                                       e.printStackTrace();
                             }finally {

                              }
                      }

                     @Override
                    public void failed(Throwable exc, Object attachment) {
                              exc.printStackTrace();
                              System.out.println("failed: " + exc);

                             //再次向处理器投递一个连接请求
                                       server.accept(null, this);
                       }
              });
            // 这里必须 保证主线程的存活

             System.in.read();

     }

    public static void main(String args[]) throws Exception {
                new IOCPTest().startWithCompletionHandler();
     }
}