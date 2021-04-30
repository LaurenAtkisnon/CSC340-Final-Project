import java.io.Serializable;

/***************
 * Message
 * Author: Christian Duncan
 * Spring 21: CSC340
 * 
 * 
 * Server sends to the client their playerID and their name.
 ***************/

public class ChangeDirectionMessage extends Message implements Serializable {
    int newDirection;

    public ChangeDirectionMessage(int newDirection)
    {
        this.newDirection = newDirection;
    }
    
}
