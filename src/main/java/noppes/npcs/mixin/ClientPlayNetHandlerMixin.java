package noppes.npcs.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import noppes.npcs.CustomEntities;
import noppes.npcs.entity.EntityProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public class ClientPlayNetHandlerMixin {

    @Inject(at = @At("TAIL"), method = "handleAddEntity")
    private void handleAddEntity(SSpawnObjectPacket packet, CallbackInfo ci) {
        Entity entity = null;
        ClientWorld level = Minecraft.getInstance().level;
        if(packet.getType() == CustomEntities.entityProjectile) {
            entity = new EntityProjectile(CustomEntities.entityProjectile, level);
            Entity entity2 = level.getEntity(packet.getData());
            if (entity2 != null) {
                ((EntityProjectile)entity).setOwner(entity2);
            }
        }

        if (entity != null) {
            int i = packet.getId();
            entity.setPacketCoordinates(packet.getX(), packet.getY(), packet.getZ());
            entity.moveTo(packet.getX(), packet.getY(), packet.getZ());
            entity.xRot = (float)(packet.getxRot() * 360) / 256.0F;
            entity.yRot = (float)(packet.getyRot() * 360) / 256.0F;
            entity.setId(i);
            entity.setUUID(packet.getUUID());
            Minecraft.getInstance().level.putNonPlayerEntity(i, entity);
        }
    }
}
