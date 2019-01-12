package models;

public class Edge {
    public Page fromPage;
    public Page toPage;
    public boolean isDecision;
    public String decisionDescription;

    public Edge(Page f, Page t, boolean d, String desc) {
        this.fromPage = f;
        this.toPage = t;
        this.isDecision = d;
        this.decisionDescription = desc;
    }
}
