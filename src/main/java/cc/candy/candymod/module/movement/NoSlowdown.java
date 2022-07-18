package cc.candy.candymod.module.movement;

import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * from phobos
 **/
public class NoSlowdown extends Module {
    public final Setting<Float> webHorizontalFactor = this.register(new Setting<Float>("WebHSpeed", 2.0F, 50.0F, 0.0F));
    public final Setting<Float> webVerticalFactor = this.register(new Setting<Float>("WebVSpeed", 2.0F, 50.0F, 0.0F));
    public Setting<Boolean> noSlow = this.register(new Setting<Boolean>("NoSlow", true));
    public Setting<Boolean> strict = this.register(new Setting<Boolean>("Strict", false));
    public Setting<Boolean> sneakPacket = this.register(new Setting<Boolean>("SneakPacket", false));
    public Setting<Boolean> webs = this.register(new Setting<Boolean>("Webs", false));
    private boolean sneaking = false;

    public NoSlowdown(){
        super("NoSlowdown" , Categories.MOVEMENT , false , false);
    }

    @Override
    public void onUpdate() {
        if (this.webs.getValue() && mc.player.isInWeb) {
            mc.player.motionX *= this.webHorizontalFactor.getValue().doubleValue();
            mc.player.motionZ *= this.webHorizontalFactor.getValue().doubleValue();
            mc.player.motionY *= this.webVerticalFactor.getValue().doubleValue();
        }
        Item item = mc.player.getActiveItemStack().getItem();
        if (this.sneaking && !mc.player.isHandActive() && this.sneakPacket.getValue()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.sneaking = false;
        }
    }

    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Item item = mc.player.getHeldItem(event.getHand()).getItem();
        if ((item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion && sneakPacket.getValue()) && !this.sneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.sneaking = true;
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && this.strict.getValue() && this.noSlow.getValue() && mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN));
        }
    }
}
