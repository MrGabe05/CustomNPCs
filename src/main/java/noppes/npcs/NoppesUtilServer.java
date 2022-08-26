package noppes.npcs;

import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.*;
import noppes.npcs.packets.server.SPacketGuiOpen;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

public class NoppesUtilServer {
	private static HashMap<UUID,Quest> editingQuests = new HashMap<UUID,Quest>();
	private static HashMap<UUID,Quest> editingQuestsClient = new HashMap<UUID,Quest>();

    public static void setEditingNpc(PlayerEntity player, EntityNPCInterface npc){
    	PlayerData data = PlayerData.get(player);
    	data.editingNpc = npc;
    	if(npc != null)
			Packets.send((ServerPlayerEntity)player, new PacketNpcEdit(npc.getId()));
    }
    public static EntityNPCInterface getEditingNpc(PlayerEntity player){
    	PlayerData data = PlayerData.get(player);
    	return data.editingNpc;
    }

	public static void setEditingQuest(PlayerEntity player, Quest quest) {
		if(player.level.isClientSide) {
			editingQuestsClient.put(player.getUUID(), quest);
		}
		else {
			editingQuests.put(player.getUUID(), quest);
		}
	}
    public static Quest getEditingQuest(PlayerEntity player){
		if(player.level.isClientSide) {
			return editingQuestsClient.get(player.getUUID());
    	}
		return editingQuests.get(player.getUUID());
    }
	
	public static void openDialog(PlayerEntity player, EntityNPCInterface npc, Dialog dia){
		Dialog dialog = dia.copy(player);
		PlayerData playerdata = PlayerData.get(player);

		if(EventHooks.onNPCDialog(npc, player, dialog)) {
			playerdata.dialogId = -1;
			return;
		}
		playerdata.dialogId = dialog.id;
		
		if(npc instanceof EntityDialogNpc || dia.id < 0){
			dialog.hideNPC = true;
			Packets.send((ServerPlayerEntity)player, new PacketDialogDummy(npc.getName().getString(), dialog.save(new CompoundNBT())));
		}
		else
			Packets.send((ServerPlayerEntity)player, new PacketDialog(npc.getId(), dialog.id));
		dia.factionOptions.addPoints(player);
        if(dialog.hasQuest())
        	PlayerQuestController.addActiveQuest(dialog.getQuest(),player);
        if(!dialog.command.isEmpty()){       
            runCommand(npc, npc.getName().getString(), dialog.command, player);
        }
        if(dialog.mail.isValid())
        	PlayerDataController.instance.addPlayerMessage(player.getServer(), player.getName().getString(), dialog.mail);
        
        PlayerDialogData data = playerdata.dialogData;
        if(!data.dialogsRead.contains(dialog.id) && dialog.id >= 0){
        	data.dialogsRead.add(dialog.id);
	        playerdata.updateClient = true;
        }
		setEditingNpc(player, npc);
		playerdata.questData.checkQuestCompletion(player, QuestType.DIALOG);
	}
	
	public static String runCommand(Entity executer, String name, String command, PlayerEntity player){
		return runCommand(executer.getCommandSenderWorld(), executer.blockPosition(), name, command, player, executer);
	}

	public static String runCommand(final World level, final BlockPos pos, final String name, String command, PlayerEntity player, final Entity executer){
		if(!level.getServer().isCommandBlockEnabled()){
			NotifyOPs("Cant run commands if CommandBlocks are disabled");
			LogWriter.warn("Cant run commands if CommandBlocks are disabled");
			return "Cant run commands if CommandBlocks are disabled";
		}
		
        if(player != null)
        	command = command.replace("@dp", player.getName().getString());
    	command = command.replace("@npc", name);

    	final StringTextComponent output = new StringTextComponent("");
		ICommandSource icommandsender = new RConConsoleSource(level.getServer()){

			@Override
			public void sendMessage(ITextComponent component, UUID senderUUID) {
				output.append(component);
			}

			@Override
			public boolean shouldInformAdmins() {
				return level.getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
			}

			@Override
			public boolean acceptsFailure() {
				return true;
			}
		};
		int permLvl = CustomNpcs.NpcUseOpCommands ? 4 : 2;
		Vector3d point = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5D, pos.getZ() + 0.5D);

		CommandSource commandSource = new CommandSource(icommandsender, point, Vector2f.ZERO, (ServerWorld) level, permLvl, "@CustomNPCs-" + name, new StringTextComponent("@CustomNPCs-" + name), level.getServer(), executer){


			@Override
			public void sendFailure(ITextComponent text) {
				super.sendFailure(text);
				NotifyOPs((TextComponent) text);
			}
		};


		Commands icommandmanager = level.getServer().getCommands();
        icommandmanager.performCommand(commandSource, command);
        
        if(output.getString().isEmpty())
        	return null;

        return output.getString();
	}
	
	public static void consumeItemStack(int i, PlayerEntity player){
		ItemStack item = player.inventory.getSelected();
		if(player.abilities.instabuild || item == null || item.isEmpty())
			return;
		
        item.shrink(1);
        if (item.getCount() <= 0){
        	player.setItemInHand(Hand.MAIN_HAND, null);
        }
	}
	
	public static DataOutputStream getDataOutputStream(ByteArrayOutputStream stream) throws IOException{
        return new DataOutputStream(new GZIPOutputStream(stream));
	}
	public static void sendOpenGui(PlayerEntity player,
			EnumGuiType gui, EntityNPCInterface npc) {
		SPacketGuiOpen.sendOpenGui(player, gui, npc, BlockPos.ZERO);
	}

	private static ContainerType getType(EnumGuiType gui){
		if(gui == EnumGuiType.PlayerAnvil){
			return CustomContainer.container_carpentrybench;
		}
		if(gui == EnumGuiType.CustomGui){
			return CustomContainer.container_customgui;
		}
		if(gui == EnumGuiType.PlayerBankUnlock){
			return CustomContainer.container_bankunlock;
		}
		if(gui == EnumGuiType.PlayerBankLarge){
			return CustomContainer.container_banklarge;
		}
		if(gui == EnumGuiType.PlayerBankUprade){
			return CustomContainer.container_bankupgrade;
		}
		if(gui == EnumGuiType.PlayerBankSmall){
			return CustomContainer.container_banksmall;
		}
		if(gui == EnumGuiType.PlayerMailman){
			return CustomContainer.container_mail;
		}
		if(gui == EnumGuiType.MainMenuInv){
			return CustomContainer.container_inv;
		}
		if(gui == EnumGuiType.QuestItem){
			return CustomContainer.container_questtypeitem;
		}
		if(gui == EnumGuiType.QuestReward){
			return CustomContainer.container_questreward;
		}
		if(gui == EnumGuiType.CompanionInv){
			return CustomContainer.container_companion;
		}
		if(gui == EnumGuiType.PlayerTrader){
			return CustomContainer.container_trader;
		}
		if(gui == EnumGuiType.PlayerFollower){
			return CustomContainer.container_follower;
		}
		if(gui == EnumGuiType.PlayerFollowerHire){
			return CustomContainer.container_followerhire;
		}
		if(gui == EnumGuiType.SetupTrader){
			return CustomContainer.container_tradersetup;
		}
		if(gui == EnumGuiType.SetupFollower){
			return CustomContainer.container_followersetup;
		}
		if(gui == EnumGuiType.SetupItemGiver){
			return CustomContainer.container_itemgiver;
		}
		if(gui == EnumGuiType.ManageBanks){
			return CustomContainer.container_managebanks;
		}

		return null;
	}

	public static void openContainerGui(ServerPlayerEntity player, EnumGuiType gui, Consumer<PacketBuffer> extraDataWriter){
		ContainerType type = getType(gui);
		PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
		extraDataWriter.accept(buffer);
		NetworkHooks.openGui(player, new INamedContainerProvider() {
			@Nullable
			@Override
			public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
				return type.create(p_createMenu_1_, p_createMenu_2_, buffer);
			}

			@Override
			public ITextComponent getDisplayName() {
				return new StringTextComponent(gui.name());
			}
		}, extraDataWriter);
	}

	public static void spawnParticle(Entity entity,String particle,int dimension){
		Packets.sendNearby(entity, new PacketParticle(entity.getX(), entity.getY(), entity.getZ(), entity.getBbHeight(), entity.getBbWidth(), particle));
    }

	public static void sendScrollData(ServerPlayerEntity player, Map<String,Integer> map){
		Packets.send(player, new PacketGuiScrollData(map));
	}

	public static void sendGuiError(PlayerEntity player, int i) {
		Packets.send((ServerPlayerEntity)player, new PacketGuiError(i, new CompoundNBT()));
	}

	public static void sendGuiClose(ServerPlayerEntity player, int i, CompoundNBT comp) {
		Packets.send(player, new PacketGuiClose(comp));
	}
	
	public static boolean isOp(PlayerEntity player) {
		return player.getServer().getPlayerList().isOp(player.getGameProfile());
	}

    public static void GivePlayerItem(Entity entity, PlayerEntity player, ItemStack item) {
        if (entity.level.isClientSide || item == null || item.isEmpty()) {
            return;
        }
        item = item.copy();
        float f = 0.7F;
        double d = (double) (entity.level.random.nextFloat() * f) + (double) (1.0F - f);
        double d1 = (double) (entity.level.random.nextFloat() * f) + (double) (1.0F - f);
        double d2 = (double) (entity.level.random.nextFloat() * f) + (double) (1.0F - f);
        ItemEntity entityitem = new ItemEntity(entity.level, entity.getX() + d, entity.getY() + d1, entity.getZ() + d2,
                item);
        entityitem.setPickUpDelay(2);
        entity.level.addFreshEntity(entityitem);

        if (player.inventory.add(item)) {
            entity.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.take(entityitem, item.getCount());
    		PlayerQuestData playerdata = PlayerData.get(player).questData;
    		playerdata.checkQuestCompletion(player, QuestType.ITEM);

            if (item.getCount() <= 0) {
                entityitem.remove();
            }
        }
    }

	
	public static BlockPos GetClosePos(BlockPos origin, World level){
		for(int x = -1; x < 2; x++){
			for(int z = -1; z < 2; z++){
				for(int y = 2; y >= -2; y--){
					BlockPos pos = origin.offset(x, y, z);
					BlockState state = level.getBlockState(pos.above());
					if(state.isRedstoneConductor(level, pos) && level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.above(2)))
						return pos.above();
				}
			}
		}
		return level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, origin);
	}

	public static void NotifyOPs(String message, Object... obs){
		NotifyOPs(new TranslationTextComponent(message, obs));
	}

	public static void NotifyOPs(TextComponent message){
		ITextComponent chatcomponenttranslation = message.withStyle(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC});

		Iterator iterator = CustomNpcs.Server.getPlayerList().getPlayers().iterator();

		while (iterator.hasNext()){
			PlayerEntity entityplayer = (PlayerEntity)iterator.next();

			if (entityplayer.shouldInformAdmins() && isOp(entityplayer)){
				entityplayer.sendMessage(chatcomponenttranslation, Util.NIL_UUID);
			}
		}

		if (CustomNpcs.Server.getLevel(World.OVERWORLD).getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)){
			LogWriter.info(chatcomponenttranslation.getString());
		}
	}

	public static void playSound(LivingEntity entity, SoundEvent sound, float volume, float pitch) {
		entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.NEUTRAL, volume, pitch);
	}

	public static void playSound(World level, BlockPos pos, SoundEvent sound, SoundCategory cat, float volume, float pitch) {
		level.playSound(null, pos, sound, cat, volume, pitch);
	}

	public static PlayerEntity getPlayer(MinecraftServer minecraftserver, UUID id) {
		List<ServerPlayerEntity> list = minecraftserver.getPlayerList().getPlayers();
		for(PlayerEntity player : list){
			if(id.equals(player.getUUID()))
				return player;
		}
        return null;
	}
	public static Entity GetDamageSourcee(DamageSource damagesource) {
		Entity entity = damagesource.getEntity();
		if(entity == null)
			entity = damagesource.getDirectEntity();
		if ((entity instanceof EntityProjectile) && ((EntityProjectile) entity).getOwner() instanceof LivingEntity)
            entity = ((AbstractArrowEntity) entity).getOwner();
		else if ((entity instanceof ThrowableEntity))
			entity = ((ThrowableEntity) entity).getOwner();
		return entity;
	}
	public static boolean IsItemStackNull(ItemStack is) {
		return is == null || is.isEmpty() || is == ItemStack.EMPTY || is.getItem() == null;
	}
	public static ItemStack ChangeItemStack(ItemStack is, Item item) {
		CompoundNBT comp = is.save(new CompoundNBT());
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(item);
        comp.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
		return ItemStack.of(comp);
	}
}
