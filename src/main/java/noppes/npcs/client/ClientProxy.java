package noppes.npcs.client;


import com.mojang.blaze3d.matrix.MatrixStack;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabFactions;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabQuests;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabVanilla;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.Particle;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import noppes.npcs.*;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.controllers.PresetController;
import noppes.npcs.client.fx.EntityEnderFX;
import noppes.npcs.client.gui.*;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.global.*;
import noppes.npcs.client.gui.mainmenu.*;
import noppes.npcs.client.gui.player.*;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionInv;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionStats;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionTalents;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeItem;
import noppes.npcs.client.gui.roles.*;
import noppes.npcs.client.gui.script.*;
import noppes.npcs.client.model.*;
import noppes.npcs.client.renderer.*;
import noppes.npcs.shared.client.util.TrueTypeFont;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.*;
import noppes.npcs.mixin.ArmorLayerMixin;
import noppes.npcs.shared.common.util.LogWriter;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class ClientProxy extends CommonProxy {
	public static PlayerData playerData = new PlayerData();
	
	public static KeyBinding QuestLog;
	
	public static KeyBinding Scene1;
	public static KeyBinding SceneReset;
	public static KeyBinding Scene2;
	public static KeyBinding Scene3;
	
	public static FontContainer Font;

	public static ModelData data;
	public static PlayerModel playerModel;
	public static ArmorLayerMixin armorLayer;

	public ClientProxy(){
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(CustomItemModels::registerModels);
	}

	@Override
	public void load() {		
		Font = new FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
		createFolders();

		CustomNpcResourceListener listener = new CustomNpcResourceListener();
		((IReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
		listener.onResourceManagerReload(Minecraft.getInstance().getResourceManager());

		SimpleReloadableResourceManager rmanager = (SimpleReloadableResourceManager)Minecraft.getInstance().getResourceManager();
		rmanager.add(new FolderPack(CustomNpcs.Dir));


		RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcPony, (EntityRendererManager manager) -> new RenderNPCPony(manager, new ModelPony()));
		RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcCrystal, (EntityRendererManager manager) -> new RenderNpcCrystal(manager, new ModelNpcCrystal()));
		RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcDragon, (EntityRendererManager manager) -> new RenderNpcDragon(manager, new ModelNpcDragon(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcSlime, (EntityRendererManager manager) -> new RenderNpcSlime(manager, new ModelNpcSlime(16), new ModelNpcSlime(0), 0.25F));
		RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityProjectile, (EntityRendererManager manager) -> new RenderProjectile(manager));
		RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityCustomNpc, (EntityRendererManager manager) -> new RenderCustomNpc(manager, new PlayerModel(0, false)));
		RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNPC64x32, (EntityRendererManager manager) -> new RenderCustomNpc(manager, new ModelPlayer64x32()));
		RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNPCGolem, (EntityRendererManager manager) -> new RenderNPCInterface(manager, new ModelNPCGolem(0), 0));
		RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcAlex, (EntityRendererManager manager) -> new RenderCustomNpc(manager, new PlayerModel(0, true)));
		RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcClassicPlayer, (EntityRendererManager manager) -> new RenderCustomNpc(manager, new ModelClassicPlayer(0)));

		ScreenManager.register(CustomContainer.container_carpentrybench, GuiNpcCarpentryBench::new);
		ScreenManager.register(CustomContainer.container_customgui, GuiCustom::new);
		ScreenManager.register(CustomContainer.container_mail, GuiMailmanWrite::new);
		ScreenManager.register(CustomContainer.container_managebanks, GuiNPCManageBanks::new);
		ScreenManager.register(CustomContainer.container_managerecipes, GuiNpcManageRecipes::new);
		ScreenManager.register(CustomContainer.container_merchantadd, GuiMerchantAdd::new);
		ScreenManager.register(CustomContainer.container_banklarge, GuiNPCBankChest::new);
		ScreenManager.register(CustomContainer.container_banksmall, GuiNPCBankChest::new);
		ScreenManager.register(CustomContainer.container_bankunlock, GuiNPCBankChest::new);
		ScreenManager.register(CustomContainer.container_bankupgrade, GuiNPCBankChest::new);
		ScreenManager.register(CustomContainer.container_companion, GuiNpcCompanionInv::new);
		ScreenManager.register(CustomContainer.container_follower, GuiNpcFollower::new);
		ScreenManager.register(CustomContainer.container_followerhire, GuiNpcFollowerHire::new);
		ScreenManager.register(CustomContainer.container_followersetup, GuiNpcFollowerSetup::new);
		ScreenManager.register(CustomContainer.container_inv, GuiNPCInv::new);
		ScreenManager.register(CustomContainer.container_itemgiver, GuiNpcItemGiver::new);
		ScreenManager.register(CustomContainer.container_questreward, GuiNpcQuestReward::new);
		ScreenManager.register(CustomContainer.container_questtypeitem, GuiNpcQuestTypeItem::new);
		ScreenManager.register(CustomContainer.container_trader, GuiNPCTrader::new);
		ScreenManager.register(CustomContainer.container_tradersetup, GuiNpcTraderSetup::new);

		new MusicController();
		MinecraftForge.EVENT_BUS.register(new ClientTickHandler());

		Minecraft mc = Minecraft.getInstance();
		
		QuestLog = new KeyBinding("Quest Log", 76, "key.categories.gameplay");

		if(CustomNpcs.SceneButtonsEnabled){
			Scene1 = new KeyBinding("Scene1 start/pause", 321, "key.categories.gameplay");
			Scene2 = new KeyBinding("Scene2 start/pause", 322, "key.categories.gameplay");
			Scene3 = new KeyBinding("Scene3 start/pause", 323, "key.categories.gameplay");
			SceneReset = new KeyBinding("Scene reset", 320, "key.categories.gameplay");

			ClientRegistry.registerKeyBinding(Scene1);
			ClientRegistry.registerKeyBinding(Scene2);
			ClientRegistry.registerKeyBinding(Scene3);
			ClientRegistry.registerKeyBinding(SceneReset);
		}

		ClientRegistry.registerKeyBinding(QuestLog);
		//mc.options.loadOptions();
		
		new PresetController(CustomNpcs.Dir);
		
		if(CustomNpcs.EnableUpdateChecker){
			VersionChecker checker = new VersionChecker();
			checker.start();
		}
		PixelmonHelper.loadClient();
	}

	@Override
	public PlayerData getPlayerData(PlayerEntity player) {
		if(player.getUUID() == Minecraft.getInstance().player.getUUID()) {
			if(playerData.player != player)
				playerData.player = player;
			return playerData;
		}
		return null;
	}
	
	@Override
	public void postload() {

        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        if(CustomNpcs.InventoryGuiEnabled){
			TabRegistry.registerEventListeners(MinecraftForge.EVENT_BUS);
	        
	        if (TabRegistry.getTabList().isEmpty()){
	        	TabRegistry.registerTab(new InventoryTabVanilla());
	        }
        	TabRegistry.registerTab(new InventoryTabFactions());
        	TabRegistry.registerTab(new InventoryTabQuests());
        }
		Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> 0x8B4513, CustomItems.mount, CustomItems.cloner, CustomItems.moving, CustomItems.scripter, CustomItems.wand, CustomItems.teleporter);

		Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
			if (stack.getItem() == CustomItems.scripted_item) {
				IItemStack item = NpcAPI.Instance().getIItemStack(stack);
				if(!item.isEmpty()){
					return ((IItemScripted) item).getColor();
				}
			}
			return -1;
		}, CustomItems.scripted_item);
	}

	private void createFolders() {
		File file = new File(CustomNpcs.Dir,"assets/customnpcs");
		if(!file.exists())
			file.mkdirs();
		
		File check = new File(file,"sounds");
		if(!check.exists())
			check.mkdir();

		File json = new File(file, "sounds.json");
		if(!json.exists()){
			try {
				json.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(json));
				writer.write("{\n\n}");
				writer.close();
			} catch (IOException e) {
			}
		}

		File meta = new File(CustomNpcs.Dir, "pack.mcmeta");
		if(!meta.exists()){
			try {
				meta.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(meta));
				writer.write("{\n" +
						"    \"pack\": {\n" +
						"        \"description\": \"customnpcs map resource pack\",\n" +
						"        \"pack_format\": 6\n" +
						"    }\n" +
						"}");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		check = new File(file,"textures");
		if(!check.exists())
			check.mkdir();
		
	}

	public static Screen getGui(EnumGuiType gui, EntityNPCInterface npc, PacketBuffer buf){
		try{
			if (gui == EnumGuiType.MainMenuDisplay) {
				if (npc != null)
					return new GuiNpcDisplay(npc);
				else
					Minecraft.getInstance().player.sendMessage(new StringTextComponent("Unable to find npc"), Util.NIL_UUID);
			}
			else if (gui == EnumGuiType.MainMenuStats)
				return new GuiNpcStats(npc);

			else if (gui == EnumGuiType.MainMenuAdvanced)
				return new GuiNpcAdvanced(npc);

			else if (gui == EnumGuiType.MovingPath)
				return new GuiNpcPather(npc);

			else if (gui == EnumGuiType.ManageFactions)
				return new GuiNPCManageFactions(npc);

			else if (gui == EnumGuiType.ManageLinked)
				return new GuiNPCManageLinkedNpc(npc);

			else if (gui == EnumGuiType.BuilderBlock)
				return new GuiBlockBuilder(buf.readBlockPos());

			else if (gui == EnumGuiType.ManageTransport)
				return new GuiNPCManageTransporters(npc);

			else if (gui == EnumGuiType.ManageDialogs)
				return new GuiNPCManageDialogs(npc);

			else if (gui == EnumGuiType.ManageQuests)
				return new GuiNPCManageQuest(npc);

			else if (gui == EnumGuiType.Companion)
				return new GuiNpcCompanionStats(npc);

			else if (gui == EnumGuiType.CompanionTalent)
				return new GuiNpcCompanionTalents(npc);

			else if (gui == EnumGuiType.MainMenuGlobal)
				return new GuiNPCGlobalMainMenu(npc);

			else if (gui == EnumGuiType.MainMenuAI)
				return new GuiNpcAI(npc);

			else if (gui == EnumGuiType.PlayerTransporter)
				return new GuiTransportSelection(npc);

			else if (gui == EnumGuiType.Script)
				return new GuiScript(npc);

			else if (gui == EnumGuiType.ScriptBlock)
				return new GuiScriptBlock(buf.readBlockPos());

			else if (gui == EnumGuiType.ScriptItem)
				return new GuiScriptItem(Minecraft.getInstance().player);

			else if (gui == EnumGuiType.ScriptDoor)
				return new GuiScriptDoor(buf.readBlockPos());

			else if (gui == EnumGuiType.ScriptPlayers)
				return new GuiScriptGlobal();

			else if (gui == EnumGuiType.SetupTransporter)
				return new GuiNpcTransporter(npc);

			else if (gui == EnumGuiType.SetupBank)
				return new GuiNpcBankSetup(npc);

			else if (gui == EnumGuiType.NpcRemote && Minecraft.getInstance().screen == null)
				return new GuiNpcRemoteEditor();

			else if (gui == EnumGuiType.PlayerMailbox)
				return new GuiMailbox();

			else if (gui == EnumGuiType.NpcDimensions)
				return new GuiNpcDimension();

			else if (gui == EnumGuiType.Border)
				return new GuiBorderBlock(buf.readBlockPos());

			else if (gui == EnumGuiType.RedstoneBlock)
				return new GuiNpcRedstoneBlock(buf.readBlockPos());

			else if (gui == EnumGuiType.MobSpawner)
				return new GuiNpcMobSpawner(buf.readBlockPos());

			else if (gui == EnumGuiType.CopyBlock)
				return new GuiBlockCopy(buf.readBlockPos());

			else if (gui == EnumGuiType.MobSpawnerMounter)
				return new GuiNpcMobSpawnerMounter();

			else if (gui == EnumGuiType.Waypoint)
				return new GuiNpcWaypoint(buf.readBlockPos());

			else if (gui == EnumGuiType.NbtBook)
				return new GuiNbtBook(buf.readBlockPos());
			return null;
		}
		finally{
			if(buf != null)
				buf.release();
		}
	}

	public void openGui(PlayerEntity player, EnumGuiType gui) {
		Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.player != player){
			return;
		}
		Screen screen = getGui(gui, null, null);
		if(screen != null){
			minecraft.setScreen(screen);
		}
	}

	@Override
	public void openGui(EntityNPCInterface npc, EnumGuiType gui) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.setScreen(getGui(gui, npc, null));
	}


	@Override
	public void openGui(PlayerEntity player, Object guiscreen) {
		Minecraft minecraft = Minecraft.getInstance();
		if(!player.level.isClientSide || !(guiscreen instanceof Screen))
			return;

		if (guiscreen != null) {
			minecraft.setScreen((Screen)guiscreen);
		}
	}

	@Override
	public void spawnParticle(LivingEntity player, String string, Object... ob) {
		if(string.equals("Block")){
			BlockPos pos = (BlockPos) ob[0];
			BlockState state = (BlockState) ob[1];
            Minecraft.getInstance().particleEngine.destroy(pos, state);
		}
		else if(string.equals("ModelData")){
			ModelData data = (ModelData) ob[0];
			ModelPartData particles = (ModelPartData) ob[1];
			EntityCustomNpc npc = (EntityCustomNpc) player;
			Minecraft minecraft =  Minecraft.getInstance();
			double height = npc.getMyRidingOffset() + data.getBodyY();
			Random rand = npc.getRandom();
			//if(particles.type == 0){
				for(int i = 0; i< 2; i++){
					EntityEnderFX fx = new EntityEnderFX(npc, (rand.nextDouble() - 0.5D) * (double)player.getBbWidth(), (rand.nextDouble() * (double)player.getBbHeight()) - height - 0.25D, (rand.nextDouble() - 0.5D) * (double)player.getBbWidth(), (rand.nextDouble() - 0.5D) * 2D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2D, particles);
					minecraft.particleEngine.add(fx);
				}
	    		
			//}
//			else if(particles.type == 1){
//	        	for(int i = 0; i < 2; i++){
//		            double x = player.posX + (rand.nextDouble() - 0.5D) * 0.9;
//		            double y = (player.posY + rand.nextDouble() * 1.9) - 0.25D - height;
//		            double z = player.posZ + (rand.nextDouble() - 0.5D) * 0.9;
//		
//		            
//		            double f = (rand.nextDouble() - 0.5D) * 2D;
//		            double f1 =  -rand.nextDouble();
//		            double f2 = (rand.nextDouble() - 0.5D) * 2D;
//		            
//		            minecraft.particles.addEffect(new EntityRainbowFX(player.level, x, y, z, f, f1, f2));
//	        	}
//			}
		}
	}

	public boolean hasClient() {
		return true;
	}
	
	public PlayerEntity getPlayer() {
		return Minecraft.getInstance().player;
	}

	public static void bind(ResourceLocation location) {
		try{
			if(location == null)
				return;
	        TextureManager manager = Minecraft.getInstance().getTextureManager();
			Texture ob = manager.getTexture(location);
	    	if(ob == null){
	    		ob = new SimpleTexture(location);
	    		manager.register(location, ob);
	    	}
        	RenderSystem.bindTexture(ob.getId());
		}
		catch(NullPointerException ex){
			
		}
	}

	@Override
	public void spawnParticle(BasicParticleType particle, double x, double y, double z,
			double motionX, double motionY, double motionZ, float scale) {
		Minecraft mc = Minecraft.getInstance();
        double xx = mc.getCameraEntity().getX() - x;
        double yy = mc.getCameraEntity().getY() - y;
        double zz = mc.getCameraEntity().getZ() - z;
		if(xx * xx + yy * yy + zz * zz > 256)
			return;
		
		Particle fx = mc.particleEngine.createParticle(particle, x, y, z, motionX, motionY, motionZ);
		if(fx == null)
			return;
        if (particle == ParticleTypes.FLAME){
			fx.scale(0.00001f);
        }
        else if (particle == ParticleTypes.SMOKE){
			fx.scale(0.00001f);
        }
	}

	@Override
	public Item.Properties getItemProperties() {
		Supplier<Callable<ItemStackTileEntityRenderer>> teisr = () -> () -> CustomTileEntityItemStackRenderer.i;
		return new Item.Properties().setISTER(teisr).tab(CustomTabs.tab);
	}

	public static class FontContainer {
		private TrueTypeFont textFont = null;
		public boolean useCustomFont = true;
		
		private FontContainer(){
			
		}
		
		public FontContainer(String fontType, int fontSize) {
	    	try {
				textFont = new TrueTypeFont(new Font(fontType, java.awt.Font.PLAIN, fontSize), 1f);
				useCustomFont = !fontType.equalsIgnoreCase("minecraft");
	    		if(!useCustomFont || fontType.isEmpty() || fontType.equalsIgnoreCase("default"))
	    			textFont = new TrueTypeFont(new ResourceLocation("customnpcs","opensans.ttf"), fontSize, 1f);
			} catch (Throwable e) {
				LogWriter.except(e);
				useCustomFont = false;
			}
		}

		public int height(String text){
			if(useCustomFont)
				return textFont.height(text);
			return Minecraft.getInstance().font.lineHeight;
		}
		
		public int width(String text){
			if(useCustomFont)
				return textFont.width(text);
			return Minecraft.getInstance().font.width(text);
		}

		public FontContainer copy() {
			FontContainer font = new FontContainer();
			font.textFont = textFont;
			font.useCustomFont = useCustomFont;
			return font;
		}

		public void draw(MatrixStack matrixStack, String text, int x, int y, int color) {
			if(useCustomFont){
				textFont.draw(text, x, y, color);
			}
			else{
				Minecraft.getInstance().font.drawShadow(matrixStack, text, x, y, color);
			}
		}

		public String getName() {
			if(!useCustomFont)
				return "Minecraft";
			return textFont.getFontName();
		}

		public void clear() {
			if(textFont != null)
				textFont.dispose();
		}
	}
}
