package cc.candy.candymod.mixin.mixins;

import cc.candy.candymod.event.events.world.BlockEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {PlayerControllerMP.class})
public class MixinPlayerControllerMP {
    @Inject(method = {"clickBlock"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void clickBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
        BlockEvent event = new BlockEvent(3, pos, face);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.isCancelled())
        {
            info.cancel();
        }
    }

    @Inject(method = {"onPlayerDamageBlock"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onPlayerDamageBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
        BlockEvent event = new BlockEvent(4, pos, face);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.isCancelled())
        {
            info.cancel();
        }
    }
}
