package cc.candy.candymod.module.movement;

import cc.candy.candymod.event.events.player.MoveEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.EntityUtil;
import cc.candy.candymod.utils.PlayerUtil;
import cc.candy.candymod.utils.Timer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speed extends Module {
    public Setting<Float> speed = register(new Setting("Speed" , 2.0F , 10.0F , 1.0F));

    public Speed()
    {
        super("Speed" , Categories.MOVEMENT , false , false);
    }

    @Override
    public void onTick(){
        if(nullCheck()) return;
        double[] s = PlayerUtil.forward(speed.getValue() / 10.0F);
        mc.player.motionX = s[0];
        mc.player.motionZ = s[1];
    }

}
