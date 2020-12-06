import java.io.*;
import javazoom.jl.player.Player;

public class Start extends Thread  {
	static Runnable muzyka = new Start();
	static Thread watek = new Thread(muzyka);
	private static final String mp3 = "start.mp3";
	boolean loop = true;
	Player player;

	public void run() {
		try {
			do {
				player = new Player(new FileInputStream(mp3));
				player.play();
			} while (loop);
		} catch (Exception e) {
			System.out.printf("BŁĄD: plik '%s' nie istnieje\n", mp3);
			System.exit(1);
		}
	}

	public static void main(String[] args) {

		watek.start();
		new Arkanoid().start();
	}
}