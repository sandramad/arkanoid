import java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

// import javazoom.jl.player.Player;

public class Start extends Thread {
	static Runnable muzyka = new Start();
	static Thread watek = new Thread(muzyka);
	static boolean loop = true;

	private static final String wav = "start.wav";

	static AudioInputStream inputStream;
	static Clip clip;

	public void run() {
		try {
			inputStream = AudioSystem.getAudioInputStream(new File(wav));
			clip = AudioSystem.getClip();
			clip.open(inputStream);
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

		do {
			clip.start();
		} while (loop);

	}

	public static void main(String[] args) {

		watek.start();
		Arkanoid.start();
	}
}