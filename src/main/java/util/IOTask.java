package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class IOTask implements Runnable {
    private Socket s;
    private String path;

    public IOTask(Socket s,String path){
        this.s = s;
        this.path = path;
    }
    @Override
    public void run() {
        String ip = s.getInetAddress().getHostAddress();
        try{
            //获取客户端输入流
            InputStream in = s.getInputStream();
            //读取信息
            byte[] names = new byte[100];
            int len = in.read(names);
            String fileName = new String(names, 0, len);
            String[] fileNames = fileName.split("\\.");
            String fileLast = fileNames[fileNames.length-1];
            //然后将后缀名发给客户端
            OutputStream out = s.getOutputStream();
            out.write(fileLast.getBytes());
            //新建文件
            File dir = new File(path+ip);
            if(!dir.exists())
                dir.mkdirs();
            File file = new File(dir,fileNames[0]+"."+fileLast);
            FileOutputStream fos = new FileOutputStream(file);
            //将Socket输入流中的信息读入到文件
            byte[] bufIn = new byte[1024];
            while((len = in.read(bufIn))!=-1){
                //写入文件
                fos.write(bufIn, 0, len);
            }
            fos.close();
            s.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
