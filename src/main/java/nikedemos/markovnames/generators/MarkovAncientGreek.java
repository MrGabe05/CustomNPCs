package nikedemos.markovnames.generators;

import nikedemos.markovnames.MarkovDictionary;

public class MarkovAncientGreek extends MarkovGenerator {

	public MarkovDictionary markov2;

	public MarkovAncientGreek(int seqlen) {
		this.markov  = new MarkovDictionary("ancient_greek_male.txt",seqlen);
		this.markov2  = new MarkovDictionary("ancient_greek_female.txt",seqlen);
	}
	
	public MarkovAncientGreek()
	{
		this(3); //3 seems best-suited for Welsh
	}
	
	@Override
	public String fetch(int gender) {
		String seq1;

		if (gender==0)
		{
			gender = MarkovDictionary.rng.nextBoolean() ? 1 : 2;
		}
		
		//now if it's 2 - a lady - feminize the 3 sequences
		if (gender==2)
			{
			seq1 = markov2.generateWord();
			}
		else
			{
			seq1 = markov.generateWord();
			}

		return seq1;
	}
}
