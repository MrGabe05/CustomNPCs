package noppes.npcs.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNbtBookBlockSave;
import noppes.npcs.packets.server.SPacketNbtBookEntitySave;
import noppes.npcs.shared.client.gui.GuiTextAreaScreen;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNbtBook extends GuiNPCInterface implements IGuiData {
	
	private final BlockPos pos;
	private TileEntity tile;
	private BlockState state;
	private ItemStack blockStack;
	
	private int entityId;
	private Entity entity;

	private CompoundNBT originalCompound;
	private CompoundNBT compound;

	private String faultyText = null;
	private String errorMessage = null;
	
    public GuiNbtBook(BlockPos pos) {
		this.pos = pos;
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
	}

	@Override
	public void init(){
    	super.init();
    	int y = guiTop + 40;
    	if(state != null) {
    		addLabel(new GuiLabel(11, "x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ(), guiLeft + 60, guiTop + 6));
    		addLabel(new GuiLabel(12, "id: " + ForgeRegistries.BLOCKS.getKey(state.getBlock()), guiLeft + 60, guiTop + 16));
    	}
    	if(entity != null) {
    		addLabel(new GuiLabel(12, "id: " + entity.getType().getDescriptionId(), guiLeft + 60, guiTop + 6));
    	}

        addButton(new GuiButtonNop(this, 0, guiLeft + 38, guiTop + 144, 180,20, "nbt.edit"));
        getButton(0).active = compound != null && !compound.isEmpty();

        addLabel(new GuiLabel(0, "", guiLeft + 4, guiTop + 167));
        addLabel(new GuiLabel(1, "", guiLeft + 4, guiTop + 177));

        addButton(new GuiButtonNop(this, 66, guiLeft + 128, guiTop + 190,120,20, "gui.close"));
        addButton(new GuiButtonNop(this, 67, guiLeft + 4, guiTop + 190,120,20, "gui.save"));

        if(errorMessage != null) {
        	getButton(67).active = false;
    		int i = errorMessage.indexOf(" at: ");
    		if(i > 0) {
                getLabel(0).setMessage(new TranslationTextComponent(errorMessage.substring(0, i)));
                getLabel(1).setMessage(new TranslationTextComponent(errorMessage.substring(i)));
    		}
    		else {
                getLabel(0).setMessage(new TranslationTextComponent(errorMessage));
    		}
        }
        if(getButton(67).active && originalCompound != null) {
        	getButton(67).active = !originalCompound.equals(compound);
        }
    }

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		int id = guibutton.id;
		if(id == 0) {
			if(faultyText != null) {
				setSubGui(new GuiTextAreaScreen(compound.toString(), faultyText).enableHighlighting());
			}
			else {
				setSubGui(new GuiTextAreaScreen(compound.toString()).enableHighlighting());
			}
		}
		if(id == 67) {
            getLabel(0).setMessage(new TranslationTextComponent("Saved"));
			if(compound.equals(originalCompound))
				return;
			if(tile == null) {
				Packets.sendServer(new SPacketNbtBookEntitySave(entityId, compound));
				return;
			}
			else {
				Packets.sendServer(new SPacketNbtBookBlockSave(pos, compound));
			}
			originalCompound = compound.copy();
        	getButton(67).active = false;
		}
		if(id == 66) {
			close();
		}
	}

	@Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    	super.render(matrixStack, mouseX, mouseY, partialTicks);
    	if(hasSubGui())
    		return;
    	
    	if(state != null) {
			RenderSystem.pushMatrix();
			RenderSystem.translatef(guiLeft + 4, guiTop + 4, 0);
			RenderSystem.scalef(3, 3, 3);
            //RenderHelper.enableGUIStandardItemLighting();
	        itemRenderer.renderAndDecorateItem(blockStack, 0, 0);
	        itemRenderer.renderGuiItemDecorations(font, blockStack, 0, 0);
            //RenderHelper.disableStandardItemLighting();
			RenderSystem.popMatrix();
    	}
    	
    	if(entity instanceof LivingEntity) {
    		drawNpc((LivingEntity)entity, 20, 80, 1, 0);
    	}
    }

	@Override
	public void subGuiClosed(Screen gui) {
		if(gui instanceof GuiTextAreaScreen){
			try {
				compound = JsonToNBT.parseTag(((GuiTextAreaScreen)gui).text);
				errorMessage = faultyText = null;
			} catch (CommandSyntaxException e) {
				errorMessage = e.getLocalizedMessage();
				faultyText = ((GuiTextAreaScreen)gui).text;
			}
	        init();
		}
	}
	
	@Override
	public void save() {
	}

	@Override
	public void setGuiData(CompoundNBT compound) {
		if(compound.contains("EntityId")) {
			entityId = compound.getInt("EntityId");
			entity = player.level.getEntity(entityId);
		}
		else {
			tile = player.level.getBlockEntity(pos);
			state = player.level.getBlockState(pos);
			blockStack = state.getBlock().getCloneItemStack(player.level, pos, state);
		}
		
		originalCompound = compound.getCompound("Data");
		this.compound = originalCompound.copy();
		init();
	}
}
