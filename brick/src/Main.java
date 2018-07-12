import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class Main {

	static LightSensor l = new LightSensor(SensorPort.S1);
	static int DIREITA = 1;
	static int ESQUERDA = 2;

	public static void main(String... strings) {
		System.out.println("Aguardando Conexão");
		BTConnection connection = Bluetooth.waitForConnection(0, NXTConnection.PACKET);
		System.out.println("Conectado!");
		int direction = -550;
		int[] result = new int[10000];
		int qtdLinhas = 300;
		while (!Button.ESCAPE.isDown() && qtdLinhas > 0) {
			qtdLinhas--;
			Motor.C.rotate(direction, true);
			long start = System.currentTimeMillis();
			int i = 0;
			while (Motor.C.isMoving()) {
				long time = System.currentTimeMillis();
				if( start < time && i < result.length)
				{
					result[i] = l.getNormalizedLightValue();
					i++;
					start = time;
				}
			}
			sendData(connection, result, i);
			direction = -direction;
			int rotation = 2;
			Motor.A.rotate(rotation, true);
			Motor.B.rotate(rotation);
		}
		byte[] resultInByte = {'@'};
		connection.write(resultInByte, resultInByte.length);
		System.out.println("terminou");
		
		while (true);
		
	}

	private static void sendData(BTConnection connection, int[] data, int interactions) {
		byte[] resultInByte = new byte[1000];
		int bytesPreenchidos = 0;
		byte[] byteNumbers;
		for (int i = 0; i < interactions; i++) {
			byteNumbers = ("" +data[i]).getBytes();
			for (int j = 0; j < byteNumbers.length; j++) {
				resultInByte[bytesPreenchidos] = byteNumbers[j];
				bytesPreenchidos++;
				if(trySend(connection, resultInByte, bytesPreenchidos))
					bytesPreenchidos = 0;
			}
			
			resultInByte[bytesPreenchidos] = 'R';
			bytesPreenchidos++;
			if(trySend(connection, resultInByte, bytesPreenchidos))
				bytesPreenchidos = 0;
		}

		resultInByte[bytesPreenchidos] = '#';
		bytesPreenchidos++;
		while (bytesPreenchidos < resultInByte.length) {
			resultInByte[bytesPreenchidos] = 'O';
			bytesPreenchidos++;
		}
		trySend(connection, resultInByte, bytesPreenchidos);
	}
	
	private static boolean trySend(BTConnection connection, byte[] data, int interaction) {
		if(data.length <= interaction) {
			connection.write(data, data.length);
			System.out.println("send");
			return true;
		}
		return false;
	}

}