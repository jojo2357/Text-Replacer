import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Test{
  public static void main(String[] args)
  {
    String stringToSearch = "What is the point of regex\n";

    Pattern p = Pattern.compile(args[0]);   // the pattern to search for
    Matcher m = p.matcher(stringToSearch);
    
    // now try to find at least one match
    if (m.find())
      System.out.println("Found a match");
    else
      System.out.println("Did not find a match");
  }
}