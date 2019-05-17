import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Retrieves a XML document through a network socket
 *
 * @author Sukhjot Sekhon
 */
public class SockReceiver {

    private static final int BUF_SIZE = 8096;

    void receive(int port, String outFile) throws IOException {
        ServerSocket receiveServer = new ServerSocket(port);

        System.out.println("Listening for connection...");
        Socket sock = receiveServer.accept();
        System.out.println("Connected to sender");

        InputStream inStream = sock.getInputStream();
        BufferedInputStream bufInStream = new BufferedInputStream(inStream);
        OutputStream outStream = new FileOutputStream(outFile);

        byte[] buffer = new byte[BUF_SIZE];
        int length = 0;
        do {
            length = bufInStream.read(buffer);
            System.out.println("length 1: " + length);
            outStream.write(buffer, 0, length);
            length = bufInStream.read(buffer);
            System.out.println("length 1: " + length);
        } while (0 < length);

        System.out.println("File received successfully");

        // Close all resources opened
        outStream.close();
        bufInStream.close();
        inStream.close();
        sock.close();
        receiveServer.close();
    }
}