import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;

/*
Ein Supermarkt hat n Kassen.
Zu Beginn wird eine Kasse geÃ¶ffnet und dann nach folgender Strategie vorgegangen:
jedes Mal wenn w Kunden bei einer Kasse warten,
wird eine neue Kasse erÃ¶ffnet (solange mÃ¶glich).
Eine Kasse wird geschlossen, wenn m Minuten kein Kunde bedient wird
(mindestens eine Kasse muss jedoch offen bleiben).
Ziel der Simulation: wie sieht eine mÃ¶glichst "optimale" Wahl der Parameter w und m aus,
sodass die Kunden nicht zu lange warten mÃ¼ssen,
aber auch die Kosten fÃ¼r besetzte Kassen nicht zu hoch werden.
 */

public class Supermarkt_Model extends Model
{
	// Zufallszahlengenerator für normale Kunden
	private ContDistExponential kundenAnkunftsZeit;

	// Zufallszahlengenerator für studenten Kunden
	private ContDistExponential studentAnkunftsZeit;

	// Zufallszahlengenerator für wochenend Kunden
	private ContDistExponential wochenendeAnkunftsZeit;

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public double getKundenAnkunftsZeit() {
	   return kundenAnkunftsZeit.sample();
    }
    
    // liefert eine Zufallszahl für studenten Kundenankunftszeit
    public double getStudentenAnkunftsZeit() {
	   return studentAnkunftsZeit.sample();
    }
    
    // liefert eine Zufallszahl für wochenend Kundenankunftszeit
    public double getWochenendeAnkunftsZeit() {
	   return wochenendeAnkunftsZeit.sample();
    }
    
    // Zufallszahlengenerator zur Ermittlung der Kunden Artikel
   	private ContDistUniform kundenArtikel;
    
    // Zufallszahlengenerator zur Ermittlung der Studenten Artikel
   	private ContDistUniform studentenArtikel;
    
    // Zufallszahlengenerator zur Ermittlung der Wochenende Artikel
   	private ContDistUniform wochenendeArtikel;
    
    // liefert eine Zufallszahl für Kunden Artikel
    public double getKundenArtikel() {
        return kundenArtikel.sample();
    }
    
    // liefert eine Zufallszahl für Studenten Artikel
    public double getStudentenArtikel() {
        return studentenArtikel.sample();
    }
    
    // liefert eine Zufallszahl für Wochenende Artikel
    public double getWochenendeArtikel() {
        return wochenendeArtikel.sample();
    }
    
    // Zufallszahlengenerator zur Ermittlung der Artikel pro Minute
   	private ContDistUniform artikelProMinute;
    
    // liefert eine Zufallszahl für Artikel pro Minute
    public double getArtikelproMinute() {
        return artikelProMinute.sample();
    }
    
    // Zufallszahlengenerator zur Ermittlung der eintippzeit
   	private ContDistUniform eintippZeit;
    
    // liefert eine Zufallszahl für die eintippzeit
    public double getEintippZeit() {
        return eintippZeit.sample();
    }
    
    // Zufallszahlengenerator zur Ermittlung der bezahlzeit mit bargeld
   	private ContDistUniform barBezahlZeit;
    
    // liefert eine Zufallszahl für die bezahlzeit mit bargeld
    public double getBarBezahlZeit() {
        return barBezahlZeit.sample();
    }
    
    // Zufallszahlengenerator zur Ermittlung der bezahlzeit mit karte
   	private ContDistUniform karteBezahlZeit;
    
    // liefert eine Zufallszahl für die bezahlzeit mit karte
    public double getKarteBezahlZeit() {
        return karteBezahlZeit.sample();
    }

    //Kassenanzahl + Warteschlangen
	private int kassenAnzahl;
	protected ProcessQueue<KundenProcess>[] kassenWarteschlange;
	protected ProcessQueue<KassaProcess> freieKassaQueue;
	
	//Konstruktur
	public Supermarkt_Model(Model owner, String name, boolean showInReport, boolean showInTrace)
	{
		super(owner, name, showInReport, showInTrace);
	}

	public String description()
	{
		return "Ein Supermarkt hat n Kassen." +
			   "Zu Beginn wird eine Kasse geÃ¶ffnet und dann nach folgender Strategie vorgegangen:" +
			   "jedes Mal wenn w Kunden bei einer Kasse warten," +
			   "wird eine neue Kasse erÃ¶ffnet (solange mÃ¶glich)." +
			   "Eine Kasse wird geschlossen, wenn m Minuten kein Kunde bedient wird" +
			   "(mindestens eine Kasse muss jedoch offen bleiben).";
	}

    public void doInitialSchedules()
    {
        // Prozess zur Erzeugung von Kunden einrichten
        NeuerKundeProcess neuerKunde = new NeuerKundeProcess(this, "Kundenkreation", true);
        
        // Prozess starten
        neuerKunde.activate(new TimeSpan(0.0));
        
		// Eine Kassa öffnen
		KassaProcess kassa = new KassaProcess(this, "Kassa 1", true);
		
		// Kassaprozess starten (= "Kassa wird eroeffnet")
		kassa.activate(new TimeSpan(0.0));
    }

	public void init()
	{
		//Ankunftszeiten initialisieren
		kundenAnkunftsZeit = new ContDistExponential(this, "Kunden Ankunftszeitintervall", 5.0, true, true);	
		kundenAnkunftsZeit.setNonNegative(true);
		
		studentAnkunftsZeit = new ContDistExponential(this, "Studenten Ankunftszeitintervall", 1.0, true, true);	
		studentAnkunftsZeit.setNonNegative(true);
		
		wochenendeAnkunftsZeit = new ContDistExponential(this, "Wochenende Ankunftszeitintervall", 3.0, true, true);	
		wochenendeAnkunftsZeit.setNonNegative(true);
		
		//Artikel initialisieren
		kundenArtikel = new ContDistUniform(this, "Kunden Artikel", 5, 30, true, true);
		studentenArtikel = new ContDistUniform(this, "Studenten Artikel", 1, 10, true, true);
		wochenendeArtikel = new ContDistUniform(this, "Kunden Artikel", 30, 80, true, true);
		
		//Artikel pro Minute initialisieren
		artikelProMinute = new ContDistUniform(this, "Artikel pro Minute", 35, 45, true, true);
		
		//Eintippzeit initialisieren
		eintippZeit = new ContDistUniform(this, "Eintippzeit eines Artikels in sekunden", 4, 7, true, true);
		
		//Bezahlzeiten initialisieren
		barBezahlZeit = new ContDistUniform(this, "Bezahlzeit mit Bargeld in sekunden", 10, 20, true, true);
		karteBezahlZeit = new ContDistUniform(this, "Bezahlzeit mit Karte in sekunden", 18, 22, true, true);
		
		//Kassen, -Warteschlange initialisieren
		kassenAnzahl = 4;
		kassenWarteschlange = new ProcessQueue[kassenAnzahl];

		for(int i = 0; i < kassenAnzahl; i++)
		{
			kassenWarteschlange[i] = new ProcessQueue<KundenProcess>(this, "Kassen Warteschlange " + (i + 1), true, true);
		}
		
		//Freie Kassa Warteschlange
    	freieKassaQueue = new ProcessQueue<KassaProcess>(this, "freie Kassa WS",true, true);
	}
	
	public static void main(String[] args)
	{
		Experiment supermarktExperiment = new Experiment("Supermakrt-Process");
		Supermarkt_Model supermarktModel = new Supermarkt_Model(null, "Supermarkt Model", true, true);
		supermarktModel.connectToExperiment(supermarktExperiment);
		
		supermarktExperiment.tracePeriod(new TimeInstant(660.0), new TimeInstant(690.0));
		supermarktExperiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));
		
		supermarktExperiment.stop(new TimeInstant(4140)); //7:30-19:00 * 6
		supermarktExperiment.start();
		supermarktExperiment.report();
		supermarktExperiment.finish();
	}
}