import java.io.*;
import java.net.Socket;

/**
 * Sends the produced XML document through a network socket
 *
 * @author Sukhjot Sekhon
 */
public class SockSender {

    private static final String HOST = "127.0.0.1"; // localhost IP address

    void send(int port, String inFile) throws IOException {
        File file = new File(inFile);
        int fileLength = (int) file.length();

        Socket sock = new Socket(HOST, port);
        OutputStream outStream = sock.getOutputStream();
        BufferedOutputStream bufOutStream = new BufferedOutputStream(outStream);
        InputStream inStream = new FileInputStream(file);

        System.out.println("Outputting file " + inFile);

        byte[] buffer = new byte[fileLength];
        do {
            bufOutStream.write(buffer, 0, inStream.read(buffer));
        } while (0 < inStream.read(buffer));

        System.out.println("File sent successfully");

        // Close all resources opened
        bufOutStream.close();
        outStream.close();
        inStream.close();
        sock.close();
    }
}