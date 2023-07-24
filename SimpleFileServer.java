import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class SimpleFileServer {

    public static void main(String[] args) {
        try {
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(true);
            serverChannel.socket().bind(new InetSocketAddress(8080));
            System.out.println("Server started on port 8080");

            while (true) {
                SocketChannel socketChannel = serverChannel.accept();
                System.out.println("Accepted connection from " + socketChannel);

                sendFile(socketChannel, "test.txt");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void sendFile(SocketChannel socketChannel, String path) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(path, "r")) {
            FileChannel fileChannel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead;

            while ((bytesRead = fileChannel.read(buffer)) != -1) {
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
            }

            System.out.println("File " + path + " sent to client");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
