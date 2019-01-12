package models;

import java.util.LinkedList;
import java.util.List;

public class Page {
    public int pageNo;
    public boolean isEnding;
    public Integer utility;
    public String summary;
    public List<Edge> adjacencyList;

    public Page(int pageNo) {
        this.pageNo = pageNo;
        this.adjacencyList = new LinkedList<>();
    }

    public void setInfo(boolean e, Integer u, String s) {
        this.isEnding = e;
        this.utility = u;
        this.summary = s;
    }
}
