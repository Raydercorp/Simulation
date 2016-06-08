import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

// stellt die Kundenaktivitaeten als Prozess dar
public class KundenProcess extends SimProcess {
	
	private int artikel;
	private boolean aufgelegt = false; //TODO: wechsel der ws nur möglich wenn false (dazu artikel auf band zählen und mit max vergleichen)

    // nuetzliche Referenz auf entsprechendes Modell
    private Supermarkt_Model meinModel;

    // Konstruktor
	  // Par 1: Modellzugehoerigkeit
	  // Par 2: Name des Ereignisses
	  // Par 3: show in trace?
    public KundenProcess(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

        meinModel = (Supermarkt_Model) owner;
        
        //TODO: Zeitabhängig ändern
        getNormaleArtikelAnzahl();
    }

    
    // Beschreibung der Aktionen des Kunden vom Eintreffen bis zum Verlassen
    //   des Schalters 
    public void lifeCycle() throws SuspendExecution{

    	//Artikel pro Kassa WS, diese werden dann durch mehrere Möglichkeiten nur grob abgeschätzt
		int artikel[] = new int[meinModel.getMaxKassenAnzahl()];
		
    	for(int i = 0; i < meinModel.getMaxKassenAnzahl(); i++)
    	{
    		if(meinModel.kassa[i] != null)
    		{
	    		for(int j = 0; j < meinModel.kassenWarteschlange[i].length(); j++)
	    		{
	    			artikel[i] += meinModel.kassenWarteschlange[i].get(j).getArtikelAnzahl();
	    		}
    		}
    		else
    		{
    			artikel[i] = -1;
    		}
    	}
    	
    	//Klassifizierung
    	for(int i = 0; i < artikel.length; i++)
    	{
    		artikel[i] = grobeArtikelAnzahl(artikel[i]);
    	}
    	
    	//Minimum
    	int min = artikel[0];
    	int minIndex = 0;
    	
    	for(int i = 1; i <  artikel.length; i++)
    	{
    		if(artikel[i] < min)
    		{
    			min = artikel[i];
    			minIndex = i;
    		}
    		else if(artikel[i] == min)
    		{
    			if(meinModel.kassenWarteschlange[minIndex].length() > meinModel.kassenWarteschlange[i].length())
    			{
    				minIndex = i;
    			}
    		}
    	}

        // Kunde betritt Kassenraum -> in die Warteschlange geben
        meinModel.kassenWarteschlange[minIndex].insert(this);
        
        for(int i = 0; i < meinModel.getAktiveKassenAnzahl(); i++)
        {
	        sendTraceNote("Laenge der Kundenreihe: " + 
	            meinModel.kassenWarteschlange[i].length());
        }

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
    
    private int grobeArtikelAnzahl(int artikel)
    {
    	if(artikel == -1)
    	{
    		return Integer.MAX_VALUE;
    	}
    	
    	if(artikel <= 20)
    	{
    		return 0;
    	}
    	else if(artikel > 20 && artikel <= 40)
    	{
    		return 1;
    	}
    	else if(artikel > 40 && artikel <= 60)
    	{
    		return 2;
    	}
    	else if(artikel > 60 && artikel <= 80)
    	{
    		return 3;
    	}
    	else if(artikel > 80 && artikel <= 100)
    	{
    		return 4;
    	}
    	else
    	{
    		return 5;
    	}
    }
}
