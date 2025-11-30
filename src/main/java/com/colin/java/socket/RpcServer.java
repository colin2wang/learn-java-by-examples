package com.colin.java.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServer {
    private Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private final int port;
    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public RpcServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                // 使用Java原生序列化简化代码
                                .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                .addLast(new ObjectEncoder())
                                .addLast(new SimpleChannelInboundHandler<RpcRequest>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest req) {
                                        // 简单的模拟实现，实际应用中应使用反射调用 Service Bean
                                        logger.info("Received request: {}",  req.toString());
                                        String result = "Error";
                                        if ("sayHello".equals(req.getMethodName())) {
                                            result = "Hello, " + req.getArgs()[0];
                                        }
                                        // 发送响应
                                        ctx.writeAndFlush(new RpcResponse(req.getRequestId(), result));
                                    }
                                });
                    }
                });

        this.channel = b.bind(port).sync().channel();
        logger.info("Server started on port {}" ,port);
    }

    public void stop() {
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
    }
}
