package dataFormatting;

import builder.GraphBuilder;
import models.Book;
import models.Edge;
import models.Page;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is used to build the query to create graph structure for
 * storage in a Neo4J graph database.
 */
public class Neo4jQueryBuilder {

    /**
     * Parses pages & edges to produce a CREATE query
     * which allows the graph to be imported into a Neo4J database.
     * The output of this method is a data.cql file in z-neo4j/
     */
    private static void buildGraphCreationQuery() throws Exception {
        // establish output path, instantiate PrintWriter
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path outFilePath = Paths.get(currentPath.toString(), "z-neo4j", "data.cql");
        PrintWriter writer = new PrintWriter(outFilePath.toString(), "UTF-8");

        // get Book graph, instantiate Query object
        GraphBuilder graphBuilder = new GraphBuilder();
        Book book = graphBuilder.buildBook();
        Query query = new Query();

        // create pages first (need to be able to reference them for edge creation), then edges
        for(Page page : book.getPages()) {
            query.createPage(page);
        }
        for(Page page : book.getPages()) {
            for(Edge edge : page.adjacencyList) {
                query.createEdge(edge);
            }
        }

        // write to file and close resource
        writer.print(query.getQueryAsString());
        writer.close();
    }

    static class Query {
        private StringBuilder q;

        Query() {
            this.q = new StringBuilder();
            this.q.append("CREATE\n");
        }

        void createPage(Page page) {
            StringBuilder pageBuilder = new StringBuilder();
            String pageId = getPageId(page);
            String pageSummary = escapeDoubleQuotes(page.summary);
            if(page.isEnding) {
                pageSummary += " (" + page.utility + ")";
            }

            pageBuilder.append(String.format("(%s:Page {pageNo: %d, isEnding: %b, summary: \"%s\"", pageId, page.pageNo, page.isEnding, pageSummary));
            if(page.isEnding) {
                pageBuilder.append(String.format(", utility: %d", page.utility));
            }
            pageBuilder.append("})");

            addLineToQuery(pageBuilder.toString());
        }

        void createEdge(Edge edge) {
            StringBuilder edgeBuilder = new StringBuilder();
            String fromPageId = getPageId(edge.fromPage);
            String toPageId = getPageId(edge.toPage);
            String decision = "";
            if(edge.isDecision) {
                decision = escapeDoubleQuotes(edge.decisionDescription);
            }

            edgeBuilder.append(String.format("(%s)-[:THEN", fromPageId));
            if(edge.isDecision) {
                edgeBuilder.append(String.format(" {decision: \"%s\"}", decision));
            }
            edgeBuilder.append(String.format("]->(%s)", toPageId));

            addLineToQuery(edgeBuilder.toString());
        }

        void addLineToQuery(String line) {
            q.append(String.format("%s,\n", line));
        }

        /**
         * replace occurrences of " with \"
         */
        String escapeDoubleQuotes(String s) {
            StringBuilder escaped = new StringBuilder();
            char[] cArr = s.toCharArray();

            for (char c : cArr) {
                if (c == '"') {
                    escaped.append('\\');
                }
                escaped.append(c);
            }

            return escaped.toString();
        }

        String getQueryAsString() {
            // build full string, remove trailing comma and new line
            String qStr = q.toString();
            return qStr.substring(0, qStr.length() - 2);
        }

        /**
         * When creating Page nodes in the query, each page needs a unique name for reference.
         * The name is created by getting the object's hashcode and converting it into a string of letters.
         * @param page page for which an id is needed
         * @return a string of letters uniquely representing the page (i.e. "ifgebjhge")
         */
        String getPageId(Page page) {
            char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
            StringBuilder idBuilder = new StringBuilder();
            String hashcode = String.valueOf(page.hashCode());
            for(int i = 0; i < hashcode.length(); i++) {
                idBuilder.append(letters[Integer.valueOf(hashcode.substring(i, i + 1))]);
            }
            return idBuilder.toString();
        }
    }

    public static void main(String[] args) throws Exception {
        buildGraphCreationQuery();
    }

}
