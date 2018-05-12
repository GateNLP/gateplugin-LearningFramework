
package gate.lib.interaction.process;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Johann Petrak
 */
public class EchoObjectStream {
  // Simple process which will run forever and echo whatever it receives on
  // standard input as an object stream to standard output as an object output
  // stream (the objects do get de-serialized and re-serialized)
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    ObjectOutputStream oos = new ObjectOutputStream(System.out);
    // Send the hello object
    oos.writeObject("Hello");
    oos.flush();
    // this will wait for the hello object header of the other side
    ObjectInputStream ois = new ObjectInputStream(System.in);
    ois.readObject();
    System.err.println("EOS: BEfore loop");
    while(true) {
      System.err.println("EchoObjectStream: before reading");
      Object obj = ois.readObject();
      System.err.println("EchoObjectStream: got an object, class is "+obj.getClass());
      // we terminate if we get the string STOP
      if(obj.equals("STOP")) {
        System.err.println("Received the stop signal");
        break;
      }
      oos.writeObject(obj);
      oos.flush();
    }
    System.err.println("Terminating echo");
  }
}
