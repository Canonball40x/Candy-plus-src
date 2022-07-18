package cc.candy.candymod.mixin.mixins;

import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.event.events.player.PlayerDeathEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={NetHandlerPlayClient.class})
public class MixinNetHandlerPlayClient {
    @Inject(method={"handleEntityMetadata"}, at={@At(value="RETURN")}, cancellable=true)
    private void handleEntityMetadataHook(SPacketEntityMetadata packetIn, CallbackInfo info) {
        EntityPlayer player;
        Entity entity;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world != null && (entity = mc.world.getEntityByID(packetIn.getEntityId())) instanceof EntityPlayer
                && (player = (EntityPlayer)entity).getHealth() <= 0.0f) {
            PlayerDeathEvent event = new PlayerDeathEvent(player);
            MinecraftForge.EVENT_BUS.post(event);
        }
    }
}
