import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import java.awt.Toolkit;

class Arkanoid extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;

	static String tytul = "Arkanoid";
	static JLabel pytanie = new JLabel("Jaki poziom wybierasz?");
	static JLabel uwaga = new JLabel("Musisz wybrać poziom");
	static JLabel wyjsc = new JLabel("Czy na pewno chcesz wyjść z gry?");
	static JLabel imie = new JLabel("Jak masz na imię?");

	static int SZEROKOSC = 750; // wielkość małej planszy
	static int WYSOKOSC = 600; // wysokość - dla wszystkich taka sama

	static int KLOCKIX = 5; // ilość kloców w rzędzie
	static int KLOCKIY = 1; // ilość rzędów

	static final double PROMIEN = 7.0; // Piłki
	static double PILKA_PREDKOSC = 0; // prędkość

	static final double PALETKA_SZEROKOSC = 100.0; // szerokosc paletki
	static final double PALETKA_WYSOKOSC = 10.0; // Wysokość paletki
	static double PALETKA_PREDKOSC = 0; // prędkosc posunięć

	static final double KLOCEK_SZEROKOSC = 40.0; // szrokość klocków
	static final double KLOCEK_WYSOKOSC = 10.0; // wysokość klocków
	static final int ZYCIA = 5; // ilość żyć gracza

	static double PREDKOSC = 0; // prędkosc zależna od lvl
	private static int ruchy = 0; // ilość wykonanych ruchów w rundzie

	static int LVL = -1;
	static String name = "Anonim";
	private boolean tryAgain;
	private static boolean uruchom;
	private boolean pauza = true;

	private Paletka paletka = new Paletka(SZEROKOSC / 2, WYSOKOSC - 50);
	private Pilka pilka = new Pilka(SZEROKOSC / 2, WYSOKOSC - 60);
	private List<Klocek> klocki = new ArrayList<Arkanoid.Klocek>();
	private Wyniki Wyniki = new Wyniki();
	private String plec = "eś";
	private String komunikatRuchy = " ruchów";
	private String komunikatKloc = " bloków";

	private double ostatnio;
	private double teraz;
	private int kloc = KLOCKIX * KLOCKIY;

	static double predkoscX = PILKA_PREDKOSC;
	static double predkoscY = PILKA_PREDKOSC;

	abstract class GameObject {
		abstract double lewo();

		abstract double prawo();

		abstract double gora();

		abstract double dol();
	}

	private static Font font(int w) {
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("poppins.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(f);
		} catch (Exception e) {
			System.out.println(e);
		}
		Font u = new Font("Poppins", Font.PLAIN, w);
		return u;
	}

	String name() {
		imie.setFont(font(10));
		imie.setForeground(Color.WHITE);
		if (name.equals("Anonim"))
			do
				name = JOptionPane.showInputDialog(null, imie, tytul, JOptionPane.QUESTION_MESSAGE);
			while (name == null || name.equals(""));
		name = (name.charAt(0) + "").toUpperCase() + name.substring(1).toLowerCase();
		return name;
	}

	class Rectangle extends GameObject {

		double x, y;
		double sizeX;
		double sizeY;

		double lewo() {
			return x - sizeX / 2.0;
		}

		double prawo() {
			return x + sizeX / 2.0;
		}

		double gora() {
			return y - sizeY / 2.0;
		}

		double dol() {
			return y + sizeY / 2.0;
		}

	}

	class Wyniki {

		int wynik = 0;
		int zyc = ZYCIA;
		boolean wygrana = false;
		boolean koniec = false;
		String text = "";

		Font font;

		Wyniki() {
			font = font(10);
			text = "Witaj, wciśnij SPACJĘ, aby rozpocząć";
		}

		String odmianaRuchy() {
			name();
			if (name().substring(name().length() - 1).equals("a"))
				plec = "aś";
			if (ruchy == 1)
				komunikatRuchy = " ruch";
			else if (ruchy % 10 >= 2 && ruchy % 10 <= 4 && ((int) (ruchy / 10) != 1))
				komunikatRuchy = " ruchy";
			else
				komunikatRuchy = " ruchów";
			return komunikatRuchy;
		}

		void podsumowanie() {
			wynik += 1 * zyc;

			if (kloc <= 1) {
				name();
				String komunikatZyc = "";
				if (zyc == ZYCIA)
					komunikatZyc = "i został Ci komplet żyć";
				else if (zyc == 1)
					komunikatZyc = "i zostało Ci ostatnie życie";
				else
					komunikatZyc = "i zostały Ci " + zyc + " życia";
				if (name().substring(name().length() - 1).equals("a"))
					plec = "aś";
				wygrana = true;
				text = name() + " wygrał" + plec + "! \nTwój wynik: " + wynik + " na " + (ZYCIA * KLOCKIX * KLOCKIY)
						+ " punktów\n" + komunikatZyc + "\nWykonał" + plec + ": " + ruchy + odmianaRuchy()
						+ "\n\nWciśnij Enter aby zrestartować" + "\n ESC wychodzi z gry";
			} else
				terazWyniki();
		}

		void koniec() {
			zyc--;
			if (zyc == 0) {
				name();
				if (name().substring(name().length() - 1).equals("a"))
					plec = "aś";
				if (kloc == 1)
					komunikatKloc = "Został Ci: " + kloc + "  blok, był" + plec + " blisko";
				else if (kloc % 10 >= 2 && kloc % 10 <= 4 && ((int) (kloc / 10) != 1))
					komunikatKloc = "Zostały Ci: " + kloc + "  bloki, mało zabrakło";
				else
					komunikatKloc = "Zostało Ci: " + kloc + " bloków, spróbuj jeszcze raz";
				koniec = true;
				text = name() + " przegrał" + plec + "! \nTwój wynik: " + wynik + " na " + (ZYCIA * KLOCKIX * KLOCKIY)
						+ " punktów\nWykonał" + plec + ": " + ruchy + odmianaRuchy() + "\n" + komunikatKloc
						+ "\n\nWciśnij Enter aby zrestartować" + "\n ESC wychodzi z gry";
			} else
				terazWyniki();
		}

		void terazWyniki() {
			text = "Wynik: " + wynik + "  Życie: " + zyc;
		}

		void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			if (wygrana || koniec) {
				name();
				if (LVL == 0)
					font = font.deriveFont(15f);
				else if (LVL == 1)
					font = font.deriveFont(25f);
				else
					font = font.deriveFont(35f);
				Color color;
				if (wygrana == true) {
					color = new Color(0, 100, 50);
				} else {
					color = new Color(100, 0, 50);
				}
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				FontMetrics fontMetrics = g2.getFontMetrics(font);
				GradientPaint gColor1 = new GradientPaint(0, 0, color, SZEROKOSC / 2, 0, Color.BLACK);
				g2.setPaint(gColor1);
				g2.fillRect(0, 0, SZEROKOSC / 2, WYSOKOSC);
				GradientPaint gColor2 = new GradientPaint(SZEROKOSC / 2, 0, Color.BLACK, SZEROKOSC - 10, 0, color);
				g2.setPaint(gColor2);
				g2.fillRect(SZEROKOSC / 2 - 2, 0, SZEROKOSC / 2, WYSOKOSC);
				g2.setColor(Color.WHITE);
				g2.setFont(font);
				int titleHeight = fontMetrics.getHeight();
				int lineNumber = 1;
				for (String line : text.split("\n")) {
					int titleLen = fontMetrics.stringWidth(line);
					g2.drawString(line, (SZEROKOSC / 2) - (titleLen / 2), (SZEROKOSC / 6) + (titleHeight * lineNumber));
					lineNumber++;
				}
			} else {
				font = font.deriveFont(10f);
				FontMetrics fontMetrics = g2.getFontMetrics(font);
				g2.setColor(Color.WHITE);
				g2.setFont(font);
				int titleLen = fontMetrics.stringWidth(text);
				g2.drawString(text, (SZEROKOSC / 2) - (titleLen / 2), WYSOKOSC - 15);

			}
		}

	}

	class Paletka extends Rectangle {

		double predkosc = 0.0;

		Paletka(double x, double y) {
			this.x = x;
			this.y = y;
			this.sizeX = PALETKA_SZEROKOSC;
			this.sizeY = PALETKA_WYSOKOSC;
		}

		void update() {
			if (LVL == 0) {
				PREDKOSC = 0.5;
			} else if (LVL == 1) {
				PREDKOSC = 0.75;
			} else {
				PREDKOSC = 1;
			}
			if (PREDKOSC > 0)
				x += predkosc * PREDKOSC;
		}

		void stop() {
			predkosc = 0.0;
		}

		void ruchLewo() {
			if (lewo() > 0.0 && pauza == false) {
				predkosc = -PALETKA_PREDKOSC;
			} else {
				predkosc = 0.0;
			}
		}

		void ruchPrawo() {
			if (prawo() < SZEROKOSC && pauza == false) {
				predkosc = PALETKA_PREDKOSC;
			} else {
				predkosc = 0.0;
			}
		}

		void paintComponent(Graphics g) {
			g.setColor(Color.ORANGE);
			g.fillRect((int) (lewo()), (int) (gora()), (int) sizeX, (int) sizeY);
		}

	}

	class Klocek extends Rectangle {

		boolean zniszczone = false;

		Klocek(double x, double y) {
			this.x = x;
			this.y = y;
			this.sizeX = KLOCEK_SZEROKOSC;
			this.sizeY = KLOCEK_WYSOKOSC;

		}

		private Color losujColor() {
			Color color = new Color((int) (Math.random() * 127 + 128), (int) (Math.random() * 127 + 128),
					(int) (Math.random() * 127 + 128));
			return color;
		}

		private Color losujColor2() {

			Color color2 = new Color((int) (Math.random() * 127), (int) (Math.random() * 127),
					(int) (Math.random() * 127));
			return color2;
		}

		void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			GradientPaint gColor = new GradientPaint((int) lewo(), (int) gora(), losujColor(),
					(int) (lewo() + sizeX / 2), (int) gora(), losujColor2());
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setPaint(gColor);
			g2.fillRect((int) lewo(), (int) gora(), (int) sizeX, (int) sizeY);
		}

	}

	class Pilka extends GameObject {

		double x, y;
		double promien = PROMIEN;
		double predkoscX = PILKA_PREDKOSC;
		double predkoscY = PILKA_PREDKOSC;

		Pilka(int x, int y) {
			this.x = x;
			this.y = y;
		}

		void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor(Color.LIGHT_GRAY);
			g2.fillOval((int) lewo(), (int) gora(), (int) promien * 2, (int) promien * 2);

		}

		void update(Wyniki Wyniki, Paletka paletka) {
			if (pauza == false) {
				if (LVL == 0)
					PREDKOSC = 0.5;
				else if (LVL == 1)
					PREDKOSC = 0.75;
				else
					PREDKOSC = 1;
				x += predkoscX * PREDKOSC;
				y += predkoscY * PREDKOSC;
			}
			if (lewo() < 0 && pauza == false)
				predkoscX = PILKA_PREDKOSC;
			else if (prawo() > SZEROKOSC)
				predkoscX = -PILKA_PREDKOSC;

			if (gora() < 0 && pauza == false)
				predkoscY = PILKA_PREDKOSC;

			else if (dol() > WYSOKOSC && pauza == false) {
				predkoscY = -PILKA_PREDKOSC;
				x = paletka.x;
				y = paletka.y;
				Wyniki.koniec();
			}
		}

		double lewo() {
			return x - promien;
		}

		double prawo() {
			return x + promien;
		}

		double gora() {
			return y - promien;
		}

		double dol() {
			return y + promien;
		}

	}

	boolean jestZderzenie(GameObject mA, GameObject mB) {
		return mA.prawo() >= mB.lewo() && mA.lewo() <= mB.prawo() && mA.dol() >= mB.gora() && mA.gora() <= mB.dol();
	}

	void testZderzenia(Paletka mPaletka, Pilka mPilka) {
		if (!jestZderzenie(mPaletka, mPilka))
			return;
		mPilka.predkoscY = -PILKA_PREDKOSC;
		if (mPilka.x < mPaletka.x)
			mPilka.predkoscX = -PILKA_PREDKOSC;
		else
			mPilka.predkoscX = PILKA_PREDKOSC;
	}

	void testZderzenia(Klocek mKlocek, Pilka mPilka, Wyniki Wyniki) {
		if (!jestZderzenie(mKlocek, mPilka))
			return;

		mKlocek.zniszczone = true;

		Wyniki.podsumowanie();

		double overlapLewo = mPilka.prawo() - mKlocek.lewo();
		double overlapPrawo = mKlocek.prawo() - mPilka.lewo();
		double overlapGora = mPilka.dol() - mKlocek.gora();
		double overlapBottom = mKlocek.dol() - mPilka.gora();

		boolean pilkaZLewo = overlapLewo < overlapPrawo;
		boolean pilkaZGora = overlapGora < overlapBottom;

		double minOverlapX = pilkaZLewo ? overlapLewo : overlapPrawo;
		double minOverlapY = pilkaZGora ? overlapGora : overlapBottom;

		if (minOverlapX < minOverlapY)
			mPilka.predkoscX = pilkaZLewo ? -PILKA_PREDKOSC : PILKA_PREDKOSC;
		else
			mPilka.predkoscY = pilkaZGora ? -PILKA_PREDKOSC : PILKA_PREDKOSC;

	}

	void inicjujKlocki(List<Klocek> klocki) {
		klocki.clear();

		for (int iX = 0; iX < KLOCKIX; ++iX) {
			for (int iY = 0; iY < KLOCKIY; ++iY) {
				klocki.add(new Klocek((iX + 1) * (KLOCEK_SZEROKOSC + 5) + 20, (iY + 5) * (KLOCEK_WYSOKOSC + 5) + 20));
			}
		}
	}

	Arkanoid() {

		if (LVL == 0) {
			SZEROKOSC = 300;
			KLOCKIX = 5;
			KLOCKIY = 1;
			kloc = KLOCKIX * KLOCKIY;
		} else if (LVL == 1) {
			SZEROKOSC = 530;
			KLOCKIX = 10;
			KLOCKIY = 3;
			kloc = KLOCKIX * KLOCKIY;
		} else {
			SZEROKOSC = 750;
			KLOCKIX = 15;
			KLOCKIY = 5;
			kloc = KLOCKIX * KLOCKIY;
		}
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		if (LVL >= 0)
			this.setSize(SZEROKOSC, WYSOKOSC);
		this.setVisible(true);
		this.addKeyListener(this);
		this.setLocationRelativeTo(null); // aby okno było po środku;

		this.createBufferStrategy(2);

		inicjujKlocki(klocki);

	}

	void run() {
		paletka = new Paletka(SZEROKOSC / 2, WYSOKOSC - 50);
		pilka = new Pilka(SZEROKOSC / 2, WYSOKOSC - 60);
		uruchom = true;

		while (uruchom) {

			long time1 = System.currentTimeMillis();

			if (!Wyniki.koniec && !Wyniki.wygrana) {
				tryAgain = false;
				update();
				plansza(pilka, klocki, Wyniki);

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} else {
				if (tryAgain) {
					tryAgain = false;
					this.setVisible(false);
					pauza = true;
					ruchy = 0;
					name = "Anonim";

					new Arkanoid().run();
				}
			}

			long time2 = System.currentTimeMillis();
			double elapsedTime = time2 - time1;
			String poziom = "";
			ostatnio = elapsedTime;
			if (LVL == 0)
				poziom = "Prosty: ";
			else if (LVL == 1)
				poziom = "Średni: ";
			else if (LVL == 2)
				poziom = "Trudny: ";
			poziom += KLOCKIX + "x" + KLOCKIY;
			this.setTitle("Arkanoid " + poziom);

		}

		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

	}

	private void update() {

		teraz += ostatnio;

		for (; teraz >= 1; teraz -= 1) {

			pilka.update(Wyniki, paletka);
			paletka.update();
			testZderzenia(paletka, pilka);

			Iterator<Klocek> it = klocki.iterator();
			while (it.hasNext()) {
				Klocek klocek = it.next();
				testZderzenia(klocek, pilka, Wyniki);
				if (klocek.zniszczone) {
					it.remove();
					Toolkit.getDefaultToolkit().beep();
					kloc--;
				}
			}

		}
	}

	private void plansza(Pilka pilka, List<Klocek> klocki, Wyniki Wyniki) {
		BufferStrategy bf = this.getBufferStrategy();
		Graphics g = null;

		try {

			g = bf.getDrawGraphics();
			String imageFile = "tlo" + LVL + ".jpg";
			try {
				g.drawImage(ImageIO.read(new File(imageFile)), 0, 0, SZEROKOSC, WYSOKOSC, null);
			} catch (IOException e) {
				e.printStackTrace();
			}

			pilka.paintComponent(g);
			paletka.paintComponent(g);
			for (Klocek klocek : klocki) {
				klocek.paintComponent(g);
			}
			Wyniki.paintComponent(g);

		} finally {
			g.dispose();
		}

		bf.show();
		Toolkit.getDefaultToolkit().sync();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			int wyjdz;
			pauza = true;
			wyjsc.setFont(font(12));
			wyjsc.setForeground(Color.WHITE);
			wyjdz = JOptionPane.showOptionDialog(null, wyjsc, tytul, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new String[] { "Tak", "Nie" }, "Nie");
			if (wyjdz == 0)
				uruchom = false;
			else
				pauza = false;
		}
		if (event.getKeyCode() == KeyEvent.VK_ENTER) {
			pytanie.setFont(font(12));
			pytanie.setForeground(Color.WHITE);
			if (name != null)
				LVL = JOptionPane.showOptionDialog(null, pytanie, tytul, JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, new String[] { "Prosty", "Średni", "Trudny" }, "Trudny");
			kloc = 0;
			tryAgain = true;
		}
		if (event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_SPACE) {
			PILKA_PREDKOSC = 0.7;
			PALETKA_PREDKOSC = 0.6;
			predkoscY = PILKA_PREDKOSC;
			predkoscX = PILKA_PREDKOSC;
			if (LVL == 0)
				PREDKOSC = 0.5;
			else if (LVL == 1)
				PREDKOSC = 0.75;
			else
				PREDKOSC = 1;
			pauza = false;
		}
		if (event.getKeyCode() == KeyEvent.VK_DOWN || event.getKeyCode() == KeyEvent.VK_P) {
			pauza = true;
		}

		switch (event.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			ruchy++;
			paletka.ruchLewo();
			break;
		case KeyEvent.VK_RIGHT:
			ruchy++;
			paletka.ruchPrawo();
			break;

		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
			paletka.stop();
			break;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	public void start() {
		pytanie.setFont(font(12));
		pytanie.setForeground(Color.WHITE);
		uwaga.setFont(font(12));
		uwaga.setForeground(Color.WHITE);
		UIManager.put("TextField.font", font(15));
		UIManager.put("OptionPane.background", Color.BLACK);
		UIManager.put("Panel.background", Color.BLACK);
		UIManager.put("Button.font", font(12));
		UIManager.put("Button.background", Color.ORANGE);

		LVL = JOptionPane.showOptionDialog(null, pytanie, tytul, JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] { "Prosty", "Średni", "Trudny" }, "Trudny");

		if (LVL > -1)
			new Arkanoid().run();
		else
			LVL = JOptionPane.showOptionDialog(null, uwaga, tytul, JOptionPane.CLOSED_OPTION,
					JOptionPane.WARNING_MESSAGE, null, new String[] { "Rozumiem" }, "Rozumiem");
	}

	public static void main(String[] args) {
		new Arkanoid().start();
		System.exit(0);
	}

}