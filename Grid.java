import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Grid extends JPanel {
    private final int GRID_HEIGHT = 100; //height for calculating the size of GUI
    private final int GRID_WIDTH = 100; //width

    private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT]; //keeps track of where player has gone on board
    private final int WIDTH = GRID_WIDTH * 5; //pixels of width
    private final int HEIGHT = GRID_HEIGHT * 5; //pixels of height
    private final Color PLAYER1 = Color.BLUE; //player1
    private final Color PLAYER2 = Color.RED; //player2

    private Bike controlledBike; //player1
  
    private NetworkConnector connector;

    private Color userColor; //color of client bike
    
    private boolean displayPopup = false;

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
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    
        //controlledBike = new Bike(25, 75, grid, -1, Bike.DIRECTION_EAST, this);

        //controlledBike.startGame();
    }

    /*
    GAME SERVER CONNECTION INTITION HERE
     */
    public void connect(String hostname, String username, int port)
    {
    	if (this.connector == null)
    	{
    		this.connector = new NetworkConnector(hostname, username, port, this);
    	}
    	else
    	{
    		this.connector.retry(hostname, port);
    	}
        
    }
    
    public Boolean getConnectStatus()
    {
    	return this.connector.getStatus();
    }
    
    public void resetConnectionStatus()
    {
    	this.connector.resetStatus();
    }
    
    public void registerPlayer(Color color, Boolean playMode)
    {
    	this.connector.registerPlayer(color, playMode);
    }
    
    public void setPlayerID(int id)
    {
    	/*
    	if (this.controlledBike == null)
    	{
    		this.startGame(20);
    	}
    	*/
    	this.controlledBike = new Bike(id, this.connector);
    }
    
    public Boolean getPlayerStatus()
    {
    	return this.controlledBike != null;
    }
    
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
        
    //popup saying you won
    public void won(){
    
    	JOptionPane.showMessageDialog(this, "You Win!");
    	
    }

    //popup message you lost
    public void lost(){
    	
    	JOptionPane.showMessageDialog(this, "You Lost :/");    	
    }

    //method communication

    public NetworkConnector getConnector() {
        return this.connector;
    }
    
    public void displayWinLose(GameState gs)
    {
      if (this.controlledBike != null)
      {
    	  if (this.displayPopup == false)
    	  {
    		if (gs.getWinner() == this.controlledBike.player)
    	  	{
    			this.displayPopup = true;
    	  		this.won();
    	  	}
    	  	else if (gs.getWinner() != -1)
    	  	{
    	  	  	this.displayPopup = true;
    	  		this.lost();
    	  	}
    	  }	
      }
	 
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
        //System.out.println("It's time to repaint the screen");
        //This information should be stored by the GameState
        if (this.connector != null)
        {
        	Graphics2D g2 = (Graphics2D) g.create();
        	//System.out.println("Let's Draw the Game State!");
        	drawGameState(this.connector.getGameState(), g2);
        }       
        /*
        for (int x = 0; x < GRID_WIDTH; x++) {
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
        */
    }

    private void drawGameState(GameState gameState, Graphics2D g) {
        
    	if (gameState == null) 
    	{
    		//System.out.println("The Game State is NULL!");
    		return;   // No game to display yet!
    	}
    	//System.out.println("Let's draw the board");
        // Iterate through all of the players and all of the cells in the game
        // Again, not done super efficiently - could crop ones that are not visible!
    	/*
        ArrayList<GameState.Player> player = gameState.getPlayers();
        for (GameState.Player p: player) {
            drawPlayer(p, g);
        }
        */
      	
    	drawGrid(gameState, g);
    }
    
    /*
     // Draw the cells for this player
     private void drawPlayer(GameState.Player p, Graphics2D g) {
        g.setPaint(p.appearance);
        g.fillRect(p.locx * 5, p.locy *5, 5, 5);
        System.out.println("X Coordinate: " + p.locx + ", Y Coordinate: " + p.locy);
        System.out.println("Let's draw Player: " + p.gridID);
    }
    */
     
     //Draw the Grid with the players
     private void drawGrid(GameState gameState, Graphics2D g) {
    	 for (int x = 0; x < GRID_WIDTH; x++) {
    		 int[][] grid = gameState.getGrid();
    		 
             for (int y = 0; y <GRID_HEIGHT; y++) {
               if (grid[x][y] != 0){
            	   for (GameState.Player p: gameState.getPlayers()) {
            		   if (grid[x][y] == p.gridID){
                           g.setColor(p.appearance);
                           g.fillRect(x * 5, y *5, 5, 5);
                           
                           break;
                       } 
                   }
                   
                   
               }
             }
         }
     }
}
