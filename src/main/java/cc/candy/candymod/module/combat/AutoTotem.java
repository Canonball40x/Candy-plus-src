package cc.candy.candymod.module.combat;

import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;

import javax.swing.text.html.parser.Entity;

public class AutoTotem extends Module {
    public Setting<Boolean> packet = register(new Setting("Packet" , true));
    public Setting<Boolean> doublE = register(new Setting("Double" , true ));

    public AutoTotem(){
        super("AutoTotem" , Categories.COMBAT , false , false);
    }

    @Override
    public void onTotemPop(EntityPlayer player){
        if(player.entityId != mc.player.entityId) return;
        if(packet.getValue()) doTotem();
    }

    @Override
    public void onTick(){
		if(nullCheck()) return;
        if(shouldTotem()) doTotem();
    }

    @Override
    public void onUpdate(){
		if(nullCheck()) return;
        if(shouldTotem() && doublE.getValue()) doTotem();
    }

    public void doTotem(){
        int totem = findTotemSlot();
        if(totem == -1) return;
        InventoryUtil.moveItemTo(totem , InventoryUtil.offhandSlot);
    }

    public boolean shouldTotem(){
        return mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING;
    }

    public int findTotemSlot(){
        for(int i = 0; i < mc.player.inventoryContainer.getInventory().size(); i++){
            if (i == InventoryUtil.offhandSlot)
                continue;
            ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
            if(stack.getItem() == Items.TOTEM_OF_UNDYING) return i;
        }

        return -1;
    }
}
