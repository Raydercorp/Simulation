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

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public double getKundenAnkunftsZeit() {
	   return kundenAnkunftsZeit.sample();
    }	
    
	// Zufallszahlengenerator für Kassa öffnet zeit
	private ContDistUniform kassaOeffnetZeit;

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public double getKassaOeffnetZeit() {
	   return kassaOeffnetZeit.sample();
    }
    
    // Zufallszahlengenerator zur Ermittlung der Artikel eines kleinen Einkaufs
   	private ContDistUniform kleinerEinkauf;
    
    // Zufallszahlengenerator zur Ermittlung der Artikel eines mittleren Einkaufs
   	private ContDistUniform mittlererEinkauf;
    
    // Zufallszahlengenerator zur Ermittlung der Artikel eines großen Einkaufs
   	private ContDistUniform grosserEinkauf;
    
    // liefert eine Zufallszahl für einen kleinen einkauf
    public double getKleinerEinkauf() {
        return kleinerEinkauf.sample();
    }
    
    // liefert eine Zufallszahl für einen mittleren einkauf
    public double getMittlererEinkauf() {
        return mittlererEinkauf.sample();
    }
    
    // liefert eine Zufallszahl für einen großen einkauf
    public double getGrosserEinkauf() {
        return grosserEinkauf.sample();
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
    
    //Maximale Anzahl an Kunden bevor eine kassa öffnet
    private int maxKunden;
    private int aktuelleMaxKunden;
    
    public int getMaxKunden()
    {
    	return maxKunden;
    }
    
    public int getAktuelleMaxKunden()
    {
    	return aktuelleMaxKunden;
    }
    
    public void setAktuelleMaxKunden(int aktuelleMaxKunden)
    {
    	this.aktuelleMaxKunden = aktuelleMaxKunden;
    }
    
    //Zeit bevor eine Kassa schließt
    private int kassaSchliessen;
    
    public int getKassaSchliessen()
    {
    	return kassaSchliessen;
    }

    //Kassenanzahl + Warteschlangen
	private int maxKassenAnzahl;
	private int aktiveKassenAnzahl;
	protected ProcessQueue<KundenProcess>[] kassenWarteschlange;
	protected KassaProcess[] kassa;
	protected ProcessQueue<KassaProcess> freieKassaQueue;
	
	public int getMaxKassenAnzahl()
	{
		return maxKassenAnzahl;
	}
	
	public int getAktiveKassenAnzahl()
	{
		return aktiveKassenAnzahl;
	}
	
	public void setAktiveKassenAnzahl(int aktiveKassenAnzahl)
	{
		this.aktiveKassenAnzahl = aktiveKassenAnzahl;
	}
	
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
		kassa[0] = new KassaProcess(this, "Kassa 1", true);
		
		kassa[0].setKassaNummer(0);
		
		// Kassaprozess starten (= "Kassa wird eroeffnet")
		kassa[0].activate(new TimeSpan(0.0));
    }

	public void init()
	{
		//Ankunftszeiten initialisieren
		kundenAnkunftsZeit = new ContDistExponential(this, "Kunden Ankunftszeitintervall", 5.0, true, true);	
		kundenAnkunftsZeit.setNonNegative(true);
		
		//Kassa öffnet zeiten initialisieren
		kassaOeffnetZeit = new ContDistUniform(this, "Kassa öffnet Zeit", 1.5, 3.0, true, true);	
		kassaOeffnetZeit.setNonNegative(true);
		
		//Einkauf initialisieren
		kleinerEinkauf = new ContDistUniform(this, "Kleiner Einkauf", 1, 10, true, true);
		mittlererEinkauf = new ContDistUniform(this, "Mittlerer Einkauf", 10, 30, true, true);
		grosserEinkauf = new ContDistUniform(this, "Großer Einkauf", 30, 100, true, true);
		
		//Artikel pro Minute initialisieren
		artikelProMinute = new ContDistUniform(this, "Artikel pro Minute", 35, 45, true, true);
		
		//Eintippzeit initialisieren
		eintippZeit = new ContDistUniform(this, "Eintippzeit eines Artikels in sekunden", 4, 7, true, true);
		
		//Bezahlzeiten initialisieren
		barBezahlZeit = new ContDistUniform(this, "Bezahlzeit mit Bargeld in sekunden", 10, 20, true, true);
		karteBezahlZeit = new ContDistUniform(this, "Bezahlzeit mit Karte in sekunden", 18, 22, true, true);
		
		//Kassen, -Warteschlange initialisieren
		maxKassenAnzahl = 4;
		aktiveKassenAnzahl = 1;
		kassenWarteschlange = new ProcessQueue[maxKassenAnzahl];
		kassa = new KassaProcess[maxKassenAnzahl];

		//TODO: Warteschlange nur erstellen wenn eine Kassa öffnet!
		for(int i = 0; i < maxKassenAnzahl; i++)
		{
			kassenWarteschlange[i] = new ProcessQueue<KundenProcess>(this, "Kassen Warteschlange " + (i + 1), true, true);
		}
		
		//Freie Kassa Warteschlange
    	freieKassaQueue = new ProcessQueue<KassaProcess>(this, "freie Kassa WS",true, true);
    	
    	//Maximale Anzahl an Kunden
    	maxKunden = 5;
    	aktuelleMaxKunden = maxKunden;
    	
    	//Zeit
    	kassaSchliessen = 3;
	}
	
	public static void main(String[] args)
	{
		Experiment supermarktExperiment = new Experiment("Supermakrt-Process");
		Supermarkt_Model supermarktModel = new Supermarkt_Model(null, "Supermarkt Model", true, true);
		supermarktModel.connectToExperiment(supermarktExperiment);
		
		supermarktExperiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(700));
		supermarktExperiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));
		
		supermarktExperiment.stop(new TimeInstant(4140)); //7:30-19:00 * 6
		supermarktExperiment.start();
		supermarktExperiment.report();
		supermarktExperiment.finish();
	}
}