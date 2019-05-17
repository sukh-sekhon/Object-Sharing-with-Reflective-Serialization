import java.util.ArrayList;
import java.util.Scanner;

/**
 * Creates an instances of objects defined by a user.
 * Types include:
 *      - Primitive (ints)
 *      - Reference
 *      - Array (ints)
 *      - Array of references
 *      - ArrayList (ints)
 *
 * @author Sukhjot Sekhon
 */

public class Creator {
    private int objID = 0; // Unique object ID number
    private ArrayList<Object> createdObjects = new ArrayList<>();
    Scanner scan = new Scanner(System.in);

    /**
     * Created instance of an object type
     * @return newly created object
     */
    Object createObject() {
        objID++; // Increment to keep ID number unique
        createdObjects.add(null);

        System.out.println("\nSelect one of the following options to create an object:\n" +
                "\t1: Object containing primitive instance variables\n" +
                "\t2: Object containing references to other objects\n" +
                "\t3: Object containing array of primitives\n" +
                "\t4: Object containing an array of object references\n" +
                "\t5: Object using an instance of a Java collection class");

        Object newObject = null;
        while(newObject == null) {
            int selection = getIntInput(1, 5);
            switch (selection) {
                case 1: newObject = createPrimitive(); break;
                case 2: newObject = createReference(); break;
                case 3: newObject = createPrimitiveArray(); break;
                case 4: newObject = createReferenceArray(); break;
                case 5: newObject = createCollections(); break;
                default: System.out.println("Invalid"); break;
            }
        }
        createdObjects.set(objID - 1, newObject);
        return newObject;
    }

    /**
     * Create a primitive object defined by user
     * @return primitive object
     */
    private ObjPrimitive createPrimitive() {
        int firstInt = getIntInput(Integer.MIN_VALUE, Integer.MAX_VALUE);
        int secondInt = getIntInput(Integer.MIN_VALUE, Integer.MAX_VALUE);
        int thirdInt = getIntInput(Integer.MIN_VALUE, Integer.MAX_VALUE);
        ObjPrimitive newObject = new ObjPrimitive(firstInt, secondInt, thirdInt);
        return newObject;
    }

    /**
     * Create a reference object defined by user
     * @return reference object
     */
    private ObjReference createReference() {
        Object reference = refObjectHelper();
        ObjReference newObject = new ObjReference(reference);
        return newObject;
    }

    /**
     * Create a primitive Array object defined by user
     * @return primitive Array object
     */
    private ObjPrimitiveArray createPrimitiveArray() {
        int arraySize = getIntInput(Integer.MIN_VALUE, Integer.MAX_VALUE);
        int[] intArray = new int[arraySize];

        for (int i = 1; i < arraySize + 1; i++)
            intArray[i-1] = getIntInput(Integer.MIN_VALUE, Integer.MAX_VALUE);

        ObjPrimitiveArray newObject = new ObjPrimitiveArray(intArray);
        return newObject;
    }

    /**
     * Create a reference Array object defined by user
     * @return reference Array object
     */
    private ObjReferenceArray createReferenceArray() {
        int arraySize = getIntInput(Integer.MIN_VALUE, Integer.MAX_VALUE);
        Object[] refArray = new Object[arraySize];

        for (int i = 1; i < arraySize + 1; i++)
            refArray[i-1] = refObjectHelper();

        ObjReferenceArray newObject = new ObjReferenceArray(refArray);
        return newObject;
    }

    /**
     * Create an ArrayList Collections object defined by user
     * @return ArrayList Collections object
     */
    private ObjCollections createCollections() {
        int arrayListSize = getIntInput(Integer.MIN_VALUE, Integer.MAX_VALUE);
        ArrayList<Integer> refArrayList = new ArrayList<>();

        for (int i = 1; i < arrayListSize + 1; i++)
            refArrayList.add(getIntInput(Integer.MIN_VALUE, Integer.MAX_VALUE));

        ObjCollections newObject = new ObjCollections(refArrayList);
        return newObject;
    }

    /**
     * Getter method for createdObjects ArrayList used in Controller class
     * @return An ArrayList containing all created objects
     */
    ArrayList<Object> getCreatedObjects() {
        return createdObjects;
    }
}