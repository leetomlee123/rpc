package com.lx.consume.proxy;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lx.consume.netty.RpcClient;
import com.lx.rpc.product.entity.RpcRequest;
import com.lx.rpc.product.entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

@Slf4j
public class Rpcproxy {
    private static String nacosHost = "10.0.100.35:8848";
    private static NamingService namingService;

    static {
        try {
            namingService = NamingFactory.createNamingService(nacosHost);
        } catch (NacosException e) {
            log.error("连接nacos失败" + e);
        }
    }

    public <T> T createProxy(final Class<?> aclass) {
        return (T) Proxy.newProxyInstance(aclass.getClassLoader(), new Class<?>[]{aclass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(aclass.getSimpleName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);
                //服务发现
                Instance product = namingService.selectOneHealthyInstance("product");


                RpcClient client = new RpcClient(product.getIp(), product.getPort());
                RpcResponse response = client.send(request);

                return response.getError() == null ? response.getResult() : response.getError();

            }
        });
    }

}
