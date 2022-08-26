package noppes.npcs.controllers.data;

import java.util.Set;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.NBTTags;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.roles.RoleInterface;

public class DataTransform {
	public CompoundNBT display;
	public CompoundNBT ai;
	public CompoundNBT advanced;
	public CompoundNBT inv;
	public CompoundNBT stats;
	public CompoundNBT role;
	public CompoundNBT job;
	
	public boolean hasDisplay, hasAi, hasAdvanced, hasInv, hasStats, hasRole, hasJob, isActive;
	
	private EntityNPCInterface npc;
	
	public boolean editingModus = false;
	
	public DataTransform(EntityNPCInterface npc){
		this.npc = npc;
	}


    public CompoundNBT save(CompoundNBT compound) {
    	compound.putBoolean("TransformIsActive", isActive);
    	writeOptions(compound);
    	if(hasDisplay)
    		compound.put("TransformDisplay", display);
    	if(hasAi)
    		compound.put("TransformAI", ai);
    	if(hasAdvanced)
    		compound.put("TransformAdvanced", advanced);
    	if(hasInv)
    		compound.put("TransformInv", inv);
    	if(hasStats)
    		compound.put("TransformStats", stats);
    	if(hasRole)
    		compound.put("TransformRole", role);
    	if(hasJob)
    		compound.put("TransformJob", job);
    	
    	return compound;
    }
    
	public CompoundNBT writeOptions(CompoundNBT compound) {
    	compound.putBoolean("TransformHasDisplay", hasDisplay);
    	compound.putBoolean("TransformHasAI", hasAi);
    	compound.putBoolean("TransformHasAdvanced", hasAdvanced);
    	compound.putBoolean("TransformHasInv", hasInv);
    	compound.putBoolean("TransformHasStats", hasStats);
    	compound.putBoolean("TransformHasRole", hasRole);
    	compound.putBoolean("TransformHasJob", hasJob);
    	compound.putBoolean("TransformEditingModus", editingModus);
		return compound;
	}
    

    public void readToNBT(CompoundNBT compound) {
    	isActive = compound.getBoolean("TransformIsActive");
    	readOptions(compound);
    	display = hasDisplay?compound.getCompound("TransformDisplay"): getDisplay();
    	ai = hasAi?compound.getCompound("TransformAI"): npc.ais.save(new CompoundNBT());
    	advanced = hasAdvanced?compound.getCompound("TransformAdvanced"): getAdvanced();
    	inv = hasInv?compound.getCompound("TransformInv"): npc.inventory.save(new CompoundNBT());
    	stats = hasStats?compound.getCompound("TransformStats"): npc.stats.save(new CompoundNBT());
    	job = hasJob?compound.getCompound("TransformJob"): getJob();
    	role = hasRole?compound.getCompound("TransformRole"): getRole();
    }
    
    public CompoundNBT getJob() {
    	CompoundNBT compound = new CompoundNBT();

        compound.putInt("NpcJob", npc.job.getType());
		npc.job.save(compound);
    	
    	return compound;
	}
    public CompoundNBT getRole() {
    	CompoundNBT compound = new CompoundNBT();

        compound.putInt("Role", npc.role.getType());
		npc.role.save(compound);
    	
    	return compound;
	}

    public CompoundNBT getDisplay() {
    	CompoundNBT compound = npc.display.save(new CompoundNBT());
    	if(npc instanceof EntityCustomNpc){
    		compound.put("ModelData", ((EntityCustomNpc)npc).modelData.save());
    	}
    	
    	return compound;
    }

    public CompoundNBT getAdvanced() {
		JobInterface jopType = npc.job;
		RoleInterface roleType = npc.role;

		npc.job = JobInterface.NONE;
		npc.role = RoleInterface.NONE;

    	CompoundNBT compound = npc.advanced.save(new CompoundNBT());

    	npc.job = jopType;
    	npc.role = roleType;
    	
    	return compound;
	}


	public void readOptions(CompoundNBT compound){
		boolean hadDisplay = hasDisplay;
		boolean hadAI = hasAi;
		boolean hadAdvanced = hasAdvanced;
		boolean hadInv = hasInv;
		boolean hadStats = hasStats;
		boolean hadRole = hasRole;
		boolean hadJob = hasJob;
		
    	hasDisplay = compound.getBoolean("TransformHasDisplay");
    	hasAi = compound.getBoolean("TransformHasAI");
    	hasAdvanced = compound.getBoolean("TransformHasAdvanced");
    	hasInv = compound.getBoolean("TransformHasInv");
    	hasStats = compound.getBoolean("TransformHasStats");
    	hasRole = compound.getBoolean("TransformHasRole");
    	hasJob = compound.getBoolean("TransformHasJob");
    	editingModus = compound.getBoolean("TransformEditingModus");

    	if(hasDisplay && !hadDisplay){
    		display = getDisplay();
    	}
    	if(hasAi && !hadAI)
    		ai = npc.ais.save(new CompoundNBT());
    	if(hasStats && !hadStats)
    		stats = npc.stats.save(new CompoundNBT());
    	if(hasInv && !hadInv)
    		inv = npc.inventory.save(new CompoundNBT());
    	if(hasAdvanced && !hadAdvanced)
    		advanced = getAdvanced();
    	if(hasJob && !hadJob)
    		job = getJob();
    	if(hasRole && !hadRole)
    		role = getRole();
    }
    
    public boolean isValid(){
    	return hasAdvanced || hasAi || hasDisplay || hasInv || hasStats || hasJob || hasRole;
    }


	public CompoundNBT processAdvanced(CompoundNBT compoundAdv,
			CompoundNBT compoundRole, CompoundNBT compoundJob) {
		
		if(hasAdvanced)
			compoundAdv = advanced;
		if(hasRole)
			compoundRole = role;
		if(hasJob)
			compoundJob = job;
		
		Set<String> names = compoundRole.getAllKeys();
		for(String name : names)
			compoundAdv.put(name, compoundRole.get(name));

		names = compoundJob.getAllKeys();
		for(String name : names)
			compoundAdv.put(name, compoundJob.get(name));
		
		return compoundAdv;
	}
	
	public void transform(boolean isActive){
		if(this.isActive == isActive)
			return;
    	if(hasDisplay){
			CompoundNBT compound = getDisplay();
			npc.display.readToNBT(NBTTags.NBTMerge(compound, display));
			if(npc instanceof EntityCustomNpc){
				((EntityCustomNpc)npc).modelData.load(NBTTags.NBTMerge(compound.getCompound("ModelData"), display.getCompound("ModelData")));
			}
			display = compound;
    	}
    	if(hasStats){
			CompoundNBT compound = npc.stats.save(new CompoundNBT());
			npc.stats.readToNBT(NBTTags.NBTMerge(compound, stats));
			stats = compound;
    	}
    	if(hasAdvanced || hasJob || hasRole){
			CompoundNBT compoundAdv = getAdvanced();
			CompoundNBT compoundRole = getRole();
			CompoundNBT compoundJob = getJob();
			
			CompoundNBT compound = processAdvanced(compoundAdv, compoundRole, compoundJob);
			npc.advanced.readToNBT(compound);
	        if (npc.role.getType() != RoleType.NONE)
	        	npc.role.load(NBTTags.NBTMerge(compoundRole, compound));
	        if (npc.job.getType() != JobType.NONE)
	        	npc.job.load(NBTTags.NBTMerge(compoundJob, compound));
	        
			if(hasAdvanced)
				advanced = compoundAdv;
			if(hasRole)
				role = compoundRole;
			if(hasJob)
				job = compoundJob;
    	}
    	if(hasAi){
			CompoundNBT compound = npc.ais.save(new CompoundNBT());
			npc.ais.readToNBT(NBTTags.NBTMerge(compound, ai));
			ai = compound;
	    	npc.setCurrentAnimation(AnimationType.NORMAL);
    	}
    	if(hasInv){
			CompoundNBT compound = npc.inventory.save(new CompoundNBT());
			npc.inventory.load(NBTTags.NBTMerge(compound, inv));
			inv = compound;
    	}

		npc.updateAI = true;
    	this.isActive = isActive;
		npc.updateClient = true;
	}
}
