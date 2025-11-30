package com.colin.java.socket;

import java.io.Serializable;
import java.util.Arrays;

// 1. 服务接口
interface HelloService {
    String sayHello(String name);
}

// 2. RPC 请求对象
class RpcRequest implements Serializable {
    private String requestId;
    private String methodName;
    private Object[] args;

    // Constructors, Getters, Setters
    public RpcRequest(String requestId, String methodName, Object[] args) {
        this.requestId = requestId;
        this.methodName = methodName;
        this.args = args;
    }
    public String getRequestId() { return requestId; }
    public String getMethodName() { return methodName; }
    public Object[] getArgs() { return args; }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", methodName='" + methodName + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}

// 3. RPC 响应对象
class RpcResponse implements Serializable {
    private String requestId;
    private Object result;

    public RpcResponse(String requestId, Object result) {
        this.requestId = requestId;
        this.result = result;
    }
    public String getRequestId() { return requestId; }
    public Object getResult() { return result; }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", result=" + result +
                '}';
    }
}