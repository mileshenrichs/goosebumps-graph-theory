package dataFormatting;

import builder.GraphBuilder;
import models.Book;
import models.Edge;
import models.Page;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is used to build a JSON object representing the story graph with the following structure:
 *      {
 *          nodes: [
 *              {id, utility, summary},
 *              { ... },
 *              { ... },
 *              ...
 *          ],
 *          links: [
 *              {source, target, decisionDesc},
 *              { ... },
 *              { ... },
 *              ...
 *          ]
 *      }
 *
 * This JSON structure can be used as input to a D3 Force-Directed Graph simulation.
 */
public class JsonDataBuilder {

    /**
     * Parses pages & edges to produce a JSON file containing a
     * list of nodes and a list of the links between them.
     */
    private static void buildJSONFile() throws Exception {
        // establish output path, instantiate PrintWriter
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path outFilePath = Paths.get(currentPath.toString(), "z-d3", "data.json");
        PrintWriter writer = new PrintWriter(outFilePath.toString(), "UTF-8");

        // build JSON object
        Book book = new GraphBuilder().buildBook();
        JSONObject json = new JSONObject();
        JSONArray nodesArr = new JSONArray();
        JSONArray linksArr = new JSONArray();

        for(Page page : book.getPages()) {
            nodesArr.put(new JSONObject()
                .put("id", page.pageNo)
                .put("utility", page.utility)
                .put("summary", page.summary));
        }

        for(Page page : book.getPages()) {
            for(Edge edge : page.adjacencyList) {
                linksArr.put(new JSONObject()
                    .put("source", edge.fromPage.pageNo)
                    .put("target", edge.toPage.pageNo)
                    .put("decisionDesc", edge.decisionDescription));
            }
        }

        json.put("nodes", nodesArr);
        json.put("links", linksArr);

        // write to file and close resource
        writer.print(json.toString(4));
        writer.close();
    }

    public static void main(String[] args) throws Exception {
        buildJSONFile();
    }

}
