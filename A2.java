import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.nio.file.Paths;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.awt.Dimension;
import javax.swing.JComboBox;
import java.util.Scanner;
import java.io.IOException;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class A2 extends JFrame{
	JRadioButton radioButton1;
	JRadioButton radioButton2;
	ButtonGroup radioButtonGroup;
	JPanel radioButtonPanel;
	JMenuBar menuBar;
	JMenu fileMenu;
	JMenuItem quit;
	JMenuItem openFile;
	String fileAddress;
	JPanel graph;
	JComboBox<String> addressSel;
	HashMap sendAddresses;
	HashMap recieveAddresses;
	public A2() {
		super("Jgib517 Network Analysis");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 500);
		this.setLayout(new BorderLayout());
		setupRadioButtons();
		setupMenu();
		setupComboBox();
		defaultGraph();
		setVisible(true);
		sendAddresses = new HashMap<String, Address>();
		recieveAddresses = new HashMap<String, Address>();
	}
	
	/**
	 * sets up the default graph that the user sees when they open the application and have no data loaded.
	 */
	public void defaultGraph() {
		graph = new JacksGraph();
		graph.setVisible(true);
		add(graph);
	}
	/**
	 * draws the actual data onto the graph and gets rid of the old graphs.
	 */
	private void drawData() {
		if (sendAddresses.containsKey(addressSel.getSelectedItem())) {
			if (!(graph==null)){
				graph.setVisible(false);
			}
			System.out.print("Draw data address obj:");
			System.out.println((Address)sendAddresses.get(addressSel.getSelectedItem()));
			graph = new JacksGraph((Address)sendAddresses.get(addressSel.getSelectedItem()));
			graph.setVisible(true);
			graph.repaint();
			add(graph);
		}else {
			if (!(graph==null)) {
				graph.setVisible(false);
			}
			System.out.print("Draw data address obj:");
			System.out.println((Address)recieveAddresses.get(addressSel.getSelectedItem()));
			graph = new JacksGraph((Address)recieveAddresses.get(addressSel.getSelectedItem()));
			graph.setVisible(true);
			graph.repaint();
			add(graph);
		}
	}
	/**
	 * updates the comboBox when the radio buttons are changed or new data is loaded. deletes all old items in the combobox and repopulates it with sorted data
	 */
	private void updateComboBox() {
		addressSel.removeAllItems();
		ArrayList<String> items = new ArrayList<String>();
		if (radioButton1.isSelected()) {
			if (!sendAddresses.isEmpty()) {
				sendAddresses.forEach((k,v)->{
				items.add((String) k);
				});
			}
		}else {
			if (!recieveAddresses.isEmpty()) {
				recieveAddresses.forEach((k, v)->{
				items.add((String) k);
			});
				addressSel.setVisible(true);
			}
		}
		if (items.size()>0) {
			class customCompare implements Comparator<String>{
				public int compare(String item1, String item2) {
					String[] list1 = item1.split("\\.");
					String[] list2 = item2.split("\\.");
					if (!(list1[2].equals(list2[2]))) {
						return Integer.parseInt(list1[2])-Integer.parseInt(list2[2]);
					}else{
						return Integer.parseInt(list1[3])-Integer.parseInt(list2[3]);
					}
				}
			}
			customCompare comparator = new customCompare();
			Collections.sort(items, comparator);
			for (int i = 0; i < items.size(); i++) {
				addressSel.addItem((String) items.get(i));
			}
		}
	}
	/**
	 * sets up the comboBox and its associated listener class.
	 */
	private void setupComboBox() {
		System.out.println("HELLO");
		addressSel = new JComboBox<String>();
		addressSel.setMaximumSize(new Dimension(300, 25));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = GridBagConstraints.RELATIVE;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.EAST;
		addressSel.setVisible(false);
		radioButtonPanel.add(addressSel, constraints);
		addressSel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Listened to a combobox action");
				drawData();
			}});
	}
	/**
	 * This method sets up the menu bar at the top of the program when the program is started. It also sets up the actionListeners for the options
	 * in the file menu and therefore is called when the user wants to open a new file.
	 */
	private void setupMenu() {
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);
		openFile = new JMenuItem("Open trace file");
		openFile.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser(".");
						int retrivalStatus = fileChooser.showOpenDialog(A2.this);
						if (retrivalStatus == JFileChooser.APPROVE_OPTION) {
							sendAddresses = null;
							recieveAddresses = null;
							new Thread(new Runnable() {
								public void run() {
									JOptionPane.showMessageDialog(null, "Your requested data is being loaded\n Press OK", "Loading", JOptionPane.INFORMATION_MESSAGE);
								}
							}).start();
							loadAndSort(fileChooser.getSelectedFile().getAbsolutePath());
							addressSel.setVisible(true);
							updateComboBox();
						}
					}
				}
		);
		fileMenu.add(openFile);
		quit = new JMenuItem("Quit");
		quit.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				}
			);
		fileMenu.add(quit);
	}
	/**
	 * This is method is where all of the loading and sorting of the data for the program is done.
	 * It uses a scanner to read every line.
	 * for every line it reads it checks if it is a valid line with a regex pattern
	 * it adds new values into the sendAddresses hashMap and the recieveAddresses HashMap. Each hashmaps key is the address that entry represents
	 * and each hashmap's data is stored by an Address object.
	 * The data is added into both hashmaps
	 * @param filePath This is passed in a filePath object that the scanner uses to find the file we want to open
	 */
	private void loadAndSort(String filePath) {
		Scanner input;
		System.out.println("FilePath: ");
		System.out.print(filePath);
		try {input = new Scanner(Paths.get(filePath));
			System.out.println("GOT HERE");
		}
		catch (IOException ioexc) {
			System.out.println("File could not read");
			return;
		}
		sendAddresses = new HashMap<String, Address>();
		recieveAddresses = new HashMap<String, Address>();
		while (input.hasNextLine()) {
			String next = input.nextLine();
			String pattern = "^\\d+\\t.*\\t192.168.0.\\d{1,3}\\t.*\\t.*\\t.*\\t.*\\t\\d{2,4}.+";
			if (Pattern.matches(pattern, next)) {
				String splitString[] = next.split("\\t");
				if (sendAddresses.containsKey(splitString[2])){
					Address address = (Address) sendAddresses.get(splitString[2]);
					address.addBytes(next);
				}else {
					Address address = new Address(splitString[2]);
					address.addBytes(next);
					sendAddresses.put(splitString[2], address);
				}
				if (recieveAddresses.containsKey(splitString[4])) {
					Address address = (Address) recieveAddresses.get(splitString[4]);
					address.addBytes(next);
				}else {
					Address address = new Address(splitString[4]);
					address.addBytes(next);
					recieveAddresses.put(splitString[4], address);
				}
			}
		
	}
	input.close();
	System.out.println(sendAddresses);
	System.out.println(recieveAddresses);
	}
	/**
	 * Sets up radio buttons and their item Listeners.
	 */
	private void setupRadioButtons() {
		radioButtonPanel = new JPanel();
		radioButtonPanel.setMaximumSize(new Dimension(100, 100));
		radioButtonPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = GridBagConstraints.RELATIVE;
		constraints.anchor = GridBagConstraints.LINE_START;
		radioButtonGroup = new ButtonGroup();
		radioButton1 = new JRadioButton("Source Hosts");
		radioButton1.setSelected(true);
		radioButtonGroup.add(radioButton1);
		radioButtonPanel.add(radioButton1, constraints);
		radioButton2 = new JRadioButton("Destination Hosts");
		radioButtonGroup.add(radioButton2);
		radioButtonPanel.add(radioButton2, constraints);
		ActionListener radioListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateComboBox();
				graph.setVisible(false);
				drawData();
				System.out.println("DID AN ACTION LISTEN");
			}
		};
		radioButton1.addActionListener(radioListener);
		radioButton2.addActionListener(radioListener);
		add(radioButtonPanel, BorderLayout.NORTH);
		
	}
}
