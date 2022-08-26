package noppes.npcs;


import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import noppes.npcs.api.event.ForgeEvent;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.util.LogWriter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ForgeEventHandler {

    public static List<String> eventNames = new ArrayList<>();

    @SubscribeEvent
    public void forgeEntity(Event event){
        if(CustomNpcs.Server == null || !ScriptController.Instance.forgeScripts.isEnabled()) {
            return;
        }

        try{
            if(event instanceof net.minecraftforge.event.entity.player.PlayerEvent) {
                net.minecraftforge.event.entity.player.PlayerEvent ev = (net.minecraftforge.event.entity.player.PlayerEvent) event;
                if(!(ev.getPlayer().level instanceof ServerWorld))
                    return;
            }
            if(event instanceof EntityEvent) {
                EntityEvent ev = (EntityEvent) event;
                if(ev.getEntity() == null || !(ev.getEntity().level instanceof ServerWorld))
                    return;
                if(event instanceof PlayerXpEvent){
                    LogWriter.info(event);
                }
                EventHooks.onForgeEntityEvent(ev);
                return;
            }
            if(event instanceof WorldEvent) {
                WorldEvent ev = (WorldEvent) event;
                if(!(ev.getWorld() instanceof ServerWorld)) {
                    return;
                }
                EventHooks.onForgeWorldEvent(ev);
                return;
            }
            if(event instanceof TickEvent) {
                if(((TickEvent)event).side == LogicalSide.CLIENT)
                    return;
            }
            EventHooks.onForgeEvent(new ForgeEvent(event), event);
        }
        catch(Throwable t){
            LogWriter.error("Error in " + event.getClass().getName(), t);
        }
    }

    public static String getEventName(Class c){
        String eventName = c.getName();
        int i = eventName.lastIndexOf(".");
        return StringUtils.uncapitalize(eventName.substring(i + 1).replace("$", ""));
    }
}
