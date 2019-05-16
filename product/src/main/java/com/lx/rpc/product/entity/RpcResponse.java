package com.lx.rpc.product.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RpcResponse {

    private String requestId;
    private Throwable error;
    private Object result;


}