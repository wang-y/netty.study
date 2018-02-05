package com.wymix.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.* ;

public class AbsIntegerEncoderTest {

    @Test
    public void testAbsIntegerEncoded() {
        ByteBuf buf= Unpooled.buffer();
        for (int i = 1; i <10 ; i++) {
            buf.writeInt(i * -1);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        assertTrue(channel.writeOutbound(buf));
        assertTrue(channel.finish());

        for (int i = 1; i < 10; i++) {
            System.out.println((Integer) channel.readOutbound());
        }
        assertNull(channel.readOutbound());
    }
}
