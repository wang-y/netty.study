package com.wymix.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

public class ChannleOptionDemo {

    public static void main(String[] args) throws InterruptedException {
        final AttributeKey<Integer> id = AttributeKey.newInstance("ID");  //创建一个 AttributeKey以标识该属性
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress("127.0.0.1",9999)
                    .handler(new ChannelInitializer(){
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                @Override
                                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                    Integer idValue = ctx.channel().attr(id).get();  //使用 AttributeKey 检索属性以及它的值
                                    // do something with the idValue
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                    System.out.println("Received data");
                                }
                            });
                        }
                    });
            b.option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);  //设置 ChannelOption,其将在 connect()或者bind()方法被调用时被设置到已经创建的Channel 上
            b.attr(id,123456); //存储该id 属性
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
