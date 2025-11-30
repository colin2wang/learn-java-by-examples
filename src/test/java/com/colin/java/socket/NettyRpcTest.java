package com.colin.java.socket;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NettyRpcTest {
    private final Logger logger = LoggerFactory.getLogger(NettyRpcTest.class);

    private RpcServer rpcServer;
    private RpcClient rpcClient;
    private final int port = 8088;

    @BeforeAll
    void setup() throws InterruptedException {
        // 1. 启动服务端
        rpcServer = new RpcServer(port);
        // 在新线程启动，避免阻塞测试主线程（尽管Server.start()里的bind是sync的，但我们设计start方法不要阻塞住）
        // 在上面的Server实现中，bind().sync()会阻塞直到端口绑定成功，但不会阻塞后续执行。
        rpcServer.start();

        // 2. 启动客户端并连接
        rpcClient = new RpcClient("localhost", port);
        rpcClient.connect();
    }

    @AfterAll
    void tearDown() {
        // 关闭资源
        if (rpcClient != null) rpcClient.close();
        if (rpcServer != null) rpcServer.stop();
    }

    @Test
    @DisplayName("测试正常RPC调用")
    void testSayHello() throws Exception {
        // 模拟调用 hello 接口
        Object result = rpcClient.sendRequest("sayHello", "Bing");

        logger.info("RPC Result: {}",result);

        assertNotNull(result);
        assertEquals("Hello, Bing", result);
    }

    @Test
    @DisplayName("测试未定义的方法")
    void testUnknownMethod() throws Exception {
        // 模拟调用不存在的方法
        Object result = rpcClient.sendRequest("unknownMethod", "test");

        // 根据Server的逻辑，默认返回 "Error"
        assertEquals("Error", result);
    }
}