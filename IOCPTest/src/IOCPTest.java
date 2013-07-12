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
     public final static int PORT = 9040;   //�˿ں�
     private AsynchronousServerSocketChannel server; //�����൱һһ����������SOCKET����������ʵ�����첽ͨ����ʽ
     public Map<String, List<AsynchronousSocketChannel>> map=
                                         new HashMap<String, List<AsynchronousSocketChannel>>(); 

 

    public IOCPTest() throws IOException {
            //   AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 1);
//         server = AsynchronousServerSocketChannel.open(group);
//         //ͨ��setOption����Socket
//         server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
//         server.setOption(StandardSocketOptions.SO_RCVBUF, 16 * 1024);
//         //�󶨵�ָ�����������˿�
//         server.bind(new InetSocketAddress("дһ��Ip��ַ", PORT)); 

           //����ע�͵���д��,���Ҳ���,�ᱨIO�쳣

           server = AsynchronousServerSocketChannel.open().bind(
                          new InetSocketAddress("127.0.0.1",PORT)); 

     }
       
    public void startWithCompletionHandler() throws InterruptedException,
                         ExecutionException, TimeoutException, IOException {
               System.out.println("Server listen on " + PORT);


                /*****************************************************
                 * ��Ҫ����:ע���¼����¼���ɺ�Ĵ�����
                 * ʵ��ԭ��:��һ���û����ӵ�������ʱ,�����accept����,
                 * �˷�����һ����������������һ����ɶ˿�(��������-CompletionHandler)���а󶨵ķ���
                 * ���û����ӳɹ�ʱ,��ɶ˿ڻ��Զ�����completed����,�ⲽ�ɲ���ϵͳ���
                 * Ҫʵ�������Ӷ��û�,������completed��������ѭ������һ��accept����
                 * ��������:server.accept(null, this);
                 * ����IOCP����ϸʵ��ԭ��μ�C++�е�IOCP
                 *****************************************************/              

               server.accept(null,new CompletionHandler<AsynchronousSocketChannel, Object>() {

                          //ByteBuffer:�������ݵĻ�����,�����ʼ����СΪ65535
                          ByteBuffer buffer = ByteBuffer.allocate(65535);

                          
                          @Override
                          public void completed(AsynchronousSocketChannel result,Object attachment) {

                                       //AsynchronousSocketChannel�൱Ψһ��ʾ�ͻ���socket
                                       //������Ҫ�Ӹ�final�ؼ��ֲ�����SocketChannel�����ڷ���read�пɼ�
                                       final AsynchronousSocketChannel SocketChannel=result;

                                      //�ٴ�������Ͷ��һ����������
                                       server.accept(null, this);
      
                                try {

                                        //��ջ�����,�ⲽ����ʡ
                                        buffer.clear();

                                        result.read(buffer, null, new CompletionHandler<Integer, Object>() {

                                                @Override
                                                public void completed(Integer result1,Object result2) {
                                                               System.out.println("result1:"+result1);
            
                                                                 if(result1==-1){

                                                                       //������ɽ��ж�ʧȥ���ӵĿͻ��˽���ɾ������
                                                                  } 
                                                 
                                                                 if(result1!=-1){
                                                                         buffer.flip();

                                                                         //���յ������ݻ�����ת�ֽ���,�˺�ɶ����������в���
                                                                 }

                                                   //��ɽ��ղ���֮��,������ջ�����,��Ȼ�������ѭ��
                                                  buffer.clear();

                                                  //�ٴ�������Ͷ��һ��������������
                                                  SocketChannel.read(buffer, null, this);
                         
                                                 }
         

                                                @Override
                                                public void failed(Throwable exc,Object result2) {
                                                         exc.printStackTrace();
                                                         System.out.println("failed: " + exc);

                                                             //��ɽ��ղ���֮��,������ջ�����,��Ȼ�������ѭ��
                                                             buffer.clear();

                                                            //�ٴ�������Ͷ��һ��������������
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

                             //�ٴ�������Ͷ��һ����������
                                       server.accept(null, this);
                       }
              });
            // ������� ��֤���̵߳Ĵ��

             System.in.read();

     }

    public static void main(String args[]) throws Exception {
                new IOCPTest().startWithCompletionHandler();
     }
}