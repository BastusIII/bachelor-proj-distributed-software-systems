package vss3.aufgabe5.communication.content;

import java.util.List;

/**
 * Abstract base class for all contents containing a path.
 */
public abstract class PathContent implements MessageContent{

    /**
     * The path contained.
     */
    private List<Integer> path;

    public List<Integer> getPath() {
        return path;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }
}
