package noppes.npcs;

import com.google.common.collect.Maps;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import nikedemos.markovnames.generators.*;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.wrapper.WrapperEntityData;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.command.CmdNoppes;
import noppes.npcs.command.CmdSchematics;
import noppes.npcs.config.ConfigLoader;
import noppes.npcs.config.ConfigProp;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.ScoreBoardMixin;
import noppes.npcs.packets.Packets;
import noppes.npcs.shared.common.util.LogWriter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Mod(CustomNpcs.MODID)
public class CustomNpcs {

    public static final String MODID = "customnpcs";
    public static final String VERSION = "1.16";

	@ConfigProp(info = "Whether scripting is enabled or not")
    public static boolean EnableScripting = true;
    
    @ConfigProp(info = "Arguments given to the Nashorn scripting library")
    public static String NashorArguments = "-strict";

	@ConfigProp(info = "Disable Chat Bubbles")
    public static boolean EnableChatBubbles = true;

    @ConfigProp(info = "Navigation search range for NPCs. Not recommended to increase if you have a slow pc or on a server")
    public static int NpcNavRange = 32;

    @ConfigProp(info = "Limit too how many npcs can be in one chunk for natural spawning")
    public static int NpcNaturalSpawningChunkLimit = 4;

    @ConfigProp(info = "Set to true if you want the dialog command option to be able to use op commands like tp etc")
    public static boolean NpcUseOpCommands = false;

    @ConfigProp(info = "If set to true only opped people can use the /noppes command")
    public static boolean NoppesCommandOpOnly = false;

    @ConfigProp
    public static boolean InventoryGuiEnabled = true;

    //@ConfigProp 1.13 might have broken this needs testing
    public static boolean FixUpdateFromPre_1_12 = false;
    
    @ConfigProp(info = "If you are running sponge and you want to disable the permissions set this to true")
    public static boolean DisablePermissions = false;

    @ConfigProp
    public static boolean SceneButtonsEnabled = true;

    @ConfigProp
    public static boolean EnableDefaultEyes = true;

    public static long ticks;

    public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    @ConfigProp(info = "Enables CustomNpcs startup update message")
    public static boolean EnableUpdateChecker = true;

    public static CustomNpcs instance;

    public static boolean FreezeNPCs = false;

    @ConfigProp(info = "Only ops can create and edit npcs")
    public static boolean OpsOnly = false;
    
    @ConfigProp(info = "Default interact line. Leave empty to not have one")
    public static String DefaultInteractLine = "Hello @p";

    @ConfigProp(info = "Number of chunk loading npcs that can be active at the same time")
    public static int ChuckLoaders = 20;

    public static File Dir;

    @ConfigProp(info = "Enables leaves decay")
    public static boolean LeavesDecayEnabled = true;
    
    @ConfigProp(info = "Enables Vine Growth")
    public static boolean VineGrowthEnabled = true;

    @ConfigProp(info = "Enables Ice Melting")
    public static boolean IceMeltsEnabled = true;
    
    @ConfigProp(info = "Normal players can use soulstone on animals")
	public static boolean SoulStoneAnimals = true;
    
    @ConfigProp(info = "Normal players can use soulstone on all npcs")
	public static boolean SoulStoneNPCs = false;

	@ConfigProp(info="Type 0 = Normal, Type 1 = Solid")
	public static int HeadWearType = 1;

	@ConfigProp(info="When set to Minecraft it will use minecrafts font, when Default it will use OpenSans. Can only use fonts installed on your PC")
	public static String FontType = "Default";

	@ConfigProp(info="Font size for custom fonts (doesn't work with minecrafts font)")
	public static int FontSize = 18;

	@ConfigProp
	public static boolean NpcSpeachTriggersChatEvent = false;
    
    public static ConfigLoader Config;
    
    public static boolean VerboseDebug = false;
    
    public static MinecraftServer Server;

    public CustomNpcs() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void postLoad(final FMLLoadCompleteEvent event) {
        proxy.postload();
        CustomItems.registerDispenser();
    }

    private void setup(final FMLCommonSetupEvent event) {
        File dir = new File(FMLPaths.CONFIGDIR.get().toFile(), "..");
        Dir = new File(dir, "customnpcs");
        Dir.mkdir();

        Config = new ConfigLoader(this.getClass(), new File(dir, "config"), "CustomNpcs");
        Config.loadConfig();

        if (NpcNavRange < 16) {
            NpcNavRange = 16;
        }

        CapabilityManager.INSTANCE.register(PlayerData.class, new Capability.IStorage(){
            @Override
            public INBT writeNBT(Capability capability, Object instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability capability, Object instance, Direction side, INBT nbt) {

            }}, PlayerData::new);

        CapabilityManager.INSTANCE.register(WrapperEntityData.class, new IStorage(){

            @Override
            public INBT writeNBT(Capability capability, Object instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability capability, Object instance, Direction side, INBT nbt) {

            }
        }, () -> null);

        CapabilityManager.INSTANCE.register(MarkData.class, new IStorage(){
            @Override
            public INBT writeNBT(Capability capability, Object instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability capability, Object instance, Direction side, INBT nbt) {

            }}, MarkData::new);

        CapabilityManager.INSTANCE.register(ItemStackWrapper.class, new IStorage<ItemStackWrapper>(){
            @Override
            public INBT writeNBT(Capability capability, ItemStackWrapper instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability capability, ItemStackWrapper instance, Direction side, INBT nbt) {

            }
        }, () -> null);

        Packets.register();
        MinecraftForge.EVENT_BUS.register(new ServerEventsHandler());
        MinecraftForge.EVENT_BUS.register(new ServerTickHandler());
        MinecraftForge.EVENT_BUS.register(new CustomEntities());
        MinecraftForge.EVENT_BUS.register(proxy);
        MinecraftForge.EVENT_BUS.register(this);

        NpcAPI.Instance().events().register(new AbilityEventHandler());

        //ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkController());
        proxy.load();

        PixelmonHelper.load();
        ScriptController controller = new ScriptController();
        if(EnableScripting && controller.languages.size() > 0) {
            MinecraftForge.EVENT_BUS.register(controller);
            MinecraftForge.EVENT_BUS.register(new ScriptPlayerEventHandler().registerForgeEvents());
            MinecraftForge.EVENT_BUS.register(new ScriptItemEventHandler());
        }
        setPrivateValue(RangedAttribute.class, (RangedAttribute) Attributes.MAX_HEALTH, Double.MAX_VALUE, 1);

        new RecipeController();
        new CustomNpcsPermissions();

        //ScreenManager.registerFactory();
        //ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> ClientProxy::openGui);
    }

    @SubscribeEvent
    public void setAboutToStart(FMLServerAboutToStartEvent event) {
    	Availability.scores.clear();
        Server = event.getServer();
        MarkovGenerator.load();
    	ChunkController.instance.clear();
        FactionController.instance.load();
        new PlayerDataController();
        new TransportController();
        new GlobalDataController();
        new SpawnController();
        new LinkedNpcController();
        new MassBlockController();
        VisibilityController.instance = new VisibilityController();
        ScriptController.Instance.loadCategories();
        ScriptController.Instance.loadStoredData();
        ScriptController.Instance.loadPlayerScripts();
        ScriptController.Instance.loadForgeScripts();
        ScriptController.HasStart = false;
        
        WrapperNpcAPI.clearCache();

        CmdSchematics.names.clear();
        CmdSchematics.names.addAll(SchematicController.Instance.list());
    }

    @SubscribeEvent
    public void started(FMLServerStartedEvent event) {
        //DatabaseController.init();
        RecipeController.instance.load();
        new BankController();
        DialogController.instance.load();
        QuestController.instance.load();
        ScriptController.HasStart = true;
        ServerCloneController.Instance = new ServerCloneController();
    }

    @SubscribeEvent
    public void stopped(FMLServerStoppedEvent event){
        ServerCloneController.Instance = null;
        Server = null;
    }

    @SubscribeEvent
    public void serverstart(FMLServerStartingEvent event) {
        EntityNPCInterface.ChatEventPlayer = new FakePlayer(event.getServer().getLevel(World.OVERWORLD), EntityNPCInterface.ChatEventProfile);
        EntityNPCInterface.CommandPlayer = new FakePlayer(event.getServer().getLevel(World.OVERWORLD), EntityNPCInterface.CommandProfile);
        EntityNPCInterface.GenericPlayer = new FakePlayer(event.getServer().getLevel(World.OVERWORLD), EntityNPCInterface.GenericProfile);


        for(ServerWorld level : Server.getAllLevels()) {
            ServerScoreboard board = level.getScoreboard();
            board.addDirtyListener(() ->
            {
                for(String objective : Availability.scores) {
                    ScoreObjective so = board.getObjective(objective);
                    if(so != null) {
                        for(ServerPlayerEntity player : Server.getPlayerList().getPlayers()) {
                            if(!board.hasPlayerScore(player.getScoreboardName(), so) && board.getObjectiveDisplaySlotCount(so) == 0) {
                                player.connection.send(new SScoreboardObjectivePacket(so, 0));
                            }
                            ScoreBoardMixin mixin = (ScoreBoardMixin) board;

                            Map<ScoreObjective, Score> map = mixin.getScores().computeIfAbsent(player.getScoreboardName(), (p_197898_0_) -> Maps.newHashMap());
                            Score sco = map.computeIfAbsent(so, (ob) ->  new Score(board, ob, player.getScoreboardName()));

                            player.connection.send(new SUpdateScorePacket(ServerScoreboard.Action.CHANGE, so.getName(), sco.getOwner(), sco.getScore()));
                        }
                    }
                }
            });
            board.addDirtyListener(() -> {
                List<ServerPlayerEntity> players = Server.getPlayerList().getPlayers();
                for(ServerPlayerEntity playerMP : players){
                    VisibilityController.instance.onUpdate(playerMP);
                }
            });
        }
    }

    @SubscribeEvent
    public void registerCommand(RegisterCommandsEvent e) {
        LiteralArgumentBuilder<CommandSource> command = Commands.literal("noppes");
        CmdNoppes.register(e.getDispatcher());
        e.getDispatcher().register(command);
    }

    public static File getWorldSaveDirectory() {
        return getWorldSaveDirectory(null);
    }

    public static File getWorldSaveDirectory(String s) {
    	try{
	        File dir = new File(".");
	        if (Server != null) {
	        	if(!Server.isDedicatedServer()) {
                    dir = new File(Minecraft.getInstance().gameDirectory, "saves");
                } else {
                    dir = Server.getWorldPath(new FolderName("customnpcs")).toFile();
                }
	        }
            if(s != null){
            	dir = new File(dir, s);
            }
            if (!dir.exists()) {
            	dir.mkdirs();
            }
            return dir;
	        
    	}
    	catch(Exception e){
    		LogWriter.error("Error getting worldsave", e);
    	}
        return null;
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex)
    {
        try
        {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch (IllegalAccessException e)
        {
            LogWriter.error("setPrivateValue error", e);
        }
    }
}
