package com.wymix.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioDatagramChannel;

public class UDPBootstrapDemo {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup group = new OioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(OioDatagramChannel.class)
                    .remoteAddress("127.0.0.1", 9999)
                    .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                            System.out.println("Received data");
                        }
                    });

            ChannelFuture f = b.bind();  //调用 bind()方法,因为该协议是无连接的
            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    System.out.println("success");
                } else {
                    System.out.println("fail");
                    future.cause().printStackTrace();
                }
            });
        } catch (Exception e) {

        } finally {
            group.shutdownGracefully().sync(); //关闭EventLoopGroup,释放所有资源
        }

    }
}
