import java.net.URL;
import java.util.Scanner;
import java.util.Stack;


public class HtmlAnalyzer {
  public static void main(String[] args) {
    if (args.length == 0) return;
    
        String urlString = args [0];
        String deepestText = null;
        try (Scanner sc = new Scanner(new URL(args[0]).openStream())) {
        Stack<String> stack = new Stack<>();
        
        int maxDepth = -1;

            while (sc.hasNextLine()){
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("</")) {
                String tag = line.substring(2, line.length() -1);
                if (stack.isEmpty() || !stack.pop().equals(tag)) {
                    System.out.println("malformed HTML");
                    return;
                }
             } else if (line.startsWith("<")) {
                String tag = line.substring(1, line.length() -1);
             }  else {
                if (stack.size() > maxDepth) {
                    maxDepth = stack.size();
                    deepestText = line;

                }
            }
         }

         if (!stack.isEmpty()) System.out.println("malformed HTML");
         else if (deepestText != null) System.out.println(deepestText);

        } catch(Exception e) {
            System.out.println("URL connection error");

        }       

    }
    
}