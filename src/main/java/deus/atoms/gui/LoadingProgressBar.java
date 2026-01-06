package deus.atoms.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoadingProgressBar {
	public static int loadingProgress = 0;
	public static int max = 100;
	public static int min = 0;
	public static void showBar() {
		JFrame frame = new JFrame("Frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(frame.getWidth() + 200, frame.getHeight() + 40);

		//Button

		frame.requestFocus();

		//Progress Bar
		JProgressBar progressBar = new JProgressBar();
		progressBar.setMaximum(max);
		progressBar.setMaximum(min);
		SwingUtilities.invokeLater(() -> {
			progressBar.setValue(loadingProgress);
		});
		//Text for progress bar
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progressBar);
		panel.add(new JLabel("Please wait......."), BorderLayout.PAGE_START);

		//linking
		panel.add(progressBar);
		frame.add(panel, BorderLayout.SOUTH);

		progressBar.addChangeListener(changeEvent -> {
			if (progressBar.getValue() == 100) {
				frame.dispose();
			}
		});

	}


}
