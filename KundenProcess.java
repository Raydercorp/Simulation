import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

// stellt die Kundenaktivitaeten als Prozess dar
public class KundenProcess extends SimProcess {
	
	private int artikel;
	private boolean aufgelegt = false; //TODO: wechsel der ws nur m�glich wenn false (dazu artikel auf band z�hlen und mit max vergleichen)

    // nuetzliche Referenz auf entsprechendes Modell
    private Supermarkt_Model meinModel;

    // Konstruktor
	  // Par 1: Modellzugehoerigkeit
	  // Par 2: Name des Ereignisses
	  // Par 3: show in trace?
    public KundenProcess(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        meinModel = (Supermarkt_Model) owner;
        
        //TODO: Zeitabh�ngig �ndern
        getNormaleArtikelAnzahl();
    }

    
    // Beschreibung der Aktionen des Kunden vom Eintreffen bis zum Verlassen
    //   des Schalters 
    public void lifeCycle() throws SuspendExecution{

    	//TODO: Auswahl der Warteschlange einf�gen!
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
        sendTraceNote("Kunde wurde bedient und verl�sst den Kassenraum");
    }
    
    public int getArtikelAnzahl()
    {
    	return artikel;
    }
    
    public boolean getAufgelegt()
    {
    	return aufgelegt;
    }
    
    public void setAufgelegt(boolean aufgelegt)
    {
    	this.aufgelegt = aufgelegt;
    }
    
    private void getNormaleArtikelAnzahl()
    {
        double rand = Math.random();
        
        if(rand <= 0.5)
        {
        	artikel = (int) meinModel.getMittlererEinkauf();
        }
        else if(rand > 0.5 && rand <= 0.7)
        {
        	artikel = (int) meinModel.getKleinerEinkauf();
        }
        else
        {
        	artikel = (int) meinModel.getGrosserEinkauf();
        }
    }
    
    private void getMittagArtikelAnzahl()
    {
        double rand = Math.random();
        
        if(rand <= 0.3)
        {
        	artikel = (int) meinModel.getMittlererEinkauf();
        }
        else if(rand > 0.3 && rand <= 0.9)
        {
        	artikel = (int) meinModel.getKleinerEinkauf();
        }
        else
        {
        	artikel = (int) meinModel.getGrosserEinkauf();
        }
    }
    
    private void getWochenendeArtikelAnzahl()
    {
        double rand = Math.random();
        
        if(rand <= 0.35)
        {
        	artikel = (int) meinModel.getMittlererEinkauf();
        }
        else if(rand > 0.35 && rand <= 0.45)
        {
        	artikel = (int) meinModel.getKleinerEinkauf();
        }
        else
        {
        	artikel = (int) meinModel.getGrosserEinkauf();
        }
    }
}
