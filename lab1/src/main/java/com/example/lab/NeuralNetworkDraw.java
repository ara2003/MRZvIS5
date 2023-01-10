package com.example.lab;

import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class NeuralNetworkDraw {
	private final NeuralNetworkModel nnModel;

	private final int width, height;

	public NeuralNetworkDraw(NeuralNetworkModel nnModel, int width, int height) throws IOException {
		this.nnModel = nnModel;
		this.width = width;
		this.height = height;
	}

	public void startRenderingCompress(Image img) {
		final var img2 = nnModel.compress(img);
		start(img, img2);
	}

	public void startRenderingDeCompress(Image img) {
		final var img2 = nnModel.deCompress(img);
		start(img, img2);
	}
	
	public void startRenderingForvard(Image img) {
		final var img2 = nnModel.run(img);
		start(img, img2);
	}

	private void start(Image img1, Image img2) {
		final var frame = new JFrame("");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width * 2, height);

		frame.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_ESCAPE)
					System.exit(0);
			}

		});

		final var contanier = new JPanel(new GridLayout());
		
		img1 = img1.getScaledInstance(width, height, Image.SCALE_REPLICATE);
		img2 = img2.getScaledInstance(width, height, Image.SCALE_REPLICATE);
		
		JLabel firstImageLabel = new JLabel(new ImageIcon(img1));
		contanier.add(firstImageLabel);
		JLabel secondImageLabel = new JLabel(new ImageIcon(img2));
		contanier.add(secondImageLabel);

		frame.add(contanier);

		frame.setLocationRelativeTo(null);
		frame.setFocusable(true);
		frame.setVisible(true);
	}

}
