package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.IScroll;

public class CustomGuiScrollWrapper extends CustomGuiComponentWrapper implements IScroll {

    int width,height;
    int defaultSelection = -1;
    String[] list;
    boolean multiSelect = false;

    public CustomGuiScrollWrapper(){}

    public CustomGuiScrollWrapper(int id, int x, int y, int width, int height, String[] list) {
        setID(id);
        setPos(x,y);
        setSize(width,height);
        setList(list);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public IScroll setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public String[] getList() {
        return list;
    }

    @Override
    public IScroll setList(String[] list) {
        this.list = list;
        return this;
    }

    @Override
    public int getDefaultSelection() {
        return defaultSelection;
    }

    @Override
    public IScroll setDefaultSelection(int defaultSelection) {
        this.defaultSelection = defaultSelection;
        return this;
    }

    @Override
    public boolean isMultiSelect() {
        return multiSelect;
    }

    @Override
    public IScroll setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
        return this;
    }

    @Override
    public int getType() {
        return GuiComponentType.SCROLL;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT compound) {
        super.toNBT(compound);
        compound.putIntArray("size", new int[] {width,height});
        if(defaultSelection >= 0)
            compound.putInt("default", defaultSelection);
        ListNBT list = new ListNBT();
        for(String s : this.list)
            list.add(StringNBT.valueOf(s));
        compound.put("list", list);
        compound.putBoolean("multiSelect", multiSelect);
        return compound;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(CompoundNBT compound) {
        super.fromNBT(compound);
        setSize(compound.getIntArray("size")[0], compound.getIntArray("size")[1]);
        if(compound.contains("default"))
            setDefaultSelection(compound.getInt("default"));
        ListNBT tagList = compound.getList("list", Constants.NBT.TAG_STRING);
        String[] list = new String[tagList.size()];
        for(int i = 0; i < tagList.size(); i++) {
            list[i] = tagList.get(i).getAsString();
        }
        setList(list);
        setMultiSelect(compound.getBoolean("multiSelect"));
        return this;
    }

}
