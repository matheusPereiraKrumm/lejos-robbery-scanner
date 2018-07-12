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
		defaultRun();
		//readData();
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

	static void defaultRun() throws IOException {
		System.out.println("Buscando...");
		NXTConnector connector = new NXTConnector();
		connector.addLogListener(new NXTCommLogListener() {

			@Override
			public void logEvent(Throwable throwable) {
				// throwable.printStackTrace();
			}

			@Override
			public void logEvent(String message) {
				// System.out.println(">>> " + message);
			}
		});

		processNXTConnection(connector);

	}

	private static void processNXTConnection(NXTConnector connector) throws IOException {
		int error = 1;
		List<Character> charstr = new ArrayList<>();
		try {
			NXTInfo[] infos = connector.search("", null, NXTCommFactory.BLUETOOTH);
			if (infos.length == 0) {
				System.out.println("Nenhum NXT encontrado.");
				Thread.sleep(5000);
				return;
			}

			if (!connector.connectTo(infos[0], NXTComm.PACKET)) {
				System.out.println(new Date() + "Não foi possível conectar ao NXT");
				Thread.sleep(5000);
				return;
			}

			NXTComm comm = connector.getNXTComm();
			InputStream inputStream = comm.getInputStream();
			char code;
			while ((code = (char) inputStream.read()) != '@') {
				if (code != 'O')
					charstr.add(code);
				else
					System.out.println(code);
			}
			System.out.println("saiu!");

			inputStream.close();
			comm.close();
			error = 0;

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    StringBuilder builder = new StringBuilder(charstr.size());
	    for(Character ch: charstr)
	    {
	        builder.append(ch);
	    }
		String retorno = builder.toString();
		System.out.println("juntou");
		saveData("retorno.txt", retorno);
		System.out.println("salvou");
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
				if (min > point)
					min = point;
				if (max < point)
					max = point;
			}
		}
		ArrayList<String[]> arrumado = arrumarLinhas(finals);
		int proporcao = 10;
		ArrayList<ArrayList<Integer>> pontos = new ArrayList<>();
		for (int i = 0; i < arrumado.size(); i++) {
			ArrayList<Integer> linha = new ArrayList<>();
			proporcao = (arrumado.get(i).length / 1000) + 1;
			for (int j = 0; j < 1000; j++) {
				int startPoint = j * proporcao;
				int endPoint = ((j + 1) * proporcao);
				if (endPoint >= arrumado.get(i).length)
					endPoint = arrumado.get(i).length - 1;
				int acumulador = 0;
				for (int k = startPoint; k < endPoint; k++) {
					acumulador += Integer.parseInt(arrumado.get(i)[k]) - min;
				}
				if (endPoint <= startPoint)
					linha.add(acumulador);
				else
					linha.add(acumulador / ((endPoint - startPoint) + 1));
			}
			pontos.add(linha);
		}
		ArrayList<ArrayList<Integer>> pontosAlinhados = alinharImagem(pontos);

		JFrame j = new JFrame();
		j.setTitle("Telinha do Scan");
		j.setLocationRelativeTo(null);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setSize(1200, 600);
		JPanel p = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for (int i = 0; i < pontosAlinhados.size(); i++) {
//					if (i % 2 != 0) {
						for (int j = 0; j < pontosAlinhados.get(i).size(); j++) {
							int value = pontosAlinhados.get(i).get(j);
							value = value > 255 ? 255 : value;
							Color c = new Color(value, value, value);
							g.setColor(c);
							g.drawLine(j + 10, i + 10, j + 10, i + 10);
						}
//					} else {
//						for (int j = pontos.get(i).size() - 1; j >= 0; j--) {
//							int value = pontos.get(i).get(j);
//							value = value > 255 ? 255 : value;
//							Color c = new Color(value, value, value);
//							g.setColor(c);
//							g.drawLine(pontos.get(i).size() - j + 9, i + 10, pontos.get(i).size() - j + 9, i + 10);
//						}
//					}
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

	private static ArrayList<ArrayList<Integer>> alinharImagem(ArrayList<ArrayList<Integer>> pontos) {
		ArrayList<ArrayList<Integer>> resultado = new ArrayList<>(pontos.size());
		ArrayList<Integer> pontosPretosLinhas = new ArrayList<>(pontos.size());
		for (ArrayList<Integer> linha : pontos) {
			int i = linha.size()-1;
			boolean naoEncontrou = true;
			int lastPoint = linha.get(i);
			while(naoEncontrou) {
				if(linha.get(i) != lastPoint)
					naoEncontrou = false;
				i--;
			}
			pontosPretosLinhas.add(linha.size()-i);
		}
		int minPtPretos = Integer.MAX_VALUE;
		int maxPtPretos = 0;
		for (Integer pontosPretos : pontosPretosLinhas) {
			if(minPtPretos > pontosPretos)
				minPtPretos = pontosPretos;
			if(maxPtPretos < pontosPretos)
				maxPtPretos = pontosPretos;
		}
		int difMinMaxPtPreto = maxPtPretos - minPtPretos;
		for (int i = 0; i < pontos.size(); i++) {
			ArrayList<Integer> linha = pontos.get(i);
			int ptsPretos = pontosPretosLinhas.get(i);
			int valorConsideracao = maxPtPretos - ptsPretos;
			int corte = maxPtPretos - ptsPretos;
			ArrayList<Integer> novaLinha = new ArrayList<>();
			for (int j = corte; j < linha.size() - (maxPtPretos - corte); j++) {
				novaLinha.add(linha.get(j));
			}
			resultado.add(novaLinha);
		}
		
		return resultado;
	}

	private static ArrayList<String[]> arrumarLinhas(ArrayList<String[]> finals) {
		ArrayList<String[]> result = new ArrayList<>();
		for (int i = 0; i < finals.size(); i++) {
			String[] linha = new String[finals.get(i).length];
			int j = 0;
			int linhaAntiga = 0;
			int interadorAntigo = 1;
			if((i % 2) == 0) {
				linhaAntiga = finals.get(i).length -1;
				interadorAntigo = -1;
			}
			while (j < finals.get(i).length) {
				linha[j] = finals.get(i)[linhaAntiga];
				
				linhaAntiga += interadorAntigo;
				j++;
			}
			
			result.add(linha);
		}
		return result;
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