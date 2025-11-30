package com.colin.java.swing;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TestLookAndFeel {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestLookAndFeel window = new TestLookAndFeel();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TestLookAndFeel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.setBounds(-10004, -10030, 113, 40);
		addPopup(frame.getContentPane(), popupMenu);

		JMenuItem menuItem_1 = new JMenuItem("New menu item");
		popupMenu.add(menuItem_1);

		JMenuItem menuItem = new JMenuItem("New menu item");
		popupMenu.add(menuItem);
		
		JButton button = new JButton("New button");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String lnfName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
				try {
					UIManager.setLookAndFeel(lnfName);
					SwingUtilities.updateComponentTreeUI(frame);
				} catch (UnsupportedLookAndFeelException ex1) {
					System.err.println("Unsupported LookAndFeel: " + lnfName);
				} catch (ClassNotFoundException ex2) {
					System.err.println("LookAndFeel class not found: "
							+ lnfName);
				} catch (InstantiationException ex3) {
					System.err
							.println("Could not load LookAndFeel: " + lnfName);
				} catch (IllegalAccessException ex4) {
					System.err.println("Cannot use LookAndFeel: " + lnfName);
				}
			}
		});
		button.setBounds(71, 80, 93, 23);
		frame.getContentPane().add(button);
		
		JButton button_1 = new JButton("New button");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String lnfName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
				try {
					UIManager.setLookAndFeel(lnfName);
					SwingUtilities.updateComponentTreeUI(frame);
				} catch (UnsupportedLookAndFeelException ex1) {
					System.err.println("Unsupported LookAndFeel: " + lnfName);
				} catch (ClassNotFoundException ex2) {
					System.err.println("LookAndFeel class not found: "
							+ lnfName);
				} catch (InstantiationException ex3) {
					System.err
							.println("Could not load LookAndFeel: " + lnfName);
				} catch (IllegalAccessException ex4) {
					System.err.println("Cannot use LookAndFeel: " + lnfName);
				}
			}
		});
		button_1.setBounds(188, 80, 93, 23);
		frame.getContentPane().add(button_1);
		
		JButton button_2 = new JButton("New button");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String lnfName = "javax.swing.plaf.metal.MetalLookAndFeel";
				try {
					UIManager.setLookAndFeel(lnfName);
					SwingUtilities.updateComponentTreeUI(frame);
				} catch (UnsupportedLookAndFeelException ex1) {
					System.err.println("Unsupported LookAndFeel: " + lnfName);
				} catch (ClassNotFoundException ex2) {
					System.err.println("LookAndFeel class not found: "
							+ lnfName);
				} catch (InstantiationException ex3) {
					System.err
							.println("Could not load LookAndFeel: " + lnfName);
				} catch (IllegalAccessException ex4) {
					System.err.println("Cannot use LookAndFeel: " + lnfName);
				}
			}
		});
		button_2.setBounds(312, 80, 93, 23);
		frame.getContentPane().add(button_2);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("New menu");
		menuBar.add(menu);
		
		JMenuItem menuItem_3 = new JMenuItem("New menu item");
		menu.add(menuItem_3);
		
		JMenu menu_2 = new JMenu("New menu");
		menu.add(menu_2);
		
		JMenuItem menuItem_5 = new JMenuItem("New menu item");
		menu_2.add(menuItem_5);
		
		JMenuItem menuItem_2 = new JMenuItem("New menu item");
		menu.add(menuItem_2);
		
		JMenu menu_1 = new JMenu("New menu");
		menuBar.add(menu_1);
		
		JMenuItem menuItem_4 = new JMenuItem("New menu item");
		menu_1.add(menuItem_4);
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
