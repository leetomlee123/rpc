package com.lx.rpc.product.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> aClass;

    public RpcDecoder(Class<?> aClass) {
        this.aClass = aClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int i = byteBuf.readInt();
        if (i < 0) {
            channelHandlerContext.close();
        }
        if (byteBuf.readableBytes() < i) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[i];

        byteBuf.readBytes(bytes);
        Object parse = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8),aClass);
        list.add(parse);
    }
}
