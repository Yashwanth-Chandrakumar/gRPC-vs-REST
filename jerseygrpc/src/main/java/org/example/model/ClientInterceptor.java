package org.example.model;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;

public class ClientInterceptor implements io.grpc.ClientInterceptor{

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        System.out.println("Client Interceptor: "+methodDescriptor.getFullMethodName());
        return channel.newCall(methodDescriptor,callOptions);
    }

}
