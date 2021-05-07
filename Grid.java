/***************
 * Grid
 * Author: Lauren Atkinson, Charles Rescsanski
 * Spring 21: CSC340
 *
 *
 * This is the game board of the client application where the elements of the game are drawn.
 ***************/
import javax.swing.*;


import java.awt.*;

public class Grid extends JPanel {

	private static final long serialVersionUID = 340L;
	private final int GRID_HEIGHT = 125; //height for calculating the size of GUI
    private final int GRID_WIDTH = 175; //width

    private final int WIDTH = GRID_WIDTH * 5; //pixels of width
    private final int HEIGHT = GRID_HEIGHT * 5; //pixels of height

    private Bike controlledBike; //player1
    private JLabel text; //displays the win/lose message at the end of each game round
    private NetworkConnector connector;

    //new instance of Grid
    public Grid(){
        //plus one to assure the edge
    	text = new JLabel();
    	text.setFont(new Font("Verdana", Font.BOLD, 30));
    	this.add(text, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(WIDTH + 1, HEIGHT + 1));
    }

    //GAME SERVER CONNECTION INTITION HERE
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
    	text.setForeground(Color.BLUE);
    	text.setText("Congratulations " + this.connector.getUserName() + "! You Win!");
       }

    //popup message you lost
    public void lost(String winner){

    	text.setForeground(Color.RED);
    	text.setText("You Lost :/ " + winner + " wins this round.");
    }

    public void clearMessage() {
    	text.setText(null);
    }

    //method communication

    public NetworkConnector getConnector() {
        return this.connector;
    }

    public void displayWinLose(GameState gs)
    {
      if (this.controlledBike != null)
      {

    		if (gs.getWinnerID() == this.controlledBike.player)
    	  	{
    	  		this.won();
    	  	}
    	  	else if (gs.getWinnerID() != -1)
    	  	{
    	  		this.lost(gs.getWinnerName());
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
    }

    private void drawGameState(GameState gameState, Graphics2D g) {

    	if (gameState == null)
    	{
    		return;   // No game to display yet!
    	}
 
      	if (gameState.getGameActivity())
      	{
      		drawGrid(gameState, g);
      	}
      	else
      	{
      		 g.setColor(Color.BLACK);
             g.setFont(new Font("Serif", Font.BOLD, 28));
             if (gameState.getPlayers().size() < 2)
             {
            	 g.drawString("Please wait for more players to join.", WIDTH/2 - WIDTH/4, HEIGHT/2);
             }
             else
             {
            	 g.drawString("The next round will commence shortly.", WIDTH/2 - WIDTH/4, HEIGHT/2);
             }

      	}

    }

     //Draw the Grid with the players
     private void drawGrid(GameState gameState, Graphics2D g) {
    	 //draw the names of players
    	 for (GameState.Player p: gameState.getPlayers()) {
    		 g.setColor(Color.DARK_GRAY);
             g.setFont(new Font("Serif", Font.BOLD, 15));
             if (p.getName() != null)
             {
            	 //make it clear which player belongs to this client
            	 if (this.getPlayerStatus() && p.playerID == this.controlledBike.player)
            	 {
            		 g.setColor(Color.BLACK);
            		 g.setFont(new Font("Serif", Font.BOLD, 18));
            	 }
            	 g.drawString(p.getName(), (float) p.locx * 5 + 5, (float) p.locy * 5);
             }
         }
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
