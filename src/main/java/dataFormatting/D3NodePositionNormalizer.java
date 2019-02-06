package dataFormatting;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * In d3.js force-directed graph simulations, fixed positions can be set for each node.
 * I manually dragged all the nodes into appropriate positions with the first page rooted at an arbitrary location.
 *
 * This class "normalizes" the positions of each node by rooting the first page at (0, 0) and linearly translating
 * every other node to be positioned relative to (0, 0), so the entire structure can be translated to any position.
 */
public class D3NodePositionNormalizer {

    private static void normalizeNodePositions() throws FileNotFoundException, UnsupportedEncodingException {
        JSONArray nodes = new JSONObject(loadJSONFile("positioning-data-raw.json")).getJSONArray("nodes");
        JSONArray links = new JSONObject(loadJSONFile("data.json")).getJSONArray("links");

        float startPosX = nodes.getJSONObject(0).getFloat("fx");
        float startPosY = nodes.getJSONObject(0).getFloat("fy");

        JSONArray normalizedNodes = new JSONArray();
        for(int i = 0; i < nodes.length(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            JSONObject normalizedNode = new JSONObject();
            normalizedNode.put("pageNo", node.getInt("pageNo"));
            normalizedNode.put("pageSummary", node.getString("pageSummary"));

            normalizedNode.put("fx", node.getFloat("fx") - startPosX);
            normalizedNode.put("fy", node.getFloat("fy") - startPosY);

            normalizedNodes.put(normalizedNode);
        }

        JSONObject output = new JSONObject();
        output.put("nodes", normalizedNodes);
        output.put("links", links);

        Path outFilePath = Paths.get(System.getProperty("user.dir"), "z-d3", "positioned-data.json");
        PrintWriter writer = new PrintWriter(outFilePath.toString(), "UTF-8");
        writer.write(output.toString(4));
        writer.close();
    }

    private static String loadJSONFile(String fileName) {
        try {
            String inputPath = String.format("%s%s%s%s%s",
                    System.getProperty("user.dir"), File.separator, "z-d3", File.separator, fileName);
            File file = new File(inputPath);
            Scanner scanner = new Scanner(file);
            String text = scanner.useDelimiter("\\A").next();
            scanner.close();
            return text;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        normalizeNodePositions();
    }

}
