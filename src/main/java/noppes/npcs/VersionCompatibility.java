package noppes.npcs;

import java.util.Collection;
import java.util.List;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Lines;
import noppes.npcs.entity.EntityNPCInterface;

public class VersionCompatibility {
	public static int ModRev = 18;

	public static void CheckNpcCompatibility(EntityNPCInterface npc, CompoundNBT compound){
		if(npc.npcVersion == ModRev)
			return;
		if(npc.npcVersion < 12){
			CompatabilityFix(compound, npc.advanced.save(new CompoundNBT()));
			CompatabilityFix(compound, npc.ais.save(new CompoundNBT()));
			CompatabilityFix(compound, npc.stats.save(new CompoundNBT()));
			CompatabilityFix(compound, npc.display.save(new CompoundNBT()));
			CompatabilityFix(compound, npc.inventory.save(new CompoundNBT()));
		}
		if(npc.npcVersion < 5){
			String texture = compound.getString("Texture");
			texture = texture.replace("/mob/customnpcs/", "customnpcs:textures/entity/");
			texture = texture.replace("/mob/", "customnpcs:textures/entity/");
			compound.putString("Texture", texture);
		}
		if(npc.npcVersion < 6 && compound.get("NpcInteractLines") instanceof ListNBT){
			List<String> interactLines = NBTTags.getStringList(compound.getList("NpcInteractLines", 10));
			Lines lines = new Lines();
			for(int i = 0; i < interactLines.size(); i++){
				Line line = new Line();
				line.setText((String) interactLines.toArray()[i]);
				lines.lines.put(i, line);
			}
			compound.put("NpcInteractLines", lines.save());

			List<String> worldLines = NBTTags.getStringList(compound.getList("NpcLines", 10));
			lines = new Lines();
			for(int i = 0; i < worldLines.size(); i++){
				Line line = new Line();
				line.setText((String) worldLines.toArray()[i]);
				lines.lines.put(i, line);
			}
			compound.put("NpcLines", lines.save());

			List<String> attackLines = NBTTags.getStringList(compound.getList("NpcAttackLines", 10));
			lines = new Lines();
			for(int i = 0; i < attackLines.size(); i++){
				Line line = new Line();
				line.setText((String) attackLines.toArray()[i]);
				lines.lines.put(i, line);
			}
			compound.put("NpcAttackLines", lines.save());

			List<String> killedLines = NBTTags.getStringList(compound.getList("NpcKilledLines", 10));
			lines = new Lines();
			for(int i = 0; i < killedLines.size(); i++){
				Line line = new Line();
				line.setText((String) killedLines.toArray()[i]);
				lines.lines.put(i, line);
			}
			compound.put("NpcKilledLines", lines.save());

		}
		if(npc.npcVersion == 12){
			ListNBT list = compound.getList("StartPos", 3);
			if(list.size() == 3){
				int z = ((IntNBT) list.remove(2)).getAsInt();
				int y = ((IntNBT) list.remove(1)).getAsInt();
				int x = ((IntNBT) list.remove(0)).getAsInt();
				
				compound.putIntArray("StartPosNew", new int[]{x,y,z});
			}
		}
		if(npc.npcVersion == 13){
			boolean bo = compound.getBoolean("HealthRegen");
			compound.putInt("HealthRegen", bo?1:0);
			CompoundNBT comp = compound.getCompound("TransformStats");
			bo = comp.getBoolean("HealthRegen");
			comp.putInt("HealthRegen", bo?1:0);
    		compound.put("TransformStats", comp);
			
		}
		if(npc.npcVersion == 15){
			ListNBT list = compound.getList("ScriptsContainers", 10);
			if(list.size() > 0){
				ScriptContainer script = new ScriptContainer(npc.script);
				for(int i = 0; i < list.size(); i++){
					CompoundNBT scriptOld = list.getCompound(i);
					EnumScriptType type = EnumScriptType.values()[scriptOld.getInt("Type")];
					script.script += "\nfunction " + type.function + "(event) {\n" + scriptOld.getString("Script") + "\n}";

					for(String s : NBTTags.getStringList(compound.getList("ScriptList", 10))){
						if(!script.scripts.contains(s))
							script.scripts.add(s);
					}
				}
			}
			if(compound.getBoolean("CanDespawn"))
				compound.putInt("SpawnCycle", 4);
			if(compound.getInt("RangeAndMelee") <= 0)
				compound.putInt("DistanceToMelee", 0);
		}
		if(npc.npcVersion == 16){
			compound.putString("HitSound", "random.bowhit");
			compound.putString("GroundSound", "random.break");
		}
		if(npc.npcVersion == 17){
			if(compound.getString("NpcHurtSound").equals("minecraft:game.player.hurt")){
				compound.putString("NpcHurtSound", "minecraft:entity.player.hurt");
			}
			if(compound.getString("NpcDeathSound").equals("minecraft:game.player.hurt")){
				compound.putString("NpcDeathSound", "minecraft:entity.player.hurt");
			}
			if(compound.getString("FiringSound").equals("random.bow")){
				compound.putString("FiringSound", "minecraft:entity.arrow.shoot");
			}
			if(compound.getString("HitSound").equals("random.bowhit")){
				compound.putString("HitSound", "minecraft:entity.arrow.hit");
			}
			if(compound.getString("GroundSound").equals("random.break")){
				compound.putString("GroundSound", "minecraft:block.stone.break");
			}
		}
		npc.npcVersion = ModRev;
	}
	public static void CheckAvailabilityCompatibility(ICompatibilty compatibilty, CompoundNBT compound){
		if(compatibilty.getVersion() == ModRev)
			return;
		CompatabilityFix(compound, compatibilty.save(new CompoundNBT()));
		
		compatibilty.setVersion(ModRev);
	}
	private static void CompatabilityFix(CompoundNBT compound,
			CompoundNBT check) {
		Collection<String> tags = check.getAllKeys();
		for(String name : tags){
			INBT nbt = check.get(name);
			if(!compound.contains(name)){
				compound.put(name, nbt);
			}
			else if(nbt instanceof CompoundNBT && compound.get(name) instanceof CompoundNBT){
				CompatabilityFix(compound.getCompound(name), (CompoundNBT)nbt);
			}
		}
	}
}
