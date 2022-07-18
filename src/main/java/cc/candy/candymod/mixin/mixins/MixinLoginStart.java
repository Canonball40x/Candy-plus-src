package cc.candy.candymod.mixin.mixins;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.crash.LoginCrash;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CPacketLoginStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(CPacketLoginStart.class)
public class MixinLoginStart {
    @Inject(method = "writePacketData" , cancellable = true , at = @At("HEAD"))
    public void writePacketData(PacketBuffer buf , CallbackInfo ci) {
        /*
        Module login = CandyMod.m_module.getModuleWithClass(LoginCrash.class);
        if(!login.isEnable) return;
        buf.writeString(null);
        ci.cancel();
         */
    }
}
