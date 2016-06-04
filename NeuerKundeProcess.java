import desmoj.core.simulator.*;
import co.paralleluniverse.fibers.SuspendExecution;

// stellt die Erscheinung eines neuen Kunden im System dar - als Prozess realisiert
// -> Beschreibung der Aktionen fÃ¼r einen neue Kundenankunft
public class NeuerKundeProcess extends SimProcess {

    // nuetzliche Referenz auf entsprechendes Modell
    private Supermarkt_Model meinModel;

    // Konstruktor
	  // Par 1: Modellzugehoerigkeit
	  // Par 2: Name des Ereignisses
	  // Par 3: show in trace?

    public NeuerKundeProcess (Model owner, String name, boolean showInTrace) {
	   super(owner, name, showInTrace);

	   meinModel = (Supermarkt_Model) owner;
    }
    
    // Aktionen, die bei Aktivierung dieses Ereignisses ausgefuehrt werden
    public void lifeCycle() throws SuspendExecution {
	
        while (true)
        {
        	//ca. 30 minuten vorm zusperren, darf keiner mehr rein
        	if(!(meinModel.presentTime().compareTo(new TimeInstant(660)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(690)) <= 0) &&
               !(meinModel.presentTime().compareTo(new TimeInstant(1350)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(1380)) <= 0) &&
               !(meinModel.presentTime().compareTo(new TimeInstant(2040)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(2070)) <= 0) &&
               !(meinModel.presentTime().compareTo(new TimeInstant(2730)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(2760)) <= 0) &&
               !(meinModel.presentTime().compareTo(new TimeInstant(3420)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(3450)) <= 0) &&
               !(meinModel.presentTime().compareTo(new TimeInstant(4110)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(4140)) <= 0))
        	{
        		//TODO: zeitabhängig andere ankunftszeit verwenden und vielleicht mit random auch mehrere kunden erstellen!
        		// Prozess deaktivieren bis naechster Kunde erzeugt werden soll
	            hold (new TimeSpan(meinModel.getKundenAnkunftsZeit()));
	     
	            // neuen Kunden erzeugen
	            KundenProcess neuerKunde = new KundenProcess (meinModel, "Kunde", true);
	    
	            // neuer Kunde betritt Schalterbereich -> Kunde ist zu aktivieren
	            //  soll unmittelbar nach diesem Generator-Ereignis geschehen
	            neuerKunde.activateAfter(this);
        	}
        	else
        	{
        		//Nächster Kunde kommt erst am nächsten morgen
        		if(meinModel.presentTime().compareTo(new TimeInstant(660)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(690)) <= 0)
        		{
            		hold (new TimeSpan(690 - meinModel.presentTime().getTimeTruncated() + meinModel.getKundenAnkunftsZeit()));
        		}
        		
        		if(meinModel.presentTime().compareTo(new TimeInstant(1350)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(1380)) <= 0)
        		{
            		hold (new TimeSpan(1380 - meinModel.presentTime().getTimeTruncated() + meinModel.getKundenAnkunftsZeit()));
        		}
        		
        		if(meinModel.presentTime().compareTo(new TimeInstant(2040)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(2070)) <= 0)
        		{
            		hold (new TimeSpan(2070 - meinModel.presentTime().getTimeTruncated() + meinModel.getKundenAnkunftsZeit()));
        		}
        		
        		if(meinModel.presentTime().compareTo(new TimeInstant(2730)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(2760)) <= 0)
        		{
            		hold (new TimeSpan(2760 - meinModel.presentTime().getTimeTruncated() + meinModel.getKundenAnkunftsZeit()));
        		}
        		
        		if(meinModel.presentTime().compareTo(new TimeInstant(3420)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(3450)) <= 0)
        		{
            		hold (new TimeSpan(3450 - meinModel.presentTime().getTimeTruncated() + meinModel.getKundenAnkunftsZeit()));
        		}
        		
        		if(meinModel.presentTime().compareTo(new TimeInstant(4110)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(4140)) <= 0)
        		{
            		hold (new TimeSpan(4140 - meinModel.presentTime().getTimeTruncated() + meinModel.getKundenAnkunftsZeit()));
        		}
        	}
        }
    }
}