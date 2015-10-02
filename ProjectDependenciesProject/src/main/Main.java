package main;

import javax.swing.JFrame;

import components.MainPanel;

public class Main
{
	public static void main(String[] args)
	{
		JFrame window = new JFrame("Project Dependencies");
		window.setContentPane(new MainPanel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);
		window.pack();
		window.setVisible(true);
	}
}
