import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.JFrame;

public class TablicaWynikow extends JFrame implements ActionListener {

	private static final long serialVersionUID = -4247704808102254415L;
	String nazwa;
	int pkt;
	int lvl;
	LocalDate data;

	public TablicaWynikow(String nazwa, int pkt, int lvl, LocalDate nowDate) {
		this.nazwa = nazwa;
		this.pkt = pkt;
		this.lvl = lvl;
		this.data = nowDate;
	}

	LocalDate getData() {
		return data;
	}

	int getLvl() {
		return lvl;
	}

	String getNazwa() {
		return nazwa;
	}

	int getPkt() {
		return pkt;
	}

	public static String Rank(int l, int ile, String plik) throws FileNotFoundException, NumberFormatException, ParseException {
		String linia = "";
		String[] dane = null;
		String wyniki = "";

		ArrayList<TablicaWynikow> data = new ArrayList<TablicaWynikow>();
		Scanner scan = new Scanner(new File(plik));

		while (scan.hasNextLine()) {
			linia = scan.nextLine();
			dane = linia.split(";");
			data.add(new TablicaWynikow(dane[0], Integer.parseInt(dane[1]), Integer.parseInt(dane[2]),
					LocalDate.parse(dane[3])));
		}
		scan.close();
		Collections.sort(data, new Comparator<TablicaWynikow>() {
			@Override
			public int compare(TablicaWynikow o1, TablicaWynikow o2) {
				return o2.getPkt() - o1.getPkt();
			}
		});
		int i = 0;
		for (TablicaWynikow str : data) {
			if (l == str.getLvl()) {
				i++;
				if (i <= ile)
					wyniki += i + ". " + str.getNazwa() + " " + str.getPkt() + "\n";
			}
		}
		return wyniki;
	}

	public static void main(String[] args) {
		try {
			System.out.println(Rank(0, 10, "wyniki.txt"));
			System.out.println(Rank(1, 10, "wyniki.txt"));
			System.out.println(Rank(2, 10, "wyniki.txt"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

}