package noppes.npcs.api.item;

public interface IItemBook extends IItemStack{

	/**
	 * @return If the item is a book, returns a string array with book pages
	 */
	String[] getText();
	
	/**
	 * Set the text for multiple pages
	 */
	void setText(String[] pages);

	String getAuthor();
	
	void setAuthor(String author);

	String getTitle();
	
	void setTitle(String title);

}
