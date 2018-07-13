import java.util.HashMap;

//Stores an ArrayList of times, draws the data into the JacksGraph class
public class Address{
	HashMap<Integer, Integer> times;
	String address;
	public Address(String a) {
		times = new HashMap<Integer, Integer>();
		address = a;
	}
	/**
	 * The addBytes method adds bytes to the corresponding time in the times HashMap.
	 * @param Line This is a line of data that contains the time and the number of bytes sent at that time.
	 */
	public void addBytes(String Line) {
		Integer lineTime;
		Integer lineBytes;
		if (!(Line.matches("^\\d+\\t.*\\t192.168.0.\\d{1,3}\\t\\d{4,6}\\t.*\\t.*\\t.*\\t\\d{2,4}.+"))){
			return;
		}
		//gets passed in a line of the data file that will be added to the address / time
		String[] splitLine = Line.split("\\t");
		lineTime = new Integer((Double.valueOf(splitLine[1]).intValue()));
		lineBytes = (Integer.parseInt(splitLine[7]));
		if(times.containsKey(lineTime)) {
			times.put(lineTime, lineBytes+(int)(times.get(lineTime)));
			return;
		}else {
			times.put(lineTime, lineBytes);
			return;
		}
		
	}
	/**
	 * @return returns the number of items in the times hashMap
	 */
	public int length() {
		return times.size();
	}
	/**
	 * returns a string representation of the times HashMap
	 */
	public String toString() {
		return String.format("Address: %s, with %d time obj's", address, times.size());
	}

}