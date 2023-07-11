package dungeonmania;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
 * Using Serialization for Persistence
 */
public class SerializationUtils {
 
    public static void serialization(Object obj, String fileName) throws IOException {
        FileOutputStream file = new FileOutputStream(fileName);
        BufferedOutputStream buffer = new BufferedOutputStream(file);
        ObjectOutputStream output = new ObjectOutputStream(buffer);
        output.writeObject(obj);
        output.close();
    }

    public static Object deserialization(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream(fileName);
        BufferedInputStream buffer = new BufferedInputStream(file);
        ObjectInputStream output = new ObjectInputStream(buffer);
        Object obj = output.readObject();
        output.close();
        return obj;
    }
}
