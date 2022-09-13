package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiScrollWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;

import java.util.Arrays;
import java.util.List;

public class CustomGuiScrollComponent extends GuiCustomScroll implements IGuiComponent {

    GuiCustom parent;

    private final CustomGuiScrollWrapper component;

    public CustomGuiScrollComponent(Screen parent, CustomGuiScrollWrapper component) {
        super(parent, component.getID(), component.isMultiSelect());
        this.component = component;

        this.minecraft = Minecraft.getInstance();
        this.font = minecraft.font;

        this.guiLeft = GuiCustom.guiLeft + component.getPosX();
        this.guiTop = GuiCustom.guiTop + component.getPosY();
        this.setSize(component.getWidth(), component.getHeight());

        this.setUnsortedList(Arrays.asList(component.getList()));

        if(component.getDefaultSelection() >= 0) {
            int defaultSelect = component.getDefaultSelection();
            if(defaultSelect < this.getList().size())
                setSelected(list.get(defaultSelect));
        }
    }

    public void setParent(GuiCustom parent) {
        this.parent = parent;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.pushPose();
        matrixStack.translate(0, 0, id);
        boolean hovered = mouseX >= this.guiLeft && mouseY >= this.guiTop && mouseX < this.guiLeft + this.getWidth() && mouseY < this.guiTop + this.getHeight();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if(hovered && component.hasHoverText()) {
            this.parent.hoverText = component.getHoverTextList();
        }
        matrixStack.popPose();
    }

    @Override
    public ICustomGuiComponent toComponent() {
        List<String> list = getList();
        component.setList(list.toArray(new String[list.size()]));
        return component;
    }

//    @Override
//    public CompoundNBT toNBT() {
//        CompoundNBT nbt = new CompoundNBT();
//        nbt.setInt("id",this.id);
//        if(!getSelectedList().isEmpty()) {
//            ListNBT tagList = new ListNBT();
//            for(String s : getSelectedList())
//                tagList.add(StringNBT.valueOf(s));
//            nbt.put("selectedList", tagList);
//        } else if(getSelected()!=null && !getSelected().isEmpty()) {
//            nbt.putString("selected", this.getSelected());
//        } else
//            nbt.putString("selected", "Null");
//        return nbt;
//    }
}
