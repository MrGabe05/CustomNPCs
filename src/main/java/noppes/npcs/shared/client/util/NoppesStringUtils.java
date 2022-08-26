package noppes.npcs.shared.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Arrays;
import java.util.regex.Pattern;

public class NoppesStringUtils {

    final static int[] illegalChars = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};
	static {
	    Arrays.sort(illegalChars);
	}
	public static String cleanFileName(String badFileName) {
	    StringBuilder cleanName = new StringBuilder();
	    for (int i = 0; i < badFileName.length(); i++) {
	        int c = (int)badFileName.charAt(i);
	        if (Arrays.binarySearch(illegalChars, c) < 0) {
	            cleanName.append((char)c);
	        }
	    }
	    return cleanName.toString();
	}
	
	public static String removeHidden(String text) {
		StringBuilder newString = new StringBuilder(text.length());
		for (int offset = 0; offset < text.length();)
		{
		    int codePoint = text.codePointAt(offset);
		    offset += Character.charCount(codePoint);

		    // Replace invisible control characters and unused code points
		    switch (Character.getType(codePoint))
		    {
		        case Character.FORMAT:      // \p{Cf}
		        case Character.PRIVATE_USE: // \p{Co}
		        case Character.SURROGATE:   // \p{Cs}
		        case Character.UNASSIGNED:  // \p{Cn}
		            break;
		        default:
		            newString.append(Character.toChars(codePoint));
		            break;
		    }
		}
		return newString.toString();
	}
	public static String formatText(ITextComponent text, Object... obs) {
		return formatText(text.getString(), obs);
	}

	public static String formatText(String text, Object... obs) {
		if(text == null || text.isEmpty())
			return "";
		text = translate(text);
		for(Object ob : obs){
			if(ob instanceof PlayerEntity){
				String username = ((PlayerEntity)ob).getDisplayName().getString();
				text = text.replace("{player}", username);
				text = text.replace("@p", username);
			}
			else if(ob instanceof EntityNPCInterface)
				text = text.replace("@npc", ((EntityNPCInterface)ob).getName().getString());
				
		}
		return text.replace("&", Character.toChars(167)[0] + "");
	}

	public static void setClipboardContents(String aString) {
//		StringSelection stringSelection = new StringSelection(aString);
//		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//        clipboard.setContents(stringSelection, (arg0, arg1) -> {
//
//		});
		Minecraft.getInstance().keyboardHandler.setClipboard(aString);
	}

	public static String getClipboardContents() {
//		String result = "";
//		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		Transferable contents = clipboard.getContents(null);
//		boolean hasTransferableText = contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
//		if (hasTransferableText) {
//			try {
//				result = (String) contents.getTransferData(DataFlavor.stringFlavor);
//			}
//			catch (Exception ex) {
//				LogWriter.except(ex);
//			}
//		}
//		return removeHidden(result);
		return  Minecraft.getInstance().keyboardHandler.getClipboard();
	}

	private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]");
	public static String stripSpecialCharacters(String in) {
		return NON_ALPHANUMERIC.matcher(in).replaceAll("");
	}

	public static String cleanResource(String s){
		return s.toLowerCase().replaceAll("[^a-z0-9_.\\-/:]","");
	}

	public static String translate(Object... arr) {
		LanguageMap languagemap = LanguageMap.getInstance();
		String s = "";
		for(Object str : arr){
			s += languagemap.getOrDefault(str.toString());
		}
		return s;
	}
	
	public static String[] splitLines(String s){
		return s.split("\r\n|\r|\n");
	}

	public static String newLine() {
		return System.getProperty("line.separator");
	}

	public static int parseInt(String s, int i) {
		try {
			return Integer.parseInt(s);
		}
		catch(NumberFormatException e) {}
		return i;
	}
}
