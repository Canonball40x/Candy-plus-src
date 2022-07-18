package cc.candy.candymod.module.render;

import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.module.Module;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnPainting;

public class NoOverlay extends Module {
    public NoOverlay()
    {
        super("NoOverlay" , Categories.RENDER , true , true);
    }

    @Override
    public void onRender3D()
    {
        if (mc.player == null) return;

        mc.player.removeActivePotionEffect(MobEffects.BLINDNESS);
        mc.player.removeActivePotionEffect(MobEffects.NAUSEA);
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        Packet<?> packet = event.packet;
        if ((packet instanceof SPacketSpawnExperienceOrb)
                || (packet instanceof SPacketExplosion)) {
            event.cancel();
        }
    }
}
