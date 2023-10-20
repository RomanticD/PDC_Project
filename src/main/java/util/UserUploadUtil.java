package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class UserUploadUtil {
    //端口
    private int port;
    //文件路径
    private String path;

    public UserUploadUtil(int port , String path) throws IOException {
        this.port = port;
        this.path = path;
        /*
         * 客户端先向服务端发送一个文件名，服务端接收到后给客户端一个反馈，然后客户端开始发送文件
         */
        //建立客户端Socket
        Socket s = new Socket(InetAddress.getLocalHost(), port);//服务器IP地址
        //获得输出流
        OutputStream out = s.getOutputStream();
        //关联发送文件
        File file = new File(path);
        String name = file.getName();//获取文件完整名称
        String[] fileName = name.split("\\.");//将文件名按照.来分割
        String fileLast = fileName[fileName.length-1];//后缀名
        //写入信息到输出流
        out.write(name.getBytes());
        //读取服务端的反馈信息
        InputStream in = s.getInputStream();
        byte[] names = new byte[50];
        int len = in.read(names);
        String nameIn = new String(names, 0, len);
        if(!fileLast.equals(nameIn)){
            //结束输出，并结束当前线程
            s.close();
            System.exit(1);
        }
        //如果正确，则发送文件信息
        //读取文件信息
        FileInputStream fr = new FileInputStream(file);
        //发送文件信息
        byte[] buf = new byte[1024];
        while((len=fr.read(buf))!=-1){
            //写入到Socket输出流
            out.write(buf,0,len);
        }
        //关流
        out.close();
        fr.close();
        s.close();
    }
}

