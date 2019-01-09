package gate.lib.interaction.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Minimalist class for running an external command. 
 * 
 * This simple passes on
 * standard output and error to our own process and closes standard input
 * as soon as possible.
 * The read and write methods for this class do nothing.
 * 
 * 
 * 
 */
public class ProcessSimple extends ProcessBase
{

  private static final Logger LOGGER = Logger.getLogger(ProcessSimple.class.getName());
  
  private final Object synchronizer = new Object();

  private ProcessSimple() {} 
  
  public static ProcessSimple create(File workingDirectory, Map<String,String> env,  List<String> command) {
    ProcessSimple ret = new ProcessSimple();
    if(workingDirectory != null) ret.workingDir = workingDirectory;
    if(env != null) ret.envvars.putAll(env);
    ret.command.addAll(command);
    ret.updateCommand4OS(ret.command);
    ret.ensureProcess();
    return ret;
  }
  public static ProcessSimple create(File workingDirectory, Map<String,String> env,  String... command) {
    ProcessSimple ret = new ProcessSimple();
    if(workingDirectory != null) ret.workingDir = workingDirectory;
    if(env != null) ret.envvars.putAll(env);
    ret.command.addAll(Arrays.asList(command));    
    ret.updateCommand4OS(ret.command);
    ret.ensureProcess(); 
    return ret;
  }
  
  

  /**
   * This always returns null for this class.
   * @return null
   */
  @Override
  public Object readObject() {
    return null;
  }
  
  
  /**
   * Does nothing.
   * @param object to send
   */
  @Override
  public void writeObject(Object object) {
  }
  
  /**
   * Check if the external process is running.
   * @return  flag
   */
  @Override
  public boolean isAlive() {
    return !need2start();
  }
  
  ///////////////////////////////////////////////////////////////////

    /**
     * Copy stream
     * @param processStream to copy
     * @param ourStream where to copy
     */
  
  protected void copyStream(final InputStream processStream, final OutputStream ourStream) {
    Thread copyThread = new Thread() {
      @Override
      public void run() {
        try {
          IOUtils.copy(processStream, ourStream);
        } catch (IOException ex) {
          LOGGER.error("Could not copy stream", ex);
        }
      }
    };
    copyThread.setDaemon(true);
    copyThread.start();
  }
  
  

  @Override
  protected void setupInteraction() {
    copyStream(process.getInputStream(),System.out);
    try {
      process.getOutputStream().close();
    } catch (IOException ex) {
      //
    }
    logStream(process.getErrorStream(), System.out);
  }

  @Override
  protected void stopInteraction() {
  }
  
  ////////////////////////////////////////////////
  
  public static void main(String[] args) {
    // TODO
  }
  
}
