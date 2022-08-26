package noppes.npcs.client.gui.roles;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;
import noppes.npcs.containers.ContainerNPCFollowerSetup;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcRoleSave;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

import java.util.HashMap;


public class GuiNpcFollowerSetup extends GuiContainerNPCInterface2<ContainerNPCFollowerSetup> {
	private RoleFollower role;
	private static final ResourceLocation field_110422_t = new ResourceLocation("textures/gui/followersetup.png");

	public GuiNpcFollowerSetup(ContainerNPCFollowerSetup container, PlayerInventory inv, ITextComponent titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
    	imageHeight = 200;
        role = (RoleFollower) npc.role;
        setBackground("followersetup.png");
    }

	@Override
    public void init() {
    	super.init();
        for(int i = 0; i < 3; i++){
        	int x = guiLeft + 66;
        	int y = guiTop + 37;
        	y += i * 25;
        	GuiTextFieldNop tf = new GuiTextFieldNop(i,this, x, y, 24, 20, "1");
        	tf.numbersOnly = true;
        	tf.setMinMaxDefault(1, Integer.MAX_VALUE, 1);
        	addTextField(tf);
        }
        int i = 0;
        for(int day : role.rates.values()){
        	getTextField(i).setValue(day + "");
        	i++;
        }
		ITextComponent text = new TranslationTextComponent("follower.hireText").append(" {days} ").append(new TranslationTextComponent("follower.days"));
		if(!role.dialogHire.isEmpty()){
			text = new TranslationTextComponent(role.dialogHire);
		}
        addTextField(new GuiTextFieldNop(3,this, guiLeft + 100, guiTop + 6, 286, 20, text));

		text = new TranslationTextComponent("follower.farewellText").append(" {player}");
		if(!role.dialogFarewell.isEmpty()){
			text = new TranslationTextComponent(role.dialogFarewell);
		}
        addTextField(new GuiTextFieldNop(4,this, guiLeft + 100, guiTop + 30, 286, 20, text));

        addLabel(new GuiLabel(7, "follower.infiniteDays", guiLeft + 180, guiTop + 80));
        addButton(new GuiButtonYesNo(this, 7, guiLeft + 260, guiTop + 75, role.infiniteDays));
        
        addLabel(new GuiLabel(8, "follower.guiDisabled", guiLeft + 180, guiTop + 104));
        addButton(new GuiButtonYesNo(this, 8, guiLeft + 260, guiTop + 99, role.disableGui));
        
        addLabel(new GuiLabel(9, "follower.allowSoulstone", guiLeft + 180, guiTop + 128));
        addButton(new GuiButtonYesNo(this, 9, guiLeft + 260, guiTop + 123, !role.refuseSoulStone));
        

        addButton(new GuiButtonNop(this, 10, guiLeft + 195, guiTop + 147, 100, 20, "gui.reset"));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton){
    	if(guibutton.id == 7){
    		role.infiniteDays = ((GuiButtonYesNo)guibutton).getBoolean();
    	}
    	if(guibutton.id == 8){
    		role.disableGui = ((GuiButtonYesNo)guibutton).getBoolean();
    	}
    	if(guibutton.id == 9){
    		role.refuseSoulStone = !((GuiButtonYesNo)guibutton).getBoolean();
    	}
    	if(guibutton.id == 10){
    		role.killed();
    	}
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int par1, int limbSwingAmount) {
    	
    }

	@Override
	public void save() {
    	HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
    	for(int i= 0;i < role.inventory.getContainerSize();i++){
    		ItemStack item = role.inventory.getItem(i);
    		if(item != null && !item.isEmpty()){
    			int days = 1;
    			if(!getTextField(i).isEmpty() && getTextField(i).isInteger())
    				days = getTextField(i).getInteger();
    			if(days <= 0)
    				days = 1;
    			
    			map.put(i,days);
    		}
        }
    	role.rates = map;
    	role.dialogHire = getTextField(3).getValue();
    	role.dialogFarewell = getTextField(4).getValue();
		Packets.sendServer(new SPacketNpcRoleSave(role.save(new CompoundNBT())));
	}
}
