package com.lx.rpc.product;

import com.lx.rpc.product.netty.NettyServer;
import com.lx.rpc.product.util.ScanClassUtils;


public class ProductMain {
    private static String host = "10.0.41.90";
    private static String nacosHost = "10.0.100.35";
    private static Integer port = 8524;


    public static void main(String[] args) {

        //把对外开放的接口放入map中
        ScanClassUtils.init();

        NettyServer.init(nacosHost, host, port);
    }


}
