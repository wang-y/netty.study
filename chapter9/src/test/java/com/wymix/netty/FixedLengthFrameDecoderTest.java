package com.wymix.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.* ;

public class FixedLengthFrameDecoderTest {
    @Test //使用了注解@Test 标注,因此JUnit 将会执行该方法
    public void testFrameDecoded() {
        ByteBuf buf= Unpooled.buffer();
        for (int i = 0; i < 9; i++) { //创建一个 ByteBuf,并存储 9 字节
            buf.writeByte(i);
        }
        ByteBuf input=buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));  //创建一个 EmbeddedChannel,并添加一个 FixedLengthFrameDecoder,其将以 3 字节的帧长度被测试

        assertTrue(channel.writeInbound(input.retain()));  //将数据写入EmbeddedChannel
        assertTrue(channel.finish());  //标记 Channel 为已完成状态

        ByteBuf read = (ByteBuf) channel.readInbound();//读取所生成的消息,并且验证是否有 3 帧(切片),其中每帧(切片)帧(切片)都为 3 字节
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }

    @Test
    public void testFrameDecoded2() {
        ByteBuf buf= Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input=buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        assertFalse(channel.writeInbound(2));
        assertTrue(channel.writeInbound(7));
        assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }
}
