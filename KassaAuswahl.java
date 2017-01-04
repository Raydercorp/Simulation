import desmoj.core.simulator.ProcessQueue;

public class KassaAuswahl
{
	private static int artikel[];
	
	public static int besteKassa(Supermarkt_Model meinModel)
	{	
		//Artikel pro Kassa WS, diese werden dann durch mehrere M�glichkeiten nur grob abgesch�tzt
		artikel = new int[meinModel.getMaxKassenAnzahl()];
		int abgeschaetzteArtikel[] = new int[meinModel.getMaxKassenAnzahl()];
		
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
			abgeschaetzteArtikel[i] = grobeArtikelAnzahl(artikel[i]);
		}
		
		//Minimum
		int min = artikel[0];
		int minIndex = 0;
		
		for(int i = 1; i <  artikel.length; i++)
		{
			if(abgeschaetzteArtikel[i] < min)
			{
				min = abgeschaetzteArtikel[i];
				minIndex = i;
			}
			else if(abgeschaetzteArtikel[i] == min) //Falls die Klasse gleich ist wird nach der Anzahl der Personen entschieden
			{
				if(meinModel.kassenWarteschlange[minIndex].length() > meinModel.kassenWarteschlange[i].length())
				{
					minIndex = i;
				}
			}
		}
		
		return minIndex;
	}
	
	//F�rs Kassa wechseln
	public static int besteKassa(Supermarkt_Model meinModel, int kassa, ProcessQueue<KundenProcess> test)
	{	
		//Artikel pro Kassa WS, diese werden dann durch mehrere M�glichkeiten nur grob abgesch�tzt
		artikel = new int[meinModel.getMaxKassenAnzahl()];
		int abgeschaetzteArtikel[] = new int[meinModel.getMaxKassenAnzahl()];
		
		for(int i = 0; i < meinModel.getMaxKassenAnzahl(); i++)
		{
			if(meinModel.kassa[i] != null && meinModel.kassa[i] != meinModel.kassa[kassa])
			{
	    		for(int j = 0; j < meinModel.kassenWarteschlange[i].length(); j++)
	    		{
	    			artikel[i] += meinModel.kassenWarteschlange[i].get(j).getArtikelAnzahl();
	    		}
			}
			else if(meinModel.kassa[i] == meinModel.kassa[kassa])
			{
				for(int j = 0; j < test.length(); j++)
	    		{
	    			artikel[i] += test.get(j).getArtikelAnzahl();
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
			abgeschaetzteArtikel[i] = grobeArtikelAnzahl(artikel[i]);
		}
		
		//Minimum
		int min = artikel[0];
		int minIndex = 0;
		
		for(int i = 1; i <  artikel.length; i++)
		{
			if(abgeschaetzteArtikel[i] < min)
			{
				min = abgeschaetzteArtikel[i];
				minIndex = i;
			}
			else if(abgeschaetzteArtikel[i] == min)
			{
				if(meinModel.kassenWarteschlange[minIndex].length() > meinModel.kassenWarteschlange[i].length())
				{
					minIndex = i;
				}
			}
		}
		
		return minIndex;
	}
	
    private static int grobeArtikelAnzahl(int artikel)
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
    
    public static int[] getArtikel()
    {
    	return artikel;
    }
}