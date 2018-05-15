/*
 * Copyright (c) 2015-2016 The University Of Sheffield.
 *
 * This file is part of gateplugin-LearningFramework 
 * (see https://github.com/GateNLP/gateplugin-LearningFramework).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package gate.plugin.learningframework;

import gate.util.GateRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author Johann Petrak
 */
public class LFUtils {

  // NOTE: we use an AugmentableFeatureVector to represent the growing featureName vector inputAS we
  // build it.
  // The Mallet documentation is close to non-existing ATM, so here is what the methods we use do:
  // afv.add("x",val) adds val to whatever the current value for "x" is oder adds the featureName, if
  //   the Alphabet can grow. If the Alphabet cannot grow, the method does nothing.
  //   UPDATE: this does not work! if one tries to do that, the indices get messed up and
  //   the fv will throw an ArrayIndexOutOfBoundsException!!!!
  //   So we have always to explicitly check if the featureName is in the alphabet!!!
  //   UPDATE: Mallet uses assert for checking things like this, so if assertsions are not enable,
  //   no exception is thrown until it is too late!
  // afv.value("x") retrieves the value if "x" is in the vector, otherwise an exception is thrown,
  //   even if "x" is in the alphabet.
  // afv.contains("x") is true if the featureName vector contains a value for "x" (which implies it must
  //   be in the alphabet)
  // afv.getAlphabet().contains("x") is true if "x" is in the alphabet.
  /**
   * Convert the object to a double or, if it is null or not convertible, use the orElse value.
   *
   * @param any what to convert 
   * @param orElse default value
   * @return converted value
   */
  public static double anyToDoubleOrElse(Object any, double orElse) {
    if (any == null) {
      return orElse;
    }
    if (any instanceof Double) {
      return (Double)any;
    } else if (any instanceof Number) {
      return ((Number) any).doubleValue();
    } else if (any instanceof String) {
      Double tmp = null;
      try {
        tmp = Double.parseDouble((String) any);
      } catch (Exception ex) {
        // do not do anything, we just are happy to find tmp=null in this case
      }
      if (tmp == null) {
        return orElse;
      } else {
        return tmp;
      }
    } else {
      return orElse;
    }
  }

  /**
   * Convert any object to a String representation
   * @param any the object
   * @param orElse default value
   * @return string representation
   */
  public static String anyToStringOrElse(Object any, String orElse) {
    if (any == null) {
      return orElse;
    }
    return any.toString();
  }

  /** 
   * If it is not a boolean, then if it is a string, anything that is not
   * "true" or "True" is false, if it is a number, then anything that
   * is not 0.0 is true.
   * 
   * @param any any object
   * @param orElse default value
   * @return  the Boolean 
   */
  public static Boolean anyToBooleanOrElse(Object any, Boolean orElse) {
    if (any == null) {
      return orElse;
    }
    if (any instanceof Boolean) {
      return (Boolean)any;
    } else if (any instanceof String) {
      return Boolean.parseBoolean((String)any);
    } else if (any instanceof Number) {
      Double val = LFUtils.anyToDoubleOrElse(any, 0.0);
      return !(val == 0.0);
    } else {
      return Boolean.parseBoolean(any.toString());      
    }
  }
  
  /**
   * Create a URL from the String.
   * If the String does not have a protocol/scheme, file:// is assumed and prepended.
   * @param str the string representation
   * @return the URL
   */
  public static URL newURL(String str) {
    try {
      if(new URI(str).getScheme() == null) {
        str = "file://"+str;
      }
      return new URL(str);
    } catch (Exception ex) {
      throw new GateRuntimeException("Cannot create URL from string "+str,ex);
    }
  }
  
  /**
   * Equivalent of creating a new file from a directory file and name string.
   * 
   * This tries to roughly provide the equivalent of "new File(dirFile, nameString)"
   * for a URL and a name String. The catch is that in order for this to work,
   * the given dir URL must end in a slash. This method assumes that the first
   * URL always refers to a directory and therefore appends a slash if necessary.
   * <p>
   * Note: if the dirURL contains a query and/or a fragment, those parts are 
   * lost in the resulting URL.
   * @param dirURL directory URL
   * @param fileName  file name
   * @return the URL
   */
  public static URL newURL(URL dirURL, String fileName) {
    URI dirURI;
    try {
      dirURI = dirURL.toURI();
    } catch (URISyntaxException ex) {
      throw new GateRuntimeException("Cannot convert URL to URI: "+dirURL);
    }
    // NOTE: adding the slash should not really be necessary if we really
    // always get a URL with a trailing slash if the URL refers to something
    // that should be a directory. However, this is not always the case, 
    // e.g. a directory URL stored in a xgapp file can sometimes miss the
    // trailing slash.
    // /*
    String path = dirURI.getPath();
    if(!path.endsWith("/")) path = path+"/";
    try {
      dirURI = new URI(
              dirURI.getScheme(),
              dirURI.getUserInfo(),
              dirURI.getHost(),
              dirURI.getPort(),
              path,
              dirURI.getQuery(),
              dirURI.getFragment());
    } catch (URISyntaxException ex) {
      throw new GateRuntimeException("Cannot conver URL to URI: "+dirURL);
    }
    // */
    try {
      URL ret = new URL(dirURI.toURL(),fileName);
      return ret;
    } catch (MalformedURLException ex) {
      throw new GateRuntimeException("Could not create URL for "+dirURL+"fileName");
    }    
  }
  
  /**
   * Return the last path component of a hierarchical path of URL.
   * @param url the URL
   * @return  the string representation of the last path component
   */
  public static String getName(URL url) {
    URI uri;
    try {
      uri = url.toURI();
    } catch (URISyntaxException ex) {
      throw new GateRuntimeException("Cannot convert URL to URI: "+url);
    }
    URL parentURL;
    try {
      parentURL = new URL(url,".");
    } catch (MalformedURLException ex) {
      throw new GateRuntimeException("Cannot find parent URL for URL "+url);
    }
    URI parentURI = null;
    try {
      parentURI = parentURL.toURI();
    } catch (URISyntaxException ex) {
      throw new GateRuntimeException("Cannot convert URL to URI: "+parentURL);
    }
    URI relative = parentURI.relativize(uri);
    return relative.toString();
  }
  
  /**
   * Return the parent path for a URL.
   * This is the path, with the last component of the path removed, i.e.
   * with that part removed that is returned by the getName() method.
   * 
   * @param url the url
   * @return  parent path url
   */
  public static URL getParentURL(URL url) {
    URL ret;
    try {
      ret = new URL(url,".");
    } catch (MalformedURLException ex) {
      throw new GateRuntimeException("Cannot convert URL to URI: "+url);
    }
    return ret;
  }
  
  /**
   * Return the parent path for a URL.
   * This is the path, with the last component of the path removed, i.e.
   * with that part removed that is returned by the getName() method.
   * 
   * @param url the url
   * @return  the parent path 
   */
  public static String getParent(URL url) {
    return getParentURL(url).toString();
  }
  
  /** 
   * Returns true if the URL can be opened for reading.
   * 
   * @param url the url
   * @return  true if can be opened
   */
  public static boolean exists(URL url) {
    boolean ret = true;
    try (InputStream is = url.openStream()) {
      // do nothing, we only want to check the opening
    } catch (IOException ex) {
      ret = false;
    }
    return ret;
  }
  
  /**
   * Return truen if the URL is a file URL.
   * @param url the url 
   * @return true if a file
   */
  public static boolean isFile(URL url) {
    return "file".equals(url.getProtocol());
  }
  
  
  
  
  
  /**
   * Return URL for the file inside the dir URL.
   * This makes sure that the dirURL ends with a slash before creating the final URL,
   * since the constructor new URL(url1, "file") will remove the last part of the 
   * path of url1 if it does not end in a slash.
   * @param dirURL URL
   * @param fileName filename
   * @return url
   */
  public static URL newURLOld(URL dirURL, String fileName) {
    String s = dirURL.toExternalForm();
    if(!s.endsWith("/")) {
      try {
        dirURL = new URL(s+"/");
      } catch (MalformedURLException ex) {
        throw new GateRuntimeException("Could not create URL for "+s+"/",ex);
      }
    }
    try {
      URL ret = new URL(dirURL,fileName);
      return ret;
    } catch (MalformedURLException ex) {
      throw new GateRuntimeException("Could not create URL for "+dirURL+"fileName");
    }
  }

}
