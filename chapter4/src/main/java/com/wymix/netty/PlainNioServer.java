package com.wymix.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        InetSocketAddress address = new InetSocketAddress(9999);  //服务器绑定端口
        serverSocket.bind(address);
        Selector selector = Selector.open();  //打开Selector处理Channel
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  //将serversocketchannel 注册到Selector 以接受连接
        ByteBuffer byteBuffer = ByteBuffer.wrap("Hi!".getBytes(Charset.forName("UTF-8")));
        while (true) {
            try {
                selector.select();  //等待需要处理的新事件,并阻塞到一个新的事件传入
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();  //获取所有接受事件的SelectionKey实例
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                try {
                    if (key.isAcceptable()) {  // 检查实践是不是一个新的已经就绪的可以被接受的事件
                        ServerSocketChannel server =
                                (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, byteBuffer.duplicate());  //接受客户端,并将其注册到选择器中
                        System.out.println(
                                "Accepted connection from " + client);
                    }
                    if (key.isWritable()) {  // 检查一个套接字是否准备好写数据
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        while (buffer.hasRemaining()) {
                            if (client.write(buffer) == 0) {  //将数据写到已连接客户端
                                break;
                            }
                        }
                        client.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                    }
                }
            }
        }
    }
}