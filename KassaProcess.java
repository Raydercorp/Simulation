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
                
                // Artikel werden gescannt
                // -> Prozess wird solange inaktiv gestellt
                hold(new TimeSpan(kunde.getArtikelAnzahl() / meinModel.getArtikelproMinute()));
                
                //Artikel kann nicht gescannt werden
                for(int i = 0; i < kunde.getArtikelAnzahl(); i++)
                {
                	//0,5% der Artikel können nicht gescannt werden
                	if(Math.random() <= 0.005)
                	{
                		//zwischen 4 und 7 sekunden zum eintippen
                		hold(new TimeSpan(1 / 60 * meinModel.getEintippZeit()));
                	}
                }
                
                //Kunde bezahlt 60% bar und 40% mit karte; bar zahlen geht etwas schneller, allerdings in einem größeren intervall
                if(Math.random() <= 0.6)
                {
                	//Bar zahlen dauert zwischen 10 und 20 sekunden
                	hold(new TimeSpan(1 / 60 * meinModel.getBarBezahlZeit()));
                }
                else
                {
                	//Mit karte zahlen dauert zwischen 18 und 22
                	hold(new TimeSpan(1 / 60 * meinModel.getKarteBezahlZeit()));
                }
                
                // Kunde wurde bedient, kann den Schalter verlassen
                // -> muss reaktiviert werden (für Abschlussaktionen)
                kunde.activate(new TimeSpan(0.0));
            }
        }
	}
}