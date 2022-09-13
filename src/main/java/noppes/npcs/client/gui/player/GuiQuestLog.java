package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabQuests;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.shared.client.gui.components.GuiButtonNextPage;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.components.GuiMenuSideButton;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITopButtonListener;
import noppes.npcs.shared.common.util.NaturalOrderComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GuiQuestLog extends GuiNPCInterface implements ITopButtonListener, ICustomScrollListener {

	private final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/standardbg.png");

	public HashMap<String, List<Quest>> activeQuests = new HashMap<String, List<Quest>>();
	private HashMap<String, Quest> categoryQuests = new HashMap<String, Quest>();
	public Quest selectedQuest = null;
	public ITextComponent selectedCategory = StringTextComponent.EMPTY;
    private final PlayerEntity player;
    private GuiCustomScroll scroll;
	private final HashMap<Integer, GuiMenuSideButton> sideButtons = new HashMap<Integer,GuiMenuSideButton>();
	private boolean noQuests = false;
	
	private final int maxLines = 10;
	private int currentPage = 0;
	private int maxPages = 1;

	TextBlockClient textblock = null;
	
	private final Minecraft mc = Minecraft.getInstance();
	
	public GuiQuestLog(PlayerEntity player) {
		super();
		this.player = player;
        imageWidth = 280;
        imageHeight = 180;
        drawDefaultBackground = false;
	}
	
	@Override
    public void init(){
        super.init();
        for(Quest quest : PlayerQuestController.getActiveQuests(player)){
    		String category = quest.category.title;
    		if(!activeQuests.containsKey(category))
    			activeQuests.put(category, new ArrayList<Quest>());
    		List<Quest> list = activeQuests.get(category);
    		list.add(quest);
        }
        
    	sideButtons.clear();
        guiTop +=10;

		TabRegistry.updateTabValues(guiLeft, guiTop, InventoryTabQuests.class);
		TabRegistry.addTabsToList((button) -> addButton(button));
        
        noQuests = false;

        if(activeQuests.isEmpty()){
        	noQuests = true;
        	return;
        }
        List<String> categories = new ArrayList<String>();
        categories.addAll(activeQuests.keySet());
        Collections.sort(categories, new NaturalOrderComparator());
        int i = 0;
        for(String category : categories){
        	if(selectedCategory == StringTextComponent.EMPTY)
        		selectedCategory = new TranslationTextComponent(category);
        	sideButtons.put(i, new GuiMenuSideButton(this, i,guiLeft - 69, this.guiTop +2 + i*21, 70,22, category));
        	i++;
        }
        sideButtons.get(categories.indexOf(selectedCategory.getString())).active = true;
        
        if(scroll == null)
        	scroll = new GuiCustomScroll(this,0);

    	HashMap<String, Quest> categoryQuests = new HashMap<String, Quest>();
        for(Quest q : activeQuests.get(selectedCategory.getString())){
        	categoryQuests.put(q.title, q);
        }
        this.categoryQuests = categoryQuests;
        
        scroll.setList(new ArrayList<String>(categoryQuests.keySet()));
        scroll.setSize(134, 174);
        scroll.guiLeft = guiLeft + 5;
        scroll.guiTop = guiTop + 15;
        addScroll(scroll);

        addButton(new GuiButtonNextPage(this, 1, guiLeft + 286, guiTop + 114, true, (b) ->{
			currentPage++;
			init();
		}));
        addButton(new GuiButtonNextPage(this, 2, guiLeft + 144, guiTop + 114, false, (b) -> {
			currentPage--;
			init();
		}));

        getButton(1).visible = selectedQuest != null && currentPage < (maxPages - 1);
        getButton(2).visible = selectedQuest != null && currentPage > 0;
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    	if(scroll != null)
    		scroll.visible = !noQuests;
    	renderBackground(matrixStack);
        minecraft.getTextureManager().bind(resource);
		blit(matrixStack, guiLeft, guiTop, 0, 0, 252, 195);
		blit(matrixStack, guiLeft + 252, guiTop, 188, 0, 67, 195);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        
        if(noQuests){
        	mc.font.draw(matrixStack, new TranslationTextComponent("quest.noquests"),guiLeft + 84,guiTop + 80, CustomNpcResourceListener.DefaultTextColor);
        	return;
        }
        for(GuiMenuSideButton button: sideButtons.values().toArray(new GuiMenuSideButton[sideButtons.size()])){
        	button.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    	mc.font.draw(matrixStack, selectedCategory, guiLeft + 5,guiTop + 5, CustomNpcResourceListener.DefaultTextColor);

        if(selectedQuest == null)
        	return;

    	drawProgress(matrixStack);

    	drawQuestText(matrixStack);

		matrixStack.pushPose();
		matrixStack.translate(guiLeft + 148, guiTop, 0);
		matrixStack.scale(1.24f, 1.24f, 1.24f);
		TranslationTextComponent title = new TranslationTextComponent(selectedQuest.title);
        font.draw(matrixStack, title, (130 - font.width(title)) / 2, 4, CustomNpcResourceListener.DefaultTextColor);
		matrixStack.popPose();
        hLine(matrixStack, guiLeft + 142, guiLeft + 312, guiTop + 17,  + 0xFF000000 + CustomNpcResourceListener.DefaultTextColor);
    }
    
    private void drawQuestText(MatrixStack matrixStack){
    	if(textblock == null)
    		return;
        int yoffset = guiTop + 5; 
    	for(int i = 0; i < maxLines; i++){
    		int index = i + currentPage * maxLines;
    		if(index >= textblock.lines.size())
    			continue;
			ITextComponent text = textblock.lines.get(index);
    		font.draw(matrixStack, text, guiLeft + 142, guiTop + 20 + (i * font.lineHeight), CustomNpcResourceListener.DefaultTextColor);
    	}
    }
    
    private void drawProgress(MatrixStack matrixStack) {
		ITextComponent title = new TranslationTextComponent("quest.objectives").append(":");
    	mc.font.draw(matrixStack, title, guiLeft + 142, guiTop + 130, CustomNpcResourceListener.DefaultTextColor);
        hLine(matrixStack, guiLeft + 142, guiLeft + 312, guiTop + 140,  + 0xFF000000 + CustomNpcResourceListener.DefaultTextColor);
    	
    	int yoffset = guiTop + 144;
        for(IQuestObjective objective : selectedQuest.questInterface.getObjectives(player)){
        	mc.font.draw(matrixStack, new StringTextComponent("- ").append(objective.getMCText()), guiLeft + 142, yoffset , CustomNpcResourceListener.DefaultTextColor);
	        yoffset += 10;
        }

        hLine(matrixStack, guiLeft + 142, guiLeft + 312, guiTop + 178,  + 0xFF000000 + CustomNpcResourceListener.DefaultTextColor);
        String complete = selectedQuest.getNpcName();
        if(complete != null && !complete.isEmpty()) {
        	mc.font.draw(matrixStack, new TranslationTextComponent("quest.completewith", complete), guiLeft + 142, guiTop + 182, CustomNpcResourceListener.DefaultTextColor);
        }
	}

    @Override
    public boolean mouseClicked(double i, double j, int k){
    	super.mouseClicked(i, j, k);
        if (k == 0){
        	if(scroll != null)
        		scroll.mouseClicked(i, j, k);
            for (GuiMenuSideButton button : new ArrayList<GuiMenuSideButton>(sideButtons.values())){
                if (button.mouseClicked(i, j, k)){
                	sideButtonPressed(button);
                	return true;
                }
            }
        }
        return false;
    }
    
    private void sideButtonPressed(GuiMenuSideButton button) {
    	if(button.active)
    		return;
    	NoppesUtil.clickSound();
        selectedCategory = button.getMessage();
        selectedQuest = null;
        this.init();
    }
    
	@Override
	public void scrollClicked(double i, double j, int k, GuiCustomScroll scroll) {
		if(!scroll.hasSelected())
			return;
		selectedQuest = categoryQuests.get(scroll.getSelected());
		textblock = new TextBlockClient(selectedQuest.getLogText(), 172, true, player);
    	if(textblock.lines.size() > maxLines) {
    		maxPages = MathHelper.ceil(1f * textblock.lines.size() / maxLines);
    	}
		currentPage = 0;
		init();
	}

    @Override
    public boolean isPauseScreen(){
        return false;
    }
    
	@Override
	public void save() {
		
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
	
}
