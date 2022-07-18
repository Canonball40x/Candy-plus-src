package cc.candy.candymod.module.movement;

import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Blink extends Module {
    public Setting<Boolean> noEntity = register(new Setting("NoEntity" , false));
    public Setting<Boolean> limit = register(new Setting("Limit" , false));
    public Setting<Integer> maxPackets = register(new Setting("MaxPackets" , 20 , 70 , 10 , s -> limit.getValue()));
    public Setting<Boolean> all = register(new Setting("Cancel All" , false));
    public Setting<Integer> skip = register(new Setting("Skip" , 0 , 3 , 0));

    public EntityPlayer entity = null;
    public BlockPos startPos = null;

    public List<Packet<?>> packets = null;

    public Blink()
    {
        super("Blink" , Categories.MOVEMENT , false , false);
    }

    public void onEnable()
    {
        if(nullCheck())
        {
            disable();
            return;
        }

        packets = new ArrayList<>();
        if(!noEntity.getValue()){
            this.entity = new EntityOtherPlayerMP(mc.world, mc.session.getProfile());
            this.entity.copyLocationAndAnglesFrom(mc.player);
            this.entity.rotationYaw = mc.player.rotationYaw;
            this.entity.rotationYawHead = mc.player.rotationYawHead;
            this.entity.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(6942069, this.entity);
            this.startPos = mc.player.getPosition();
        }
    }

    @Override
    public void onDisable()
    {
        if(nullCheck() || packets == null) return;

        int counter = 0;
        Iterator packets = this.packets.iterator();
        while(packets.hasNext())
        {
            Packet packet = (Packet) packets.next();
            if(skip.getValue() <= counter) {
                mc.player.connection.sendPacket(packet);
                counter = 0;
            }
            counter++;
        }

        mc.world.removeEntityFromWorld(this.entity.entityId);
    }

    @Override
    public void onUpdate()
    {
        if(nullCheck() || packets == null) return;

        if(limit.getValue() &&
                packets.size() > maxPackets.getValue())
        {
            sendMessage("Packets size has reached the limit! disabling...");
            packets = new ArrayList<>();
            disable();
        }
    }

    @Override
    public void onPacketSend(PacketEvent.Send event)
    {
        if(nullCheck() || packets == null) return;

        Packet<?> packet = event.packet;
        if(!all.getValue())
        {
            if((packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus))
            {
                return;
            }

            packets.add(packet);
            event.cancel();
        }
        else
        {
            if(packet instanceof CPacketPlayer) {
                packets.add(packet);
                event.cancel();
            }
        }
    }
}
