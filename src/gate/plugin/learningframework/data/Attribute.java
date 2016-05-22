package gate.plugin.learningframework.data;

import cc.mallet.types.Alphabet;
import gate.plugin.learningframework.features.CodeAs;
import gate.plugin.learningframework.features.Datatype;
import java.util.Objects;
import org.apache.log4j.Logger;


/**
 * Class that describes a single attribute/feature.
 * @author Johann Petrak
 */
public class Attribute {
  private static final Logger LOG = Logger.getLogger(Attribute.class.getName());
  /**
   * Name of the attribute
   */
  public String name;

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 41 * hash + Objects.hashCode(this.name);
    hash = 41 * hash + this.index;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Attribute other = (Attribute) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (this.index != other.index) {
      return false;
    }
    return true;
  }

  public Attribute(String name, int index, Datatype datatype, CodeAs codeAs, Alphabet alphabet) {
    this.name = name;
    this.index = index;
    this.datatype = datatype;
    this.codeAs = codeAs;
    this.alphabet = alphabet;
  }
  /** 
   * Index/location of the attribute in a (sparse) feature vector.
   */
  public int index; 
  /**
   * The type of the values of the attribute/feature
   */
  public Datatype datatype;
  /**
   * If the attribute/feature is nominal, how the value is coded 
   */
  public CodeAs codeAs;

  @Override
  public String toString() {
    return "Attribute{" + "name=" + name + ", index=" + index + ", datatype=" + datatype + ", codeAs=" + codeAs + ", alphabet=" + alphabet + '}';
  }
  /**
   * Dictionary of possible values and their codes if the attribute/feature 
   * is nominal and coded as number
   */
  public Alphabet alphabet;
}
