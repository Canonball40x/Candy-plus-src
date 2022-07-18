package cc.candy.candymod.module.misc;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.combat.AutoMend;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.InventoryUtil;
import cc.candy.candymod.utils.Timer;
import net.minecraft.init.Items;
import net.minecraft.item.*;

public class AutoDrop extends Module {
    public Setting<Boolean> sword = register(new Setting<>("Sword" , true));
    public Setting<Boolean> bow = register(new Setting<>("Bow" , true));
    public Setting<Boolean> pickel = register(new Setting<>("Pickel" , true));
    public Setting<Boolean> armor = register(new Setting<>("DamageArmor" , true));
    public Setting<Boolean> autoMend = register(new Setting<>("PauseWhenAutoMend" , true , v -> armor.getValue()));
    public Setting<Float> damege = register(new Setting("MinDamege" , 50.0F , 100.0F , 0.0F ,v -> armor.getValue()));
    public Setting<Float> delay = register(new Setting("Delay" , 1.0F , 10.0F , 0.0F));

    public Timer timer = new Timer();

    public AutoDrop(){
        super("AutoDrop" , Categories.MISC , false , false);
    }

    @Override
    public void onUpdate(){
        if(nullCheck()) return;
        if(timer == null) timer = new Timer();
        if(timer.passedDms(delay.getValue())){
            timer.reset();
            for(int i = 9; i < 36; i++){
                ItemStack itemStack = mc.player.inventoryContainer.getInventory().get(i);
                Item item = itemStack.getItem();
                if(itemStack.isEmpty || itemStack.getItem() == Items.AIR) continue;
                //sword
                if(item instanceof ItemSword && this.sword.getValue()){
                    InventoryUtil.dropItem(i);
                    break;
                }
                //bow
                if(item instanceof ItemBow && this.bow.getValue()){
                    InventoryUtil.dropItem(i);
                    break;
                }
                //pickel
                if(item instanceof ItemPickaxe && this.pickel.getValue()){
                    InventoryUtil.dropItem(i);
                    break;
                }
                //armor
                if(item instanceof ItemArmor && this.armor.getValue()){
                    Module automend = CandyMod.m_module.getModuleWithClass(AutoMend.class);
                    if(automend.isEnable && autoMend.getValue()) continue;
                    if(getDamage(itemStack) > this.damege.getValue()) continue;
                    InventoryUtil.dropItem(i);
                    break;
                }
            }
        }
    }

    public float getDamage(ItemStack itemStack){
        return ((itemStack.getMaxDamage() - itemStack.getItemDamage()) / (itemStack.getMaxDamage() * 1.0F)) * 100.0F;
    }

}
