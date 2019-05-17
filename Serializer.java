import java.util.ArrayList;
import java.util.HashMap;
import org.jdom2.*;
import java.lang.reflect.*;
import org.jdom2.output.*;
import java.io.FileOutputStream;

/**
 * Serializes objects and generates an XML document
 *
 * @author Sukhjot Sekhon
 */
public class Serializer {
    private static int objID = 0; // Unique object ID number for each object
    private HashMap hMap; // HashMap containing serialized objects and their keys
    private Document document;

    /**
     * Serialize an object
     *
     * @param obj object to serialize
     * @return XML document containing serialized objects
     */
    public Document serialize(Object obj) {
        // Initialize on first run
        if (objID == 0) {
            hMap = new HashMap();
            document = new Document();
            Element elemRoot = new Element("serialized");
            document.setRootElement(elemRoot);
        }

        // Allows to get by Object or by ID since this implementation ensures one-to-one and onto map
        hMap.put(objID++, obj);
        hMap.put(obj, objID);

        Class objClass = obj.getClass();
        Element elemRoot = document.getRootElement();
        Element elemObject = new Element("object");
        elemObject.setAttribute("class", objClass.getName());
        elemObject.setAttribute("id", Integer.toString(objID));
        elemRoot.addContent(elemObject);

        if (objClass.getName().equals("java.util.ArrayList"))
            serializeArrayList(obj, elemObject);
        else if (objClass.isArray())
            serializeArrayObj(obj, elemObject);
        else
            serializeNonArrayObj(obj, elemObject);

        outputXML(document);
        return document;
    }

    /**
     * Serialize ArrayList objects
     * @param obj ArrayList object to serialize
     * @param elemObject XML element of object
     */
    private void serializeArrayList(Object obj, Element elemObject) {
        try {
            Method size = ArrayList.class.getMethod("size", null);
            int length = (int) size.invoke(obj);
            elemObject.setAttribute("length", Integer.toString(length));

            Method get = ArrayList.class.getMethod("get", new Class[]{int.class});
            for (int i = 0; i < length; i++) {
                Object component = get.invoke(obj, i);
                addObjElement(int.class, component, elemObject);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Serialize Array objects
     * @param obj Array object to serialize
     * @param elemObject XML element of object
     */
    private void serializeArrayObj(Object obj, Element elemObject) {
        int length = Array.getLength(obj);
        elemObject.setAttribute("length", Integer.toString(length));

        Class objClass = obj.getClass();
        Class componentType = objClass.getComponentType();
        for (int i = 0; i < length; i++) {
            Object component = Array.get(obj, i);
            addObjElement(componentType, component, elemObject);
        }
    }

    /**
     * Serialize non-ArrayList and non-Array objects
     * @param obj object to serialize
     * @param elemObject XML element of object
     */
    private void serializeNonArrayObj(Object obj, Element elemObject) {
        Class objClass = obj.getClass();
        Field fields[] = objClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);

            int modifiers = fields[i].getModifiers();
            if (Modifier.isStatic(modifiers)) continue;

            Element elemField = new Element("field");
            elemField.setAttribute("name", fields[i].getName());
            elemField.setAttribute("declaringclass", fields[i].getDeclaringClass().getName());

            Object fieldObj = null;
            try {
                fieldObj = fields[i].get(obj);
            } catch (IllegalAccessException e) { e.printStackTrace(); }

            Class type = fields[i].getType();
            addObjElement(type, fieldObj, elemField);
            elemObject.addContent(elemField);
        }
    }

    /**
     * Adds a value or reference to the parent element
     * @param type the type of object
     * @param fieldObj value to set to the object
     * @param parentElem object or field element
     */
    private void addObjElement(Class type, Object fieldObj, Element parentElem) {
        // Add value if primitive
        if (type.isPrimitive())
            addValue(fieldObj, parentElem);
        // Add reference if non-primitive and serialize it if it hasn't already
        else {
            boolean objectExists = addReference(fieldObj, parentElem);
            if (!objectExists) serialize(fieldObj);
        }
    }

    /**
     * Adds a reference to the parent element and check if it needs to be serialized
     * @param objToReference object with a reference object to serialize
     * @param parentElem object or field element
     * @return true if reference exists; false otherwise
     */
    private boolean addReference (Object objToReference, Element parentElem) {
        Element elemReference = new Element("reference");
        boolean objectExists = hMap.containsValue(objToReference);
        if (objectExists) {
            int hMapKey = (int) hMap.get(objToReference);
            elemReference.addContent(Integer.toString(hMapKey));
        } else {
            elemReference.setText(Integer.toString(objID + 1));
        }
        parentElem.addContent(elemReference);
        return objectExists;
    }

    /**
     * Adds value to the parent element
     * @param objToValue object with a value to serialize
     * @param parentElem element to set the value of
     */
    private void addValue (Object objToValue, Element parentElem) {
        Element elemValue = new Element("value");
        elemValue.setText(objToValue.toString());
        parentElem.addContent(elemValue);
    }

    /**
     * Output the document as an XML document in the cwd called "objects.xml"
     * @param document XML document to output
     */
    private void outputXML(Document document) {
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        FileOutputStream fileStream = new FileOutputStream("objects(send).xml");
        out.output(document, fileStream);
    }
}