import java.io.Serializable;

/***************
 * Message
 * Author: Christian Duncan
 * Spring 21: CSC340
 * 
 * 
 * Server sends to the client their playerID and their name.
 ***************/

public class MovePlayerMessage extends Message implements Serializable {
    int x;
    int y;

    public MovePlayerMessage(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
}
