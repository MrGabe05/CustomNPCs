package nikedemos.markovnames.generators;

import java.util.Random;

import net.minecraft.util.text.TranslationTextComponent;
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
	public String fetch(int gender) //Aztec names are genderless
	{
		return markov.generateWord();
	}
}
