import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class KassaProcess extends SimProcess
{
	private Supermarkt_Model meinModel;
	
	public KassaProcess(Model owner, String name, boolean showInTrace)
	{
		super(owner, name, showInTrace);
		
		meinModel = (Supermarkt_Model) owner;
	}

	public void lifeCycle() throws SuspendExecution
	{
		// Kassa ist immer in Aktion -> Endlosschleife
        while (true)
        {
        	//TODO: Alle Kassen unterstützen!
            // kein Kunde wartet
            if (meinModel.kassenWarteschlange[0].isEmpty()) {
                
                // Kassa in entsprechende WS
                meinModel.freieKassaQueue.insert(this);
                
                // abwarten weiterer Aktionen
                passivate();
            }
            
            // Kunde wartet
            else {
                
                // ersten Kunden aus WS entfernen
                KundenProcess kunde = meinModel.kassenWarteschlange[0].first();
                meinModel.kassenWarteschlange[0].remove(kunde);
                
                //TODO: Artikel Anzahl / Artikel pro minute!
                // Kunde wird bedient
                // -> Prozess wird solange inaktiv gestellt
                hold(new TimeSpan(1));
                
                // Kunde wurde bedient, kann den Schalter verlassen
                // -> muss reaktiviert werden (für Abschlussaktionen)
                kunde.activate(new TimeSpan(0.0));
            }
        }
	}
}