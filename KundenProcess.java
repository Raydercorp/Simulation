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

        // Kunde betritt Schalterraum -> in die Warteschlange geben
        meinModel.kassenWarteschlange[0].insert(this);
        sendTraceNote("Laenge der Kundenreihe: " + 
            meinModel.kassenWarteschlange[0].length());

        // Schalter frei? 
        /*if (!meinModel.freieSchalterQueue.isEmpty()) {
            // Schalter frei, von entsprechender WS holen
            SchalterProcess schalter = meinModel.freieSchalterQueue.first();
            // extra Entfernen von WS notwendig
            meinModel.freieSchalterQueue.remove(schalter);
            
            // Schalter sofort als naechsten Prozess aktivieren
            schalter.activateAfter(this);
            
            // Bedienvorgang ueber sich ergehen lassen
            passivate();
        }
        // Schalter besetzt
        else {
        	//Aufgabe
            meinModel.anzahlWartendeKunden.update(meinModel.kundenReiheQueue.length());
            
            // Kunde wartet in der WS
            passivate();
        }*/
        
        // Kunde wurde bedient und verlaesst den Schalterraum
        //  -> in diesem Beispiel nur eine Meldung sinnvoll
        sendTraceNote("Kunde wurde bedient und verlaesst den Schalterraum");
    }
}
