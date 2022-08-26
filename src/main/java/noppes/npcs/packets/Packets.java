package noppes.npcs.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.CNpcsNetworkHelper;
import net.minecraft.network.IPacket;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.client.*;
import noppes.npcs.packets.server.*;
import noppes.npcs.util.CustomNPCsScheduler;

public class Packets {
	private static final String PROTOCOL = "CNPCS";
	
	public static SimpleChannel Channel;

	public static int index = 0;
	
	public static void register() {
		Channel = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(CustomNpcs.MODID, "packets"))
			.clientAcceptedVersions(PROTOCOL::equals)
			.serverAcceptedVersions(PROTOCOL::equals)
			.networkProtocolVersion(() -> PROTOCOL)
			.simpleChannel();

		index = 0;
		Channel.registerMessage(index++, PacketAchievement.class, PacketAchievement::encode, PacketAchievement::decode, PacketAchievement::handle);
		Channel.registerMessage(index++, PacketChat.class, PacketChat::encode, PacketChat::decode, PacketChat::handle);
		Channel.registerMessage(index++, PacketChatBubble.class, PacketChatBubble::encode, PacketChatBubble::decode, PacketChatBubble::handle);
		Channel.registerMessage(index++, PacketConfigFont.class, PacketConfigFont::encode, PacketConfigFont::decode, PacketConfigFont::handle);
		Channel.registerMessage(index++, PacketDialog.class, PacketDialog::encode, PacketDialog::decode, PacketDialog::handle);
		Channel.registerMessage(index++, PacketDialogDummy.class, PacketDialogDummy::encode, PacketDialogDummy::decode, PacketDialogDummy::handle);
		Channel.registerMessage(index++, PacketEyeBlink.class, PacketEyeBlink::encode, PacketEyeBlink::decode, PacketEyeBlink::handle);
		Channel.registerMessage(index++, PacketGuiCloneOpen.class, PacketGuiCloneOpen::encode, PacketGuiCloneOpen::decode, PacketGuiCloneOpen::handle);
		Channel.registerMessage(index++, PacketGuiClose.class, PacketGuiClose::encode, PacketGuiClose::decode, PacketGuiClose::handle);
		Channel.registerMessage(index++, PacketGuiData.class, PacketGuiData::encode, PacketGuiData::decode, PacketGuiData::handle);
		Channel.registerMessage(index++, PacketGuiError.class, PacketGuiError::encode, PacketGuiError::decode, PacketGuiError::handle);
		Channel.registerMessage(index++, PacketGuiOpen.class, PacketGuiOpen::encode, PacketGuiOpen::decode, PacketGuiOpen::handle);
		Channel.registerMessage(index++, PacketGuiScrollData.class, PacketGuiScrollData::encode, PacketGuiScrollData::decode, PacketGuiScrollData::handle);
		Channel.registerMessage(index++, PacketGuiScrollList.class, PacketGuiScrollList::encode, PacketGuiScrollList::decode, PacketGuiScrollList::handle);
		Channel.registerMessage(index++, PacketGuiScrollSelected.class, PacketGuiScrollSelected::encode, PacketGuiScrollSelected::decode, PacketGuiScrollSelected::handle);
		Channel.registerMessage(index++, PacketGuiUpdate.class, PacketGuiUpdate::encode, PacketGuiUpdate::decode, PacketGuiUpdate::handle);
		Channel.registerMessage(index++, PacketItemUpdate.class, PacketItemUpdate::encode, PacketItemUpdate::decode, PacketItemUpdate::handle);
		Channel.registerMessage(index++, PacketMarkData.class, PacketMarkData::encode, PacketMarkData::decode, PacketMarkData::handle);
		Channel.registerMessage(index++, PacketNpcDelete.class, PacketNpcDelete::encode, PacketNpcDelete::decode, PacketNpcDelete::handle);
		Channel.registerMessage(index++, PacketNpcEdit.class, PacketNpcEdit::encode, PacketNpcEdit::decode, PacketNpcEdit::handle);
		Channel.registerMessage(index++, PacketNpcRole.class, PacketNpcRole::encode, PacketNpcRole::decode, PacketNpcRole::handle);
		Channel.registerMessage(index++, PacketNpcUpdate.class, PacketNpcUpdate::encode, PacketNpcUpdate::decode, PacketNpcUpdate::handle);
		Channel.registerMessage(index++, PacketParticle.class, PacketParticle::encode, PacketParticle::decode, PacketParticle::handle);
		Channel.registerMessage(index++, PacketPlayMusic.class, PacketPlayMusic::encode, PacketPlayMusic::decode, PacketPlayMusic::handle);
		Channel.registerMessage(index++, PacketPlaySound.class, PacketPlaySound::encode, PacketPlaySound::decode, PacketPlaySound::handle);
		Channel.registerMessage(index++, PacketQuestCompletion.class, PacketQuestCompletion::encode, PacketQuestCompletion::decode, PacketQuestCompletion::handle);
		Channel.registerMessage(index++, PacketSync.class, PacketSync::encode, PacketSync::decode, PacketSync::handle);
		Channel.registerMessage(index++, PacketSyncRemove.class, PacketSyncRemove::encode, PacketSyncRemove::decode, PacketSyncRemove::handle);
		Channel.registerMessage(index++, PacketSyncUpdate.class, PacketSyncUpdate::encode, PacketSyncUpdate::decode, PacketSyncUpdate::handle);
		Channel.registerMessage(index++, PacketNpcVisibleFalse.class, PacketNpcVisibleFalse::encode, PacketNpcVisibleFalse::decode, PacketNpcVisibleFalse::handle);
		Channel.registerMessage(index++, PacketNpcVisibleTrue.class, PacketNpcVisibleTrue::encode, PacketNpcVisibleTrue::decode, PacketNpcVisibleTrue::handle);

		Channel.registerMessage(index++, SPacketBankGet.class, SPacketBankGet::encode, SPacketBankGet::decode, SPacketBankGet::handle);
		Channel.registerMessage(index++, SPacketBankRemove.class, SPacketBankRemove::encode, SPacketBankRemove::decode, SPacketBankRemove::handle);
		Channel.registerMessage(index++, SPacketBankSave.class, SPacketBankSave::encode, SPacketBankSave::decode, SPacketBankSave::handle);
		Channel.registerMessage(index++, SPacketBanksGet.class, SPacketBanksGet::encode, SPacketBanksGet::decode, SPacketBanksGet::handle);
		Channel.registerMessage(index++, SPacketBanksSlotOpen.class, SPacketBanksSlotOpen::encode, SPacketBanksSlotOpen::decode, SPacketBanksSlotOpen::handle);
		Channel.registerMessage(index++, SPacketBankUnlock.class, SPacketBankUnlock::encode, SPacketBankUnlock::decode, SPacketBankUnlock::handle);
		Channel.registerMessage(index++, SPacketBankUpgrade.class, SPacketBankUpgrade::encode, SPacketBankUpgrade::decode, SPacketBankUpgrade::handle);
		Channel.registerMessage(index++, SPacketCloneList.class, SPacketCloneList::encode, SPacketCloneList::decode, SPacketCloneList::handle);
		Channel.registerMessage(index++, SPacketCloneNameCheck.class, SPacketCloneNameCheck::encode, SPacketCloneNameCheck::decode, SPacketCloneNameCheck::handle);
		Channel.registerMessage(index++, SPacketCloneRemove.class, SPacketCloneRemove::encode, SPacketCloneRemove::decode, SPacketCloneRemove::handle);
		Channel.registerMessage(index++, SPacketCloneSave.class, SPacketCloneSave::encode, SPacketCloneSave::decode, SPacketCloneSave::handle);
		Channel.registerMessage(index++, SPacketCompanionOpenInv.class, SPacketCompanionOpenInv::encode, SPacketCompanionOpenInv::decode, SPacketCompanionOpenInv::handle);
		Channel.registerMessage(index++, SPacketCompanionTalentExp.class, SPacketCompanionTalentExp::encode, SPacketCompanionTalentExp::decode, SPacketCompanionTalentExp::handle);
		Channel.registerMessage(index++, SPacketDialogCategoryRemove.class, SPacketDialogCategoryRemove::encode, SPacketDialogCategoryRemove::decode, SPacketDialogCategoryRemove::handle);
		Channel.registerMessage(index++, SPacketDialogRemove.class, SPacketDialogRemove::encode, SPacketDialogRemove::decode, SPacketDialogRemove::handle);
		Channel.registerMessage(index++, SPacketDialogSelected.class, SPacketDialogSelected::encode, SPacketDialogSelected::decode, SPacketDialogSelected::handle);
		Channel.registerMessage(index++, SPacketDimensionsGet.class, SPacketDimensionsGet::encode, SPacketDimensionsGet::decode, SPacketDimensionsGet::handle);
		Channel.registerMessage(index++, SPacketDimensionTeleport.class, SPacketDimensionTeleport::encode, SPacketDimensionTeleport::decode, SPacketDimensionTeleport::handle);
		Channel.registerMessage(index++, SPacketFactionGet.class, SPacketFactionGet::encode, SPacketFactionGet::decode, SPacketFactionGet::handle);
		Channel.registerMessage(index++, SPacketFactionRemove.class, SPacketFactionRemove::encode, SPacketFactionRemove::decode, SPacketFactionRemove::handle);
		Channel.registerMessage(index++, SPacketFactionSave.class, SPacketFactionSave::encode, SPacketFactionSave::decode, SPacketFactionSave::handle);
		Channel.registerMessage(index++, SPacketFactionsGet.class, SPacketFactionsGet::encode, SPacketFactionsGet::decode, SPacketFactionsGet::handle);
		Channel.registerMessage(index++, SPacketFollowerExtend.class, SPacketFollowerExtend::encode, SPacketFollowerExtend::decode, SPacketFollowerExtend::handle);
		Channel.registerMessage(index++, SPacketFollowerHire.class, SPacketFollowerHire::encode, SPacketFollowerHire::decode, SPacketFollowerHire::handle);
		Channel.registerMessage(index++, SPacketFollowerState.class, SPacketFollowerState::encode, SPacketFollowerState::decode, SPacketFollowerState::handle);
		Channel.registerMessage(index++, SPacketGuiOpen.class, SPacketGuiOpen::encode, SPacketGuiOpen::decode, SPacketGuiOpen::handle);
		Channel.registerMessage(index++, SPacketLinkedAdd.class, SPacketLinkedAdd::encode, SPacketLinkedAdd::decode, SPacketLinkedAdd::handle);
		Channel.registerMessage(index++, SPacketLinkedGet.class, SPacketLinkedGet::encode, SPacketLinkedGet::decode, SPacketLinkedGet::handle);
		Channel.registerMessage(index++, SPacketLinkedRemove.class, SPacketLinkedRemove::encode, SPacketLinkedRemove::decode, SPacketLinkedRemove::handle);
		Channel.registerMessage(index++, SPacketLinkedSet.class, SPacketLinkedSet::encode, SPacketLinkedSet::decode, SPacketLinkedSet::handle);
		Channel.registerMessage(index++, SPacketMailSetup.class, SPacketMailSetup::encode, SPacketMailSetup::decode, SPacketMailSetup::handle);
		Channel.registerMessage(index++, SPacketMenuClose.class, SPacketMenuClose::encode, SPacketMenuClose::decode, SPacketMenuClose::handle);
		Channel.registerMessage(index++, SPacketMenuGet.class, SPacketMenuGet::encode, SPacketMenuGet::decode, SPacketMenuGet::handle);
		Channel.registerMessage(index++, SPacketMenuSave.class, SPacketMenuSave::encode, SPacketMenuSave::decode, SPacketMenuSave::handle);
		Channel.registerMessage(index++, SPacketNaturalSpawnGet.class, SPacketNaturalSpawnGet::encode, SPacketNaturalSpawnGet::decode, SPacketNaturalSpawnGet::handle);
		Channel.registerMessage(index++, SPacketNaturalSpawnGetAll.class, SPacketNaturalSpawnGetAll::encode, SPacketNaturalSpawnGetAll::decode, SPacketNaturalSpawnGetAll::handle);
		Channel.registerMessage(index++, SPacketNaturalSpawnRemove.class, SPacketNaturalSpawnRemove::encode, SPacketNaturalSpawnRemove::decode, SPacketNaturalSpawnRemove::handle);
		Channel.registerMessage(index++, SPacketNaturalSpawnSave.class, SPacketNaturalSpawnSave::encode, SPacketNaturalSpawnSave::decode, SPacketNaturalSpawnSave::handle);
		Channel.registerMessage(index++, SPacketNbtBookBlockSave.class, SPacketNbtBookBlockSave::encode, SPacketNbtBookBlockSave::decode, SPacketNbtBookBlockSave::handle);
		Channel.registerMessage(index++, SPacketNbtBookEntitySave.class, SPacketNbtBookEntitySave::encode, SPacketNbtBookEntitySave::decode, SPacketNbtBookEntitySave::handle);
		Channel.registerMessage(index++, SPacketNpcDelete.class, SPacketNpcDelete::encode, SPacketNpcDelete::decode, SPacketNpcDelete::handle);
		Channel.registerMessage(index++, SPacketNpcDialogRemove.class, SPacketNpcDialogRemove::encode, SPacketNpcDialogRemove::decode, SPacketNpcDialogRemove::handle);
		Channel.registerMessage(index++, SPacketNpcDialogSet.class, SPacketNpcDialogSet::encode, SPacketNpcDialogSet::decode, SPacketNpcDialogSet::handle);
		Channel.registerMessage(index++, SPacketNpcDialogsGet.class, SPacketNpcDialogsGet::encode, SPacketNpcDialogsGet::decode, SPacketNpcDialogsGet::handle);
		Channel.registerMessage(index++, SPacketNpcFactionSet.class, SPacketNpcFactionSet::encode, SPacketNpcFactionSet::decode, SPacketNpcFactionSet::handle);
		Channel.registerMessage(index++, SPacketNpcJobGet.class, SPacketNpcJobGet::encode, SPacketNpcJobGet::decode, SPacketNpcJobGet::handle);
		Channel.registerMessage(index++, SPacketNpcJobSave.class, SPacketNpcJobSave::encode, SPacketNpcJobSave::decode, SPacketNpcJobSave::handle);
		Channel.registerMessage(index++, SPacketNpcJobSpawnerSet.class, SPacketNpcJobSpawnerSet::encode, SPacketNpcJobSpawnerSet::decode, SPacketNpcJobSpawnerSet::handle);
		Channel.registerMessage(index++, SPacketNpcMarketSet.class, SPacketNpcMarketSet::encode, SPacketNpcMarketSet::decode, SPacketNpcMarketSet::handle);
		Channel.registerMessage(index++, SPacketNpcRoleCompanionUpdate.class, SPacketNpcRoleCompanionUpdate::encode, SPacketNpcRoleCompanionUpdate::decode, SPacketNpcRoleCompanionUpdate::handle);
		Channel.registerMessage(index++, SPacketNpcRoleGet.class, SPacketNpcRoleGet::encode, SPacketNpcRoleGet::decode, SPacketNpcRoleGet::handle);
		Channel.registerMessage(index++, SPacketNpcRoleSave.class, SPacketNpcRoleSave::encode, SPacketNpcRoleSave::decode, SPacketNpcRoleSave::handle);
		Channel.registerMessage(index++, SPacketNpcTransform.class, SPacketNpcTransform::encode, SPacketNpcTransform::decode, SPacketNpcTransform::handle);
		Channel.registerMessage(index++, SPacketNpcTransportGet.class, SPacketNpcTransportGet::encode, SPacketNpcTransportGet::decode, SPacketNpcTransportGet::handle);
		Channel.registerMessage(index++, SPacketPlayerCloseContainer.class, SPacketPlayerCloseContainer::encode, SPacketPlayerCloseContainer::decode, SPacketPlayerCloseContainer::handle);
		Channel.registerMessage(index++, SPacketPlayerDataGet.class, SPacketPlayerDataGet::encode, SPacketPlayerDataGet::decode, SPacketPlayerDataGet::handle);
		Channel.registerMessage(index++, SPacketPlayerDataRemove.class, SPacketPlayerDataRemove::encode, SPacketPlayerDataRemove::decode, SPacketPlayerDataRemove::handle);
		Channel.registerMessage(index++, SPacketPlayerKeyPressed.class, SPacketPlayerKeyPressed::encode, SPacketPlayerKeyPressed::decode, SPacketPlayerKeyPressed::handle);
		Channel.registerMessage(index++, SPacketPlayerLeftClicked.class, SPacketPlayerLeftClicked::encode, SPacketPlayerLeftClicked::decode, SPacketPlayerLeftClicked::handle);
		Channel.registerMessage(index++, SPacketPlayerMailDelete.class, SPacketPlayerMailDelete::encode, SPacketPlayerMailDelete::decode, SPacketPlayerMailDelete::handle);
		Channel.registerMessage(index++, SPacketPlayerMailGet.class, SPacketPlayerMailGet::encode, SPacketPlayerMailGet::decode, SPacketPlayerMailGet::handle);
		Channel.registerMessage(index++, SPacketPlayerMailOpen.class, SPacketPlayerMailOpen::encode, SPacketPlayerMailOpen::decode, SPacketPlayerMailOpen::handle);
		Channel.registerMessage(index++, SPacketPlayerMailRead.class, SPacketPlayerMailRead::encode, SPacketPlayerMailRead::decode, SPacketPlayerMailRead::handle);
		Channel.registerMessage(index++, SPacketPlayerMailSend.class, SPacketPlayerMailSend::encode, SPacketPlayerMailSend::decode, SPacketPlayerMailSend::handle);
		Channel.registerMessage(index++, SPacketPlayerTransport.class, SPacketPlayerTransport::encode, SPacketPlayerTransport::decode, SPacketPlayerTransport::handle);
		Channel.registerMessage(index++, SPacketQuestCategoryRemove.class, SPacketQuestCategoryRemove::encode, SPacketQuestCategoryRemove::decode, SPacketQuestCategoryRemove::handle);
		Channel.registerMessage(index++, SPacketQuestCompletionCheck.class, SPacketQuestCompletionCheck::encode, SPacketQuestCompletionCheck::decode, SPacketQuestCompletionCheck::handle);
		Channel.registerMessage(index++, SPacketQuestCompletionCheckAll.class, SPacketQuestCompletionCheckAll::encode, SPacketQuestCompletionCheckAll::decode, SPacketQuestCompletionCheckAll::handle);
		Channel.registerMessage(index++, SPacketQuestDialogTitles.class, SPacketQuestDialogTitles::encode, SPacketQuestDialogTitles::decode, SPacketQuestDialogTitles::handle);
		Channel.registerMessage(index++, SPacketQuestOpen.class, SPacketQuestOpen::encode, SPacketQuestOpen::decode, SPacketQuestOpen::handle);
		Channel.registerMessage(index++, SPacketQuestRemove.class, SPacketQuestRemove::encode, SPacketQuestRemove::decode, SPacketQuestRemove::handle);
		Channel.registerMessage(index++, SPacketRecipeGet.class, SPacketRecipeGet::encode, SPacketRecipeGet::decode, SPacketRecipeGet::handle);
		Channel.registerMessage(index++, SPacketRecipeRemove.class, SPacketRecipeRemove::encode, SPacketRecipeRemove::decode, SPacketRecipeRemove::handle);
		Channel.registerMessage(index++, SPacketRecipeSave.class, SPacketRecipeSave::encode, SPacketRecipeSave::decode, SPacketRecipeSave::handle);
		Channel.registerMessage(index++, SPacketRecipesGet.class, SPacketRecipesGet::encode, SPacketRecipesGet::decode, SPacketRecipesGet::handle);
		Channel.registerMessage(index++, SPacketRemoteFreeze.class, SPacketRemoteFreeze::encode, SPacketRemoteFreeze::decode, SPacketRemoteFreeze::handle);
		Channel.registerMessage(index++, SPacketRemoteMenuOpen.class, SPacketRemoteMenuOpen::encode, SPacketRemoteMenuOpen::decode, SPacketRemoteMenuOpen::handle);
		Channel.registerMessage(index++, SPacketRemoteNpcDelete.class, SPacketRemoteNpcDelete::encode, SPacketRemoteNpcDelete::decode, SPacketRemoteNpcDelete::handle);
		Channel.registerMessage(index++, SPacketRemoteNpcReset.class, SPacketRemoteNpcReset::encode, SPacketRemoteNpcReset::decode, SPacketRemoteNpcReset::handle);
		Channel.registerMessage(index++, SPacketRemoteNpcsGet.class, SPacketRemoteNpcsGet::encode, SPacketRemoteNpcsGet::decode, SPacketRemoteNpcsGet::handle);
		Channel.registerMessage(index++, SPacketRemoteNpcTp.class, SPacketRemoteNpcTp::encode, SPacketRemoteNpcTp::decode, SPacketRemoteNpcTp::handle);
		Channel.registerMessage(index++, SPacketSceneReset.class, SPacketSceneReset::encode, SPacketSceneReset::decode, SPacketSceneReset::handle);
		Channel.registerMessage(index++, SPacketSceneStart.class, SPacketSceneStart::encode, SPacketSceneStart::decode, SPacketSceneStart::handle);
		Channel.registerMessage(index++, SPacketSchematicsStore.class, SPacketSchematicsStore::encode, SPacketSchematicsStore::decode, SPacketSchematicsStore::handle);
		Channel.registerMessage(index++, SPacketSchematicsTileBuild.class, SPacketSchematicsTileBuild::encode, SPacketSchematicsTileBuild::decode, SPacketSchematicsTileBuild::handle);
		Channel.registerMessage(index++, SPacketSchematicsTileGet.class, SPacketSchematicsTileGet::encode, SPacketSchematicsTileGet::decode, SPacketSchematicsTileGet::handle);
		Channel.registerMessage(index++, SPacketSchematicsTileSave.class, SPacketSchematicsTileSave::encode, SPacketSchematicsTileSave::decode, SPacketSchematicsTileSave::handle);
		Channel.registerMessage(index++, SPacketSchematicsTileSet.class, SPacketSchematicsTileSet::encode, SPacketSchematicsTileSet::decode, SPacketSchematicsTileSet::handle);
		Channel.registerMessage(index++, SPacketScriptGet.class, SPacketScriptGet::encode, SPacketScriptGet::decode, SPacketScriptGet::handle);
		Channel.registerMessage(index++, SPacketTileEntityGet.class, SPacketTileEntityGet::encode, SPacketTileEntityGet::decode, SPacketTileEntityGet::handle);
		Channel.registerMessage(index++, SPacketTileEntitySave.class, SPacketTileEntitySave::encode, SPacketTileEntitySave::decode, SPacketTileEntitySave::handle);
		Channel.registerMessage(index++, SPacketToolMounter.class, SPacketToolMounter::encode, SPacketToolMounter::decode, SPacketToolMounter::handle);
		Channel.registerMessage(index++, SPacketTransportCategoriesGet.class, SPacketTransportCategoriesGet::encode, SPacketTransportCategoriesGet::decode, SPacketTransportCategoriesGet::handle);
		Channel.registerMessage(index++, SPacketTransportCategoryRemove.class, SPacketTransportCategoryRemove::encode, SPacketTransportCategoryRemove::decode, SPacketTransportCategoryRemove::handle);
		Channel.registerMessage(index++, SPacketTransportCategorySave.class, SPacketTransportCategorySave::encode, SPacketTransportCategorySave::decode, SPacketTransportCategorySave::handle);
		Channel.registerMessage(index++, SPacketTransportGet.class, SPacketTransportGet::encode, SPacketTransportGet::decode, SPacketTransportGet::handle);
		Channel.registerMessage(index++, SPacketTransportRemove.class, SPacketTransportRemove::encode, SPacketTransportRemove::decode, SPacketTransportRemove::handle);
		Channel.registerMessage(index++, SPacketTransportSave.class, SPacketTransportSave::encode, SPacketTransportSave::decode, SPacketTransportSave::handle);
		Channel.registerMessage(index++, SPacketCustomGuiButton.class, SPacketCustomGuiButton::encode, SPacketCustomGuiButton::decode, SPacketCustomGuiButton::handle);
		Channel.registerMessage(index++, SPacketCustomGuiTextUpdate.class, SPacketCustomGuiTextUpdate::encode, SPacketCustomGuiTextUpdate::decode, SPacketCustomGuiTextUpdate::handle);
		Channel.registerMessage(index++, SPacketCustomGuiScrollClick.class, SPacketCustomGuiScrollClick::encode, SPacketCustomGuiScrollClick::decode, SPacketCustomGuiScrollClick::handle);
		Channel.registerMessage(index++, SPacketNpRandomNameSet.class, SPacketNpRandomNameSet::encode, SPacketNpRandomNameSet::decode, SPacketNpRandomNameSet::handle);
		Channel.registerMessage(index++, SPacketPlayerSoundPlays.class, SPacketPlayerSoundPlays::encode, SPacketPlayerSoundPlays::decode, SPacketPlayerSoundPlays::handle);

		CNpcsNetworkHelper.addPacket(SPacketScriptSave.class, SPacketScriptSave::new);
		CNpcsNetworkHelper.addPacket(SPacketToolMobSpawner.class, SPacketToolMobSpawner::new);
		CNpcsNetworkHelper.addPacket(SPacketQuestSave.class, SPacketQuestSave::new);
		CNpcsNetworkHelper.addPacket(SPacketQuestCategorySave.class, SPacketQuestCategorySave::new);
		CNpcsNetworkHelper.addPacket(SPacketDialogSave.class, SPacketDialogSave::new);
		CNpcsNetworkHelper.addPacket(SPacketDialogCategorySave.class, SPacketDialogCategorySave::new);
	}

	public static <MSG> void send(ServerPlayerEntity player, MSG msg) {
		Channel.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}

	public static <MSG> void sendDelayed(ServerPlayerEntity player, MSG msg, int delay) {
		CustomNPCsScheduler.runTack(() -> Channel.send(PacketDistributor.PLAYER.with(() -> player), msg), delay);
	}

	public static <MSG> void sendNearby(World level, BlockPos pos, int range, MSG msg) {
		Channel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), range, level.dimension)), msg);
	}
	
	public static <MSG> void sendNearby(Entity entity, MSG msg) {
		Channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
	}
	
	public static <MSG> void sendAll(MSG msg) {
		Channel.send(PacketDistributor.ALL.noArg(), msg);
	}
	
	public static <MSG> void sendServer(MSG msg){
		if(msg instanceof IPacket){
			Minecraft.getInstance().getConnection().getConnection().send((IPacket)msg);
		}
		else{
			Channel.sendToServer(msg);
		}
	}

	public static void doExplosion(World level, Explosion explosion, float size){
		if(level.isClientSide)
			return;
		for(Object ob : level.players()){
			ServerPlayerEntity player = (ServerPlayerEntity) ob;
			Vector3d vec = explosion.getPosition();
			SExplosionPacket packet = new SExplosionPacket(vec.x, vec.y, vec.z, size, explosion.getToBlow(), explosion.getHitPlayers().get(player));
			player.connection.send(packet);
		}
	}
}
