import desmoj.core.simulator.TimeInstant;

public class KundenAnkunft
{
	public static int getKundenAnkunft(Supermarkt_Model meinModel)
	{
		//Morgens
		if((meinModel.presentTime().compareTo(new TimeInstant(1)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(180)) <= 0) ||
	       (meinModel.presentTime().compareTo(new TimeInstant(1+690)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(180+690)) <= 0)||
	       (meinModel.presentTime().compareTo(new TimeInstant(1+690*2)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(180+690*2)) <= 0)||
	       (meinModel.presentTime().compareTo(new TimeInstant(1+690*3)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(180+690*3)) <= 0)||
	       (meinModel.presentTime().compareTo(new TimeInstant(1+690*4)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(180+690*4)) <= 0))
		{
			return 0;
		}
		//Mittags
		else if((meinModel.presentTime().compareTo(new TimeInstant(181)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(450)) <= 0) ||
				(meinModel.presentTime().compareTo(new TimeInstant(181+690)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(450+690)) <= 0)||
				(meinModel.presentTime().compareTo(new TimeInstant(181+690*2)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(450+690*2)) <= 0)||
				(meinModel.presentTime().compareTo(new TimeInstant(181+690*3)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(450+690*3)) <= 0)||
				(meinModel.presentTime().compareTo(new TimeInstant(181+690*4)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(450+690*4)) <= 0))
		{
			return 1;
		}
		//Abends
		else if((meinModel.presentTime().compareTo(new TimeInstant(451)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(690)) <= 0) ||
				(meinModel.presentTime().compareTo(new TimeInstant(451+690)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(690+690)) <= 0) ||
				(meinModel.presentTime().compareTo(new TimeInstant(451+690*2)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(690+690*2)) <= 0) ||
				(meinModel.presentTime().compareTo(new TimeInstant(451+690*3)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(690+690*3)) <= 0) ||
				(meinModel.presentTime().compareTo(new TimeInstant(451+690*4)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(690+690*4)) <= 0))
		{
			return 2;
		}
		//Wochenende
		else
		{
			//Morgens
			if(meinModel.presentTime().compareTo(new TimeInstant(1+690*5)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(180+690*5)) <= 0)
			{
				return 3;
			}
			//Mittags
			else if(meinModel.presentTime().compareTo(new TimeInstant(181+690*5)) >= 0 && meinModel.presentTime().compareTo(new TimeInstant(450+690*5)) <= 0)
			{
				return 4;
			}
			//Abends
			else
			{
				return 5;
			}
		}
	}
}