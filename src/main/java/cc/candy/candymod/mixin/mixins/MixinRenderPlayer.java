package cc.candy.candymod.mixin.mixins;

import cc.candy.candymod.hud.modules.TargetHUD;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    private void renderLivingLabel(AbstractClientPlayer entity, double x, double y, double z, String name, double distanceSq, CallbackInfo callbackInfo) {
        if(entity.equals(TargetHUD.target)){
            callbackInfo.cancel();
        }
    }
}
