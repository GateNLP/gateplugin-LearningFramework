package gate.lib.interaction.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Minimalist class for exchanging objects through obejct streams  with a command line process.
 * 
 * NOTE: at the moment, reading from the process can block forever, there is
 * no timeout! 
 */
public class Process4ObjectStream extends ProcessBase
{

  private static final Logger LOGGER = Logger.getLogger(Process4ObjectStream.class.getName());
  
  private final Object synchronizer = new Object();

  ObjectInputStream ois;
  ObjectOutputStream oos;
  
  private Process4ObjectStream() {}
  
  public static Process4ObjectStream create(File workingDirectory, Map<String,String> env,  List<String> command) {
    Process4ObjectStream ret = new Process4ObjectStream();
    if(workingDirectory != null) ret.workingDir = workingDirectory;
    if(env != null) ret.envvars.putAll(env);
    ret.command.addAll(command);
    ret.updateCommand4OS(ret.command);
    ret.ensureProcess();
    return ret;
  }
  public static Process4ObjectStream create(File workingDirectory, Map<String,String> env,  String... command) {
    Process4ObjectStream ret = new Process4ObjectStream();
    if(workingDirectory != null) ret.workingDir = workingDirectory;
    if(env != null) ret.envvars.putAll(env);
    ret.command.addAll(Arrays.asList(command));    
    ret.updateCommand4OS(ret.command);
    ret.ensureProcess(); 
    return ret;
  }
  
  
  public Object readObject() {
    try {
      synchronized(synchronizer) {
        return ois.readObject();
      }
    } catch (Exception ex) {
      throw new RuntimeException("Problem when reading from object stream",ex);
    }
  }
  
  
  /**
   * Send a message to the process.
   * @param object 
   */
  public void writeObject(Object object) {
    try {
      synchronized(synchronizer) {
        oos.writeObject(object);
        oos.flush();
      }
    } catch (IOException ex) {
      throw new RuntimeException("Problem when writing to object stream",ex);
    }
  }
  
  /**
   * Check if the external process is running.
   * @return 
   */
  public boolean isAlive() {
    return !need2start();
  }
  
  ///////////////////////////////////////////////////////////////////
  
  

  @Override
  protected void setupInteraction() {
    // NOTE: creating an object input stream will block until the header of 
    // the first object is received so we have to have the convention that
    // both sides need to first send some Hello object.
    // This should be a string that indicates the sending component and version.
    try {
      oos = new ObjectOutputStream(process.getOutputStream());
      oos.writeObject("Hello from Process4ObjectStream v1.0");
    } catch (IOException ex) {
      throw new RuntimeException("Could not create object output stream",ex);
    }
    try {
      InputStream pis = process.getInputStream();
      ois = new ObjectInputStream(pis);
      try {
        Object ret = ois.readObject();
        System.err.println("Got hello from process: "+ret);
      } catch (ClassNotFoundException ex) {
        throw new RuntimeException("Could not receive the other side's hello object");
      }
    } catch (IOException ex) {
      throw new RuntimeException("Could not create object input stream",ex);      
    }
  }

  @Override
  protected void stopInteraction() {
    try {
      ois.close();
    } catch (IOException ex) {
      //ignore
    }
    try {
      oos.close();
    } catch (IOException ex) {
      //ignore
    }
  }
  
  ////////////////////////////////////////////////
  
  public static void main(String[] args) {
    System.err.println("Running the Process4ObjectStream class");
    Process4ObjectStream pr = 
            Process4ObjectStream.create(new File("."),null,
                    "java -cp target/interaction-1.0-SNAPSHOT.jar gate.lib.interaction.process.EchoObjectStream");
    String someString = "this is some string";
    System.err.println("Right before writing to process");
    pr.writeObject(someString);
    System.err.println("Right before reading from process");
    Object obj = pr.readObject();
    System.err.println("Got the object back: "+obj);
    System.err.println("Writing another one (1234)");
    pr.writeObject("1234");
    System.err.println("Right before reading again");
    obj = pr.readObject();
    System.err.println("Got "+obj);
    System.err.println("Shutting down");
    pr.stop();
  }
  
}
