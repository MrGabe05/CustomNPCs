package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import noppes.npcs.api.constants.ItemType;
import noppes.npcs.api.item.IItemBook;

public class ItemBookWrapper extends ItemStackWrapper implements IItemBook{

	protected ItemBookWrapper(ItemStack item) {
		super(item);
	}

	@Override
	public String getTitle(){
		return getTag().getString("title");
	}
	
	@Override
	public void setTitle(String title){
		getTag().putString("title", title);
	}

	@Override
	public String getAuthor(){
		return getTag().getString("author");
	}
	
	@Override
	public void setAuthor(String author){
		getTag().putString("author", author);
	}
	
	@Override
	public String[] getText(){
		List<String> list = new ArrayList<String>();
        ListNBT pages = getTag().getList("pages", 8);
        for (int i = 0; i < pages.size(); ++i){
        	list.add(pages.getString(i));
        }        
        return list.toArray(new String[list.size()]);
	}

	@Override
	public void setText(String[] pages){
		ListNBT list = new ListNBT();
		if(pages != null && pages.length > 0){
			for(String page : pages){
				list.add(StringNBT.valueOf(page));
			}
		}
		getTag().put("pages", list);
	}
	
	private CompoundNBT getTag(){
		CompoundNBT comp = item.getTag();
		if(comp == null)
			item.setTag(comp = new CompoundNBT());
		return comp;
	}

	@Override
	public boolean isBook(){
		return true;
	}

	@Override
	public int getType(){
		return ItemType.BOOK;
	}
}
