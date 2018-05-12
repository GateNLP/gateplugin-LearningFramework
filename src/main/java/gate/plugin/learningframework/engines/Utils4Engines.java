package gate.plugin.learningframework.engines;

import gate.util.GateRuntimeException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

/**
 * Class for factoring out static methods that do not fit into the Engine
 * hierarchy.
 *
 * @author Johann Petrak <johann.petrak@gmail.com>
 */
public class Utils4Engines {

  public static void copyWrapper(String wrapperName, File targetDirectory) {
    // First of all, check if the target directory already has the directory expected.
    // If ths is the case just silently quit.
    if (new File(targetDirectory, wrapperName).exists()) {
      return;
    }
    // Otherwise go on and actually try to copy everything ...
    copyResources(targetDirectory, "/resources/wrappers/"+wrapperName);
  }

  public static void copyResources(File targetDir, String root) {

    // TODO: check targetDir is a dir?
    //if (!hasResources())
    //  throw new UnsupportedOperationException(
    //      "this plugin doesn't have any resources you can copy as you would know had you called hasResources first :P");
    URL artifactURL = Utils4Engines.class.getResource("/creole.xml");
    try {
      artifactURL = new URL(artifactURL, ".");
    } catch (MalformedURLException ex) {
      throw new GateRuntimeException("Could not get jar URL");
    }
    try (
            FileSystem zipFs
            = FileSystems.newFileSystem(artifactURL.toURI(), new HashMap<>());) {

      Path target = Paths.get(targetDir.toURI());
      System.err.println("DEBUG: target=" + target);
      Path pathInZip = zipFs.getPath(root);
      System.err.println("DEBUG: pathInZip=" + pathInZip);
      if (!Files.isDirectory(pathInZip)) {
        throw new GateRuntimeException("ODD: not a directory " + pathInZip);
      }
      Path parentPathInZip = pathInZip.getParent();
      Files.walkFileTree(pathInZip, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path filePath,
                BasicFileAttributes attrs) throws IOException {
          // Make sure that we conserve the hierachy of files and folders
          // inside the zip
          System.err.println("DEBUG filePath=" + filePath);
          Path relativePathInZip = parentPathInZip.relativize(filePath);
          Path targetPath = target.resolve(relativePathInZip.toString());
          System.err.println("DEBUG: WARNING create directories" + targetPath.getParent());
          Files.createDirectories(targetPath.getParent());

          // And extract the file
          System.err.println("DEBUG: WARNING copy from " + filePath + " to " + targetPath);
          Files.copy(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
          // if the file ends in .sh or .cmd make it executable
          String tp = targetPath.toString();
          if(tp.endsWith(".sh") || tp.endsWith(".cmd")) {
            targetPath.toFile().setExecutable(true);
          }

          return FileVisitResult.CONTINUE;
        }
      });
    } catch (Exception ex) {
      throw new GateRuntimeException("Error trying to copy the resources", ex);
    }
  }

}
