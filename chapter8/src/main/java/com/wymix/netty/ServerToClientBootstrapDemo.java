package com.wymix.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class ServerToClientBootstrapDemo {

    public static void main(String[] args) {
        try {
            ServerBootstrap b = new ServerBootstrap(); // 创建 ServerBootstrap 以创建 ServerSocketChannel,并绑定它
            b.group(new NioEventLoopGroup(), new NioEventLoopGroup())  //设置 EventLoopGroup,其将提供用以处理 Channel 事件的 EventLoop
                    .localAddress(new InetSocketAddress(9999))
                    .channel(NioServerSocketChannel.class)  //指定要使用的Channel 实现
                    .childHandler(new ChannelInitializer<SocketChannel>() {  //设置用于处理已被接受的子 Channel 的 I/O 和数据的ChannelInboundHandler
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                ChannelFuture cf;

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    Bootstrap cb = new Bootstrap();  //创建一个 Bootstrap类的实例以连接到远程主机
                                    cb.group(ctx.channel().eventLoop())  //使用与分配给已被接受的子Channel 相同的EventLoop
                                            .channel(NioSocketChannel.class)  //指定要使用的Channel 实现
                                            .remoteAddress("127.0.0.1", 9999)
                                            .handler(new SimpleChannelInboundHandler<ByteBuf>() {  //为入站 I/O 设置ChannelInboundHandler
                                                @Override
                                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                                    System.out.println("Received data");
                                                }
                                            });
                                    cf = cb.connect();
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                    if (cf.isDone()) {
                                        // do something with the data  //当连接完成时,执行一些数据操作(如代理)
                                    }
                                }
                            });
                        }
                    });
            ChannelFuture f = b.bind();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("success");
                    } else {
                        System.out.println("fail");
                        future.cause().printStackTrace();
                    }
                }
            });
        } catch (Exception e) {

        }
    }

}
