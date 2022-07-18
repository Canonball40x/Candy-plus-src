package cc.candy.candymod.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ResistanceDetector implements Util{
    public static HashMap<String, Integer> resistanceList = new HashMap();

    public static void init() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                HashMap<String, Integer> a = new HashMap<String, Integer>();
                ArrayList i = new ArrayList();
                resistanceList.forEach((k, v) -> {
                    if (v > 0) {
                        a.put((String)k, v - 1);
                    } else {
                        i.add(k);
                    }
                });
                a.forEach((k, v) -> {
                    if (resistanceList.containsKey(k)) {
                        resistanceList.replace((String)k, (Integer)v);
                    }
                });
                a.clear();
                i.forEach(w -> {
                    if (resistanceList.containsKey(i)) {
                        resistanceList.remove(i);
                    }
                });
            }
            catch (ConcurrentModificationException concurrentModificationException) {
                // empty catch block
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    public static void onUpdate() {
        if (mc.world != null && mc.player != null) {
            for (EntityPlayer uwu : mc.world.playerEntities) {
                if (!(((EntityLivingBase)uwu).getAbsorptionAmount() >= 9.0f)) continue;
                if (resistanceList.containsKey(uwu.getName())) {
                    resistanceList.remove(uwu.getName());
                }
                resistanceList.put(uwu.getName(), 180);
            }
        }
    }
}
