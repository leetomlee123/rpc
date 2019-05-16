package com.lx.rpc.product.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> aClass;

    public RpcEncoder(Class<?> aClass) {
        this.aClass = aClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] bytes = JSON.toJSONBytes(o);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

    }
}
