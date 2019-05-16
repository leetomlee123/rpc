package com.lx.rpc.product.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RpcRequest {

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;


}