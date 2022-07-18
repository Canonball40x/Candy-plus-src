package cc.candy.candymod.module.movement;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.MathUtil;
import cc.candy.candymod.utils.PlayerUtil;
import cc.candy.candymod.utils.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumHand;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TPCart extends Module {
    public Setting<Float> range = register(new Setting("Range" , 10.0F , 20.0F , 2.0F));
    public Setting<Float> delay = register(new Setting("Delay" , 10.0F , 30.0F , 1.0F));

    public Timer timer = new Timer();

    public TPCart(){
        super("TPCart" , Categories.MOVEMENT , false , false);
    }

    @Override
    public void onTick(){
        try{
            if(nullCheck()) return;

            if(timer == null) timer = new Timer();
            if(timer.passedX(delay.getValue())){
                //find minecart
                List<Entity> carts = mc.world.loadedEntityList
                        .stream().filter(e -> e instanceof EntityMinecart)
                        .filter(e -> !e.equals(mc.player.getRidingEntity()))
                        .filter(e -> PlayerUtil.getDistance(e) <= range.getValue() )
                        .collect(Collectors.toList());

                Entity minecart = carts.get(new Random().nextInt(carts.size()));

                if(minecart == null) return;
                //ride
                mc.playerController.interactWithEntity(mc.player , minecart , EnumHand.MAIN_HAND);
            }
        }
        catch (Exception ignored){

        }
    }
}
