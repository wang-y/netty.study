package com.wymix.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    private int frameLength=3;

    public FixedLengthFrameDecoder(int frameLength) {
        this.frameLength=frameLength;
    }

    public FixedLengthFrameDecoder() {
        this.frameLength=3;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            while (in.readableBytes()>frameLength) {  //检查是否有足够的字节可以被读取,以生成下一个帧
                ByteBuf byteBuf = in.readBytes(frameLength);  //从 ByteBuf 中读取一个新帧
                out.add(byteBuf);  //将该帧添加到已被解码的消息列表中
            }
    }

}
