package server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommInputStream;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTCommOutputStream;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;

public class Main {

	final double xmax = 1.6;
	final double xmin = -1.6;
	final double ymax = 1.6;
	final double ymin = -1.6;
	final int itmax = 128;
	final int swid = 640;
	final int shei = 480;
	final double cz = -0.74543; // Or any other number. Will probably
	final double cw = 0.11301; // be replaced by Math.random() later
	double z, w, q = 0;
	double x, y = 0;
	int it = 0;

	public static void main(String[] args) throws IOException {
		Main m = new Main();
		//defaultRun();
		readData();
	}

	private static void readData() {
		String retorno;
		try {
			retorno = readFile("retorno.txt", Charset.defaultCharset());
			retorno = retorno.replaceAll("\n", "");

			String[] finals = retorno.split("#");
			ArrayList<String[]> list = new ArrayList<>();
			for (int i = 0; i < finals.length; i++) {
				list.add(finals[i].split("R"));
			}
			paint(list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}


	static void readFile() throws IOException{
		System.out.println("Buscando...");
		NXTConnector connector = new NXTConnector();
		connector.addLogListener(new NXTCommLogListener() {

			@Override
			public void logEvent(Throwable throwable) {
				//throwable.printStackTrace();
			}

			@Override
			public void logEvent(String message) {
				//System.out.println(">>> " + message);
			}
		});

		processNXTConnection(connector);

	}

	private static void processNXTConnection(NXTConnector connector) throws IOException {
		int error = 1;
		List<Character> charstr = new ArrayList<>();
		while (error == 1) {
			try {
				NXTInfo[] infos = connector.search("", null, NXTCommFactory.BLUETOOTH);
				if (infos.length == 0) {
					System.out.println("Nenhum NXT encontrado.");
					Thread.sleep(5000);
					continue;
				}

				if (!connector.connectTo(infos[0], NXTComm.PACKET)) {
					System.out.println(new Date() +  "Não foi possível conectar ao NXT");
					Thread.sleep(5000);
					continue;
				}

				NXTComm comm = connector.getNXTComm();
				InputStream inputStream = comm.getInputStream();
				char code;
				while ((code = (char)inputStream.read()) != '@') {
					if(code != 'O')
						charstr.add(code);
				}

				inputStream.close();
				comm.close();
				error = 0;

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		char[] stockArr = new char[charstr.size()];
		String retorno = "";
		for (int i = 0; i < charstr.size(); i++) {
			retorno += charstr.get(i);
		}
		saveData("retorno.txt", retorno);
		String[] finals = retorno.split("#");
		ArrayList<String[]> list = new ArrayList<>();
		for (int i = 0; i < finals.length; i++) {
			list.add(finals[i].split("R"));
		}
		paint(list);
	}

	private static void saveData(String file, String data) {
		try (PrintWriter out = new PrintWriter(file)) {
		    out.println(data);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static int min = Integer.MAX_VALUE;
	static int max = 0;
	
	private static void paint(ArrayList<String[]> finals) {
		for (int i = 0; i < finals.size(); i++) {
			for (int j = 0; j < finals.get(i).length; j++) {
				int point = Integer.parseInt(finals.get(i)[j]);
				if(min > point) min = point;
				if(max < point) max = point;
			}
		}
		JFrame j = new JFrame();
		j.setTitle("Telinha do Scan");
		j.setLocationRelativeTo(null);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setSize(1200, 600);
		JPanel p = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				// g.drawRect(200, 200, 200, 200);
				int tamanho = 1;
				for (int i = 0; i < finals.size() ; ++i) {
					
					int acumulador = 0;
					int coluna = 50;
					int contador = 0;
					for (int j = 0; j < finals.get(i).length; j++) {
						int value = Integer.parseInt(finals.get(i)[j]) - min;
						value = value > 255 ? 255 : value;
						acumulador += value;
						contador++;
						if(contador == 4) {
							coluna++;
							int novoValue = acumulador/contador;
							acumulador = 0;
							contador = 0;
							Color c = new Color(value, value, value);
							g.setColor(c);
							g.drawLine(coluna, (i+1)*tamanho, coluna, ((i+1)*tamanho)+tamanho);
						}
					}
					if(contador >= 1) {
						coluna++;
						int novoValue = acumulador/contador;
						Color c = new Color(novoValue, novoValue, novoValue);
						g.setColor(c);
						g.drawLine(coluna, (i+1)*tamanho, coluna, ((i+1)*tamanho)+tamanho);
					}
				}
			}
		};
		j.add(p);
		// p.paintComponent(p);
		// p.getGraphicsConfiguration().
		// Graphics g2d = new Graphics();
		// g2d.drawLine(0, 30, 0, 30);

		j.setVisible(true);
		
	}

	class Panel2 extends JPanel {

		Panel2() {
			// set a preferred size for the custom panel.
			setPreferredSize(new Dimension(420, 420));
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.drawString("BLAH", 20, 20);
			// g.drawRect(200, 200, 200, 200);

		}
	}

}