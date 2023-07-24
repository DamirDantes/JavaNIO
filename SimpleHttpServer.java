import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SimpleHttpServer {
    private static final String HTTP_RESPONSE = "HTTP/1.1 200 OK\r\n"
            + "Content-Length: 38\r\n"
            + "Content-Type: text/html\r\n"
            + "\r\n"
            + "<html><body>Hello, World!</body></html>";

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            serverSocket.bind(new InetSocketAddress(8080));
            System.out.println("Listening for connections on port 8080...");

            while (true) {
                try (SocketChannel socketChannel = serverSocket.accept()) {
                    handle(socketChannel);
                } catch (IOException e) {
                    System.out.println("Failed to handle connection: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Failed to bind on port 8080: " + e.getMessage());
        }
    }

    private static void handle(SocketChannel socketChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = socketChannel.read(buffer);

        if (bytesRead != -1) {
            String request = new String(buffer.array(), StandardCharsets.UTF_8);
            System.out.println("Received HTTP request: " + request);
            ByteBuffer responseBuffer = ByteBuffer.wrap(HTTP_RESPONSE.getBytes(StandardCharsets.UTF_8));
            socketChannel.write(responseBuffer);
        } else {
            System.out.println("Connection was closed by the client.");
        }
    }
}
