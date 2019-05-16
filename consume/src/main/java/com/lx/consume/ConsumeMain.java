package com.lx.consume;

import com.lx.consume.proxy.Rpcproxy;
import com.lx.consume.rpc.Hello;

public class ConsumeMain {
    public static void main(String[] args) {
        Hello proxy = (Hello) new Rpcproxy().createProxy(Hello.class);
        String hello = proxy.hello("I am xiang Lee");
        System.out.println(hello);
    }
}
