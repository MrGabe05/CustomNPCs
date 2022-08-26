package noppes.npcs.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CmdSlay{
	static Map<String, Class<?>> slayMap = new LinkedHashMap<String, Class<?>>();

	private static Map<String, Class<?>> getSlay(World level){
		if(!slayMap.isEmpty()){
			return slayMap;
		}

		slayMap.put("all",LivingEntity.class);
		slayMap.put("mobs", MonsterEntity.class);
		slayMap.put("animals", AnimalEntity.class);
		slayMap.put("items", ItemEntity.class);
		slayMap.put("xporbs", ExperienceOrbEntity.class);
		slayMap.put("npcs", EntityNPCInterface.class);

		for(ResourceLocation resource : ForgeRegistries.ENTITIES.getKeys()){
			EntityType<?> ent = ForgeRegistries.ENTITIES.getValue(resource);
			if(ent.getCategory() == EntityClassification.MISC)
				continue;
			String name = ent.getDescriptionId();
			try{
				Entity e = ent.create(level);
				e.remove(false);
				Class<? extends Entity> cls = e.getClass();
				if(EntityNPCInterface.class.isAssignableFrom(cls))
					continue;
				if(!LivingEntity.class.isAssignableFrom(cls))
					continue;
				slayMap.put(name.toLowerCase(), cls);
			}
			catch(Throwable e){

			}
		}

		slayMap.remove("monster");
		slayMap.remove("mob");
		return slayMap;
	}

	public static LiteralArgumentBuilder<CommandSource> register() {

		LiteralArgumentBuilder<CommandSource> command = Commands.literal("slay").requires((source) -> source.hasPermission(4))
				.then(Commands.argument("type", StringArgumentType.word()).then(Commands.argument("range", IntegerArgumentType.integer(1))
						.executes(context -> {
					ArrayList<Class<?>> toDelete = new ArrayList<Class<?>>();
					boolean deleteNPCs = false;
					String delete = StringArgumentType.getString(context, "type");
					Class<?> cls = getSlay(context.getSource().getLevel()).get(delete);
					if(cls != null)
						toDelete.add(cls);
					if(delete.equals("mobs")){
						toDelete.add(GhastEntity.class);
						toDelete.add(EnderDragonEntity.class);
					}
					if(delete.equals("npcs")) {
						deleteNPCs = true;
					}
					int count = 0;
					int range = IntegerArgumentType.getInteger(context, "range");

					AxisAlignedBB box = new AxisAlignedBB(context.getSource().getPosition(), context.getSource().getPosition().add(1, 1, 1)).inflate(range, range, range);
					List<? extends Entity> list = context.getSource().getLevel().getEntitiesOfClass(LivingEntity.class, box);

					for(Entity entity : list){
						if(entity instanceof PlayerEntity)
							continue;
						if(entity instanceof TameableEntity && ((TameableEntity)entity).isTame())
							continue;
						if(entity instanceof EntityNPCInterface && !deleteNPCs)
							continue;
						if(delete(entity,toDelete))
							count++;
					}
					if(toDelete.contains(ExperienceOrbEntity.class)){
						list = context.getSource().getLevel().getEntitiesOfClass(ExperienceOrbEntity.class, box);
						for(Entity entity : list){
							entity.removed = true;
							count++;
						}
					}
					if(toDelete.contains(ItemEntity.class)){
						list = context.getSource().getLevel().getEntitiesOfClass(ItemEntity.class, box);
						for(Entity entity : list){
							entity.removed = true;
							count++;
						}
					}

					context.getSource().sendSuccess(new TranslationTextComponent(count + " entities deleted"), false);
					return 1;
				})));
		return command;
	}



    private static boolean delete(Entity entity, ArrayList<Class<?>> toDelete) {
		for(Class<?> delete : toDelete){
			if(delete == AnimalEntity.class && (entity instanceof HorseEntity)){
				continue;
			}
			if(delete.isAssignableFrom(entity.getClass())){
				entity.removed = true;
				return true;
			}
		}
		return false;
	}
}
