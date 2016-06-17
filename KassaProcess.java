import java.util.ArrayList;
import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;

public class KassaProcess extends SimProcess
{
	private int maxArtikelAufBand = 100;
	private int kassaNummer;
	private TimeInstant passivateTime;
	private TimeInstant kassaStartzeit;
	
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
                if(meinModel.getAktiveKassenAnzahl() > 1 && meinModel.kassenWarteschlange[kassaNummer].length() == 0 && passivateTime != null &&
                  (meinModel.kassa[kassaNummer].presentTime().getTimeAsDouble() - passivateTime.getTimeAsDouble()) >= meinModel.getKassaSchliessen())
                {
            		meinModel.setAktuelleMaxKunden(meinModel.getAktuelleMaxKunden() - meinModel.getMaxKunden());
            		meinModel.setAktiveKassenAnzahl(meinModel.getAktiveKassenAnzahl() - 1);

                	sendTraceNote("Kassa: " + (kassaNummer + 1) + " schließt!");
                	meinModel.kassa[kassaNummer] = null;
                	
                	//TODO: Kassa kosten aktualisieren.
                	double laufZeit = meinModel.presentTime().getTimeAsDouble() - kassaStartzeit.getTimeAsDouble();
                	meinModel.setKassaKosten((laufZeit * meinModel.getKassaKostenProMinute()) + meinModel.getKassaKosten());
                	return;
                }

                // Kassa in entsprechende WS
                meinModel.freieKassaQueue.insert(this);
                
                if(passivateTime == null)
                {
                	passivateTime = meinModel.kassa[kassaNummer].presentTime();
                }
                    
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
            				TimeSpan startZeit = new TimeSpan(meinModel.getKassaOeffnetZeit());
            				meinModel.kassa[i].activate(startZeit);
            				meinModel.kassa[i].setKassaStartzeit(meinModel.presentTime());
            				
            				//Kunden die keine Artikel aufs Band gelegt haben können die WS wechseln
            				ArrayList<KundenProcess> kunden = new ArrayList<>();
            				
            				for(int j = 0; j < meinModel.kassa.length; j++)
            				{
            					for(int k = 0; k < meinModel.kassenWarteschlange[j].length(); k++)
            					{
            						if(!meinModel.kassenWarteschlange[j].get(k).getAufgelegt())
            						{
            							kunden.add(meinModel.kassenWarteschlange[j].get(k));
            						}
            					}
            				}
            				
            				//Randomize Kunden
            				shuffle(kunden);
            				
            				for(int j = 0; j < kunden.size(); j++)
            				{
            					KundenProcess kunde = kunden.get(j);
            					ProcessQueue<KundenProcess> test = new ProcessQueue<>(meinModel, "test", false, false);
            					int startKassa = -1;
            					
            					for(int k = 0; k < meinModel.kassa.length; k++)
        			        	{
        			        		if(meinModel.kassenWarteschlange[k].contains(kunde))
        			        		{
        			        			startKassa = k;
        			        			break;
        			        		}
        			        	}
            					
            					for(KundenProcess k : meinModel.kassenWarteschlange[startKassa])
            					{
	            					test.insert(k);
            					}
            					
            					test.remove(kunde);
            					
            					int minIndex = KassaAuswahl.besteKassa(meinModel, startKassa, test);
            					
            					//Kunde wechselt nicht zur gleichen Kassa!
            					if(minIndex == startKassa)
            					{
            						continue;
            					}

                				//Kunden wechseln mit 50% Wahrscheinlichkeit die WS
            			        if(meinModel.getRandom() <= 0.5)
            			        {
            			        	meinModel.kassenWarteschlange[startKassa].remove(kunde);
            			        	startKassa++;
            			        	
            			        	//Kunde zu neuer Kassa hinzufügen
            			        	meinModel.kassenWarteschlange[minIndex].insert(kunde);
            			        	
            			        	//Kann der Kunde nun Artikel aufs Band legen?
            			        	if(KassaAuswahl.getArtikel()[minIndex] < meinModel.kassa[minIndex].getMaxArtikelAufBand())
            			            {
            			        		kunde.setAufgelegt(true);
            			            }
            			        	
            			        	sendTraceNote(kunde + " wechselt von Kassa " + startKassa + " zu Kassa " + (minIndex + 1) + " und kann Artikel auflegen? " + kunde.getAufgelegt());
            			        }
            				}
            				
            				break;
            			}
            		}

            		meinModel.setAktuelleMaxKunden(meinModel.getAktuelleMaxKunden() + meinModel.getMaxKunden());
            		meinModel.setAktiveKassenAnzahl(meinModel.getAktiveKassenAnzahl() + 1);
            	}
            	
            	passivateTime = null;
                
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
                	if(meinModel.getRandom() <= 0.001)
                	{
                		//zwischen 4 und 7 sekunden zum eintippen
                		hold(new TimeSpan(1 / 60 * meinModel.getEintippZeit()));
                	}
                }
                
                //Kunde bezahlt 60% bar und 40% mit karte; bar zahlen geht etwas schneller, allerdings in einem größeren intervall
                if(meinModel.getRandom() <= 0.6)
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
	
	public void setKassaStartzeit(TimeInstant kassaStartzeit)
	{
		this.kassaStartzeit = kassaStartzeit;
	}
	
	public TimeInstant getKassaStartzeit()
	{
		return kassaStartzeit;
	}
	
	public int getMaxArtikelAufBand()
	{
		return maxArtikelAufBand;
	}
	
	private void shuffle (ArrayList<KundenProcess> a)
	{		
	    int n = a.size();
	    while (n > 1)
	    {
	        int k = meinModel.getShuffle(n--); //decrements after using the value
	        KundenProcess temp = a.get(n);
	        a.set(n, a.get(k));
	        a.set(k, temp);
	    }
	}
}