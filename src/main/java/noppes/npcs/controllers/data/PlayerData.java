package noppes.npcs.controllers.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.NBTJsonUtil;

import java.io.File;
import java.util.UUID;

public class PlayerData implements ICapabilityProvider {
	
	@CapabilityInject(PlayerData.class)
	public static Capability<PlayerData> PLAYERDATA_CAPABILITY = null;
    public BlockPos scriptBlockPos = BlockPos.ZERO;

    private final LazyOptional<PlayerData> instance = LazyOptional.of(() -> this);
	
	public PlayerDialogData dialogData = new PlayerDialogData();
	public PlayerBankData bankData = new PlayerBankData();
	public PlayerQuestData questData = new PlayerQuestData();
	public PlayerTransportData transportData = new PlayerTransportData();
	public PlayerFactionData factionData = new PlayerFactionData();
	public PlayerItemGiverData itemgiverData = new PlayerItemGiverData();
	public PlayerMailData mailData = new PlayerMailData();
	public PlayerScriptData scriptData;

	public CompoundNBT scriptStoreddata = new CompoundNBT();

	public DataTimers timers = new DataTimers(this);
	
	public EntityNPCInterface editingNpc;
	public CompoundNBT cloned;
	
	public PlayerEntity player;

	public String playername = "";
	public String uuid = "";
	
	private EntityNPCInterface activeCompanion = null;
	public int companionID = 0;
	
	public int playerLevel = 0;
	
	public boolean updateClient = false;

	public int dialogId = -1;
	public ItemStack prevHeldItem = ItemStack.EMPTY;

	public Entity mounted;

	public UUID iAmStealingYourDatas = UUID.randomUUID();

	public void setNBT(CompoundNBT data){
		dialogData.loadNBTData(data);
		bankData.loadNBTData(data);
		questData.loadNBTData(data);
		transportData.loadNBTData(data);
		factionData.loadNBTData(data);
		itemgiverData.loadNBTData(data);
		mailData.loadNBTData(data);	
		timers.load(data);

		if(player != null){
			playername = player.getName().getString();
			uuid = player.getUUID().toString();
		}
		else{
			playername = data.getString("PlayerName");
			uuid = data.getString("UUID");
		}
		companionID = data.getInt("PlayerCompanionId");
		
		if(data.contains("PlayerCompanion") && !hasCompanion() && player != null){
			EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, player.level);
			npc.readAdditionalSaveData(data.getCompound("PlayerCompanion"));
			npc.setPos(player.getX(), player.getY(), player.getZ());
			if(npc.role.getType() == RoleType.COMPANION){
				((RoleCompanion)npc.role).setSitting(false);
				npc.forcedLoading = true;
				player.level.addFreshEntity(npc);
				setCompanion(npc);
			}
		}
		scriptStoreddata = data.getCompound("ScriptStoreddata");
	}
	
	public CompoundNBT getSyncNBT(){
		CompoundNBT compound = new CompoundNBT();
		dialogData.saveNBTData(compound);
		questData.saveNBTData(compound);
		factionData.saveNBTData(compound);
		
		return compound;
	}
	
	public CompoundNBT getNBT() {
		if(player != null){
			playername = player.getName().getString();
			uuid = player.getUUID().toString();
		}
		CompoundNBT compound = new CompoundNBT();
		dialogData.saveNBTData(compound);
		bankData.saveNBTData(compound);
		questData.saveNBTData(compound);
		transportData.saveNBTData(compound);
		factionData.saveNBTData(compound);
		itemgiverData.saveNBTData(compound);
		mailData.saveNBTData(compound);
		timers.save(compound);
		
		compound.putString("PlayerName", playername);
		compound.putString("UUID", uuid);
		compound.putInt("PlayerCompanionId", companionID);
		compound.put("ScriptStoreddata", scriptStoreddata);
		
		if(hasCompanion()){
			CompoundNBT nbt = new CompoundNBT();
			if(activeCompanion.saveAsPassenger(nbt))
				compound.put("PlayerCompanion", nbt);
		}
		return compound;
	}
	
	public boolean hasCompanion(){
		return activeCompanion != null && !activeCompanion.removed;
	}

	public void setCompanion(EntityNPCInterface npc) {
		if(npc != null && npc.role.getType() != RoleType.COMPANION)//shouldnt happen
			return;
		companionID++;
		activeCompanion = npc;
		if(npc != null)
			((RoleCompanion)npc.role).companionID = companionID;
		save(false);
	}


	public void updateCompanion(World level) {
		if(!hasCompanion() || level == activeCompanion.level)
			return;
		RoleCompanion role = (RoleCompanion) activeCompanion.role;
		role.owner = player;
		if(!role.isFollowing())
			return;
		CompoundNBT nbt = new CompoundNBT();
		activeCompanion.saveAsPassenger(nbt);
		activeCompanion.removed = true;

		EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
		npc.readAdditionalSaveData(nbt);
		npc.setPos(player.getX(), player.getY(), player.getZ());
		setCompanion(npc);
		((RoleCompanion)npc.role).setSitting(false);
		npc.forcedLoading = true;
		level.addFreshEntity(npc);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability == PLAYERDATA_CAPABILITY)
			return instance.cast();
		return LazyOptional.empty();
	}

	private static final ResourceLocation key = new ResourceLocation("customnpcs", "playerdata");
	public static void register(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof PlayerEntity) {
			event.addCapability(key, new PlayerData());
		}
	}

	public synchronized void save(boolean update) {
		final CompoundNBT compound = getNBT();
		final String filename = uuid + ".json";

        CustomNPCsScheduler.runTack(() -> {
            try {
                File saveDir = CustomNpcs.getWorldSaveDirectory("playerdata");
                File file = new File(saveDir, filename + "_new");
                File file1 = new File(saveDir, filename);
                NBTJsonUtil.SaveFile(file, compound);
                if (file1.exists()) {
                    file1.delete();
                }
                file.renameTo(file1);
            } catch (Exception e) {
                LogWriter.except(e);
            }
        });

        if(update)
			updateClient = true;
	}

	public static CompoundNBT loadPlayerData(String player){
		File saveDir = CustomNpcs.getWorldSaveDirectory("playerdata");
		String filename = player;
		if(filename.isEmpty())
			filename = "noplayername";
		filename += ".json";
		File file = null;
		try {
	        file = new File(saveDir, filename);
	        if(file.exists()){
		        return NBTJsonUtil.LoadFile(file);
	        }
		} catch (Exception e) {
			LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
		}

		return new CompoundNBT();
	}

	private static final PlayerData backup = new PlayerData(); //sometimes getCapability gives null no clue why
	public static PlayerData get(PlayerEntity player) {
		if(player.level.isClientSide)
			return CustomNpcs.proxy.getPlayerData(player);
		PlayerData data = player.getCapability(PLAYERDATA_CAPABILITY, null).orElse(backup);
		if(data.player == null){
			data.player = player;
			data.playerLevel = player.experienceLevel;
			data.scriptData = new PlayerScriptData(player);

			CompoundNBT compound = loadPlayerData(player.getUUID().toString());
			data.setNBT(compound);
		}
		return data;
	}
}
