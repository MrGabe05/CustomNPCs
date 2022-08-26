package noppes.npcs.constants;

import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;

import java.util.Locale;

public enum EnumGuiType {
	MainMenuDisplay, MainMenuInv(true), MainMenuStats, ManageFactions, MainMenuAdvanced, MainMenuGlobal, MainMenuAI,
	ManageTransport, ManageBanks(true), ManageDialogs, ManageQuests, ManageRecipes(true), ManageLinked,
	PlayerFollowerHire(true), PlayerFollower(true),
	PlayerBankSmall(true), PlayerBankUnlock(true), PlayerBankUprade(true), PlayerBankLarge(true),
	PlayerMailbox, PlayerMailman(true), PlayerTrader(true), PlayerAnvil(true),
	SetupItemGiver(true), SetupTrader(true), SetupFollower(true),
	PlayerTransporter, RedstoneBlock, SetupTransporter,
	MobSpawner, SetupBank(true), QuestReward(true), QuestItem(true),
	NpcRemote, MovingPath,
	MobSpawnerAdd, Waypoint, MerchantAdd(true), MobSpawnerMounter, NpcDimensions,
	Border, Script, ScriptBlock, ScriptDoor, Companion, CompanionInv(true), CompanionTalent, CompanionTrader,
	BuilderBlock, CopyBlock, ScriptPlayers, ScriptItem, NbtBook, CustomGui(true);

	public boolean hasContainer = false;
	public final ResourceLocation resource;

	EnumGuiType(){
		resource = new ResourceLocation(CustomNpcs.MODID, "gui" + this.ordinal());
	}

	EnumGuiType(boolean hasContainer){
		this();
		this.hasContainer = hasContainer;
	}

	public static EnumGuiType getEnum(ResourceLocation location){
		for(EnumGuiType type : values()){
			if(type.resource.equals(location)){
				return type;
			}
		}
		return null;
	}


}
