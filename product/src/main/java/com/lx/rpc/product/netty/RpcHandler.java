package com.lx.rpc.product.netty;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.lx.rpc.product.entity.RpcRequest;
import com.lx.rpc.product.entity.RpcResponse;
import com.lx.rpc.product.util.ScanClassUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author admin
 */
@Slf4j
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private Map<String, Class> inters;

    public RpcHandler(Map<String, Class> inters) {
        this.inters = inters;
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("连接错误" + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());
        try {
            Object result = handle(rpcRequest);
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t);
            log.info(t.getMessage());
        }
        log.info(response.toString());
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest rpcRequest) throws  ExecutionException {
        //根据传过来类名得到Class
        Class aClass = inters.get(rpcRequest.getClassName());
        MethodAccess methodAccess = MethodAccess.get(aClass);
        Object invoke = methodAccess.invoke(ScanClassUtils.beans.get(aClass), rpcRequest.getMethodName(), rpcRequest.getParameterTypes(), rpcRequest.getParameters());
        return invoke;
    }
}
