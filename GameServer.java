/***************
 * GameServer
 * Author: Christian Duncan
 * Spring 21: CSC340
 *
 * Modified by: Charles Rescsanski, Timothy Carta, Ryan Hayes
 *
 * This is a stand-alone application with the following functions:
 *  1. Listen for connections on a port.
 *  2. One thread that continuously listens for connections.
 *  3. One thread for each new connection that is established
 *  4. One thread responsible for transmitting the gameState to all clients
 * Note that this server can handle multiple types of games.
 ***************/
import java.awt.Color;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.io.*;

public class GameServer implements Runnable {
    GameState gameState;
    GameEngine gameEngine;
    public static final int GAME_STATE_REFRESH = 100; // Set to 500 for slower speeds, good for debugging
    Debug debug;
    HashSet<Connection> connection; // The set of client connections
    int spectatorCount = -1;

    public static final int DEFAULT_PORT = 1340;

    // A new thread class to handle connections with a client
    class Connection extends Thread {
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        boolean done;
        String name; // Name of the player
        Color color; // Color of player
        boolean playMode; //whether connection corresponds to a player or spectator
        int playerID; // the ID of the player;

        /**
         * The constructor
         *
         * @param socket The socket attached to this connection
         * @param name   The "name" of this connection - for debugging purposes
         **/
        public Connection(Socket socket, String name) {
        	this.playMode = true;
            this.socket = socket;
            done = false;
            this.name = name;
            this.playerID = -1;
        }

        /**
         * Start running the thread for this connection
         **/
        public void run() {
            try {
                // First get the I/O streams for communication.
                // in -- Used to read input from client
                // out -- Used to send output to client
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                while (!done) {
                    Object message = in.readObject();
                    processMessage(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                printMessage("I/O Error while communicating with Client.  Terminating connection.");
                printMessage("    Message: " + e.getMessage());
            }

            // Close the socket (and its streams)
            try {
                printMessage("CLIENT is closing down.");
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                printMessage("Error trying to close socket. " + e.getMessage());
            }

            //Inform the server to remove its reference to this connection thread class	
            done = true;
            debug.println(3, "Game Server: Removing Connection to client: " + this.name);
            //If this connection belonged to a player, we must inform the gameEngine to remove the corresponding player
            //from the gameState
        	if (this.playMode)
        	{
        		gameEngine.removePlayer(this.playerID);
        	}
        }

        /**
         * Process one message that has been sent through this connection
         *
         * @param line The message to process
         **/
        private void processMessage(Object message) {
            // Process the line
            // debug.println(3, "Processing message: " + message);

            if (message instanceof JoinMessage) {
                processJoinMessage((JoinMessage) message);
            }
            else if (message instanceof ChangeDirectionMessage) {
                processChangeDirectionMessage((ChangeDirectionMessage) message);
            } else {
                debug.println(3, "Unrecognized Message");
            }
        }

        private void processChangeDirectionMessage(ChangeDirectionMessage message) {
            if (this.playerID == -1)
                return;
            gameEngine.changePlayerDirection(this.playerID, message.newDirection);
        }

        private void processJoinMessage(JoinMessage message) {
            this.name = message.name;
            this.color = message.color;

            if (message.playMode)
            {
            	this.playerID = gameEngine.addPlayer(name, color);
            	debug.println(3, "Player " + this.name + " is registered with id = " + this.playerID);
            }
            else {
            	this.playMode = false;
            
            	this.playerID = adjustSpectatorCount();
            	debug.println(3, "Spectator " + this.name + " is registered with id = " + this.playerID);
            }


            transmitMessage(new JoinResponseMessage(this.name, this.playerID));

        }

        public void transmitMessage(Object message) {
            try {
                if (out == null)
                    return;
                synchronized (out) {
                    out.writeObject(message);
                    out.flush(); // ensures that info gets sent out immediately
                }
            } catch (IOException | NullPointerException e) {
                debug.println(3, "Error transmitting message: " + message);
            }

        }

        /**
         * Print out the message (with a little name id in front)
         *
         * @param message The message to print out
         **/
        private void printMessage(String message) {
            System.out.println("[" + name + "]: " + message);
        }
    } /* End Class Connection */

    int port;
    boolean done;

    public GameServer(int port) {
        gameState = new GameState();
        gameEngine = new GameEngine();

        debug = Debug.getInstance();
        connection = new HashSet<>();
        this.port = port;
    }

    public GameServer() {
        this(DEFAULT_PORT);
    }

    /**
     * Run the main server... just listen for and create connections
     **/
    public void run() {
        System.out.println("G Server:  WELCOME!  Starting up...");
        try {
            // Create a server socket bound to the given port
            ServerSocket serverSocket = new ServerSocket(port);

            // Create a dedicated thread to transmit game state to all connections.
            createGameStateTransmitter();

            // Start the game engine thread running
            new Thread(gameEngine).start();

            while (!done) {
                // Wait for a client request, establish new thread, and repeat
                Socket clientSocket = serverSocket.accept();
                addConnection(clientSocket);
            }
        } catch (Exception e) {
            System.err.println("ABORTING: An error occurred while creating server socket. " + e.getMessage());
            System.exit(1);
        }
    }

    public void addConnection(Socket clientSocket) {
        String name = clientSocket.getInetAddress().toString();
        debug.println(3, "Game Server: Connecting to client: " + name);
        Connection c = new Connection(clientSocket, name);
        connection.add(c);
        c.start(); // Start the thread.
    }
    
    public synchronized int adjustSpectatorCount(){
        this.spectatorCount --;
        return this.spectatorCount;
    }

    /**
     *
     * /** Return a (deep) clone of the game state. Thus any changes to the game
     * state must be made through the game server.
     **/
    public GameState getGameState() {
        try {
            return (GameState) gameState.clone();
        } catch (CloneNotSupportedException e) {
            debug.println(1, "Coding error: GameState cloning is not supported.  Why not?");
        }
        return null;
    }

    public synchronized int addPlayer(String name, Color color) {
        return gameEngine.addPlayer(name, color);
    }

    private void createGameStateTransmitter() {
        // we create an anonymous class for the thread
        Thread t = new Thread() {
            public void run() {
                while (!done) {
                    // debug.println(3, "Transmitting game states.");
                    if (gameEngine != null) {
                        // debug.println(3, "Transmitting game states.");
                        GameState currentGameState = gameEngine.getGameState();
                        // transmit game state to every connection
                        
                        // Remove empty connections
                        connection.removeIf((con)-> con.done == true);
                       
                        // ONLY transmit if the player is registered
                        for (Connection con : connection) {

                            if (con.playerID != -1) {
                                con.transmitMessage(currentGameState);
                            }

                        }
                    }
                    try {
                        Thread.sleep(GAME_STATE_REFRESH);
                    } catch (Exception e) {
                    }
                }
            }
        };
        // must explicitly call the start method to start a thread
        t.start();
    }

    /**
     * The main entry point. It just processes the command line argument and starts
     * an instance of the GameServer running.
     **/
    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        // Set the port if specified
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Usage: java GameServer [PORT]");
                System.err.println("       PORT must be an integer.");
                System.exit(1);
            }
        }

        // Create and start the server
        GameServer s = new GameServer(port);
        s.run();
    }
}
