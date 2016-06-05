import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

// stellt die Kundenaktivitaeten als Prozess dar
public class KundenProcess extends SimProcess {

    // nuetzliche Referenz auf entsprechendes Modell
    private Supermarkt_Model meinModel;

    // Konstruktor
	  // Par 1: Modellzugehoerigkeit
	  // Par 2: Name des Ereignisses
	  // Par 3: show in trace?
    public KundenProcess(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        meinModel = (Supermarkt_Model) owner;
    }

    
    // Beschreibung der Aktionen des Kunden vom Eintreffen bis zum Verlassen
    //   des Schalters 
    public void lifeCycle() throws SuspendExecution{

    	//TODO: Auswahl der Warteschlange einfügen!
        // Kunde betritt Kassenraum -> in die Warteschlange geben
        meinModel.kassenWarteschlange[0].insert(this);
        sendTraceNote("Laenge der Kundenreihe: " + 
            meinModel.kassenWarteschlange[0].length());

        // Kassa frei? 
        if (!meinModel.freieKassaQueue.isEmpty()) {
            // Schalter frei, von entsprechender WS holen
            KassaProcess kassa = meinModel.freieKassaQueue.first();
            // extra Entfernen von WS notwendig
            meinModel.freieKassaQueue.remove(kassa);
            
            // Schalter sofort als naechsten Prozess aktivieren
            kassa.activateAfter(this);
            
            // Bedienvorgang ueber sich ergehen lassen
            passivate();
        }
        // Schalter besetzt
        else {            
            // Kunde wartet in der WS
            passivate();
        }
        
        // Kunde wurde bedient und verlaesst den Schalterraum
        //  -> in diesem Beispiel nur eine Meldung sinnvoll
        sendTraceNote("Kunde wurde bedient und verlässt den Kassenraum");
    }
}
