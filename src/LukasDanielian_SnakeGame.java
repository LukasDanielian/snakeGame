//Lukas Danielian, Ms. Roberts, 5/12/22, This code is the snake game, The user moves around eating apples trying not to hit the wall or itself
import javax.swing.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.Timer;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class LukasDanielian_SnakeGame extends JFrame implements KeyListener
{
	public enum DIR {UP, DOWN, LEFT, RIGHT};
	private final int DIM = 15;
	private PicPanel allPanels[][];
	private Snake theSnake;
	private Location appleLoc;
	private Timer timer;
	private int time;
	private int numEat;
	private Clip appleSound;
	private Clip song;
	private Clip lose;
	private ArrayList<Color> cols = new ArrayList<Color>();
	
	public LukasDanielian_SnakeGame() 
	{
		setSize(600,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.black);
		setLayout(new GridLayout(DIM,DIM,3,3));
		allPanels = new PicPanel[DIM][DIM];
		time = 300;
		numEat = 0;
		
		cols.add(Color.RED);
		cols.add(Color.ORANGE);
		cols.add(Color.YELLOW);
		cols.add(Color.GREEN); 
		cols.add(Color.BLUE); 
		cols.add(Color.MAGENTA);
	
		//Audio
		try
		{
			File file = new File("sound.wav");
			File file2 = new File("song.wav");
			File file3 = new File("lose.wav");
			
			AudioInputStream sound = AudioSystem.getAudioInputStream(file);
			AudioInputStream sound2 = AudioSystem.getAudioInputStream(file2);
			AudioInputStream sound3 = AudioSystem.getAudioInputStream(file3);
			
			appleSound = AudioSystem.getClip();
			song = AudioSystem.getClip();
			lose = AudioSystem.getClip();
			
			appleSound.open(sound);
			song.open(sound2);
			lose.open(sound3);
		}
		catch(Exception e)
		{
			System.out.print("error");
		}
		
		//Makes board
		for(int row = 0; row < DIM; row++) 
		{
			for(int col = 0; col < DIM; col++) 
			{
				allPanels[row][col] = new PicPanel();
				allPanels[row][col].setBackground(Color.white);
				add(allPanels[row][col]);
			}
		}

		this.addKeyListener(this);
		theSnake = new Snake();
		placeApple();
		setVisible(true);

		//Timer for movement 
		timer = new Timer(time, new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				theSnake.move();
			}
		});
	}

	//Determines where to place next apple
	public void placeApple()
	{
		appleLoc = new Location((int)(Math.random()*DIM),(int)(Math.random()*DIM));
		while(allPanels[appleLoc.row][appleLoc.col].image != null)
		{
			appleLoc = new Location((int)(Math.random()*DIM),(int)(Math.random()*DIM));
		}
		allPanels[appleLoc.row][appleLoc.col].changePic("snake_images/apple.png");
	}

	//Runs when key is clicked
	public void keyPressed(KeyEvent e) 
	{	
		int keyVal = e.getKeyCode();

		if(keyVal == KeyEvent.VK_SPACE)
		{
			timer.start();
			song.setFramePosition(0);
			song.start();
		}

		else if(keyVal == KeyEvent.VK_UP)
		{

			theSnake.snakeDIR = DIR.UP;
		}

		else if(keyVal == KeyEvent.VK_DOWN)
		{
			theSnake.snakeDIR = DIR.DOWN;
		}

		else if(keyVal == KeyEvent.VK_LEFT)
		{
			theSnake.snakeDIR = DIR.LEFT;
		}

		else if(keyVal == KeyEvent.VK_RIGHT)
		{
			theSnake.snakeDIR = DIR.RIGHT;
		}
	}

	public void keyReleased(KeyEvent arg0) 
	{
		// DO NOT IMPLEMENT
	}

	public void keyTyped(KeyEvent arg0) 
	{
		// DO NOT IMPLEMENT
	}

	public class Snake
	{

		private ArrayList<Location> snakeLocs;  // head of snake is always at spot 0 in the AL
		private DIR snakeDIR = DIR.DOWN;		//current dir the snake head is facing
		private DIR prevDIR = DIR.DOWN;		    // last dir snake was facing (used to draw the neck correctly)

		public Snake() 
		{	
			//initialize four tiles to the appropriate location
			snakeLocs = new ArrayList<Location>();
			int startRow = 4;
			int startCol = 4;

			//add the head of the snake
			snakeLocs.add(new Location(startRow,startCol));
			allPanels[startRow][startCol].changePic("snake_images/head_down.png");

			//add the body (assuming an initial length of 4)
			for(int i = 1; i <= 3; i++) 
			{
				snakeLocs.add(new Location(startRow-i,startCol));
				allPanels[startRow-i][startCol].changePic("snake_images/body_vert.png");
			}

			changeTail();

			prevDIR = snakeDIR;
		}

		//Moves the snake and determines if the game is over
		public void move() 
		{
			int addRow = 0;
			int addCol = 0;
			String image = "";		

			//Sets next location and image
			if(snakeDIR == DIR.UP)
			{
				addRow--;
				image = "snake_images/head_up.png";
			}

			else if(snakeDIR == DIR.DOWN)
			{
				addRow++;
				image = "snake_images/head_down.png";
			}

			else if(snakeDIR == DIR.LEFT)
			{
				addCol--;
				image = "snake_images/head_left.png";
			}

			else if(snakeDIR == DIR.RIGHT)
			{
				addCol++;
				image = "snake_images/head_right.png";
			}			

			snakeLocs.add(0,new Location(snakeLocs.get(0).row+addRow,snakeLocs.get(0).col+addCol));
			
			//Checks if eats apple
			if(snakeLocs.get(0).row == appleLoc.row && snakeLocs.get(0).col == appleLoc.col)
			{
				numEat++;
				appleSound.setFramePosition(0);
				appleSound.start();
				
				placeApple();

				if(numEat % 3 == 0)
				{	
					timer.setDelay(time -= 10);
				}
			}
			
			//Checks if goes out of bounds or hits itself
			else if(snakeLocs.get(0).row >= DIM || snakeLocs.get(0).row < 0 || snakeLocs.get(0).col >= DIM || snakeLocs.get(0).col < 0 || allPanels[snakeLocs.get(0).row][snakeLocs.get(0).col].image != null)
			{
				song.stop();
				lose.setFramePosition(0);
				lose.start();
				timer.stop();
				JOptionPane.showMessageDialog(null, "Game Over!\nApples Eaten: " + numEat);
				return;
			}
			
			//Removes tail
			else
			{
				allPanels[snakeLocs.get(snakeLocs.size()-1).row][snakeLocs.get(snakeLocs.size()-1).col].changePic("");
				snakeLocs.remove(snakeLocs.size()-1);
			}
			
			//Adds next location of head
			allPanels[snakeLocs.get(0).row][snakeLocs.get(0).col].changePic(image);
			changeTail();
			changeNeck();	
			prevDIR = snakeDIR;
			
			if(snakeDIR == DIR.DOWN || snakeDIR == DIR.RIGHT)
			{
				Color temp = cols.get(0);
				cols.remove(0);
				cols.add(cols.size()-1,temp);
			}
			else
			{
				Color temp = cols.get(cols.size()-1);
				cols.remove(cols.size()-1);
				cols.add(0,temp);
			}
			for(int row = 0; row < DIM; row++) 
			{
				for(int col = 0; col < DIM; col++) 
				{		
					if(snakeDIR == DIR.DOWN)
						allPanels[row][col].setBackground(cols.get(row%cols.size()));
					else if(snakeDIR == DIR.RIGHT)
						allPanels[row][col].setBackground(cols.get(col%cols.size()));
					else if(snakeDIR == DIR.UP)
						allPanels[row][col].setBackground(cols.get(row%cols.size()));
					else
						allPanels[row][col].setBackground(cols.get(col%cols.size()));
					if(row == appleLoc.row && col == appleLoc.col)
						allPanels[row][col].setBackground(Color.BLACK);
				}
			}
		}

		//determine which pic to draw for the tail and update the image (as necessary)
		private void changeTail() 
		{
			Location tailLoc = snakeLocs.get(snakeLocs.size()-1);
			Location spotBefore = snakeLocs.get(snakeLocs.size()-2);

			String tailPic = "";

			if(tailLoc.row == spotBefore.row) 
			{
				if(tailLoc.col < spotBefore.col)
					tailPic = "snake_images/tail_left.png";
				else
					tailPic = "snake_images/tail_right.png";
			}
			else 
			{
				if(tailLoc.row<spotBefore.row) 
					tailPic = "snake_images/tail_up.png";	
				else
					tailPic = "snake_images/tail_down.png";
			}

			allPanels[tailLoc.row][tailLoc.col].changePic(tailPic);
		}

		//changes the image of the neck based off of previous and current direction of the snake
		private void changeNeck() 
		{
			Location headLoc = snakeLocs.get(0);
			Location neckLoc = snakeLocs.get(1);	
			String neckPic = "";

			if(prevDIR == snakeDIR) 
			{
				if(headLoc.row == neckLoc.row)
					neckPic = "snake_images/body_horz.png";
				else
					neckPic = "snake_images/body_vert.png";
			}

			else if(snakeDIR == DIR.UP) 
			{
				if(prevDIR == DIR.RIGHT) 
					neckPic = "snake_images/right_up.png";
				else
					neckPic = "snake_images/left_up.png";	
			}
			else if(snakeDIR == DIR.DOWN) 
			{
				if(prevDIR == DIR.LEFT)
				{
					neckPic = "snake_images/left_down.png";
				}
				else
					neckPic = "snake_images/right_down.png";
			}
			else if(snakeDIR == DIR.RIGHT) 
			{
				if(prevDIR == DIR.UP) 
				{
					neckPic = "snake_images/left_down.png";
				}
				else
					neckPic = "snake_images/left_up.png";
			}
			else  
			{
				if(prevDIR == DIR.UP) 
				{
					neckPic = "snake_images/right_down.png";
				}
				else
					neckPic = "snake_images/right_up.png";
			}		
			allPanels[neckLoc.row][neckLoc.col].changePic(neckPic);
		}
	}

	public class Location
	{
		private int row;
		private int col;

		public Location(int r, int c) 
		{
			row = r;
			col = c;
		}

		public String toString() 
		{
			return row+", "+col;
		}
	}

	public class PicPanel extends JPanel
	{
		private BufferedImage image;

		public PicPanel() 
		{

		}

		public PicPanel(String fname)
		{
			changePic(fname);
		}

		//places an image in a given panel or clears one out if "" is passed in instead
		public void changePic(String fname) 
		{
			if(fname.equals("")) 
			{
				image = null;
			}
			else 
			{
				//reads the image
				try 
				{
					image = ImageIO.read(new File(fname));
				} 
				catch (IOException ioe) {
					System.out.println("Could not read in the pic: "+fname);
					System.exit(0);
				}
			}
			this.repaint();
		}

		//this will draw the image
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if(image !=null)
				g.drawImage(image,0,0,this);
		}
	}

	public static void main(String[] args) 
	{
		new LukasDanielian_SnakeGame();
	}
}