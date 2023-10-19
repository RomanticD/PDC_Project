package util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerUploadUtil {
    private int port;

    private String path;
   public ServerUploadUtil(int port,String path)  {
       this.port = port;
       this.path = path;
        /*
         * 服务端先接收客户端传过来的信息，然后向客户端发送接收成功，新建文件，接收客户端信息
         */
        //建立服务端
        ServerSocket ss = null;//客户端端口需要与服务端一致
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while(true){
            //获取客户端Socket
            Socket s = null;
            try {
                s = ss.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            new Thread(new IOTask(s,path)).start();
        }
    }
}