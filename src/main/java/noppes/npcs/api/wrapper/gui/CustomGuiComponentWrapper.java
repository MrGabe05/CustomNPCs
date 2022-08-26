package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.ICustomGuiComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomGuiComponentWrapper implements ICustomGuiComponent {

    int id;
    int posX,posY;
    List<TranslationTextComponent> hoverText = new ArrayList<>();

    @Override
    public int getID() {
        return id;
    }

    @Override
    public ICustomGuiComponent setID(int id) {
        this.id = id;
        return this;
    }

    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }

    @Override
    public ICustomGuiComponent setPos(int x, int y) {
        this.posX = x;
        this.posY = y;
        return this;
    }

    @Override
    public boolean hasHoverText() {
        return hoverText.size()>0;
    }

    @Override
    public String[] getHoverText() {
        String[] ht = new String[hoverText.size()];
        for(int i = 0; i < hoverText.size(); i++){
            ht[i] = hoverText.get(i).getKey();
        }
        return ht;
    }

    public List<TranslationTextComponent> getHoverTextList() {
        return hoverText;
    }

    @Override
    public ICustomGuiComponent setHoverText(String text) {
        this.hoverText = new ArrayList<>();
        this.hoverText.add(new TranslationTextComponent(text));
        return this;
    }

    @Override
    public ICustomGuiComponent setHoverText(String[] text) {
        List<TranslationTextComponent> list = new ArrayList<>();
        for(String s : text){
            list.add(new TranslationTextComponent(s));
        }
        this.hoverText = list;
        return this;
    }

    public ICustomGuiComponent setHoverText(List<TranslationTextComponent> list) {
        this.hoverText = list;
        return this;
    }

    public abstract int getType();

    public CompoundNBT toNBT(CompoundNBT nbt) {
        nbt.putInt("id", id);
        nbt.putIntArray("pos", new int[]{posX,posY});
        if(hoverText!=null) {
            ListNBT list = new ListNBT();
            for (TranslationTextComponent s : hoverText){
                list.add(StringNBT.valueOf(s.getKey()));
            }
            if(list.size() > 0)
                nbt.put("hover", list);
        }
        nbt.putInt("type", getType());
        return nbt;
    }

    public CustomGuiComponentWrapper fromNBT(CompoundNBT nbt) {
        setID(nbt.getInt("id"));
        setPos(nbt.getIntArray("pos")[0],nbt.getIntArray("pos")[1]);
        if(nbt.contains("hover")) {
            ListNBT list = nbt.getList("hover", Constants.NBT.TAG_STRING);
            String[] hoverText = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                hoverText[i] =  list.get(i).getAsString();
            }
            setHoverText(hoverText);
        }
        return this;
    }

    public static CustomGuiComponentWrapper createFromNBT(CompoundNBT nbt) {
        switch (nbt.getInt("type")) {
            case GuiComponentType.BUTTON:
                return new CustomGuiButtonWrapper().fromNBT(nbt);
            case GuiComponentType.LABEL:
                return new CustomGuiLabelWrapper().fromNBT(nbt);
            case GuiComponentType.TEXTURED_RECT:
                return new CustomGuiTexturedRectWrapper().fromNBT(nbt);
            case GuiComponentType.TEXT_FIELD:
                return new CustomGuiTextFieldWrapper().fromNBT(nbt);
            case GuiComponentType.SCROLL:
                return new CustomGuiScrollWrapper().fromNBT(nbt);
            case GuiComponentType.ITEM_SLOT:
                return new CustomGuiItemSlotWrapper().fromNBT(nbt);
            default:
                return null;
        }
    }

}
