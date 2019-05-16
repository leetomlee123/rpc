package com.lx.consume.netty;

import com.lx.rpc.product.entity.RpcRequest;
import com.lx.rpc.product.entity.RpcResponse;
import com.lx.rpc.product.netty.RpcDecoder;
import com.lx.rpc.product.netty.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
    private String serverIp;
    private Integer port;


    private RpcResponse response;
    private final Object obj = new Object();

    public RpcClient(String serverIp, Integer port) {
        this.port = port;
        this.serverIp = serverIp;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;
        synchronized (obj) {
            obj.notifyAll();
        }
    }

    public RpcResponse send(RpcRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group).channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline()
                                        .addLast(new RpcEncoder(RpcRequest.class))
                                        .addLast(new RpcDecoder(RpcResponse.class))
                                        .addLast(RpcClient.this);
                            }
                        })
                        .option(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture future = bootstrap.connect(serverIp, port).sync();
            future.channel().writeAndFlush(request);
            synchronized (obj) {
                obj.wait();
            }
            if (response != null) {
                future.channel().closeFuture();
            }
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }

}
