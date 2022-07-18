package cc.candy.candymod.module.misc;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.*;
import cc.candy.candymod.utils.StringUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;

import java.util.ArrayList;
import java.util.List;

public class DonkeyNotifier extends Module {

    private Setting<Boolean> donkey = register(new Setting("Donkey" , true));
    private Setting<Boolean> llama = register(new Setting("Llama" , true));

    private List<Entity> entities = new ArrayList<>();

    public DonkeyNotifier()
    {
        super("DonkeyNotifer" , Categories.MISC , false , false);
    }

    @Override
    public void onDisable()
    {
        //clear all donkeys
        entities = new ArrayList<>();
    }

    @Override
    public void onUpdate()
    {
		if(nullCheck()) return;
        //search donkey or llama
        List<Entity> donkeys = new ArrayList<>(mc.world.loadedEntityList);
        donkeys.removeIf(e -> !((donkey.getValue() && e instanceof EntityDonkey) || (llama.value && e instanceof EntityLlama)));

        for (Entity e : donkeys
             ) {
            if(!entities.contains(e))
            {
                entities.add(e);
                sendMessage("Found a "
                        + ((e instanceof EntityDonkey) ? "Donkey" : "Llama")
                         + " at " + StringUtil.getPositionString(e.getPosition()));
            }
        }

    }
}
