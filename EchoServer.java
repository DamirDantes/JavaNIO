import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {

    private Selector selector;
    private ServerSocketChannel serverSocket;

    public EchoServer(int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() throws IOException {
        while (true) {
            int readyChannels = selector.select();

            if (readyChannels == 0) {
                continue;
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = readyKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    SocketChannel clientSocket = serverSocket.accept();
                    clientSocket.configureBlocking(false);
                    clientSocket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    System.out.println("Accepted a connection from " + clientSocket.getRemoteAddress());
                } else if (key.isReadable()) {
                    SocketChannel clientSocket = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    clientSocket.read(buffer);
                    String result = new String(buffer.array()).trim();
                    System.out.println("Received message: " + result);

                    if (key.isWritable()) {
                        clientSocket.write(ByteBuffer.wrap(("Echo: " + result).getBytes()));
                    }
                }

                keyIterator.remove();
            }
        }
    }

    public static void main(String[] args) {
        try {
            new EchoServer(8000).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
