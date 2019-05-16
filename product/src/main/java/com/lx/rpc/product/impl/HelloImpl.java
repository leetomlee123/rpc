package com.lx.rpc.product.impl;

import com.lx.rpc.product.annotation.RpcService;
import com.lx.rpc.product.service.Hello;

@RpcService(value = Hello.class)
public class HelloImpl implements Hello {
    @Override
    public String hello(String name) {

        return "Hello " + name;
    }

}
