package com.wymix.netty;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class PlainOioServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=new ServerSocket(9999);  //服务器绑定指定端口
        while (true){
            Socket accept = serverSocket.accept();  // 接受链接
            new Thread(()->{  //创建新线程处理新的链接
                OutputStream outputStream=null;
                try {
                    outputStream = accept.getOutputStream();
                    outputStream.write("Hi!".getBytes(Charset.forName("UTF-8")));  // 将消息写给已连接的客户端
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                            accept.close();  //释放连接
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }
}
