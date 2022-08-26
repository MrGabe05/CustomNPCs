package noppes.npcs.controllers.data;

import java.util.ArrayList;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class PlayerMailData{
	public ArrayList<PlayerMail> playermail = new ArrayList<PlayerMail>();

	public void loadNBTData(CompoundNBT compound) {
		ArrayList<PlayerMail> newmail = new ArrayList<PlayerMail>();
		ListNBT list = compound.getList("MailData", 10);
		if(list == null)
			return;
		
		for(int i = 0; i < list.size(); i++){
			PlayerMail mail = new PlayerMail();
			mail.readNBT(list.getCompound(i));
			newmail.add(mail);
		}
		playermail = newmail;
	}

	public CompoundNBT saveNBTData(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		
		for(PlayerMail mail : playermail){
			list.add(mail.writeNBT());
		}
		
		compound.put("MailData", list);
		return compound;
	}

	public boolean hasMail() {
		for(PlayerMail mail : playermail)
			if(!mail.beenRead)
				return true;
		return false;
	}
}
