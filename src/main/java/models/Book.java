package models;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private Page[] pages;
    public Page startPage;
    List<Page> endings;

    public Book(int pageCount) {
        this.pages = new Page[pageCount];
        this.endings = new ArrayList<>();
    }

    public void setPage(Page page) {
        pages[page.pageNo - 1] = page;
    }

    public Page getPage(int pageNo) {
        return pages[pageNo - 1];
    }

    public void addEnding(Page endingPage) {
        endings.add(endingPage);
    }

    public Page[] getPages() {
        return pages;
    }
}
