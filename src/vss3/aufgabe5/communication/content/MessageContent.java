package vss3.aufgabe5.communication.content;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * Content for salesmen messages.
 */
/* Annotation voodoo for jackson json mapping.
 * Needed for abstract classes and interfaces. */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Task.class, name = "Task"),
        @JsonSubTypes.Type(value = NewClientID.class, name = "NewClientID"),
        @JsonSubTypes.Type(value = TableContent.class, name = "TableContent"),
        @JsonSubTypes.Type(value = ShortestPath.class, name = "ShortestPath"),
        @JsonSubTypes.Type(value = TaskRequest.class, name = "TaskRequest"),
        @JsonSubTypes.Type(value = TaskFinished.class, name = "TaskFinished")
})
public interface MessageContent {
}
