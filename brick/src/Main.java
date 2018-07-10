
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.USB;

public class Main {

	static LightSensor l = new LightSensor(SensorPort.S1);
	static int DIREITA = 1;
	static int ESQUERDA = 2;

	public static void main(String... strings) {

		LCD.drawString("Aguardando...", 0, 0);
		BTConnection connection = Bluetooth.waitForConnection(0, NXTConnection.PACKET);
		Motor.C.setSpeed(20);
		int direcao = ESQUERDA;
		for (int line = 0; line < 50; line++) {

			LCD.clear();
			LCD.drawString("Conectado Packet!.", 0, 0);

			Motor.C.flt();
			if (direcao == DIREITA)
				Motor.C.forward();
			else
				Motor.C.backward();

			long start_time = System.currentTimeMillis();
			long difference = 0;
			while (difference < 5000) {
				String result;
				if (direcao == ESQUERDA) {
					result = l.getNormalizedLightValue() + "R";
				} else {
					l.getNormalizedLightValue();
					result = "O";
				}

				byte[] resultInByte = result.getBytes();
				connection.write(resultInByte, resultInByte.length);
				long end_time = System.currentTimeMillis();
				difference = end_time - start_time;
			}
			if (direcao == DIREITA)
				direcao = ESQUERDA;
			else
				direcao = DIREITA;
			// }

			if (direcao == ESQUERDA) {
				String result = "#";
				byte[] resultInByte = result.getBytes();
				connection.write(resultInByte, resultInByte.length);
				int rotation = 3;
				Motor.A.rotate(rotation, true);
				Motor.B.rotate(rotation);
			} else {
				String result = "O";
				byte[] resultInByte = result.getBytes();
				connection.write(resultInByte, resultInByte.length);
				int rotation = 0;
				Motor.A.rotate(rotation, true);
				Motor.B.rotate(rotation);
			}
		}
		String result = "@";
		byte[] resultInByte = result.getBytes();
		connection.write(resultInByte, resultInByte.length);
		connection.close();
		/*
		 * while(!Button.LEFT.isDown() && !Button.RIGHT.isDown()); List<Integer> ligths
		 * = new ArrayList<>(); if(Button.LEFT.isDown()) { Motor.C.forward(); }else
		 * if(Button.RIGHT.isDown()) { Motor.C.backward(); } int i = 0; long start_time
		 * = System.currentTimeMillis(); while(!Button.ENTER.isDown()) { //
		 * Motor.A.forward(); // Motor.B.forward();
		 * //ligths.add(l.getNormalizedLightValue()); i++;
		 * if(l.getNormalizedLightValue() < 150) break; } Motor.C.flt();
		 * 
		 * long end_time = System.currentTimeMillis(); long difference =
		 * end_time-start_time; System.out.println("Interações:" + i);
		 * System.out.println("Time:" + difference); System.out.println("Sensor:" +
		 * l.getNormalizedLightValue()); while(!Button.ESCAPE.isDown()) { }
		 */
		/*
		 * LCD.drawString("Aguardando...", 0, 0); BTConnection connection =
		 * Bluetooth.waitForConnection(0, NXTConnection.PACKET);
		 * 
		 * LCD.clear(); LCD.drawString("Conectado Packet!.", 0, 0);
		 * 
		 * byte[] data = new byte[1]; while (!Button.ESCAPE.isPressed()) { if
		 * (Button.LEFT.isPressed()) { data[0] = 1; } else if (Button.RIGHT.isPressed())
		 * { data[0] = 2; } else { data[0] = 0; } connection.write(data, 1); } data[0] =
		 * 3; connection.write(data, 1); connection.close();
		 */

		/*
		 * Motor.A.setSpeed(50); Button.ENTER.waitForPress();
		 * 
		 * int distMov = 0; int distPadrao = 100; //RConsole.openUSB(10000); while
		 * (!Button.ESCAPE.isDown()) { try { if (Button.ENTER.isDown()) {
		 * //RConsole.print("issae"); } while (!Button.ENTER.isDown()) { if
		 * (Button.RIGHT.isDown()) { distPadrao += 10; System.out.println("DistPadrao: "
		 * + distPadrao); } else if (Button.LEFT.isDown()) { distPadrao -= 10;
		 * System.out.println("DistPadrao: " + distPadrao); } Thread.sleep(1000); } for
		 * (int i = 0; i < distPadrao; ++i) { Motor.A.forward(); } //Motor.A.flt();
		 * distMov += distPadrao; System.out.println("DistTotal: " + distMov); } catch
		 * (Exception e) { System.out.println(e.getMessage()); } } /*
		 * System.out.println("Claro:"); Button.ENTER.waitForPress(); l.calibrateHigh();
		 * System.out.println("Escuro:"); Button.ENTER.waitForPress(); l.calibrateLow();
		 * 
		 * atribuirCor("rosa", 4); atribuirCor("azulEscuro", 2); atribuirCor("verde",
		 * 1); atribuirCor("azClaro", 3); atribuirCor("vermelho", 0);
		 * atribuirCor("default", 99);
		 * 
		 * while(!Button.ESCAPE.isDown()){ printCor(l.getLightValue()); try {
		 * Thread.sleep(1000); } catch (InterruptedException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } }
		 * 
		 * 
		 * iniciaMapeamento();
		 */
	}

	private static void sendData() {
		// TODO Auto-generated method stub

	}

}