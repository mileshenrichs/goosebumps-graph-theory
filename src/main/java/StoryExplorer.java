import builder.GraphBuilder;
import models.Book;
import models.Edge;
import models.Page;

/**
 * StoryExplorer uses graph traversal algorithms like BFS/DFS to discover paths through
 * the variety of storylines this choose-your-own-adventure novel has to offer.
 * This class possesses knowledge that can save lives (of Goosebumps characters).
 */
public class StoryExplorer {

    public static void main(String[] args) {
        // build graph structure from input file
        Book book = new GraphBuilder().buildBook();

        // choose first option every time
        followFirstPath(book);
    }

    private static void followFirstPath(Book book) {
        Page page = book.startPage;
        while(!page.isEnding) {
            System.out.println(page.pageNo + ": " + page.summary);

            Edge edge = page.adjacencyList.get(0);
            if(!edge.isDecision) {
                System.out.println();
            } else {
                System.out.println();
                System.out.println("\t  -> " + edge.decisionDescription);
                System.out.println();
            }

            page = edge.toPage;
        }

        System.out.println();
        System.out.println("====== ENDING =======================================");
        System.out.println(page.summary);
        System.out.println("Utility: " + page.utility);
    }

}
