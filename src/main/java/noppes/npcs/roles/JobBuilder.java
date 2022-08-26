package noppes.npcs.roles;

import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobBuilder;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Stack;

public class JobBuilder extends JobInterface implements IJobBuilder{
	public TileBuilder build = null;
	private BlockPos possibleBuildPos = null;
	private Stack<BlockData> placingList = null;
	private BlockData placing = null;

	private int tryTicks = 0;
	private int ticks = 0;
	
	public JobBuilder(EntityNPCInterface npc) {
		super(npc);
		overrideMainHand = true;
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if(build != null){
			compound.putInt("BuildX", build.getBlockPos().getX());
			compound.putInt("BuildY", build.getBlockPos().getY());
			compound.putInt("BuildZ", build.getBlockPos().getZ());
			if(placingList != null && !placingList.isEmpty()){
				ListNBT list = new ListNBT();
				for(BlockData data : placingList){
					list.add(data.getNBT());
				}
				if(placing != null)
					list.add(placing.getNBT());
				compound.put("Placing", list);
			}
		}
		return compound;
	}

	@Override
	public void load(CompoundNBT compound) {
		if(compound.contains("BuildX")){
			possibleBuildPos = new BlockPos(compound.getInt("BuildX"), compound.getInt("BuildY"), compound.getInt("BuildZ"));
		}
		if(possibleBuildPos != null && compound.contains("Placing")){
			Stack<BlockData> placing = new Stack<BlockData>();
			ListNBT list = compound.getList("Placing", 10);
			for(int i = 0; i < list.size(); i++){
				BlockData data = BlockData.getData(list.getCompound(i));
				if(data != null)
					placing.add(data);
			}
			this.placingList = placing;
		}
		npc.ais.doorInteract = 1;
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
	public boolean aiShouldExecute() {
		if(possibleBuildPos != null){
			TileEntity tile = npc.level.getBlockEntity(possibleBuildPos);
			if(tile instanceof TileBuilder){
				build = (TileBuilder) tile;
			}
			else
				placingList.clear();
			possibleBuildPos = null;
		}
		return build != null;
	}

	@Override
	public void aiUpdateTask() {
		if(build.finished && placingList == null || !build.enabled || build.isRemoved()){
			build = null;
			npc.getNavigation().moveTo(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos(), 1);
			return;
		}
		if(ticks++ < 10)
			return;
		ticks = 0;
		if((placingList == null || placingList.isEmpty()) && placing == null){
			placingList = build.getBlock();
            npc.setJobData("");
            return;
		}
		if(placing == null){
			placing = placingList.pop();
			if(placing.state.getBlock() == Blocks.STRUCTURE_VOID){
				placing = null;
				return;
			}
			tryTicks = 0;
			npc.setJobData(blockToString(placing));
			
		}
		npc.getNavigation().moveTo(placing.pos.getX(), placing.pos.getY() + 1, placing.pos.getZ(), 1);
		if(tryTicks++ > 40 || npc.nearPosition(placing.pos)){
			BlockPos blockPos = placing.pos;
			placeBlock();
			if(tryTicks > 40){
				blockPos = NoppesUtilServer.GetClosePos(blockPos, npc.level);
				npc.teleportTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
			}
		}
	}
	
	private String blockToString(BlockData data){
		if(data.state.getBlock() == Blocks.AIR)
            return Items.IRON_PICKAXE.getRegistryName().toString();
		return itemToString(data.getStack());
	}

	@Override
	public void stop() {
		reset();
	}

	@Override
	public void reset(){
		build = null;
		npc.setJobData("");
	}
	
	public void placeBlock(){
		if(placing == null)
			return;
		npc.getNavigation().stop();
		npc.swing(Hand.MAIN_HAND);
		npc.level.setBlock(placing.pos, placing.state, 2);
    	if(placing.state.getBlock() instanceof ITileEntityProvider && placing.tile != null){
    		TileEntity tile = npc.level.getBlockEntity(placing.pos);
    		if(tile != null){
    			try{
    				tile.load(placing.state, placing.tile);
    			}
    			catch(Exception e){
    				
    			}
    		}
    	}
    	placing = null;
	}

	@Override
	public boolean isBuilding() {
		return build != null && build.enabled && !build.finished && build.started;
	}

	@Override
	public int getType() {
		return JobType.BUILDER;
	}
}
