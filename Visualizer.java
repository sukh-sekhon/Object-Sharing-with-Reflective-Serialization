import java.util.*;
import  java.lang.reflect.*;

/**
 * Inspects objects and produces a visualization in the terminal
 * Traverses inheritance hierarchies for full inspection
 * Capable of displaying Arrays and ArrayLists
 *
 * Displays:
 *      - Declaring class, Superclass, and Interface(s)
 *      - Method(s)
 *          - Exception(s)
 *          - Parameter(s)
 *          - Modifier(s)
 *      - Constructor(s)
 *          - Parameter(s)
 *          - Modifier(s)
 *      - Fields(s)
 *          - Type
 *          - Modifier(s)
 *          - Value
 *
 * @author Sukhjot Sekhon
 */
public class Visualizer {
    ArrayList<Object[]> inspected = new ArrayList<>(); // Contains all Object-Class pairs that have been inspected

    /**
     * Run a full inspection on an object
     * @param obj object to inspect
     * @param recursive Determines if fields will be fully inspected recursively
     */
    public void inspect (Object obj, boolean recursive) {
        Class objClass = obj.getClass();
        if (checkDuplicate(obj, objClass)) return; // Return if the Object-Class pair has been inspected

        System.out.println("******************************************************\n" +
                "*              RUNNING FULL INSPECTION               *\n" +
                "******************************************************");
        inspectClass(obj, objClass); // Includes: declaring class, superclass, and interface(s)
        runInspections(obj, objClass, recursive); // Includes: constructors, methods, and fields
        traverseHierarchy(obj, objClass, recursive); // Traverses superclasses and superinterfaces
    }

    /**
     * Inspects the constructors, methods, and fields of an object
     * Useful when inspecting superclasses and superinterfaces while traversing the hierarchy
     *
     * @param obj object to inspect
     * @param objClass Class of object
     * @param recursive Determines if fields will be fully inspected recursively
     */
    private void runInspections (Object obj, Class objClass, boolean recursive) {
        if (checkDuplicate(obj, objClass)) return; // Return if the Object-Class pair has been inspected
        Object[] objectClassPair = {obj, objClass};
        inspected.add(objectClassPair); // Add the Object-Class pair to inspected

        inspectConstructors(objClass);
        inspectMethods(objClass);
        inspectFields(obj, objClass, recursive);
    }

    /**
     * Prints basic inspection information for a class/interface
     * Includes: declaring class, superclass, and interface(s)
     *
     * @param obj object to inspect
     * @param objClass Class of object
     */
    private void inspectClass (Object obj, Class objClass) {
        System.out.println("\n-|| Classes And Interfaces ||-------------------------");
        System.out.println("Declaring Class:\t\t" + objClass.getSimpleName()); // Print the declaring class
        if (objClass.isArray()) inspectArrayClass(obj, objClass); // Use the inspectArrayClass method if type is Array
        System.out.println("Superclass:\t\t\t\t" + objClass.getSuperclass().getSimpleName()); // Print the superclass

        Class[] interfaces = objClass.getInterfaces(); // Interface(s) for class
        // Print interface(s) using the formatPrinter helper method
        if (interfaces.length > 0)
            formatPrinter(interfaces, "\n\t\t\t\t\t\t","Interface(s):\t\t\t", "");
    }

    /**
     * Prints information for an array class object
     * Includes: component type, length, and contents
     *
     * @param obj object to inspect
     * @param objClass Class of object
     */
    private void inspectArrayClass (Object obj, Class objClass) {
        System.out.print("Array Type --->" +
                "\t\t\tComponent Type:\t\t" + objClass.getComponentType().getTypeName() + // Print the component type
                "\n\t\t\t\t\t\tLength:\t\t\t\t" + Array.getLength(obj) + // Print the array length
                "\n\t\t\t\t\t\tContents:\t\t\t");
        // Print the array contents using the formatPrinter helper method
        try { formatPrinter(obj, ", ","[", "]");
        } catch (NullPointerException e) {
            System.out.println("]");
        }
    }

    /**
     * Inspects a class's declared constructors
     * Includes: constructor name(s), parameter type(s), and modifier(s)
     *
     * @param objClass Class of object
     */
    private void inspectConstructors (Class objClass) {
        Constructor[] constructors = objClass.getDeclaredConstructors(); // Constructors within the class
        if (constructors.length == 0) return; // Return if no constructors exist
        System.out.println("\n-|| Constructors ||-----------------------------------");
        // Iterate for each constructor
        for (int i = 0; i < constructors.length; i++) {
            System.out.println("Constructor #" + i);
            Class[] parameters = constructors[i].getParameterTypes(); // Parameter(s) for the constructor
            // Print parameter(s) using the formatPrinter helper method
            if (parameters.length > 0)
                formatPrinter(parameters, ", ", "\tParameter Type(s):\t", "");
            // Print the modifier(s)
            System.out.println("\tModifier(s):\t\t" + Modifier.toString(constructors[i].getModifiers()));
        }
    }

    /**
     * Inspects a class's declared methods
     * Includes: method name(s), exception(s) thrown, parameter type(s), and the return type
     *
     * @param objClass Class of object
     */
    private void inspectMethods (Class objClass) {
        Method[] methods = objClass.getDeclaredMethods(); // Methods within the class
        if (methods.length == 0) return; // Return if no methods exist
        System.out.println("\n-|| Methods ||----------------------------------------");
        // Iterate for each method
        for (int i = 0; i < methods.length; i++) {
            Class[] exceptions = methods[i].getExceptionTypes(); // Exception(s) for a method
            Class[] parameters = methods[i].getParameterTypes(); // Parameter(s) for a method

            System.out.println(methods[i].getName()); // Print the method's name
            // Print exception(s) using the formatPrinter helper method
            if (exceptions.length > 0)
                formatPrinter(methods[i].getExceptionTypes(), "\n\t\t\t\t\t\t","\tException(s):\t\t", "");
            // Print parameter(s) using the formatPrinter helper method
            if (parameters.length > 0)
                formatPrinter(methods[i].getParameterTypes(), ", ","\tParameter Type(s):\t", "");
            // Print the method's return type(s)
            System.out.println("\tReturn Type:\t\t" + methods[i].getReturnType().getSimpleName());
            // Print the method's modifier(s)
            System.out.println("\tModifier(s):\t\t" + Modifier.toString(methods[i].getModifiers()));
        }
    }

    /**
     * Inspects a class's declared fields (possibly recursively)
     * Includes: field name(s), type, modifier(s), and value
     *
     * @param obj object to inspect
     * @param objClass Class of object
     * @param recursive Determines if fields will be fully inspected recursively
     */
    private void inspectFields (Object obj, Class objClass, boolean recursive) {
        Field[] fields = objClass.getDeclaredFields(); // Fields within the class
        if (fields.length == 0) return; // Return if no fields exist

        ArrayList<Field> objectsToInspect = new ArrayList<>(); // Objects to inspect if using recursion
        System.out.println("\n-|| Fields ||-----------------------------------------");
        try {
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true); // Set all fields to accessible
                // Print the name of a field
                System.out.println(fields[i].getName());
                String type = fields[i].getType().getSimpleName();
                // Print the type of a field
                System.out.println("\tType:\t\t\t\t" + type);
                // Print the modifier(s) of a field
                if (fields[i].getModifiers() != 0)
                    System.out.println("\tModifier(s):\t\t" + Modifier.toString(fields[i].getModifiers()));
                // Print the value of a field if an Array using the formatPrinter helper method
                if (fields[i].getType().isArray())
                    formatPrinter(fields[i].get(obj), ", ", "\tValue:\t\t\t\t[", "]");
                // Print the value of a field if not an Array
                else
                    System.out.println("\tValue:\t\t\t\t" + fields[i].get(obj));
                // Adds new field objects to ArrayList if recursive, non-primitive, and non-null
                if (recursive && !fields[i].getType().isPrimitive() && fields[i].get(obj)!=null)
                    objectsToInspect.add(fields[i]);
            }

            // Change method of adding objects
            for (Field fieldToInspect : objectsToInspect) {
                System.out.println("\n\n******************************************************\n" +
                        "* Recursively Inspecting Field: " + fieldToInspect.getName() + " (from " + objClass.getSimpleName() + ")");
                inspect(fieldToInspect.get(obj), recursive);
            }
        } catch ( NullPointerException nullPtrEx) {
            System.out.println("]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursive method that traverses the inheritance hierarchy for superclasses and superinterfaces
     * Runs a full inspection on all superclasses and superinterfaces encountered
     *
     * @param obj object to inspect
     * @param objClass Class of object
     * @param recursive Determines if fields will be fully inspected recursively
     */
    private void traverseHierarchy (Object obj, Class objClass, boolean recursive) {
        // Handles interface traversal
        if (objClass.getInterfaces().length > 0) {
            // ArrayList of interfaces to inspect
            ArrayList<Class> interfacesToInspect = new ArrayList<>(Arrays.asList(objClass.getInterfaces()));
            // Loop is dynamic as new interfaces are added to the ArrayList
            for (int i = 0; i < interfacesToInspect.size(); i++) {
                if (interfacesToInspect.get(i) != null) {
                    // Adds all interfaces from the current interfaces to interfacesToInspect
                    interfacesToInspect.addAll(Arrays.asList(interfacesToInspect.get(i).getInterfaces()));
                    // Continue if the Object-Class pair has been inspected
                    if (checkDuplicate(obj, objClass)) continue;
                    System.out.println("\n\n******************************************************\n" +
                            "* Inspecting Interface: " + interfacesToInspect.get(i).getSimpleName() + "\n" +
                            "******************************************************");
                    // Runs full inspection on the interface
                    runInspections(obj, interfacesToInspect.get(i), recursive);
                }
            }
        }
        // Handles class traversal and is the recursive component of the method
        if (objClass.getSuperclass() != null) {
            Class superclass = objClass.getSuperclass(); // Superclass of the current class
            if (checkDuplicate(obj, objClass)) return; // Return if the Object-Class pair has been inspected
            System.out.println("\n\n******************************************************\n" +
                    "* Inspecting Superclass: " + superclass.getSimpleName() + " (of " + objClass.getSimpleName() + ")\n" +
                    "******************************************************");
            // Runs full inspection on the superinterface
            runInspections(obj, objClass, recursive);
            // Calls this method recursively with the superclass
            traverseHierarchy(obj, superclass, recursive);
        }
    }

    /**
     * Checks if an Object-Class pair has been inspected
     * Returns true if already in the inspected ArrayList and false otherwise
     *
     * @param obj object to inspect
     * @param objClass Class of object
     */
    private boolean checkDuplicate (Object obj, Class objClass) {
        Object[] objectClassPair = {obj, objClass};
        if (inspected.contains(objectClassPair)) {
            System.out.println("This has already been inspected!");
            return true;
        }   return false;
    }

    /**
     * Helper method to print array contents or multiple interfaces, parameters, exceptions, or fields
     */
    private void formatPrinter (Object obj, String separation, String prefix, String suffix) {
        boolean isType = obj instanceof Class[]; // If Class[], then interfaces, parameters, exceptions, or fields
        System.out.print(prefix);
        for (int i = 0; i < Array.getLength(obj); i++) {
            // For printing multiple interfaces, parameters, exceptions, or fields
            if (isType) {
                Class classType = (Class) Array.get(obj, i);
                System.out.print(classType.getSimpleName());
            }
            // For printing array contents
            else System.out.print(Array.get(obj, i).toString());
            if (i != Array.getLength(obj) - 1) System.out.print(separation);
        }
        System.out.println(suffix);
    }
}