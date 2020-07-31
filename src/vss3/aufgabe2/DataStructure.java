package vss3.aufgabe2;

import java.util.LinkedList;
import java.util.List;

/**
 * Multi-threading-viable data structure.
 */
public class DataStructure {

    /** Default instance of the data structure. */
    private final static DataStructure INSTANCE = new DataStructure(10);

    /** List of all contained DataObjects. */
    private final List<DataObject> dataObjects = new LinkedList<>();

    /** Monitor object for consumer access. */
    private final Object consumerWait = new Object();

    /** Monitor object for producer access. */
    private final Object producerWait = new Object();

    /** Maximum size of the structure. If the list is full no further items are accepted. */
    private final int structureSize;

    public DataStructure(final int structureSize) {
        this.structureSize = structureSize;
    }

    /**
     * Thread-save appending of a DataObject to the list.
     *
     * @param dataObject The DataObject to append.
     */
    public void putDataObject(final DataObject dataObject) {

        synchronized (this.producerWait) {

            // try appending a DataObject and wait if it was not possible
            boolean appended = false;
            while (!appended) {
                appended = tryAppending(dataObject);
                if (!appended) {
                    try {
                        this.producerWait.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Producerwait interrupted!");
                    }
                }
            }
            System.out.println(Thread.currentThread() + "Dataobject appended!");

        }
        synchronized (this.consumerWait) {
            this.consumerWait.notifyAll();
        }
    }

    /**
     * Tries to append a DataObject to the structure.
     * @param dataObject  The object that is tried to append.
     * @return  true if the appending was successful.
     */
    private boolean tryAppending(final DataObject dataObject) {
        // Syncing on list to avoid parallel access.
        boolean appended = false;
        synchronized (this.dataObjects) {
            if (!structureIsFull()) {
                this.dataObjects.add(dataObject);
                appended = true;
            }
        }
        return appended;
    }

    /**
     * Shows if the structure is already full.
     * @return  true if the structure is full.
     */
    private boolean structureIsFull() {
        return this.dataObjects.size() >= this.getStructureSize();
    }

    /**
     * Shows if the structure is already empty.
     * @return  true if the structure is empty.
     */
    private boolean structureIsEmpty() {
        return this.dataObjects.isEmpty();
    }

    /**
     * Thread-save retrieval of a DataObject.
     *
     * @return The oldest DataObject from the List.
     */
    public DataObject getDataObject() {
        DataObject dataObject = null;

        synchronized (this.consumerWait) {

            // try adding and wait if it was not possible
            while (dataObject == null) {
                dataObject = tryRemoving();
                if (dataObject == null) {
                    try {
                        this.consumerWait.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Consumerwait interrupted!");
                    }
                }
            }
            System.out.println(Thread.currentThread() + "Dataobject removed!");


        }
        synchronized (this.producerWait) {
            this.producerWait.notifyAll();
        }


        return dataObject;
    }

    /**
     * Tries to remove a object from the structure. Fails if structure is empty.
     * @return   The removed DataObject, null if removing was not successful.
     */
    private DataObject tryRemoving() {
        DataObject dataObject = null;
        synchronized (this.dataObjects) {
            if (!structureIsEmpty()) {
                dataObject = this.dataObjects.remove(0);
            }
        }
        return dataObject;
    }

    /**
     * Returns the default instance of the DataStructure.
     * @return  default instance.
     */
    public static DataStructure getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the maximum size of the structure.
     * @return The maximum structure size.
     */
    public int getStructureSize() {
        return structureSize;
    }
}
