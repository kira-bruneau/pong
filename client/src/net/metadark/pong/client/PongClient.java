package net.metadark.pong.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.metadark.pong.client.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

public class PongClient extends Thread {

	private static String DEFAULT_HOST = "localhost";
	private static int DEFAULT_PORT = 5436;

	private static final int MOVE_UP = 0;
	private static final int MOVE_DOWN = 1;
	private static final int BALL = 2;
	
	private GameScreen game;
	private DataOutputStream output;
	private DataInputStream input;

	private volatile boolean running;

	public PongClient(GameScreen game) {
		this(game, DEFAULT_HOST, DEFAULT_PORT);
	}

	public PongClient(GameScreen game, String host) {
		this(game, host, DEFAULT_PORT);
	}

	public PongClient(GameScreen game, String host, int port) {
		this.game = game;

		SocketHints socketHint = new SocketHints();
		socketHint.connectTimeout = 1000;

		Socket socket = Gdx.net.newClientSocket(Protocol.TCP, host, port, socketHint);
		output = new DataOutputStream(socket.getOutputStream());
		input = new DataInputStream(socket.getInputStream());

		start();
	}

	@Override
	public void run() {
		running = true;

		while (running) {
			try {
				int type = input.readInt();
				switch (type) {
				case MOVE_UP:
					game.getRightPaddle().moveUpPure(input.readBoolean());
					break;
				case MOVE_DOWN:
					game.getRightPaddle().moveDownPure(input.readBoolean());
					break;
				case BALL:
					System.out.println("Do the ball thing");
					break;
				default:
					System.out.println("Bad message");
					break;
				}
			} catch (IOException e) {
				close();
			}
		}
	}

	public void close() {
		running = false;

		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void moveUp(boolean toggle) {
		try {
			output.writeInt(MOVE_UP);
			output.writeBoolean(toggle);
		} catch (IOException e) {
			close();
		}
	}

	public void moveDown(boolean toggle) {
		try {
			output.writeInt(MOVE_DOWN);
			output.writeBoolean(toggle);
		} catch (IOException e) {
			close();
		}
	}

}
