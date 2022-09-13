package noppes.npcs;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ObjectHolder;
import noppes.npcs.containers.*;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid= CustomNpcs.MODID)
@ObjectHolder(CustomNpcs.MODID)
public class CustomContainer {

    @ObjectHolder("container_carpentrybench")
    public static ContainerType<ContainerCarpentryBench> container_carpentrybench;

    @ObjectHolder("container_customgui")
    public static ContainerType<ContainerCustomGui> container_customgui;

    @ObjectHolder("container_mail")
    public static ContainerType<ContainerMail> container_mail;

    @ObjectHolder("container_managebanks")
    public static ContainerType<ContainerManageBanks> container_managebanks;

    @ObjectHolder("container_managerecipes")
    public static ContainerType<ContainerManageRecipes> container_managerecipes;

    @ObjectHolder("container_merchantadd")
    public static ContainerType<ContainerManageRecipes> container_merchantadd;

    @ObjectHolder("container_banklarge")
    public static ContainerType<ContainerNPCBankInterface> container_banklarge;

    @ObjectHolder("container_banksmall")
    public static ContainerType<ContainerNPCBankInterface> container_banksmall;

    @ObjectHolder("container_bankunlock")
    public static ContainerType<ContainerNPCBankInterface> container_bankunlock;

    @ObjectHolder("container_bankupgrade")
    public static ContainerType<ContainerNPCBankInterface> container_bankupgrade;

    @ObjectHolder("container_companion")
    public static ContainerType<ContainerNPCCompanion> container_companion;

    @ObjectHolder("container_follower")
    public static ContainerType<ContainerNPCFollower> container_follower;

    @ObjectHolder("container_followerhire")
    public static ContainerType<ContainerNPCFollowerHire> container_followerhire;

    @ObjectHolder("container_followersetup")
    public static ContainerType<ContainerNPCFollowerSetup> container_followersetup;

    @ObjectHolder("container_inv")
    public static ContainerType<ContainerNPCInv> container_inv;

    @ObjectHolder("container_itemgiver")
    public static ContainerType<ContainerNpcItemGiver> container_itemgiver;

    @ObjectHolder("container_questreward")
    public static ContainerType<ContainerNpcQuestReward> container_questreward;

    @ObjectHolder("container_questtypeitem")
    public static ContainerType<ContainerNpcQuestTypeItem> container_questtypeitem;

    @ObjectHolder("container_trader")
    public static ContainerType<ContainerNPCTrader> container_trader;

    @ObjectHolder("container_tradersetup")
    public static ContainerType<ContainerNPCTraderSetup> container_tradersetup;

    @SubscribeEvent
    public static void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
                createContainer("container_carpentrybench", (IContainerFactory) (containerId, inv, data) -> new ContainerCarpentryBench(containerId, inv, data.readBlockPos())),
                createContainer("container_customgui", (IContainerFactory) (containerId, inv, data) -> new ContainerCustomGui(containerId, inv, data.readInt())),
                createContainer("container_mail", (IContainerFactory) (containerId, inv, data) -> new ContainerMail(containerId, inv, data.readBoolean(), data.readBoolean())),
                createContainer("container_managebanks", (IContainerFactory) (containerId, inv, data) -> new ContainerManageBanks(containerId, inv)),
                createContainer("container_managerecipes", (IContainerFactory) (containerId, inv, data) -> new ContainerManageRecipes(containerId, inv, data.readInt())),
                createContainer("container_merchantadd", (IContainerFactory) (containerId, inv, data) -> new ContainerMerchantAdd(containerId, inv)),
                createContainer("container_banklarge", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCBankLarge(containerId, inv, data.readInt(), data.readInt())),
                createContainer("container_banksmall", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCBankSmall(containerId, inv, data.readInt(), data.readInt())),
                createContainer("container_bankunlock", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCBankUnlock(containerId, inv, data.readInt(), data.readInt())),
                createContainer("container_bankupgrade", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCBankUpgrade(containerId, inv, data.readInt(), data.readInt())),
                createContainer("container_companion", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCCompanion(containerId, inv, data.readInt())),
                createContainer("container_follower", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCFollower(containerId, inv, data.readInt())),
                createContainer("container_followerhire", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCFollowerHire(containerId, inv, data.readInt())),
                createContainer("container_followersetup", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCFollowerSetup(containerId, inv, data.readInt())),
                createContainer("container_inv", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCInv(containerId, inv, data.readInt())),
                createContainer("container_itemgiver", (IContainerFactory) (containerId, inv, data) -> new ContainerNpcItemGiver(containerId, inv, data.readInt())),
                createContainer("container_questreward", (IContainerFactory) (containerId, inv, data) -> new ContainerNpcQuestReward(containerId, inv)),
                createContainer("container_questtypeitem", (IContainerFactory) (containerId, inv, data) -> new ContainerNpcQuestTypeItem(containerId, inv)),
                createContainer("container_trader", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCTrader(containerId, inv, data.readInt())),
                createContainer("container_tradersetup", (IContainerFactory) (containerId, inv, data) -> new ContainerNPCTraderSetup(containerId, inv, data.readInt()))
        );
    }

    private static ContainerType createContainer(String key, ContainerType.IFactory factoryIn){
        ContainerType type = new ContainerType(factoryIn);
        type.setRegistryName(CustomNpcs.MODID, key);
        return type;
    }
}
