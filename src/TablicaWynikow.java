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

	public static String Rank() throws FileNotFoundException, NumberFormatException, ParseException {
		String linia = "";
		String[] dane = null;
		String wyniki = "LP.\tImię\tPKT\tLVL\tData\n";

		ArrayList<TablicaWynikow> data = new ArrayList<TablicaWynikow>();
		Scanner scan = new Scanner(new File("wyniki.txt"));

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
		String lvl="";
		for (TablicaWynikow str : data) {
			if(str.getLvl()==0) lvl ="Prosty";
			else if(str.getLvl()==1) lvl ="Średni";
			else lvl = "Trudny";
			i++;
			if (i < 11)
				wyniki += i + "\t" + str.getNazwa() + "\t" + str.getPkt() + "\t" + lvl + "\t" + str.getData()
						+ "\n";
		}
		return wyniki;
	}

	public static void main(String[] args) {
		try {
			System.out.println(Rank());
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