package nikedemos.markovnames.generators;

import java.util.Random;

import net.minecraft.util.text.TranslationTextComponent;
import nikedemos.markovnames.MarkovDictionary;
import nikedemos.markovnames.MarkovDictionarySPA;

public class MarkovSpanish extends MarkovGenerator {
	public MarkovDictionary markov2;
	public MarkovDictionary markov3;

	public MarkovSpanish(int seqlen) {
		this.markov = new MarkovDictionary("spanish_given_male.txt", seqlen);
		this.markov2 = new MarkovDictionary("spanish_given_female.txt", seqlen);
		this.markov3 = new MarkovDictionarySPA("spanish_surnames.txt", seqlen);
	}
	public MarkovSpanish() {
		this(3);
	}

	@Override
	public String fetch(int gender) {
		String giv = "";

		String sur = markov3.generateWord();

		// check the gender.
		// 0 = random gender, 1 = male, 2 = female
		// if there's no gender specified (0),
		// now it's time to pick it at random
		//
		if (gender == 0) {
			gender = MarkovDictionary.rng.nextBoolean() == true ? 1 : 2;
		}

		if (gender == 1) {
			giv = markov.generateWord();
		} 
		else {
			giv = markov2.generateWord();

		}

		return new StringBuilder(giv).append(" ").append(sur).toString();
	}
}
