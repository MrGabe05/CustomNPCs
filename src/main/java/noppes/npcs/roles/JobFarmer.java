package noppes.npcs.roles;

import net.minecraft.block.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobFarmer;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.MassBlockController.IMassBlock;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class JobFarmer extends JobInterface implements IMassBlock, IJobFarmer{
	
	public int chestMode = 1; //0:nothing, 1:bring to chest, 2:drop
	
	private List<BlockPos> trackedBlocks = new ArrayList<BlockPos>();
	
	private int ticks = 0;
	private int walkTicks = 0;
	private int blockTicks = 800;
	private boolean waitingForBlocks = false;
	
	private BlockPos ripe = null;
	private BlockPos chest = null;
	
	private ItemStack holding = ItemStack.EMPTY;

	public JobFarmer(EntityNPCInterface npc) {
		super(npc);
		overrideMainHand = true;
	}
	

	@Override
	public IItemStack getMainhand(){
		String name = npc.getJobData();
		ItemStack item = stringToItem(name);
		if(item.isEmpty())
			return npc.inventory.weapons.get(0);
			
		return NpcAPI.Instance().getIItemStack(item);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("JobChestMode", chestMode);
		if(!holding.isEmpty()){
			compound.put("JobHolding", holding.save(new CompoundNBT()));
		}
		return compound;
	}

	@Override
	public void load(CompoundNBT compound) {
		chestMode = compound.getInt("JobChestMode");
		
		holding = ItemStack.of(compound.getCompound("JobHolding"));
		
		blockTicks = 1100;
	}
	
	public void setHolding(ItemStack item){
		holding = item;
		npc.setJobData(itemToString(holding));	
			
	}

	@Override
	public boolean aiShouldExecute() {
		if(!holding.isEmpty()){
			if(chestMode == 0)
				setHolding(ItemStack.EMPTY);
			else if(chestMode == 1){
				if(chest == null){
					dropItem(holding);
					setHolding(ItemStack.EMPTY);
				}
				else
					chest();
			}
			else if(chestMode == 2){
				dropItem(holding);
				setHolding(ItemStack.EMPTY);
			}
			return false;
		}
		if(ripe != null){
			pluck();
			return false;
		}
		if(!waitingForBlocks && blockTicks++ > 1200){
			blockTicks = 0;
			waitingForBlocks = true;
			MassBlockController.Queue(this);
		}
		if(ticks++ < 100)
			return false;
		ticks = 0;
		return true;
	}
	
	private void dropItem(ItemStack item){
        ItemEntity entityitem = new ItemEntity(npc.level, npc.getX(), npc.getY(), npc.getZ(), item);
        entityitem.setDefaultPickUpDelay();
        npc.level.addFreshEntity(entityitem);
	}

	private void chest() {
		BlockPos pos = chest;
		npc.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1);
		npc.getLookControl().setLookAt(pos.getX(), pos.getY(), pos.getZ(), 10, npc.getMaxHeadXRot());
		if(npc.nearPosition(pos) || walkTicks++ > 400){
			if(walkTicks < 400){
				npc.swing(Hand.MAIN_HAND);
			}
			npc.getNavigation().stop();
			ticks = 100;
			walkTicks = 0;
			BlockState state = npc.level.getBlockState(pos);
			TileEntity tile = npc.level.getBlockEntity(pos);
			IInventory inventory = tile instanceof IInventory? (IInventory) tile : null;
			if(state.getBlock() instanceof ChestBlock){
				inventory = ChestBlock.getContainer((ChestBlock)state.getBlock(), state, npc.level, pos, true);
			}
			if(inventory != null){
				for(int i = 0; !holding.isEmpty() && i < inventory.getContainerSize(); i++){
					holding = mergeStack(inventory, i, holding);
				}
				for(int i = 0; !holding.isEmpty() && i < inventory.getContainerSize(); i++){
					ItemStack item = inventory.getItem(i);
					if(item.isEmpty()){
						inventory.setItem(i, holding);
						holding = ItemStack.EMPTY;
					}
				}
				if(!holding.isEmpty()){//chest is full so drop the item
					dropItem(holding);
					holding = ItemStack.EMPTY;
				}
			}
			else{
				chest = null;
			}
			setHolding(holding);
		}
	}
	
	private ItemStack mergeStack(IInventory inventory, int slot, ItemStack item){
		ItemStack item2 = inventory.getItem(slot);
		if(!NoppesUtilPlayer.compareItems(item, item2, false, false))
			return item;
		int size = item2.getMaxStackSize() - item2.getCount();
		if(size >= item.getCount()){
			item2.setCount(item2.getCount() + item.getCount());
			return ItemStack.EMPTY;
		}
		item2.setCount(item2.getMaxStackSize());
		item.setCount(item.getCount() - size);
		if(item.isEmpty())
			return ItemStack.EMPTY;
		return item;
	}

	private void pluck() {
		BlockPos pos = ripe;
		npc.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1);
		npc.getLookControl().setLookAt(pos.getX(), pos.getY(), pos.getZ(), 10, npc.getMaxHeadXRot());
		if(npc.nearPosition(pos) || walkTicks++ > 400){

			if(walkTicks > 400){
				pos = NoppesUtilServer.GetClosePos(pos, npc.level);
				npc.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			}
			ripe = null;
			npc.getNavigation().stop();
			ticks = 90;
			walkTicks = 0;
			npc.swing(Hand.MAIN_HAND);
			BlockState state = npc.level.getBlockState(pos);
			Block b = state.getBlock();
			if(b instanceof CropsBlock && ((CropsBlock)b).isMaxAge(state)){
				CropsBlock crop = (CropsBlock) b;
				Item item = NpcBlockHelper.getCrop(crop);

				LootContext.Builder builder = (new LootContext.Builder((ServerWorld) npc.level)).withRandom(npc.level.random).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(pos)).withParameter(LootParameters.TOOL, npc.getMainHandItem()).withParameter(LootParameters.BLOCK_STATE, state).withOptionalParameter(LootParameters.BLOCK_ENTITY, npc.level.getBlockEntity(pos));
				LootTable loottable = npc.getServer().getLootTables().get(b.getLootTable());
				List<ItemStack> l = loottable.getRandomItems(builder.create(LootParameterSets.BLOCK));

				npc.level.setBlock(pos, crop.getStateForAge(0), 2);

				if(l.isEmpty()){
					holding = ItemStack.EMPTY;
				}
				else if(l.size() == 1){
					holding = l.get(0);
				}
				else{
					List<ItemStack> fl = l.stream().filter(t -> t.getItem() != item).collect(Collectors.toList());
					if(fl.isEmpty()){
						fl = l;
					}
					holding = fl.get(npc.getRandom().nextInt(fl.size()));
				}

				holding.setCount(1);
			}
			if(b instanceof StemGrownBlock){
				b = npc.level.getBlockState(pos).getBlock();
				npc.level.removeBlock(pos, false);
				holding = new ItemStack(b);
			}
			setHolding(holding);
		}
	}

	@Override
	public boolean aiContinueExecute() {
		return false;
	}

	@Override
	public void aiUpdateTask() {
		Iterator<BlockPos> ite = trackedBlocks.iterator();
		while(ite.hasNext() && ripe == null){
			BlockPos pos = ite.next();
			BlockState state = npc.level.getBlockState(pos);
			Block b = state.getBlock();
			if((b instanceof CropsBlock && ((CropsBlock)b).isMaxAge(state) || b instanceof StemGrownBlock) && b.getLootTable() != LootTables.EMPTY){
				ripe = pos;
			}
			else{
				ite.remove();
			}
		}
		npc.ais.returnToStart = ripe == null;
		if(ripe != null){
			npc.getNavigation().stop();
			npc.getLookControl().setLookAt(ripe.getX(), ripe.getY(), ripe.getZ(), 10, npc.getMaxHeadXRot());
		}
	}

	@Override
	public boolean isPlucking(){
		return ripe != null || !holding.isEmpty();
	}

	@Override
	public EntityNPCInterface getNpc() {
		return npc;
	}

	@Override
	public int getRange() {
		return 16;
	}

	@Override
	public void processed(List<BlockData> list) {
		List<BlockPos> trackedBlocks = new ArrayList<BlockPos>();
		BlockPos chest = null;
		for(BlockData data : list){
			TileEntity tile = npc.level.getBlockEntity(data.pos);
			Block b = data.state.getBlock();
			if(tile instanceof LockableLootTileEntity){
				if(chest == null || npc.distanceToSqr(chest.getX(), chest.getY(), chest.getZ()) > npc.distanceToSqr(data.pos.getX(), data.pos.getY(), data.pos.getZ()))
					chest = data.pos;
				continue;
			}
			if(!(b instanceof CropsBlock) && !(b instanceof StemBlock))
				continue;
			if(!trackedBlocks.contains(data.pos))
				trackedBlocks.add(data.pos);
		}
		this.chest = chest;
		this.trackedBlocks = trackedBlocks;
		waitingForBlocks = false;
	}

	@Override
	public EnumSet<Goal.Flag> getFlags() {
		return EnumSet.of(Goal.Flag.MOVE);
	}

	@Override
	public int getType() {
		return JobType.FARMER;
	}
}
