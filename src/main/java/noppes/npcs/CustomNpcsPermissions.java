package noppes.npcs;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CustomNpcsPermissions{
	public static final Permission NPC_DELETE = new Permission("customnpcs.npc.delete");
	public static final Permission NPC_CREATE = new Permission("customnpcs.npc.create");
	public static final Permission NPC_GUI = new Permission("customnpcs.npc.gui");
	public static final Permission NPC_FREEZE = new Permission("customnpcs.npc.freeze");
	public static final Permission NPC_RESET = new Permission("customnpcs.npc.reset");
	public static final Permission NPC_AI = new Permission("customnpcs.npc.ai");
	public static final Permission NPC_ADVANCED = new Permission("customnpcs.npc.advanced");
	public static final Permission NPC_DISPLAY = new Permission("customnpcs.npc.display");
	public static final Permission NPC_INVENTORY = new Permission("customnpcs.npc.inventory");
	public static final Permission NPC_STATS = new Permission("customnpcs.npc.stats");
	public static final Permission NPC_CLONE = new Permission("customnpcs.npc.clone");

	public static final Permission GLOBAL_LINKED = new Permission("customnpcs.global.linked");
	public static final Permission GLOBAL_PLAYERDATA = new Permission("customnpcs.global.playerdata");
	public static final Permission GLOBAL_BANK = new Permission("customnpcs.global.bank");
	public static final Permission GLOBAL_DIALOG = new Permission("customnpcs.global.dialog");
	public static final Permission GLOBAL_QUEST = new Permission("customnpcs.global.quest");
	public static final Permission GLOBAL_FACTION = new Permission("customnpcs.global.faction");
	public static final Permission GLOBAL_TRANSPORT = new Permission("customnpcs.global.transport");
	public static final Permission GLOBAL_RECIPE = new Permission("customnpcs.global.recipe");
	public static final Permission GLOBAL_NATURALSPAWN = new Permission("customnpcs.global.naturalspawn");

	public static final Permission SPAWNER_MOB = new Permission("customnpcs.spawner.mob");
	public static final Permission SPAWNER_CREATE = new Permission("customnpcs.spawner.create");
	
	public static final Permission TOOL_MOUNTER = new Permission("customnpcs.tool.mounter");
	public static final Permission TOOL_PATHER = new Permission("customnpcs.tool.pather");
	public static final Permission TOOL_SCRIPTER = new Permission("customnpcs.tool.scripter");
	public static final Permission TOOL_NBTBOOK = new Permission("customnpcs.tool.nbtbook");

	public static final Permission EDIT_VILLAGER = new Permission("customnpcs.edit.villager");	
	public static final Permission EDIT_BLOCKS = new Permission("customnpcs.edit.blocks");
	
	public static final Permission SOULSTONE_ALL = new Permission("customnpcs.soulstone.all", false);

	public static final Permission SCENES = new Permission("customnpcs.scenes");
	
	public static CustomNpcsPermissions Instance;
	
	public CustomNpcsPermissions(){
		Instance = this;
		if(!CustomNpcs.DisablePermissions){
			LogManager.getLogger(CustomNpcs.class).info("CustomNPC Permissions available:");
            Collections.sort(Permission.permissions, (o1, o2) -> o1.name.compareToIgnoreCase(o2.name));
			for(Permission p : Permission.permissions){
				PermissionAPI.registerNode(p.name, p.defaultValue?DefaultPermissionLevel.ALL:DefaultPermissionLevel.OP, p.name);
				LogManager.getLogger(CustomNpcs.class).info(p.name);
			}
		}
	}
	
	public static boolean hasPermission(PlayerEntity player, Permission permission){
		if(CustomNpcs.OpsOnly){
			return player.hasPermissions(4);
		}
		if(CustomNpcs.DisablePermissions) {
			return permission.defaultValue;
		}
		return hasPermissionString(player, permission.name);
	}

	public static boolean hasPermissionString(PlayerEntity player, String permission) {
		if(CustomNpcs.OpsOnly){
			return player.hasPermissions(4);
		}
		if(CustomNpcs.DisablePermissions)
			return true;
		return PermissionAPI.hasPermission(player, permission);
	}
	
	public static class Permission{
		private static final List<Permission> permissions = new ArrayList<Permission>();
		public String name;
		public boolean defaultValue = true;
		public Permission(String name){
			this.name = name;
			permissions.add(this);
		}
		
		public Permission(String name, boolean defaultValue){
			this.name = name;
			permissions.add(this);
			this.defaultValue  = defaultValue;
		}
	}
}
