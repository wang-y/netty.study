package com.wymix.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
//        if (args.length != 1) {
//            System.err.println(
//                    "Usage: " + EchoServer.class.getSimpleName() +
//                            " <port>");
//        }
//        int port = Integer.parseInt(args[0]);
        new EchoServer(9999).start();

    }


    public void start() throws InterruptedException {
        final EchoServerHandler echoServerHandler = new EchoServerHandler(); // 实现了业务逻辑
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(); //创建EventLoopGroup,nio传输 ,实例化NioEventLoopGroup
        try {
            ServerBootstrap b = new ServerBootstrap();//创建 ServerBootstrap
            b.group(eventLoopGroup) //指定使用NioEventLoopGroup接受和处理新的连接
                    .channel(NioServerSocketChannel.class) //制定channel类型为NioServersocketChannel
                    .localAddress(new InetSocketAddress(port)) //绑定到该地址进行监听新的连接请求
                    .childHandler(new ChannelInitializer<SocketChannel>() {  // ** 重要 当有一个新的连接被接受时 一个新的channel将被创建,而 ChannelInitializer 将会把一个 Handler 的实例添加到该 Channel 的 ChannelPipeline 中。
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(echoServerHandler);  //将 EchoServerHandler实例 添加到channel的ChannelPipeline中
                        }
                    });
            ChannelFuture f = b.bind().sync();// 异步绑定到无服务器上; 当调用sync()方法时将阻塞等待到绑定完成
            f.channel().closeFuture().sync();// 获取Channel的CloseFuture,并且阻塞到当前线程直到完成
        } finally {
            eventLoopGroup.shutdownGracefully().sync(); //关闭EventLoopGroup,释放所有资源
        }


    }
}
