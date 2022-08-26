package noppes.npcs.client.gui.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.ModelData;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.mainmenu.GuiNpcDisplay;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiSliderNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.ISliderListener;

public abstract class GuiCreationScreenInterface extends GuiNPCInterface implements ISliderListener {
	public static String Message = "";
	public LivingEntity entity;
	
	private boolean saving = false;
	protected boolean hasSaving = true;
	public int active = 0;

	private PlayerEntity player;
	public int xOffset = 0;
	public ModelData playerdata;
	
	protected CompoundNBT original = new CompoundNBT();

	private static float rotation = 0.5f;
	
	public GuiCreationScreenInterface(EntityNPCInterface npc){
		super(npc);
		playerdata = ((EntityCustomNpc)npc).modelData;
		original = playerdata.save();
		imageWidth = 400;
		imageHeight = 240;
		xOffset = 140;

		player = Minecraft.getInstance().player;
		this.drawDefaultBackground = true;
	}

    @Override
    public void init() {
    	super.init();
    	entity = playerdata.getEntity(npc);
    	this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

    	addButton(new GuiButtonNop(this, 1, guiLeft + 62, guiTop, 60, 20, "gui.entity"){
			@Override
			public void onClick(double x, double y){
				openGui(new GuiCreationEntities(npc));
			}
		});
    	if(entity == null) {
			addButton(new GuiButtonNop(this, 2, guiLeft, guiTop + 23, 60, 20, "gui.parts"){
				@Override
				public void onClick(double x, double y){
					openGui(new GuiCreationParts(npc));
				}
			});
		}
    	else if(!(entity instanceof EntityNPCInterface)){
    		GuiCreationExtra gui = new GuiCreationExtra(npc);
    		gui.playerdata = playerdata;
    		if(!gui.getData(entity).isEmpty())
    			addButton(new GuiButtonNop(this, 2, guiLeft, guiTop + 23, 60, 20, "gui.extra"){
					@Override
					public void onClick(double x, double y){
						openGui(new GuiCreationExtra(npc));
					}
				});
    		else if(active == 2){
				minecraft.setScreen(new GuiCreationEntities(npc));
    			return;
    		}
    	}
    	if(entity == null)
    		addButton(new GuiButtonNop(this, 3, guiLeft + 62, guiTop + 23, 60, 20, "gui.scale"){
				@Override
				public void onClick(double x, double y){
					openGui(new GuiCreationScale(npc));
				}
			});
    	if(hasSaving){
    		addButton(new GuiButtonNop(this, 4, guiLeft, guiTop + imageHeight - 24, 60, 20, "gui.save"){
				@Override
				public void onClick(double x, double y){
					GuiCreationScreenInterface.this.setSubGui(new GuiPresetSave(GuiCreationScreenInterface.this, playerdata));
				}
			});
    		addButton(new GuiButtonNop(this, 5, guiLeft + 62, guiTop + imageHeight - 24, 60, 20, "gui.load"){
				@Override
				public void onClick(double x, double y){
					openGui(new GuiCreationLoad(npc));
				}
			});
    	}
    	if(getButton(active) == null){
    		openGui(new GuiCreationEntities(npc));
    		return;
    	}
    	getButton(active).active = false;
    	addButton(new GuiButtonNop(this, 66, guiLeft + imageWidth - 20, guiTop, 20, 20, "X"){
			@Override
			public void onClick(double x, double y){
				save();
				NoppesUtil.openGUI(player, new GuiNpcDisplay(npc));
			}
		});
    	    	
    	addLabel(new GuiLabel(0, Message, guiLeft + 120, guiTop + imageHeight - 10, 0xff0000, imageWidth - 120, 20));

    	addSlider(new GuiSliderNop(this, 500, guiLeft + xOffset + 142, guiTop + 210, 120, 20, rotation));
    }
    
    @Override
    public boolean mouseClicked(double i, double j, int k){
    	if(!saving)
    		super.mouseClicked(i, j, k);
    	return true;
    }
    
    @Override
    public void render(MatrixStack stack, int x, int y, float f){
    	super.render(stack, x, y, f);
    	entity = playerdata.getEntity(npc);
    	LivingEntity entity = this.entity;
    	if(entity == null)
    		entity = this.npc;
    	else
    		EntityUtil.Copy(npc, entity);
    	
    	drawNpc(npc, xOffset + 200, 200, 2, (int)(-rotation * 360 - 180));
    }
    
    @Override
    public void onClose(){
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    @Override
    public void save(){
    	CompoundNBT newCompound = playerdata.save();
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.DISPLAY, npc.display.save(new CompoundNBT())));
		Packets.sendServer(new SPacketMenuSave(EnumMenuType.MODEL, newCompound));
    }
    
    public void openGui(Screen gui){
    	minecraft.setScreen(gui);
    }
    
	public void subGuiClosed(Screen subgui){
		init();
	}
	@Override
	public void mouseDragged(GuiSliderNop slider) {
		if(slider.id == 500){
			rotation = slider.sliderValue;
			slider.setString("" + (int)(rotation * 360));
		}
	}

	@Override
	public void mousePressed(GuiSliderNop slider) {
		
	}

	@Override
	public void mouseReleased(GuiSliderNop slider) {
		
	}
}
