import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;

/*
Ein Supermarkt hat n Kassen.
Zu Beginn wird eine Kasse geöffnet und dann nach folgender Strategie vorgegangen:
jedes Mal wenn w Kunden bei einer Kasse warten,
wird eine neue Kasse eröffnet (solange möglich).
Eine Kasse wird geschlossen, wenn m Minuten kein Kunde bedient wird
(mindestens eine Kasse muss jedoch offen bleiben).
Ziel der Simulation: wie sieht eine möglichst "optimale" Wahl der Parameter w und m aus,
sodass die Kunden nicht zu lange warten müssen,
aber auch die Kosten für besetzte Kassen nicht zu hoch werden.
 */

public class Supermarkt_Model extends Model
{
	private int kassenAnzahl;
	
	// Zufallszahlengenerator fuer Kundenankuenfte
	private ContDistExponential kundenAnkunftsZeit;

    // liefert eine Zufallszahl fuer Kundenankunftszeit
    public double getKundenAnkunftsZeit() {
	   return kundenAnkunftsZeit.sample();
    }

	protected ProcessQueue<SimProcess>[] kassenWarteschlange;
	
	//Konstruktur
	public Supermarkt_Model(Model owner, String name, boolean showInReport, boolean showInTrace)
	{
		super(owner, name, showInReport, showInTrace);
	}

	public String description()
	{
		return "Ein Supermarkt hat n Kassen." +
			   "Zu Beginn wird eine Kasse geöffnet und dann nach folgender Strategie vorgegangen:" +
			   "jedes Mal wenn w Kunden bei einer Kasse warten," +
			   "wird eine neue Kasse eröffnet (solange möglich)." +
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
		kundenAnkunftsZeit = new ContDistExponential(this, "Ankunftszeitintervall", 3.0, true, true);	
		kundenAnkunftsZeit.setNonNegative(true);
		
		kassenAnzahl = 4;
		kassenWarteschlange = new ProcessQueue[kassenAnzahl];

		for(int i = 0; i < kassenAnzahl; i++)
		{
			kassenWarteschlange[i] = new ProcessQueue<SimProcess>(this, "Kassen Warteschlange " + (i + 1), true, true);
		}
	}
	
	public static void main(String[] args)
	{
		Experiment supermarktExperiment = new Experiment("Supermakrt-Process");
		Supermarkt_Model supermarktModel = new Supermarkt_Model(null, "Supermarkt Model", true, true);
		supermarktModel.connectToExperiment(supermarktExperiment);
		
		supermarktExperiment.tracePeriod(new TimeInstant(0.0), new TimeInstant(60));
		supermarktExperiment.debugPeriod(new TimeInstant(0.0), new TimeInstant(60));
		
		supermarktExperiment.stop(new TimeInstant(4140)); //7:00-18:30 * 6
		supermarktExperiment.start();
		supermarktExperiment.report();
		supermarktExperiment.finish();
	}
}