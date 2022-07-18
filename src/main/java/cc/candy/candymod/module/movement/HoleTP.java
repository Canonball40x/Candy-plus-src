package cc.candy.candymod.module.movement;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class HoleTP extends Module {
    public HoleTP(){
        super("HoleTP" , Categories.MOVEMENT , false , false);
    }

    public Setting<Float> range = register(new Setting("Range" , 1.0F , 3.0F , 0.1F));
    public Setting<Boolean> stopMotion = register(new Setting("StopMotion" , false));

    @Override
    public void onEnable()
    {
        BlockPos hole = CandyMod.m_hole.calcHoles().stream().min(Comparator.comparing(p -> mc.player.getDistance(p.getX() , p.getY() , p.getZ()))).orElse(null);

        if(hole != null)
        {
            //check range
            if(mc.player.getDistance(hole.getX() , hole.getY() , hole.getZ()) < range.getValue() + 1.5)
            {
                //tp to hole
                mc.player.setPosition(hole.getX() + 0.5 , mc.player.posY , hole.getZ() + 0.5);
                //anti stuck
                if(stopMotion.getValue()){
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                }
                mc.player.motionY = -3;
                sendMessage("Accepting teleport...");
            }
            else
            {
                sendMessage("Out of range! disabling...");
            }
        }
        else
        {
            sendMessage("Not found hole! disabling...");
        }

        disable();
    }
}
