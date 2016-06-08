import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class KassaProcess extends SimProcess
{
	private int maxArtikelAufBand = 150;
	private int kassaNummer;
	
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
            // kein Kunde wartet
            if (meinModel.kassenWarteschlange[kassaNummer].isEmpty())
            {
                if(meinModel.getAktiveKassenAnzahl() > 1 && meinModel.kassenWarteschlange[kassaNummer].maxWaitTime().compareTo(new TimeSpan(meinModel.getKassaSchliessen())) >= 0)
                {
                	//TODO: Kassa, -warteschlange löschen!
                	meinModel.kassenWarteschlange[kassaNummer].reset();
            		meinModel.setAktuelleMaxKunden(meinModel.getAktuelleMaxKunden() - meinModel.getMaxKunden());
            		meinModel.setAktiveKassenAnzahl(meinModel.getAktiveKassenAnzahl() - 1);

                	sendTraceNote("Kassa: " + (kassaNummer + 1) + " schließt!");
                	meinModel.kassa[kassaNummer] = null;
                	return;
                }

                // Kassa in entsprechende WS
                meinModel.freieKassaQueue.insert(this);
                    
                // abwarten weiterer Aktionen
                passivate();
            }
            
            // Kunde wartet
            else {
            	int wartendeKunden = 0;
            	
            	for(int i = 0; i < meinModel.kassenWarteschlange.length; i++)
            	{
            		wartendeKunden += meinModel.kassenWarteschlange[i].length();
            	}
            	
            	if(wartendeKunden >= meinModel.getAktuelleMaxKunden() && meinModel.getAktiveKassenAnzahl() != meinModel.getMaxKassenAnzahl())
            	{
            		// Eine Kassa öffnen
            		for(int i = 0; i < meinModel.kassa.length; i++)
            		{
            			if(meinModel.kassa[i] == null)
            			{
            				meinModel.kassa[i] = new KassaProcess(meinModel, "Kassa " + (i + 1), true);
            				meinModel.kassa[i].setKassaNummer(i);
                    		
                    		// Kassaprozess starten (= "Kassa wird eroeffnet")
            				meinModel.kassa[i].activate(new TimeSpan(meinModel.getKassaOeffnetZeit()));
            				
            				break;
            			}
            		}

            		meinModel.setAktuelleMaxKunden(meinModel.getAktuelleMaxKunden() + meinModel.getMaxKunden());
            		meinModel.setAktiveKassenAnzahl(meinModel.getAktiveKassenAnzahl() + 1);
            	}
                
                // ersten Kunden aus WS entfernen
                KundenProcess kunde = meinModel.kassenWarteschlange[kassaNummer].first();
                meinModel.kassenWarteschlange[kassaNummer].remove(kunde);
                
                // Artikel werden gescannt
                // -> Prozess wird solange inaktiv gestellt
                hold(new TimeSpan(kunde.getArtikelAnzahl() / meinModel.getArtikelproMinute()));
                
                //Artikel kann nicht gescannt werden
                for(int i = 0; i < kunde.getArtikelAnzahl(); i++)
                {
                	//0,1% der Artikel können nicht gescannt werden
                	if(Math.random() <= 0.001)
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
	
	public void setKassaNummer(int kassaNummer)
	{
		this.kassaNummer = kassaNummer;
	}
}