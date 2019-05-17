import java.util.ArrayList;
import java.util.HashMap;
import org.jdom2.*;
import java.lang.reflect.*;
import java.util.List;

/**
 * Deserializes an XML document by instantiating and setting objects
 *
 * @author Sukhjot Sekhon
 */
public class Deserializer {
    private HashMap hMap;

    /**
     * Deserializes a document
     *
     * @param document Document to deserialize
     * @return HashMap containing deserialized objects
     */
    public Object deserialize(Document document) {
        hMap = new HashMap();

        Element elemRoot = document.getRootElement();
        List<Element> objs = elemRoot.getChildren(); // List of objects in document

        for (Element obj : objs) {
            String className = obj.getAttributeValue("class");
            Class classObj = null;
            try {
                classObj = Class.forName(className);
            } catch (ClassNotFoundException e) { e.printStackTrace(); }

            Object objLoaded = null;
            //Arraylist
            if (className.equals("java.util.ArrayList")) {
                try {
                    Class componentType = int.class;
                    int lengthLoaded = Integer.parseInt(obj.getAttributeValue("length"));
                    objLoaded = ArrayList.class.newInstance();
                } catch (Exception e) { e.printStackTrace(); }
            }
            // Array
            else if (classObj.isArray()) {
                Class componentType = classObj.getComponentType();
                int lengthLoaded = Integer.parseInt(obj.getAttributeValue("length"));
                objLoaded = Array.newInstance(componentType, lengthLoaded);
            }
            // Other
            else {
                try {
                    Constructor constructor = classObj.getDeclaredConstructor(null); // no-arg constructor
                    objLoaded = constructor.newInstance(null);
                } catch (Exception e) { e.printStackTrace(); }
            }
            // Get the ID and put it in the HashMap
            int objID = Integer.parseInt(obj.getAttributeValue("id"));
            hMap.put(objID, objLoaded);
        }
        setFields(objs);
        return hMap;
    }

    /**
     * Sets fields or references of an object
     * @param objs objects to set fields/references to
     */
    private void setFields(List<Element> objs) {
        int objID = 1; // Object ID's start at 1
        for (Element obj : objs) {
            Object objToSet = hMap.get(objID++);
            List<Element> fieldElems = obj.getChildren();

            Class objClass = objToSet.getClass();
            // ArrayList
            if (objClass.getName().equals("java.util.ArrayList") || objClass.getName().equals("ObjCollections"))
                setArrayListFields(objToSet, fieldElems);
            // Array
            else if (objClass.isArray())
                setArrayFields(objToSet, fieldElems);
            // Other
            else
                setNonArrayFields(objToSet, fieldElems);
        }
    }

    /**
     * Sets fields of an ArrayList object
     *
     * @param objToSet ArrayList object to set
     * @param fieldElems Values to set object to
     */
    private void setArrayListFields(Object objToSet, List<Element> fieldElems) {
        try {
            for (Element fieldElem : fieldElems) {
                int newValue = Integer.parseInt(fieldElem.getText());
                Method add = ArrayList.class.getDeclaredMethod("add", new Class[] {Object.class});
                add.invoke(objToSet, newValue);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Sets fields/references of an Array object
     *
     * @param objToSet Array object to set
     * @param fieldElems Values to set object to
     */
    private void setArrayFields(Object objToSet, List<Element> fieldElems) {
        int arrayIndex = 0; // Index of the array being set
        for (Element fieldElem : fieldElems) {
            String fieldName = fieldElem.getName();
            // Sets a reference field
            if (fieldName.equals("reference")) {
                Object newReference = hMap.get(fieldElem.getText());
                Array.set(objToSet, arrayIndex++, newReference); // sets reference and increments index
            }
            // Sets a value field
            else {
                int newValue = Integer.parseInt(fieldElem.getText());
                Array.setInt(objToSet, arrayIndex++, newValue); // sets value and increments index
            }
        }
    }

    /**
     * Sets fields/references of non-Array and non-ArrayList objects
     * @param objToSet Object to set
     * @param fieldElems Values to set object to
     */
    private void setNonArrayFields(Object objToSet, List<Element> fieldElems) {
        for (Element fieldElem : fieldElems) {
            Class decObjClass = null;
            String decClassName = fieldElem.getAttributeValue("declaringclass");
            try {
                decObjClass = Class.forName(decClassName);
            } catch (ClassNotFoundException e) { e.printStackTrace(); }

            String fieldName = fieldElem.getAttributeValue("name");
            Field fieldObj = null;
            try {
                fieldObj = decObjClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) { e.printStackTrace(); }

            fieldObj.setAccessible(true);

            List<Element> fields = fieldElem.getChildren();
            String fieldType = fields.get(0).getName();
            String newValue = fields.get(0).getText();
            try {
                if (fieldType.equals("reference")) {
                    Object newReference = hMap.get(newValue);
                    fieldObj.set(objToSet, newReference);
                } else {
                    int newIntValue = Integer.valueOf(newValue);
                    fieldObj.setInt(objToSet, newIntValue);
                }
            } catch (IllegalAccessException e) { e.printStackTrace(); }
        }
    }
}
