package noppes.npcs;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import noppes.npcs.items.*;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid=CustomNpcs.MODID)
@ObjectHolder("customnpcs")
public class CustomItems {

	@ObjectHolder("npcwand")
	public static Item wand = null;
	@ObjectHolder("npcmobcloner")
	public static Item cloner = null;
	@ObjectHolder("npcscripter")
	public static Item scripter = null;
	@ObjectHolder("npcmovingpath")
	public static Item moving = null;
	@ObjectHolder("npcmounter")
	public static Item mount = null;
	@ObjectHolder("npcteleporter")
	public static Item teleporter = null;
	@ObjectHolder("scripted_item")
	public static ItemScripted scripted_item = null;
	@ObjectHolder("nbt_book")
	public static ItemNbtBook nbt_book = null;

	@ObjectHolder("npcsoulstoneempty")
	public static final Item soulstoneEmpty = null;
	@ObjectHolder("npcsoulstonefilled")
	public static final Item soulstoneFull = null;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new ItemNpcWand().setRegistryName(CustomNpcs.MODID, "npcwand"),
				new ItemNpcCloner().setRegistryName(CustomNpcs.MODID, "npcmobcloner"),
				new ItemNpcScripter().setRegistryName(CustomNpcs.MODID, "npcscripter"),
				new ItemNpcMovingPath().setRegistryName(CustomNpcs.MODID, "npcmovingpath"),
				new ItemMounter().setRegistryName(CustomNpcs.MODID, "npcmounter"),
				new ItemTeleporter().setRegistryName(CustomNpcs.MODID, "npcteleporter"),
				new ItemSoulstoneEmpty().setRegistryName(CustomNpcs.MODID, "npcsoulstoneempty"),
				new ItemSoulstoneFilled().setRegistryName(CustomNpcs.MODID, "npcsoulstonefilled"),
				new ItemScripted(CustomNpcs.proxy.getItemProperties().stacksTo(1).tab(CustomTabs.tab)).setRegistryName(CustomNpcs.MODID, "scripted_item"),
				new ItemNbtBook().setRegistryName(CustomNpcs.MODID, "nbt_book")
		);
	}

	public static void registerDispenser(){
		DispenserBlock.registerBehavior(soulstoneFull, new DefaultDispenseItemBehavior(){

			@Override
			public ItemStack execute(IBlockSource source, ItemStack item){
				Direction enumfacing = source.getBlockState().getValue(DispenserBlock.FACING);
				double x = source.x() + enumfacing.getStepX();
				double z = source.z() + enumfacing.getStepZ();
				ItemSoulstoneFilled.Spawn(null, item, source.getLevel(), new BlockPos(x, source.y(), z));
				item.split(1);
				return item;
			}
		});
	}

//	@OnlyIn(Dist.CLIENT)
//	@SubscribeEvent
//	public void registerModels(ModelRegistryEvent event) {
//        ModelLoader.setCustomStateMapper(mailbox, (new StateMap.Builder()).ignore(BlockMailbox.ROTATION, BlockMailbox.TYPE).build());
//        ModelLoader.setCustomStateMapper(scriptedDoor, (new StateMap.Builder()).ignore(DoorBlock.POWERED).build());
//        ModelLoader.setCustomStateMapper(builder, (new StateMap.Builder()).ignore(BlockBuilder.ROTATION).build());
//        ModelLoader.setCustomStateMapper(carpentyBench, (new StateMap.Builder()).ignore(BlockCarpentryBench.ROTATION).build());
//
//		ForgeHooksClient.registerTESRItemStack(Item.byBlock(carpentyBench), 0, TileBlockAnvil.class);
//		ForgeHooksClient.registerTESRItemStack(Item.byBlock(mailbox), 0, TileMailbox.class);
//		ForgeHooksClient.registerTESRItemStack(Item.byBlock(mailbox), 1, TileMailbox2.class);
//		ForgeHooksClient.registerTESRItemStack(Item.byBlock(mailbox), 2, TileMailbox3.class);
//
//	}
}
