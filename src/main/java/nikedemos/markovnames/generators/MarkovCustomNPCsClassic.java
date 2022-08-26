package nikedemos.markovnames.generators;

import java.util.Random;

import net.minecraft.util.text.TranslationTextComponent;
import nikedemos.markovnames.MarkovDictionary;

/* This implementation is made specifically for
 * CustomNPCs, one of the best Minecraft mods ever...
 * I'm a huge fan and would recommend it to anyone.
 * Check it out if you haven't yet, at
 * http://www.kodevelopment.nl/minecraft/category/customnpcs/
 * Minecraft 1.12.2 Java Edition + Forge required.
 * Also, you have to like cats, it's in CNPCs EULA, I'm quite sure
 */

public class MarkovCustomNPCsClassic extends MarkovGenerator {

	public MarkovCustomNPCsClassic(int seqlen)
	{
		this.markov  = new MarkovDictionary("customnpcs_classic.txt",seqlen);
	}
	
	public MarkovCustomNPCsClassic()
	{
		this(3);
	}

	@Override
	public String fetch(int gender)
	{
		return markov.generateWord();
	}
}
