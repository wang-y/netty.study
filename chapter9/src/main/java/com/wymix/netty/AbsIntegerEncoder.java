package com.wymix.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        while (msg.readableBytes() >= 4) {  //检查是否有足够的字节用来编码
            Integer i=msg.readInt();  // 从输入的 ByteBuf中读取下一个整数
            out.add(Math.abs(i));  //将该整数取绝对值写入到编码消息的 List 中
        }
    }

}
