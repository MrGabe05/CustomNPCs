package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.mixin.MouseHelperMixin;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.common.util.NaturalOrderComparator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GuiCustomScroll extends Screen {
	public static final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/misc.png");
	protected List<String> list;
	private int listSize = 0;

	public int id;
	public int guiLeft = 0;
	public int guiTop = 0;

	private int selected = -1;
	private List<Integer> selectedList;
	private int hover;

	private int listHeight;
	private int scrollY;
	private int maxScrollY;
	private int scrollHeight;
	private boolean isScrolling;
	public boolean multipleSelection = false;
	public ICustomScrollListener listener;
	private boolean isSorted = true;
	public boolean visible = true;
	private boolean selectable = true;
	private boolean mouseInList = false;

	private int lastClickedItem = -1;
	private long lastClickedTime = 0;

	private final GuiTextFieldNop textField;
	private boolean hasSearch = true;

	private String searchStr = "";
	private String[] searchWords = new String[0];
	public GuiCustomScroll(Screen parent, int id){
		super(null);
		width = 176;
		height = 159;
		hover = -1;
		selectedList = new ArrayList<>();
		listHeight = 0;
		scrollY = 0;
		scrollHeight = 0;
		isScrolling = false;
		if(parent instanceof ICustomScrollListener)
			listener = (ICustomScrollListener) parent;
		this.list = new ArrayList<>();
		this.id = id;

		textField = new GuiTextFieldNop(0, null, 0, 0, 176, 20, "");
		//setSize(176, 159);
	}

	public GuiCustomScroll(Screen parent, int id, boolean multipleSelection){
		this(parent,id);
		this.multipleSelection = multipleSelection;
	}

	public void setSize(int x, int y){
		textField.setWidth(x);
		height = y - textFieldHeight();
		width = x;
		listHeight = 14 * listSize;

		if(listHeight > 0)
			scrollHeight = (int) (((double)(height - 8) / (double)listHeight) * (height -8));
		else
			scrollHeight = Integer.MAX_VALUE;

		maxScrollY = listHeight - (height - 8) - 1;
		if(maxScrollY > 0 && scrollY > maxScrollY || maxScrollY <= 0 && scrollY > scrollHeight){
			scrollY = 0;
		}
	}

	public void disabledSearch(){
		hasSearch = false;
	}

	private int textFieldHeight(){
		return hasSearch ? 22 : 0;
	}

	private void reset(){
		if(searchWords.length == 0){
			listSize = list.size();
		}
		else{
			listSize = (int) list.stream().filter(this::isSearched).count();
		}
		setSize(width, height + textFieldHeight());
	}

	private boolean isSearched(String s){
		s = I18n.get(s);
		for(String k : searchWords){
			if(!s.toLowerCase().contains(k.toLowerCase())){
				return false;
			}
		}
		return true;
	}

	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height + textFieldHeight();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if(!visible)
			return;

		if(hasSearch) {
			textField.x = guiLeft;
			textField.y = guiTop;
			textField.render(matrixStack, mouseX, mouseY, partialTicks);
		}
		guiTop += textFieldHeight();
		mouseInList = isMouseOver(mouseX, mouseY);

		fillGradient(matrixStack, guiLeft, guiTop, width +guiLeft , height +guiTop, 0xc0101010, 0xd0101010);
		minecraft.getTextureManager().bind(resource);

		if(scrollHeight < height - 8){
			drawScrollBar(matrixStack);
		}
		matrixStack.pushPose();
		matrixStack.translate(guiLeft, guiTop, 0.0F);

		if(selectable)
			hover = getMouseOver(mouseX, mouseY);

		drawItems(matrixStack);

		matrixStack.popPose();
		if(scrollHeight < height - 8){
			mouseX -= guiLeft;
			mouseY -= guiTop;
			if(((MouseHelperMixin)minecraft.mouseHandler).getActiveButton() == 0){
				if(mouseX >= width -10 && mouseX < width -2 && mouseY >= 4 && mouseY < height){
					isScrolling = true;
				}
			}
			else
				isScrolling = false;

			if(isScrolling){
				scrollY = (((mouseY - 8) * listHeight) / (height -8)) - (scrollHeight);
				if(scrollY < 0){
					scrollY = 0;
				}
				if(scrollY > maxScrollY){
					scrollY = maxScrollY;
				}
			}
		}
		guiTop -= textFieldHeight();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
		if(mouseScrolled != 0 && mouseInList){
			scrollY += mouseScrolled > 0?-14:14;
			if(scrollY > maxScrollY)
				scrollY = maxScrollY;
			if(scrollY < 0)
				scrollY = 0;
			return true;
		}
		return false;
	}

	public boolean mouseInOption(int i, int j, int k)
	{
		int xOffset = scrollHeight < height - 8?10:0;
		int l = 4;
		int i1 = ((14 * k + 4) - scrollY);
		return i >= l - 1 && i < width - 2 - xOffset && j >= i1 - 1 && j < i1 + 8;
	}

	protected void drawItems(MatrixStack matrixStack) {
		int displayIndex = 0;
		for(int i = 0; i < list.size(); i++) {
			if(!isSearched(list.get(i))){
				continue;
			}
			int j = 4;
			int k = (14 * displayIndex + 4) - scrollY;
			if(k >= 4 && k + 12 < height){
				int xOffset = scrollHeight < height - 8?0:10;
				String displayString = I18n.get(list.get(i));

				String text = "";
				float maxWidth = (width + xOffset - 8) * 0.8f;
				if(font.width(displayString) > maxWidth){
					for(int h = 0; h < displayString.length(); h++){
						char c = displayString.charAt(h);
						text += c;
						if(font.width(text) > maxWidth)
							break;
					}
					if(displayString.length() > text.length())
						text += "...";
				}
				else
					text = displayString;
				if((multipleSelection && selectedList.contains(i)) || (!multipleSelection && selected == i)) {
					vLine(matrixStack, j-2, k-4, k + 10, 0xffffffff);
					vLine(matrixStack, j + width - 18 + xOffset, k - 4, k + 10, 0xffffffff);
					hLine(matrixStack, j - 2, j + width - 18 + xOffset, k - 3 , 0xffffffff);
					hLine(matrixStack, j - 2, j + width - 18 + xOffset, k + 10 , 0xffffffff);
					font.draw(matrixStack, text, j , k, 0xffffff);
				}
				else if(i == hover)
					font.draw(matrixStack, text, j , k, 0x00ff00);
				else
					font.draw(matrixStack, text, j , k, 0xffffff);
			}
			displayIndex++;
		}

	}
	public String getSelected(){
		if(selected < 0 || selected >= list.size())
			return null;
		return list.get(selected);
	}

	private int getMouseOver(int i, int j){
		i -= guiLeft;
		j -= guiTop;
		//font.draw(matrixStack, ".", i , j, 0xffffff);
		if(i >= 4 && i < width - 4 && j >= 4 && j < height) {
			int displayIndex = 0;
			for(int j1 = 0; j1 < list.size(); j1++) {
				if(!isSearched(list.get(j1))){
					continue;
				}
				if(!mouseInOption(i, j, displayIndex)) {
					displayIndex++;
					continue;
				}
				return j1;
			}
		}

		return -1;
	}

	@Override
	public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
		if(hasSearch) {
			boolean bo = this.textField.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
			if (!searchStr.equals(textField.getValue())) {
				searchStr = textField.getValue().trim();
				searchWords = searchStr.split(" ");
				reset();
			}
			return bo;
		}
		return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
	}

	@Override
	public boolean charTyped(char p_231042_1_, int p_231042_2_) {
		if(hasSearch) {
			boolean bo = this.textField.charTyped(p_231042_1_, p_231042_2_);
			if (!searchStr.equals(textField.getValue())) {
				searchStr = textField.getValue().trim();
				searchWords = searchStr.split(" ");
				reset();
			}
			return bo;
		}
		return super.charTyped(p_231042_1_, p_231042_2_);
	}

	@Override
	public boolean mouseClicked(double i, double j, int k) {
		if(hasSearch) {
			textField.mouseClicked(i, j, k);
		}
		if(k != 0 || hover < 0)
			return false;

		if(multipleSelection) {
			if(selectedList.contains(hover)) {
				selectedList.remove(Integer.valueOf(hover));
			} else {
				selectedList.add(hover);
			}
		} else {
			selected = hover;
			hover = -1;
		}

		if(listener != null) {
			long time = System.currentTimeMillis();
			listener.scrollClicked(i, j, k, this);
			if(selected >= 0 && selected == lastClickedItem && time - lastClickedTime < 500) {
				listener.scrollDoubleClicked(getSelected(), this);
			}
			lastClickedTime = time;
			lastClickedItem = selected;
		}
		return true;
	}

	private void drawScrollBar(MatrixStack matrixStack)
	{
		int i = guiLeft + width - 9;
		int j = guiTop + (int)(((double)scrollY / (double)listHeight) * (double)(height -8)) + 4;
		int k = j;
		blit(matrixStack, i, k, width, 9, 5, 1);
		for(k++; k < (j + scrollHeight) - 1; k++)
		{
			blit(matrixStack, i, k, width, 10, 5, 1);
		}

		blit(matrixStack, i, k, width, 11, 5, 1);
	}
	public boolean hasSelected() {
		return selected >= 0;
	}
	public void setList(List<String> list){
		if(isSameList(list))
			return;
		isSorted = true;
		scrollY = 0;
		list.sort(new NaturalOrderComparator());
		this.list = list;
		reset();
	}

	public void setUnsortedList(List<String> list){
		if(isSameList(list))
			return;
		isSorted = false;
		scrollY = 0;
		this.list = list;
		reset();
	}

	private boolean isSameList(List<String> list) {
		if(this.list.size() != list.size())
			return false;

		for(String s : this.list) {
			if(!list.contains(s))
				return false;
		}
		return true;
	}

	public void replace(String old, String name) {
		String select = getSelected();
		list.remove(old);
		list.add(name);
		if(isSorted)
			list.sort(new NaturalOrderComparator());
		if(old.equals(select))
			select = name;

		setSelected(select);
		reset();
	}
	public void setSelected(String name) {
		selected = list.indexOf(name);
	}
	public void clear() {
		list = new ArrayList<>();
		selected = -1;
		scrollY = 0;
		searchStr = "";
		searchWords = new String[0];
		textField.setValue("");
		reset();
	}

	public void clearSelection() {
		list = new ArrayList<>();
		selected = -1;
	}
	public List<String> getList() {
		return list;
	}
	public List<String> getSelectedList() {
		return IntStream.range(0, list.size()).filter(i -> selectedList.contains(i)).mapToObj(i -> list.get(i)).collect(Collectors.toList());
	}
	public void setSelectedList(Collection<String> selectedList) {
		this.selectedList = selectedList.stream().map(t -> list.indexOf(t)).collect(Collectors.toList());
	}
	public GuiCustomScroll setUnselectable() {
		selectable = false;
		return this;
	}

	public void scrollTo(String name) {
		int i = list.indexOf(name);
		if(i < 0 || scrollHeight >= height - 8)
			return;

		int pos = (int) (1f * i / list.size() * listHeight);
		if(pos > maxScrollY)
			pos = maxScrollY;
		scrollY = pos;
	}

	public boolean isMouseOver(int x, int y) {
		return x >= guiLeft && x <= guiLeft + width && y >= guiTop && y <= guiTop + height;
	}

	public int getSelectedIndex() {
		return selected;
	}

	public void setSelectedIndex(int i) {
		if(i < 0){
			selected = -1;
		}
		else{
			selected = i;
		}
	}
}
