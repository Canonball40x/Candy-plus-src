package cc.candy.candymod.module.combat;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.InventoryUtil;
import cc.candy.candymod.utils.Timer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoMend extends Module {
    public Setting<Float> delay = register(new Setting("Delay" , 3.0F , 10.0F , 0.0F));
    public Setting<Float> armor = register(new Setting("ArmorDelay" , 3.0F , 20.0F , 0.0F));
    public Setting<Float> pct = register(new Setting("Pct" , 80F , 100F , 10F));
    public Setting<Boolean> press = register(new Setting("Press" , true));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , true));

    public boolean toggleoff = false;
    public Map<Integer , Integer> armors = new HashMap<Integer , Integer>();
    public Timer xpTimer , armorTimer = new Timer();

    public AutoMend(){
        super("AutoMend" , Categories.COMBAT , false , false);
        disable();
    }

    public void onEnable(){
        xpTimer = new Timer();
        armorTimer = new Timer();
    }

    public void onDisable(){
        if(!toggleoff){
            toggleoff = true;
            armors = new HashMap<>();
            enable();
            return;
        }
        toggleoff = false;
    }

    public void onTick(){
        if(nullCheck()) return;

        if(!toggleoff) {
            if(armorTimer == null) armorTimer = new Timer();
            armorTimer.reset();

            //bottle check
            int xp = InventoryUtil.getItemHotbar(Items.EXPERIENCE_BOTTLE);
            if (xp == -1) {
                sendMessage("Cannot find XP! disabling");
                setDisable();
                return;
            }

            //armor check
            if (isDone()) {
                setDisable();
                return;
            }

            //throw xp
            if(xpTimer == null) xpTimer = new Timer();
            if(xpTimer.passedX(delay.getValue())){
                xpTimer.reset();
                setItem(xp);
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0 , 90 , false));
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                restoreItem();
            }

            //pressing
            if (key.key != -1 && press.getValue()) {
                if (!Keyboard.isKeyDown(key.key)) setDisable();
                return;
            }
        }
        //armor
        else{
            if(armorTimer == null) armorTimer = new Timer();
            if(armorTimer.passedDms(armor.getValue())){
                armorTimer.reset();
                AtomicInteger c = new AtomicInteger();
                AtomicInteger key = new AtomicInteger();
                armors.forEach((k , v) -> {
                    if(c.get() == 0) {
                        InventoryUtil.moveItemTo(k , v);
                        key.set(k);
                    }
                    c.getAndIncrement();
                });
                if(c.get() == 0){
                    disable();
                }
                else{
                    armors.remove(key.get());
                }
            }

        }
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            packet.yaw = 0;
            packet.pitch = 90;
        }
    }

    public void setDisable(){
        toggleoff = true;
    }

    public void moveArmorToSlot(int armor , int empty){
        InventoryUtil.moveItemTo(armor , empty);
        armors.put(empty , armor);
    }

    public boolean isDone(){
        boolean done = true;
        for(int i = 0; i < mc.player.inventoryContainer.getInventory().size(); i++){
            ItemStack itemStack = mc.player.inventoryContainer.getInventory().get(i);
            if(!(i > 4 && i < 9)) continue;
            if(itemStack.isEmpty) continue;
            if(getRate(itemStack) < pct.getValue()){
                done = false;
            }
            //move armor
            else{
                int slot = getFreeSlot();
                if(slot == -1) continue;
                moveArmorToSlot(i , slot);
            }
        }
        return done;
    }

    public int getFreeSlot(){
        for(int i = 0; i < mc.player.inventoryContainer.getInventory().size(); i++){
            if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8)
                continue;
            ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
            if(stack.isEmpty || stack.getItem() == Items.AIR) return i;
        }

        return -1;
    }

    public float getRate(ItemStack itemStack){
        return ((itemStack.getMaxDamage() - itemStack.getItemDamage()) / (itemStack.getMaxDamage() * 1.0F)) * 100.0F;
    }

    private EnumHand oldhand = null;
    private int oldslot = -1;

    public void setItem(int slot)
    {
        if(silentSwitch.getValue()) {
            oldhand = null;
            if(mc.player.isHandActive()) {
                oldhand = mc.player.getActiveHand();
            }
            oldslot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        }
        else {
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public void restoreItem()
    {
        if(oldslot != -1 && silentSwitch.getValue())
        {
            if(oldhand != null) {
                mc.player.setActiveHand(oldhand);
            }
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            oldslot = -1;
            oldhand = null;
        }
    }
}
