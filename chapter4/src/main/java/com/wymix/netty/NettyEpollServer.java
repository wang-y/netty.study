package com.wymix.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class NettyEpollServer {
    public static void main(String[] args) throws InterruptedException {
        ByteBuf msg = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!".getBytes(Charset.forName("UTF-8"))));

        EventLoopGroup eventLoopGroup = new EpollEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap(); //创建 ServerBootstrap

            serverBootstrap.group(eventLoopGroup).channel(EpollServerSocketChannel.class) //使用 EpollEventLoopGroup以允许非阻塞模式(新的I/O)
                    .localAddress(new InetSocketAddress(9999))
                    .childHandler(new ChannelInitializer<SocketChannel>() {  //指定 ChannelInitializer,对于每个已接受的连接都调用它
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {  // 添加一个ChannelInboundHandlerAdapter以连接和处理事件
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(msg.duplicate()).addListener(ChannelFutureListener.CLOSE);  // 将消息写到客户端,并添加ChannelFutureListener以便在消息写完后关闭连接
                                }
                            });
                        }
                    });
            try {
                ChannelFuture f = serverBootstrap.bind().sync();  //绑定服务器接受连接
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            eventLoopGroup.shutdownGracefully().sync(); //释放所有的资源
        }
    }
}
