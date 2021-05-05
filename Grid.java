/***************
 * Team Members: Lauren Atkinson, Timothy Carta, Ryan Hayes, Griffin King, Charles Rescanscki
 * Spring 21 | CSC340
 *
 * Grid class -- drawss the grid and keeps track of the players on the game 
 ***************/

import javax.swing.*;
import java.awt.*;

public class Grid extends JPanel {
    private final int GRID_HEIGHT = 100; //height for calculating the size of GUI
    private final int GRID_WIDTH = 100; //width

    private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT]; //keeps track of where player has gone on board
    private final int WIDTH = GRID_WIDTH * 5; //pixels of width
    private final int HEIGHT = GRID_HEIGHT * 5; //pixels of height
    private final Color PLAYER1 = Color.BLUE; //player1
    private final Color PLAYER2 = Color.RED; //player2

    private Bike bike1; //player1
    private Bike bike2; //player2

    private Bike controlledBike; //remote playr controls via Server

    private NetworkConnector connector;

    private Color userColor; //color of client bike

    //new instance of Grid
    public Grid(){
        //plus one to assure the edge
        setPreferredSize(new Dimension(WIDTH + 1, HEIGHT + 1));

        //set everything to 0
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                grid[x][y] = 0;
            } }
        userColor = UIManager.getColor("Panel.background");
    }
//adds bikes to grid
    public void startGame(int controlled){
        try{
                Thread.sleep(1000);
        } catch (Exception e)
        {
        e.printStackTrace();
    }
    bike1 = new Bike(25, 75, grid, 1, Bike.DIRECTION_EAST, this);
    bike2 = new Bike(75, 25, grid, 2, Bike.DIRECTION_WEST, this);

    if(controlled ==1){
        controlledBike = bike1;
        serverBike = bike2;
    } else {
        controlledBike = bike2;
        serverBike = bike1;
    }
    controlledBike.startGame();

    /*
    GAME SERVER CONNECTION INTITION HERE
     */

    //turns the user bike north
    public void turnNorth(){
        controlledBike.turnNorth();
    }
    //turns the user bike east
    public void turnEast(){
        controlledBike.turnEast();
    }
    //turns the user bike south
    public void turnSouth(){
        controlledBike.turnSouth();
    }
    //turns the user bike west
    public void turnWest(){
        controlledBike.turnWest();
    }
    //signals to stop moving
    public void stop(){
        controlledBike.stop();
    }
    //popup saying you won
    public void won(){
        JOptionPane.showMessageDialog(this, "You Win!");
    }

    //popup message you lost
    public void lost(){
        JOptionPane.showMessageDialog(this, "You Lost :/");
    }

    // bike controlled by the remote player
    public Bike getServerBike(){
        return serverBike;
    }

    //method communication

    public NetworkConnector getConnector() {
        return connector;
    }

    //paints the grid
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //edges
        g.drawLine(0,0,0,HEIGHT); //left side
        g.drawLine(WIDTH, 0, WIDTH, HEIGHT); //RIGHT SIDE
        g.drawLine(0,0,WIDTH, 0); //TOP
        g.drawLine(0, HEIGHT, WIDTH, HEIGHT); //BOTTOM

        //draw the snakes
        for (int x = 0, x < GRID_WIDTH; x++) {
            for (int y = 0; y <GRID_HEIGHT; y++) {
              if (grid[x][y] != 0){
                  if (grid[x][y] == 1){
                      g.setColor(PLAYER1);
                  } else if (grid [x][y] ==2){
                      g.setColor(PLAYER2);
                  }
                  g.fillRect(x * 5, y *5, 5, 5);
              }
            }
        }
        g.setColor(userColor);
        g.fillRect(0,501,501,505);
    }
}
