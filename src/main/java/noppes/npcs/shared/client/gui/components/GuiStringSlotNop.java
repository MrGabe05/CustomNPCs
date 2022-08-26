package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractList;
import noppes.npcs.shared.common.util.NaturalOrderComparator;

import java.util.*;

public class GuiStringSlotNop<E extends GuiStringSlotNop.ListEntry> extends AbstractList {

    public HashSet<String> selectedList;
    private boolean multiSelect;
    private GuiBasic parent;
    public GuiStringSlotNop(Collection<String> list, GuiBasic parent, boolean multiSelect){
        super(Minecraft.getInstance(), parent.width, parent.height, 32, parent.height - 64, parent.getFontRenderer().lineHeight + 3);
        selectedList = new HashSet<String>();
        this.parent = parent;
        this.multiSelect = multiSelect;
        if(list != null){
            setList(list);
        }
    }

    public void setList(Collection<String> l){
        clearEntries();
        List<String> list = new ArrayList<>(l);
        Collections.sort(list, new NaturalOrderComparator());
        for(String s : list){
            this.addEntry(new ListEntry(s));
        }
        setSelected((ListEntry)null);
    }

    public void setColoredList(Map<String, Integer> m){
        clearEntries();
        List<String> list = new ArrayList<>(m.keySet());
        Collections.sort(list, new NaturalOrderComparator());
        for(String s : list){
            this.addEntry(new ListEntry(s, m.get(s)));
        }
        setSelected((ListEntry)null);
    }

    public void setSelected(String s){
        if(s == null){
            setSelected((ListEntry)null);
        }
        else{
            for(Object e : children()){
                if(((ListEntry)e).data.equals(s)){
                    setSelected((ListEntry)e);
                }
            }
        }
    }

    public String getSelectedString(){
        if(getSelected() == null){
            return null;
        }
        return ((ListEntry)getSelected()).data;
    }

    @Override
    protected boolean isSelectedItem(int i) {
    	if(!multiSelect){
	        return super.isSelectedItem(i);
    	}
        return selectedList.contains(((ListEntry)getEntry(i)).data);
    }

//    protected int getItemCount() {
//        return list.size();
//    }

//    @Override
//    protected int getContentHeight()
//    {
//        return list.size() * size;
//    }

    @Override
    public void renderBackground(MatrixStack matrixStack) {
        parent.renderBackground(matrixStack);
    }

//    @Override
//	protected void drawSlot(int i, int j, int k, int l, int var6, int var7, float partialTick) {
//    	String s = list.get(i);
//    	//if(!parent.drawSlot(i, j, k, l, tessellator, s))
//    	parent.draw(parent.getFontRenderer(), s, j + 50, k + 3, 0xFFFFFF);
//    }

	public void clear() {
		clearEntries();
	}

    public class ListEntry extends AbstractList.AbstractListEntry {
        public final String data;
        public final int color;
        public ListEntry(String data){
            this.data = data;
            this.color = 0xFFFFFF;
        }
        public ListEntry(String data, int color){
            this.data = data;
            this.color = color;
        }

        @Override
        public void render(MatrixStack mStack, int index, int rowTop, int rowBottom, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            //AbstractGui.drawString(mStack, parent.getFontRenderer(), this.data, rowBottom, rowTop, 10526880);
            parent.getFontRenderer().draw(mStack, data, rowBottom, rowTop, color);
        }


        private long prevTime = 0;
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        GuiSelectWorld.onElementSelected(parentWorldGui, i);
//        boolean flag1 = GuiSelectWorld.getSelectedWorld(parentWorldGui) >= 0 && GuiSelectWorld.getSelectedWorld(parentWorldGui) < getSize();
//        GuiSelectWorld.getSelectButton(parentWorldGui).enabled = flag1;
//        GuiSelectWorld.getRenameButton(parentWorldGui).enabled = flag1;
//        GuiSelectWorld.getDeleteButton(parentWorldGui).enabled = flag1;
//        if(flag && flag1)
//        {
//            parentWorldGui.selectWorld(i);
//        }
            long time = System.currentTimeMillis();
            ListEntry s = (ListEntry) GuiStringSlotNop.this.getSelected();
            if(s == this && time - prevTime < 400 ) {
                parent.doubleClicked();
            }
            prevTime = time;

            GuiStringSlotNop.this.setSelected(this);
            if(selectedList.contains(data))
                selectedList.remove(data);
            else
                selectedList.add(data);
            parent.elementClicked();
            return true;
        }
    }
}
