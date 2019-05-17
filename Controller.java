import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

/**
 * Primary controller class for sending or recieving objects.
 *
 * @author Sukhjot Sekhon
 */
public class Controller {
    Controller control = new Controller();
    Creator creator = new Creator();
    Serializer serializer = new Serializer();
    SockSender sender = new SockSender();
    SockReceiver receiver = new SockReceiver();
    Deserializer deserializer = new Deserializer();
    Visualizer visualizer = new Visualizer();

    public static void main(String[] args) {
        System.out.println("Select mode");
        int userMode = creator.getIntInput(1, 2);
        if (userMode == 1)  control.sendMode();
        else                control.receiveMode();
    }

    /**
     * Run in send mode to create, serialize, and send an XML document
     */
    private void sendMode() {
        // Create objects
        int input = 1;
        while (input != 2) {
            creator.createObject();
            System.out.println("1: Continue creating" + "2: Finished creating");
            input = creator.getIntInput(1, 2);
        }

        // Serialize objects to XML document
        ArrayList<Object> createdObjects = creator.getCreatedObjects();
        for (Object object : createdObjects)
            serializer.serialize(object);

        // Send XML document
        int portNum = creator.getIntInput(1, 65535);
        sender.send(portNum, "objects(send).xml");
    }

    /**
     * Run in receive mode to recieve, deserialize, and visualize an XML document
     */
    private void receiveMode() {
        // Recieve XML document
        int portNum = creator.getIntInput(1, 65535);
        receiver.receive(portNum, "objects(receive).xml");

        // Deserialize XML document
        SAXBuilder builder = new SAXBuilder();
        File file = new File("objects(receive).xml");
        Document document = builder.build(file);
        HashMap hMap = (HashMap) deserializer.deserialize(document);

        // Visualize objects
        for (Object obj : hMap.keySet()) {
            Object deserializedObj = hMap.get(obj);
            visualizer.inspect(deserializedObj, false);
        }
    }
}