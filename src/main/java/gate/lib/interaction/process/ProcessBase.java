package gate.lib.interaction.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Minimalist base class for exchanging objects with a command line process.
 * The various subclasses of this implement more specific ways of how 
 * the data actually gets exchanged with the process.
 */
public abstract class ProcessBase 
{

  private static final Logger LOGGER = Logger.getLogger(ProcessBase.class.getName());
  
  protected List<String> command = new ArrayList<String>();
  protected ProcessBuilder builder = null;
  protected Process process = null;
  protected File workingDir = new File(".");
  protected Thread loggerThread;
  protected Map<String,String> envvars = new HashMap<String,String>();
    
  /**
   * Make sure the process is running.
   * Returns true if the process was started freshly, false if it was 
   * already running.
   * 
   * @return
   * @throws IOException 
   */
  public boolean ensureProcess() {
    if(need2start()) {
      System.err.println("ProcessBase: running command:");
      for(int i=0; i<command.size();i++) { System.err.println(i+": "+command.get(i)); }
      builder = new ProcessBuilder(command);
      builder.directory(workingDir);
      Map<String,String> env = builder.environment();
      env.putAll(envvars);
      try {
        process = builder.start();
      } catch (IOException ex) {
        throw new RuntimeException("Could not start the process "+command,ex);
      }
      // copy the standard output of the process to the logger
      logStream(process.getErrorStream(),Level.ERROR);
      // do the class-specific setup of how to interact
      setupInteraction();
      return true;
    } else {
      return false;
    }
  }
  
  
  /**
   * Read an object from the process.
   * This will block until the message is available and may currently 
   * block forever!
   * @return
   * @throws IOException 
   */
  public abstract Object readObject();
  
  
  /**
   * Send an object to the process.
   * @param object 
   */
  public abstract void writeObject(Object message);
  
  /**
   * Check if the external process is running.
   * @return 
   */
  public boolean isAlive() {
    return !need2start();
  }

  public int waitFor() {
    int exitCode = 0;
    try {
      // wait until the process finishes
      exitCode = process.waitFor();
    } catch (InterruptedException ex) {
      exitCode = Integer.MIN_VALUE;
    }
    return exitCode;
  }
  
  /**
   * Attempt to stop the external process.
   * We try all we can to destroy the process, but
   * there is no guarantee that the process will actually be stopped by this.
   */
  public void stop() {
    // wait a little so any pending standard error can still be processed
    try {
      Thread.sleep(500);
    } catch (InterruptedException ex) {
      //
    }    
    process.destroy(); 
    if(process.isAlive()) {
      process.destroyForcibly();
    }    
    // loggerThread.stop();
  }
  
  
  ///////////////////////////////////////////////////////////////////
  
  /**
   * This handles the exact way of how the communication is set up.
   */
  protected abstract void setupInteraction();
  
  protected abstract void stopInteraction();
  
  
  /**
   * Copy the stream output to the logger using the given logging level.
   * @param stream 
   */
  protected void logStream(final InputStream stream, Level level) {
    // Not sure how to do this yet, probably a thread that copies the 
    // stream to another stream which is our own implementation that
    // actually writes to the logger
    // For now we do nothing at all
    loggerThread = new Thread() {
      public void run() {
        try {
          IOUtils.copy(stream, System.err);
        } catch (IOException ex) {
          LOGGER.error("Could not copy standard error from the process to our own standard error", ex);
        }
      }
    };
    loggerThread.setDaemon(true);
    loggerThread.start();
  }
  
  protected boolean need2start() {
    boolean ret = false;
    if(builder==null) {
      ret = true;
    } else if(process==null) {
      ret = true;
    } else if(!process.isAlive()) {
      System.err.println("Apparently process is alive");
      ret = true;
    } else {
      boolean stillRunning = false;
      try {
        int code = process.exitValue();
        System.err.println("Exit value is "+code);
      } catch(IllegalThreadStateException ex) {
        //System.err.println("Got illegalthreadstate");
        stillRunning = true;
      }
      if(!stillRunning) ret = true;
    }
    return ret;
  }
  
  /**
   * Does an in-place update of the command to conform to what the OS expects.
   * This is mainly about dealing with commands and arguments that contain spaces for now.
   * On a Windows-like system, everything that contains spaces is surrounded with double quotes.
   * On a Linux-like system, spaces are escaped with a backslash.
   * @param command 
   */
  protected void updateCommand4OS(List<String> command) {
    boolean linuxLike = System.getProperty("file.separator").equals("/");
    boolean windowsLike = System.getProperty("file.separator").equals("\\");
    for(int i=0; i<command.size(); i++) {
      String arg = command.get(i);
      if(arg.contains(" ")) {
        if(linuxLike) {
          // NOTE: although it looked as if we can only get it to work with escaping, it
          // turns out it actually works without the escaping. Not sure why it did not
          // work when I originally tested this. 
          //command.set(i, arg.replaceAll(" ", "\\ "));
        } else if(windowsLike) {
          command.set(i, "\""+arg+"\"");          
        }
      }
    }
    
  }
  
  
}
