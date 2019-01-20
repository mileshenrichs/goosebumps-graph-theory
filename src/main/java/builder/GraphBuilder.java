package builder;

import models.Book;
import models.Edge;
import models.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

public class GraphBuilder {

    /**
     * Parses through page & edge data to build graph representation of the book's storylines
     * @return directed graph of pages in the book
     */
    public Book buildBook() {
        String graphContents = loadGraphFileContents();
        final int INPUT_DATA_START_LINE_NO = 6;
        final int PAGE_COUNT = 137;
        Book book = new Book(PAGE_COUNT);
        if(graphContents == null) {
            return book;
        }

        String[] dataEntries = graphContents.split("\n");
        for(int i = INPUT_DATA_START_LINE_NO; i < dataEntries.length; i++) {
            String entry = dataEntries[i].trim();
            String[] parts = entry.split(" \\| ");

            if(parts[0].equals("PAGE")) {
                addPageDetails(book, parts);
            } else if(parts[0].equals("EDGE")) {
                addEdge(book, parts);
            }
        }

        book.startPage = book.getPage(1);

        return book;
    }

    private String loadGraphFileContents() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("story-graph-short.txt");
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

    private void addPageDetails(Book book, String[] pageEntry) {
        int pageNo = Integer.valueOf(pageEntry[1]);
        Page page = getPageFromBook(book, pageNo);

        boolean isEnding = Boolean.valueOf(pageEntry[2]);
        Integer utility = pageEntry[3].equals("--") ? null : Integer.valueOf(pageEntry[3]);
        String summary = pageEntry[4];

        page.setInfo(isEnding, utility, summary);

        if(isEnding) {
            book.addEnding(page);
        }
    }

    private void addEdge(Book book, String[] edgeEntry) {
        Page fromPage = getPageFromBook(book, Integer.valueOf(edgeEntry[1]));
        Page toPage = getPageFromBook(book, Integer.valueOf(edgeEntry[2]));
        boolean isDecision = Boolean.valueOf(edgeEntry[3]);
        String decisionDesc = edgeEntry[4].equals("--") ? null : edgeEntry[4];

        Edge edge = new Edge(fromPage, toPage, isDecision, decisionDesc);

        book.getPage(fromPage.pageNo).adjacencyList.add(edge);
    }

    private Page getPageFromBook(Book book, int pageNo) {
        Page page = book.getPage(pageNo);
        if(page == null) {
            page = new Page(pageNo);
            book.setPage(page);
        }

        return page;
    }

}
