package zProject_GameOfLife;

import java.awt.*;
import java.util.Random;

import javax.swing.JPanel;

public class MyPanel extends JPanel implements Runnable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int screenWidth = 600;
	private static final int screenHeight = 600;
	
	private static final int unitSize = 10;
	
	private static final int units = screenWidth/unitSize;
	
	int[][] array;
	int[][] arrayHelper;
	int checker;
	
	Random random;
	
	boolean running = false;
	Thread myThread;

	MyPanel(){
		
		initializePanel();
		initializeValues();
		
		start();
	}

	private void start() {
		myThread = new Thread(this);
		running = true;
		myThread.start();
	}

	private void initializeValues() {
		
		for (int i = 0; i<units; i++) {
			for(int j = 0; j<units ; j++) {
				array[i][j] = random.nextInt(2);
			}
		}
		
	}

	private void initializePanel() {
		this.setPreferredSize(new Dimension(screenWidth,screenHeight));
		this.setBackground(new Color(40,40,40));
		this.setFocusable(true);
		
		random = new Random();
		array = new int[units][units];
		arrayHelper = new int[units][units];
	}
	
	public void update() {
		
		// Checking the cell if it will live or die
		for(int i=0; i<units; i++) {
			for(int j=0; j<units; j++) { 
					liveOrDie(i,j);
			}
		}
		
		// Updating original array by using new (helper) array
		for(int i=0; i<units; i++) {
			for(int j=0; j<units; j++) {
				array[i][j] = arrayHelper[i][j];
			}
		}
		
	}

	private void liveOrDie(int i, int j) {
		
		int state = array[i][j];
		checker = 0;
		
		/* Condition Check for wrapping around
		 * lets say i = 0 and j = units-1
		 * (i + x + units) % units = (0 - 1 + units) % units = units - 1
		 * (j + y + units) % units = (units - 1 + 1) % units = 0
		 * this way the edge and corners will wrap around 
		 */

		for(int x=-1; x < 2; x++) {
			for(int y=-1; y<2; y++) {
				
				//For Wrapping around
				int rows = (i+x + units) % units;
				int cols = (j+y + units) % units;
				checker+=array[rows][cols];
				
				// For Ignoring out of boundary
//				int rows = (i+x + units) / units;
//				int cols = (j+y + units) / units;
//				if(rows==1 && cols==1) {
//					checker+=array[i+x][j+y];
//				}
			}
		}
		
		checker-=array[i][j];
		
		// Result output
		if (checker >= 2 && checker <= 3 && state == 1)
			arrayHelper[i][j] = 1;
		else if (checker == 3 && state == 0)
			arrayHelper[i][j] = 1;
		else 
			arrayHelper[i][j] = 0;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (running) {
			draw(g);
		}
	}

	private void draw(Graphics g) {
		
		for (int i = 0; i<units ; i++) {
			for(int j = 0; j<units ; j++) {
				if(array[i][j] == 0) {
					g.setColor(new Color(240,240,240));
					g.fillRect(i*unitSize,j*unitSize,unitSize,unitSize);
				}
				else {
					g.setColor(new Color(20,20,20));
					g.fillRect(i*unitSize,j*unitSize,unitSize,unitSize);
				}
			}
		}
		
		
		Graphics2D g2 = (Graphics2D) g;
	    g2.setStroke(new BasicStroke(2));
	    g2.setColor(new Color(200,200,200));
		for(int i = 1; i <= units; i++) {
			g2.drawLine(unitSize*i,0,unitSize*i,screenHeight);
			g2.drawLine(0,unitSize*i,screenWidth,unitSize*i);
		}
	}

	@Override
	public void run() {
		
		int frameRate = 30;
		int updateRate = 10;
		
		double frameOptimum = 1000000000/frameRate;
		double updateOptimum = 1000000000/updateRate;
		
		double fdelta = 0;
		double udelta = 0;
		
		long lastTime = System.nanoTime();
		
		while(running) {
			
			long currentTime = System.nanoTime();
			
			fdelta += (currentTime-lastTime);
			udelta += (currentTime-lastTime);
			
			lastTime = currentTime;
			
			if(udelta>=updateOptimum) {
				update();
				udelta -= updateOptimum;
			}
			
			if(fdelta>=frameOptimum) {
				repaint();
				fdelta+= frameOptimum;
			}
		}
		
	}

}