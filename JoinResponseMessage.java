/***************
 * JoinResponseMessage
 * Author: Christian Duncan
 * Spring 21: CSC340
 *
 *
 * Server sends to the client their playerID and their name.
 ***************/

import java.io.Serializable;

public class JoinResponseMessage extends Message implements Serializable {
    String name;
    int playerID;

    public JoinResponseMessage(String name, int playerID)
    {
        this.name = name;
        this.playerID = playerID;
    }

}
