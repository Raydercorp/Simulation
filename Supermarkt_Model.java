import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.SimProcess;
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

    //Kassenanzahl + Warteschlangen
	private int kassenAnzahl;
	protected ProcessQueue<KundenProcess>[] kassenWarteschlange;
	
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
		
		//Kassen initialisieren
		kassenAnzahl = 4;
		kassenWarteschlange = new ProcessQueue[kassenAnzahl];

		for(int i = 0; i < kassenAnzahl; i++)
		{
			kassenWarteschlange[i] = new ProcessQueue<KundenProcess>(this, "Kassen Warteschlange " + (i + 1), true, true);
		}
	}
	
	public static void main(String[] args)
	{
		Experiment supermarktExperiment = new Experiment("Supermakrt-Process");
		Supermarkt_Model supermarktModel = new Supermarkt_Model(null, "Supermarkt Model", true, true);
		supermarktModel.connectToExperiment(supermarktExperiment);
		
		supermarktExperiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(60));
		supermarktExperiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));
		
		supermarktExperiment.stop(new TimeInstant(4140)); //7:30-19:00 * 6
		supermarktExperiment.start();
		supermarktExperiment.report();
		supermarktExperiment.finish();
	}
}