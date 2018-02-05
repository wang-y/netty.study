package com.wymix.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;



public class ClientBootstrapDemo {
    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress("127.0.0.1",9999)
                    .handler(new ChannelInitializer (){
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                    System.out.println("Received data");
                                }
                            });
                        }
                    });
            ChannelFuture f = b.connect().sync();
            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    System.out.println("success");
                }  else {
                    System.out.println("fail");
                    future.cause().printStackTrace();
                }
            });
        } catch (Exception e) {

        }finally {
            group.shutdownGracefully().sync(); //关闭EventLoopGroup,释放所有资源
        }

    }
}
