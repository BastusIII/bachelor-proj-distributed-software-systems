package vss3.aufgabe5.communication.content;

/**
 * Content for tasks for clients.
 */
public class Task extends PathContent{

    /**
     * The shortest path found so far. For optimization strategies.
     */
    private int shortestWayFound = Integer.MAX_VALUE;

    public int getShortestWayFound() {
        return shortestWayFound;
    }

    public void setShortestWayFound(int shortestWayFound) {
        this.shortestWayFound = shortestWayFound;
    }
}
