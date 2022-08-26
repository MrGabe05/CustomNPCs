package nikedemos.markovnames.generators;

import java.util.Random;

import net.minecraft.util.text.TranslationTextComponent;
import nikedemos.markovnames.MarkovDictionary;

public class MarkovJapanese extends MarkovGenerator {
	public MarkovDictionary markov2;
	public MarkovDictionary markov3;

	public MarkovJapanese(int seqlen) {
		this.markov = new MarkovDictionary("japanese_surnames.txt", seqlen);
		this.markov2 = new MarkovDictionary("japanese_given_male.txt", seqlen);
		this.markov3 = new MarkovDictionary("japanese_given_female.txt", seqlen);
	}

	public MarkovJapanese() {
		this(4); // 4 seems best suited for Japanese
	}

	@Override
	public String fetch(int gender) {

		StringBuilder name = new StringBuilder(markov.generateWord());
		name.append(" ");

		// check the gender.
		// 0 = random gender, 1 = male, 2 = female
		// if there's no gender specified (0),
		// now it's time to pick it at random
		//
		if (gender == 0) {
			gender = MarkovDictionary.rng.nextBoolean() == true ? 1 : 2;
		}

		if (gender == 2) {
			name.append(markov3.generateWord());
		} 
		else {
			name.append(markov2.generateWord());
		}

		return name.toString();
	}
}
