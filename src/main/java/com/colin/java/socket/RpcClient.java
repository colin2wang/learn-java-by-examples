package com.colin.java.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RpcClient {
    private final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private final String host;
    private final int port;
    private Channel channel;
    private EventLoopGroup group;
    // 用于保存请求ID和Future的映射，实现异步转同步
    private final Map<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                .addLast(new ObjectEncoder())
                                .addLast(new SimpleChannelInboundHandler<RpcResponse>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) {
                                        // 收到响应，找到对应的Future并设置结果
                                        logger.info("Received request: {}",  msg.toString());
                                        CompletableFuture<Object> future = pendingRequests.remove(msg.getRequestId());
                                        if (future != null) {
                                            future.complete(msg.getResult());
                                        }
                                    }
                                });
                    }
                });

        this.channel = b.connect(host, port).sync().channel();
    }

    // 发送请求的方法
    public Object sendRequest(String methodName, Object... args) throws Exception {
        String reqId = UUID.randomUUID().toString();
        RpcRequest request = new RpcRequest(reqId, methodName, args);

        CompletableFuture<Object> future = new CompletableFuture<>();
        pendingRequests.put(reqId, future);

        channel.writeAndFlush(request);

        // 等待结果 (超时控制)
        return future.get(5, TimeUnit.SECONDS);
    }

    public void close() {
        if (group != null) group.shutdownGracefully();
    }
}
