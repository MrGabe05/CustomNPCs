package noppes.npcs.api.entity.data;

/**
 * Returns objects from the Pixelmon API see <a href="https://reforged.gg/docs/">https://reforged.gg/docs/</a>
 */
public interface IPixelmonPlayerData {

	/**
	 * Returns <a href="https://reforged.gg/docs/com/pixelmonmod/pixelmon/api/storage/PartyStorage.html">PartyStorage</a>
	 */
    Object getParty();

	/**
	 * Returns <a href="https://reforged.gg/docs/com/pixelmonmod/pixelmon/api/storage/PCStorage.html">PCStorage</a>
	 */
    Object getPC();
	
}
