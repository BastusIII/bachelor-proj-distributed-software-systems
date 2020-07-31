package vss3.aufgabe5.communication.content;

/**
 * Content for a shortest path with length.
 */
public class ShortestPath extends PathContent {

    /**
     * The path length.
     */
    private int pathLength;

    public int getPathLength() {
        return pathLength;
    }

    public void setPathLength(int pathLength) {
        this.pathLength = pathLength;
    }

}
