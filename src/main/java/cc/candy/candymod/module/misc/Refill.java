package cc.candy.candymod.module.misc;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.InventoryUtil;
import cc.candy.candymod.utils.Timer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Refill extends Module {
    public Setting<Float> delay = register(new Setting("Delay" , 1.0F , 10.0F , 0.0F));
    public Timer timer = new Timer();

    public Refill(){
        super("Refill" , Categories.MISC , false , false);
    }

    @Override
    public void onUpdate(){
        if(nullCheck()) return;
        if(timer == null) timer = new Timer();
        if(timer.passedDms(delay.getValue())){
            timer.reset();
            for(int i = 0; i < 9; i++) {
                ItemStack itemstack = mc.player.inventory.mainInventory.get(i);
                if(itemstack.isEmpty) continue;
                if(!itemstack.isStackable()) continue;
                if(itemstack.getCount() >= itemstack.getMaxStackSize()) continue;
                if(doRefill(itemstack)) break;
            }
        }
    }

    public boolean doRefill(ItemStack stack){
        //from salhack
        for(int i = 9; i < 36; i++){
            ItemStack item = mc.player.inventory.getStackInSlot(i);
            if(!CanItemBeMergedWith(item , stack)) continue;
            //do refill
            InventoryUtil.moveItem(i);
            return true;
        }
        return false;
    }

    private boolean CanItemBeMergedWith(ItemStack p_Source, ItemStack p_Target)
    {
        return p_Source.getItem() == p_Target.getItem() && p_Source.getDisplayName().equals(p_Target.getDisplayName());
    }
}
