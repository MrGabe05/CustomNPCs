package noppes.npcs.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;

public class MassBlockController {
	private static Queue<IMassBlock> queue;
	private static MassBlockController Instance;
	
	public MassBlockController(){
		 queue = new LinkedList<IMassBlock>();
		 Instance = this;
	}
	
	public static void Update(){		
		if(queue.isEmpty())
			return;
		IMassBlock imb = queue.remove();
		World level = imb.getNpc().level;
		BlockPos pos = imb.getNpc().blockPosition();
		int range = imb.getRange();
		List<BlockData> list = new ArrayList<BlockData>();
		for(int x = -range; x < range; x++){
			for(int z = -range; z < range; z++){
                if (!level.isLoaded(new BlockPos(x + pos.getX(), 64, z + pos.getZ())))
                	continue;
                for(int y = 0; y < range; y++){
                	BlockPos blockPos = pos.offset(x, y - range/2, z);
                	list.add(new BlockData(blockPos, level.getBlockState(blockPos), null));
                }
			}
			
		}
		imb.processed(list);
	}

	public static void Queue(IMassBlock imb) {
		queue.add(imb);
	}
	
	public interface IMassBlock{

		EntityNPCInterface getNpc();
		int getRange();
		void processed(List<BlockData> list);
	}
}
