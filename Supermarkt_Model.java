import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.core.statistic.Count;
import desmoj.core.statistic.TimeSeries;

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
    
	private ContDistUniform anzahlKundenMorgen;

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public int getAnzahlKundenMorgen() {
	   return (int) Math.floor(anzahlKundenMorgen.sample());
    }
    
	private ContDistUniform anzahlKundenMittag;

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public int getAnzahlKundenMittag() {
	   return (int) Math.floor(anzahlKundenMittag.sample());
    }
    
	private ContDistUniform anzahlKundenAbend;

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public int getAnzahlKundenAbend() {
	   return (int) Math.floor(anzahlKundenAbend.sample());
    }
    
	private ContDistUniform anzahlKundenWochenendeMorgen;

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public int getAnzahlKundenWochenendeMorgen() {
	   return (int) Math.floor(anzahlKundenWochenendeMorgen.sample());
    }
    
	private ContDistUniform anzahlKundenWochenendeMittag;

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public int getAnzahlKundenWochenendeMittag() {
	   return (int) Math.floor(anzahlKundenWochenendeMittag.sample());
    }
    
	private ContDistUniform anzahlKundenWochenendeAbend;

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public int getAnzahlKundenWochenendeAbend() {
	   return (int) Math.floor(anzahlKundenWochenendeAbend.sample());
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
    
    private ContDistUniform random;

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public double getRandom() {
	   return random.sample();
    }
    
    private ContDistUniform shuffle;

	// liefert eine Zufallszahl für normale Kundenankunftszeit
    public int getShuffle(int n) {
    	shuffle = new ContDistUniform(this, "Shuffle", 0, n - 1, false, false);
    	return (int) Math.ceil(shuffle.sample());
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
    private double kassaSchliessen;
    
    public double getKassaSchliessen()
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
	
	//Kassa kosten pro minute, Insgesamt
	private double kassaKostenProMinute;
	
	public double getKassaKostenProMinute()
	{
		return kassaKostenProMinute;
	}

	protected Count kassaKosten;
	
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
		kassa[0].setKassaStartzeit(this.presentTime());
    }

	public void init()
	{
		//Ankunftszeiten initialisieren
		kundenAnkunftsZeit = new ContDistExponential(this, "Kunden Ankunftszeitintervall", 1.0, true, true);
		kundenAnkunftsZeit.setNonNegative(true);
		
		//KundenAnzahl initialisieren
		anzahlKundenMorgen = new ContDistUniform(this, "Kundenanzahl Morgen", 0, 3, true, true);
		anzahlKundenMittag = new ContDistUniform(this, "Kundenanzahl Mittag", 0, 5, true, true);
		anzahlKundenAbend = new ContDistUniform(this, "Kundenanzahl Abend", 0, 4, true, true);
		anzahlKundenWochenendeMorgen = new ContDistUniform(this, "Kundenanzahl Wochenende Morgen", 0, 4, true, true);
		anzahlKundenWochenendeMittag = new ContDistUniform(this, "Kundenanzahl Wochenende Mittag", 0, 6, true, true);
		anzahlKundenWochenendeAbend = new ContDistUniform(this, "Kundenanzahl Wochenende Abend", 0, 5, true, true);
		
		//Kassa öffnet zeiten initialisieren
		kassaOeffnetZeit = new ContDistUniform(this, "Kassa öffnet Zeit", 0.5, 2.0, true, true);	
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
		
		random = new ContDistUniform(this, "Randomwert", 0, 1, true, false);
		
		//Kassen, -Warteschlange initialisieren
		maxKassenAnzahl = 4;
		aktiveKassenAnzahl = 1;
		kassenWarteschlange = new ProcessQueue[maxKassenAnzahl];
		kassa = new KassaProcess[maxKassenAnzahl];

		for(int i = 0; i < maxKassenAnzahl; i++)
		{
			kassenWarteschlange[i] = new ProcessQueue<KundenProcess>(this, "Kassen Warteschlange " + (i + 1), true, true);
		}
		
		//Freie Kassa Warteschlange
    	freieKassaQueue = new ProcessQueue<KassaProcess>(this, "freie Kassa WS", true, true);
    	
    	//Maximale Anzahl an Kunden
    	maxKunden = 10;
    	aktuelleMaxKunden = maxKunden;
    	
    	//Zeit
    	kassaSchliessen = 0.1;
    	
    	//Kassa kosten pro minute (Lohn + Betriebskosten; Schätzung)
    	kassaKostenProMinute = (12 + 5) / 60.0;
    	kassaKosten = new Count(this, "Kassakosten", true, true);
	}
	
	public static void main(String[] args)
	{
		Experiment supermarktExperiment = new Experiment("Supermakrt-Process");
		Supermarkt_Model supermarktModel = new Supermarkt_Model(null, "Supermarkt Model", true, true);
		supermarktModel.connectToExperiment(supermarktExperiment);
		
		supermarktExperiment.tracePeriod(new TimeInstant(0), new TimeInstant(690));
		supermarktExperiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));
		
		supermarktExperiment.stop(new TimeInstant(4140)); //7:30-19:00 * 6
		supermarktExperiment.start();

		for(int i = 0; i < supermarktModel.kassa.length; i++)
		{
			if(supermarktModel.kassa[i] != null)
			{
				double laufZeit = supermarktModel.presentTime().getTimeAsDouble() - supermarktModel.kassa[i].getKassaStartzeit().getTimeAsDouble();
				supermarktModel.kassaKosten.update((long) (laufZeit * supermarktModel.getKassaKostenProMinute()));
			}
		}
		
		System.out.format("Kassakosten für alle Kassen: %d€\n", supermarktModel.kassaKosten.getValue());
		
		supermarktExperiment.report();
		supermarktExperiment.finish();
	}
}