/***************
 * ChangeDirectionMessage
 * Author: Charles Rescanski
 * Spring 21: CSC340
 *
 *
 * Client sends to the server a request to change the direction of their player.
 ***************/

import java.io.Serializable;

public class ChangeDirectionMessage extends Message implements Serializable {
    int newDirection;

    public ChangeDirectionMessage(int newDirection)
    {
        this.newDirection = newDirection;
    }

}
