package nikedemos.markovnames.generators;

import nikedemos.markovnames.MarkovDictionary;

public class MarkovSlavic extends MarkovGenerator {

	public MarkovSlavic(int seqlen) {
		this.markov = new MarkovDictionary("slavic_given.txt", seqlen);
	}

	public MarkovSlavic() {
		this(3); // 3 seems best-suited for Slavic
	}

	@Override
	public String feminize(String element, boolean flag) {
		// add "a" at the end, if there isn't one
		String lastChar = element.substring(element.length() - 1);

		if (element.endsWith("o")) {
			element = element.substring(0, element.length() - 1) + "a";
		} 
		else if (!lastChar.endsWith("a")) {
			element += "a";
		}

		return element;
	}

	@Override
	public String fetch(int gender) {

		String seq1 = markov.generateWord();

		if (gender == 0) {
			gender = MarkovDictionary.rng.nextBoolean() ? 1 : 2;
		}

		// now if it's 2 - a lady - feminize the 3 sequences
		if (gender == 2) {
			seq1 = feminize(seq1, false);
		}

		return seq1;
	}
}
