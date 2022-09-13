package noppes.npcs.api.entity.data.role;

public interface IRoleDialog {

	String getDialog();

	void setDialog(String text);

	String getOption(int option);

	/**
	 * @param option The dialog option (1-6)
	 */
    void setOption(int option, String text);

	String getOptionDialog(int option);

	/**
	 * @param option The dialog option (1-6)
	 */
    void setOptionDialog(int option, String text);

}
