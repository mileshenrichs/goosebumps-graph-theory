import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

public class GraphBuilder {

    public static void main(String[] args) {
        GraphBuilder graphBuilder = new GraphBuilder();
        String graphFileContents = graphBuilder.loadGraphFileContents();
    }

    private String loadGraphFileContents() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("story-graph.txt");
        if(resource != null) {
            try {
                File file = new File(resource.getFile());
                Scanner scanner = new Scanner(file);
                String text = scanner.useDelimiter("\\A").next();
                scanner.close();
                return text;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
