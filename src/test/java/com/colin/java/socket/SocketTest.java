package com.colin.java.socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test class for NIO socket operations
 * Demonstrates non-blocking socket communication using Selector pattern
 */
public class SocketTest {
    private static final Logger logger = LoggerFactory.getLogger(SocketTest.class);
    private Selector selector;
    private ServerSocketChannel server;
    private static final int PORT = 7777;
    private static final String HOST = "127.0.0.1";
    private Thread serverThread;
    private volatile boolean running = false;
    
    /**
     * Sets up resources before each test
     */
    @BeforeEach
    public void setUp() throws IOException {
        logger.info("Setting up test resources");
        selector = Selector.open();
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(HOST, PORT));
        server.configureBlocking(false);
        running = false;
    }
    
    /**
     * Cleans up resources after each test
     */
    @AfterEach
    public void tearDown() throws IOException {
        logger.info("Cleaning up test resources");
        running = false;
        
        if (selector != null && selector.isOpen()) {
            // Close all channels registered with this selector
            for (SelectionKey key : selector.keys()) {
                if (key.channel() != null) {
                    key.channel().close();
                }
                key.cancel();
            }
            selector.close();
        }
        
        if (server != null && server.isOpen()) {
            server.close();
        }
        
        if (serverThread != null && serverThread.isAlive()) {
            try {
                serverThread.interrupt();
                serverThread.join(1000);
            } catch (InterruptedException e) {
                logger.error("Error joining server thread", e);
            }
        }
    }
    
    /**
     * Starts the server in a separate thread
     */
    private void startServer() {
        running = true;
        serverThread = new Thread(() -> {
            try {
                server.register(selector, SelectionKey.OP_ACCEPT);
                logger.info("Server started on {}:{}", HOST, PORT);
                
                while (running) {
                    if (selector.select(100) == 0) {
                        // Check if we should continue running
                        if (!running) break;
                        continue;
                    }
                    
                    Set<SelectionKey> keys = selector.selectedKeys();
                    for (SelectionKey key : new HashSet<>(keys)) {
                        try {
                            processSelectionKey(key);
                        } catch (IOException e) {
                            logger.error("Error processing selection key", e);
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        } finally {
                            keys.remove(key);
                        }
                    }
                }
            } catch (IOException e) {
                if (running) { // Only log error if we weren't intentionally shutting down
                    logger.error("Server error", e);
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }
    
    /**
     * Processes a selection key based on its ready operations
     */
    private void processSelectionKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            handleAccept(key);
        }
        if (key.isReadable()) {
            handleRead(key);
        }
        if (key.isWritable()) {
            handleWrite(key);
        }
    }
    
    /**
     * Handles ACCEPT operation
     */
    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        channel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        logger.info("Accepted connection from {}", channel.getRemoteAddress());
    }
    
    /**
     * Handles READ operation
     */
    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        int bytesRead = channel.read(buffer);
        
        if (bytesRead == -1) {
            // Connection closed by client
            channel.close();
            key.cancel();
            logger.info("Client connection closed");
        } else if (bytesRead > 0) {
            // Data read successfully
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            logger.info("Read {} bytes: {}", bytesRead, new String(data));
            buffer.clear();
            
            // Register for write operation to echo back the data
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }
    
    /**
     * Handles WRITE operation
     */
    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        
        // Echo back a simple message
        String response = "Echo from server";
        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
        channel.write(responseBuffer);
        
        // Switch back to read-only mode
        key.interestOps(SelectionKey.OP_READ);
        logger.info("Sent response to client");
    }
    
    /**
     * Tests server initialization
     */
    @Test
    public void testServerInitialization() throws IOException {
        // Verify selector is open
        assertTrue(selector.isOpen(), "Selector should be open");
        
        // Verify server socket is bound and open
        assertTrue(server.isOpen(), "ServerSocketChannel should be open");
        
        // Verify server is in non-blocking mode
        assertFalse(server.isBlocking(), "ServerSocketChannel should be in non-blocking mode");
        
        // Verify server is bound to the correct address
        InetSocketAddress address = (InetSocketAddress) server.getLocalAddress();
        assertEquals(HOST, address.getHostString(), "Server should be bound to correct host");
        assertEquals(PORT, address.getPort(), "Server should be bound to correct port");
        
        logger.info("Server initialization test passed");
    }
    
    /**
     * Tests registration of ServerSocketChannel to Selector
     */
    @Test
    public void testServerRegistrationToSelector() throws IOException {
        // Register server with selector
        SelectionKey key = server.register(selector, SelectionKey.OP_ACCEPT);
        
        // Verify registration is valid
        assertNotNull(key, "SelectionKey should not be null");
        assertTrue(key.isValid(), "SelectionKey should be valid");
        
        // Verify interest set contains only OP_ACCEPT
        int interestOps = key.interestOps();
        assertEquals(SelectionKey.OP_ACCEPT, interestOps, "Server should only be interested in ACCEPT operations");
        
        // Verify selector has registered keys
        Set<SelectionKey> registeredKeys = selector.keys();
        assertEquals(1, registeredKeys.size(), "Selector should have exactly one registered key");
        
        logger.info("Server registration to selector test passed");
    }
    
    /**
     * Tests basic client-server communication
     * This test starts a server and a client, sends data from client to server,
     * and verifies the server responds
     */
    @Test
    public void testClientServerCommunication() throws IOException, InterruptedException {
        // Start the server in a separate thread
        startServer();
        
        // Give the server a moment to start
        Thread.sleep(100);
        
        // Create a client connection
        try (SocketChannel client = SocketChannel.open()) {
            client.connect(new InetSocketAddress(HOST, PORT));
            client.configureBlocking(false);
            
            // Verify client is connected
            assertTrue(client.isConnected(), "Client should be connected to server");
            logger.info("Client connected to server");
            
            // Send data to server
            String message = "Hello from client";
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            client.write(buffer);
            logger.info("Client sent message: {}", message);
            
            // Wait for server to process and respond
            Thread.sleep(100);
            
            // Read response from server
            buffer.clear();
            int bytesRead = client.read(buffer);
            assertTrue(bytesRead > 0, "Client should read at least one byte from server");
            
            buffer.flip();
            byte[] responseData = new byte[buffer.remaining()];
            buffer.get(responseData);
            String response = new String(responseData);
            
            logger.info("Client received response: {}", response);
            // Verify response contains expected content
            assertTrue(response.contains("Echo"), "Response should contain 'Echo'");
        }
        
        logger.info("Client-server communication test passed");
    }
    
    /**
     * Tests server shutdown and resource cleanup
     */
    @Test
    public void testServerShutdown() throws IOException, InterruptedException {
        // Start the server
        startServer();
        
        // Give the server a moment to start
        Thread.sleep(100);
        
        // Stop the server by setting running flag to false
        running = false;
        
        // Wait for server thread to terminate
        if (serverThread != null) {
            serverThread.join(1000);
            assertFalse(serverThread.isAlive(), "Server thread should terminate after shutdown");
        }
        
        // Verify resources are closed in tearDown method
        logger.info("Server shutdown test passed");
    }
    
    /**
     * Tests multiple client connections
     */
    @Test
    public void testMultipleClientConnections() throws IOException, InterruptedException {
        final int clientCount = 3;
        final CountDownLatch clientLatch = new CountDownLatch(clientCount);
        
        // Start the server
        startServer();
        
        // Give the server a moment to start
        Thread.sleep(100);
        
        // Create multiple client connections
        for (int i = 0; i < clientCount; i++) {
            final int clientId = i;
            Thread clientThread = new Thread(() -> {
                try (SocketChannel client = SocketChannel.open()) {
                    client.connect(new InetSocketAddress(HOST, PORT));
                    client.configureBlocking(false);
                    
                    logger.info("Client {} connected", clientId);
                    
                    // Send client ID to server
                    String message = "Hello from client " + clientId;
                    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                    client.write(buffer);
                    
                    // Wait for response
                    Thread.sleep(50);
                    
                    buffer.clear();
                    client.read(buffer);
                    
                    logger.info("Client {} completed", clientId);
                } catch (IOException | InterruptedException e) {
                    logger.error("Error in client {}: {}", clientId, e.getMessage());
                } finally {
                    clientLatch.countDown();
                }
            });
            clientThread.start();
        }
        
        // Wait for all clients to complete or timeout
        boolean completed = clientLatch.await(2, TimeUnit.SECONDS);
        assertTrue(completed, "All clients should complete within timeout period");
        
        logger.info("Multiple client connections test passed");
    }
}