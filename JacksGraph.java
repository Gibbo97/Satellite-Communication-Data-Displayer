import java.awt.event.ActionEvent;
import java.util.Map;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.JPanel;

public class JacksGraph extends JPanel {
	private int frameHeight;
	private int frameWidth;
	private int inset;
	private Address address;
	private Integer maxBits;
	private Integer maxTime;
	
	public JacksGraph() {
		System.out.println("Jacks Graph Constructor ()");
		maxBits = 100000;
		maxTime = 600;
		this.repaint();
	}

	public JacksGraph(Address address) {
		if (address==null||address.length()==0) {
			maxBits = 100000;
			maxTime = 600;
			this.repaint();
			return;
		}
		System.out.println("Jacks Graph constructor (address)");
		this.address = address;
		this.repaint();
	}
	/**
	 * is the paintComponent that draws all of the data onto the graph JPanel.
	 * Does setup work for the methods that is calls to do the drawing and also draws the basic x and y lines.
	 * @param Graphics g is the graphics object that the repaint() method gives it.
	 * @return returns void.
	 **/
	
	public void paintComponent(Graphics g) {
		System.out.println("Ran the Paint Component");
		super.paintComponent(g);
		frameHeight = (int)this.getSize().getHeight();
		frameWidth = (int)this.getSize().getWidth();
		inset = 50;
		super.setBackground (Color.white);
		g.drawLine(inset, inset, inset, frameHeight-inset);
		g.drawLine(inset, frameHeight-inset, frameWidth-inset, frameHeight-inset);
		g.drawString("Time [s]", frameWidth/2-"Time [s]".length(), frameHeight-10);
		g.drawString("Volume [bytes]", 15, 25);
		if (!(address==null || address.length()==0)) {
			//find max number of bytes
			maxTime = new Integer("0");
			maxBits = new Integer("0");
			Map<Integer, Integer> hashMap = address.times;
			for (Map.Entry<Integer, Integer> entry: hashMap.entrySet()) {
				if (!(entry.getKey()==null)) {
					Integer key = entry.getKey();
					Integer value = entry.getValue();
					if (value > maxBits) {
						maxBits = entry.getValue();
					}
					if (key>maxTime) {
						maxTime = key;
					}
				}
			}
			System.out.printf("Highest Bytes: %d", maxBits);
			System.out.printf("Max time: %d", maxTime);
			System.out.println();
			g.setColor(Color.red);
			drawData(g);
		}
		g.setColor(Color.BLACK);
		drawTicks(g);
	}
	
	/**
	 * Draws the actual lines of data onto the JPanel
	 * Loops through the HashMap that was supplied to the constructor (otherwise drawData is not called)
	 * @param Graphics g is the A graphics obejct belonging to the JPanel that it belongs in.
	 * @return is void
	 **/
	private void drawData(Graphics g) {
		int plotHeight = frameHeight - 2*inset;
		int plotWidth = frameWidth - 3*inset;
		Map<Integer, Integer> hashMap = address.times;
		for (Map.Entry<Integer, Integer> entry: hashMap.entrySet()) {
			Integer key = entry.getKey();
			Integer value = entry.getValue();
			double x = (double)key/maxTime*plotWidth;
			double y = (double)value/maxBits*plotHeight;
			g.drawLine((int)x+inset, frameHeight-inset-1, (int)x+inset, frameHeight-((int)y+inset));
		}
	}
	/**
	 * Draws the ticks and tick labels onto the graph
	 * @param g is the Graphics object that belongs to this class
	 */
	private void drawTicks(Graphics g) {
		System.out.println("Started Ticks Drawing");
		int xTickLabelSize = maxTime/14;
		xTickLabelSize = nearestWholeX(xTickLabelSize);
		int numberOfTicksX = findCorrectedTicksX(xTickLabelSize);
		int ticksWidth = (frameWidth-(2*inset))/numberOfTicksX;

		for(int i = 0;i<numberOfTicksX+1; i++) {
			g.drawLine(inset+i*ticksWidth, frameHeight-inset, inset+i*ticksWidth, frameHeight-inset+5);
			String str = toLabelStringY(i*xTickLabelSize);
			g.drawString(str, inset+i*ticksWidth-8*str.length()/2, frameHeight-inset+20);
			
		}
		int yTickLabelSize = maxBits/6;
		yTickLabelSize = nearestWholeY(yTickLabelSize);
		int numberOfTicksY = findCorrectedTicksY(yTickLabelSize);
		int ticksHeight = (frameHeight-(2*inset))/numberOfTicksY;
		for(int i = 0; i<numberOfTicksY+1; i++) {
			g.drawLine(inset, frameHeight-inset-ticksHeight*i, inset-5, frameHeight-inset-ticksHeight*i);
			g.drawString(toLabelStringY(i*yTickLabelSize), 5, frameHeight-inset-ticksHeight*i+5);
		}
	}
	/**
	 * This method takes the rounded tick sizes and then finds many we need to actually cover the whole range of data on the y Axis
	 * @param yTickLabelSize is amount of bits that is between each tick on the Y axis
	 * @return return an integer of how many ticks we need on our Y axis based on large of an interval each tick covers.
	 */
	private int findCorrectedTicksY(int yTickLabelSize) {
		int totalLength = 0;
		for (int i = 0; totalLength<maxBits;i++) {
			totalLength += yTickLabelSize;
		}
		return totalLength/yTickLabelSize;
	}
	/**
	 * nearestWholeY takes the ideally sized tick size and rounds it to the nearest number contained within validNums
	 * @param exactNum is an integer that represents the maximum y value divided by our ideal amount of ticks.
	 * @return returns an integer that is rounded to a good looking number and would not cause us to have too few or too many ticks on the Y axis.
	 */
	private int nearestWholeY(int exactNum) {
		int[] validNums = {5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000};
		int[] closeness = {0, 0, 0, 0, 0, 0, 0, 0};
		int minIndex = 0;
		for (int i = 0 ; i <validNums.length; i++) {
			closeness[i] = Math.abs(validNums[i]-exactNum);
			if (closeness[i] <= closeness[minIndex]) {
				minIndex = i;
			}
		}
		return validNums[minIndex];
	}
	/**
	 * This method takes the rounded tick size and then finds how many of these ticks we need to cover all of our data on the Y axis
	 * @param xTickLabelSize is the amount of bits that are between each tick on the Y axis
	 * @return returns the number of ticks we need on the Y axis
	 */
	private int findCorrectedTicksX(int xTickLabelSize) {
		int totalLength = 0;
		for (int i = 0 ; totalLength<maxTime;i++) {
			totalLength += xTickLabelSize;
		}
		System.out.print("Correct Number of ticks ");
		System.out.println(totalLength/xTickLabelSize);
		return totalLength/xTickLabelSize;
	}
	/**
	 * nearestWholeX takes the ideally sized tick size on the X axis and rounds it to the nearest number contained with the validNums array.
	 * @param exactNum is the integer that when used makes us take up the whole X axis with data. We round this to a number that is easier to read, at the cost of a little resolution.
	 * @return returns the rounded integer value that is now our increment between each tick on the x axis.
	 */
	private int nearestWholeX (int exactNum) {
		int[] validNums = {1,2,5,10,20,50,100};
		int[] closeness = {0, 0, 0, 0, 0, 0, 0};
		int minIndex = 0;
		for (int i = 0 ; i < validNums.length; i++) {
			closeness[i] = Math.abs(validNums[i]-exactNum);
			if (closeness[i] <= closeness[minIndex]) {
				minIndex = i;
			}
		}
		return validNums[minIndex];
		
	}
	/**
	 * toLabelStringY converts a long integer into a string that represents that integer in a shorter amount of characters
	 * It adds a letter to the end of the string if we remove zeros.
	 * @param number is the integer that we want to shorten into a string format
	 * @return returns a string we use to populate the labels on the Y axis of the graph.
	 */
	private String toLabelStringY(int number) {
		String numString = Integer.toString(number);
		int whileCount = 0;
		while (numString.length()>3) {
			numString = numString.substring(0, numString.length()-3);
			whileCount++;
		}
		String[] postFixArray= {"K", "M", "B"};
		if (whileCount>0) {
			if (postFixArray[whileCount-1].equals("M")) {
				return Integer.toString(number).substring(0,1) + "." + Integer.toString(number).substring(1,2) + "M";
			}
			return numString+postFixArray[whileCount-1];
		}else {
			return numString;
		}
	}
}
