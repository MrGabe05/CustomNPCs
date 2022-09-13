package nikedemos.markovnames.generators;

import nikedemos.markovnames.MarkovDictionary;

public class MarkovSaami extends MarkovGenerator {

	public MarkovDictionary markov2;

	public MarkovSaami(int seqlen)
	{
		this.markov  = new MarkovDictionary("saami_bothgenders.txt",seqlen);
	}
	
	public MarkovSaami()
	{
		this(3); //3 seems best-suited for Saami
	}

	@Override
	public String fetch(int gender) //Saami names are genderless
	{
		return markov.generateWord();
	}
}
