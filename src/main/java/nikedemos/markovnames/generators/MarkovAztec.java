package nikedemos.markovnames.generators;

import nikedemos.markovnames.MarkovDictionary;

public class MarkovAztec extends MarkovGenerator {

	public MarkovAztec(int seqlen)
	{
		this.markov  = new MarkovDictionary("aztec_given.txt",seqlen);
	}
	
	public MarkovAztec()
	{
		this(3); //3 seems best-suited for Aztec
	}

	@Override
	public String fetch(int gender) {
		return markov.generateWord();
	}
}
