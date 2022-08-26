package noppes.npcs.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import noppes.npcs.CustomItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CCreativeInventoryActionPacket.class)
public class CCreativeInventoryActionPacketMixin {
    private ItemStack item = ItemStack.EMPTY;

    @Inject(at = @At("TAIL"), method = "write")
    private void write(PacketBuffer buffer, CallbackInfo ci) {
        CCreativeInventoryActionPacket p = (CCreativeInventoryActionPacket)(Object)this;
        if(p.getItem().getItem() == CustomItems.scripted_item){
            buffer.writeItem(p.getItem());
        }
        else{
            buffer.writeItem(ItemStack.EMPTY);
        }
    }

    @Inject(at = @At("TAIL"), method = "read")
    private void read(PacketBuffer buffer, CallbackInfo ci) {
        item = buffer.readItem();
    }

    @Inject(at = @At("HEAD"), method = "getItem", cancellable = true)
    private void getItem(CallbackInfoReturnable<ItemStack> cir) {
        if(!item.isEmpty()){
            cir.setReturnValue(item);
            cir.cancel();
        }
    }
}
