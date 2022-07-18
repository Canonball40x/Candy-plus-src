package cc.candy.candymod.module.movement;

import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.event.events.player.MoveEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.utils.PlayerUtil;
import ibxm.Player;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PhaseWalk extends Module {
    boolean lastInSideStat;
    BlockPos lastPosition;
    double serverX;
    double serverY;
    double serverZ;
    Vec3d fix;

    int stack;

    public PhaseWalk(){
        super("PhaseWalk" , Categories.MOVEMENT , false , false);
    }

    @Override
    public void onEnable(){
        lastInSideStat = PlayerUtil.isInsideBlock();
        lastPosition = new BlockPos(mc.player);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        if (stack>0) {
            stack --;
        }
        if (PlayerUtil.isInsideBlock() || lastInSideStat) {
            fix = mc.player.getPositionVector();
            PlayerUtil.setPosition(new BlockPos(mc.player));
            PlayerUtil.vClip(5);
            Vec3d motionVector = PlayerUtil.getMotionVector();
            PlayerUtil.move(0, -15, 0);
            PlayerUtil.setMotionVector(motionVector);
            BlockPos pos = new BlockPos(mc.player).offset(EnumFacing.DOWN, mc.player.isSneaking()?2:1);
            lastPosition = pos;
            serverX = pos.getX()+.5;
            serverY = pos.getY();
            serverZ = pos.getZ()+.5;
            mc.getConnection().sendPacket(new CPacketPlayer.Position(serverX, serverY, serverZ, true));
            PlayerUtil.setPosition(fix.x, fix.y, fix.z);
            lastInSideStat = mc.player.collidedVertically;
        }
        if (mc.player.posY<0) {
            mc.player.setPosition(mc.player.posX, 1.1, mc.player.posZ);
            lastInSideStat = true;
        }
    }

    @Override
    public void onPacketSend(PacketEvent.Send event){
        if (lastInSideStat && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            if (packet.getX(serverX) != serverX) event.cancel();
            if (packet.getY(serverY) != serverY) event.cancel();
            if (packet.getZ(serverZ) != serverZ) event.cancel();
        }
        stack += 2;
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event){
        if (event.getPacket() instanceof SPacketPlayerPosLook && lastInSideStat) {
            PlayerUtil.vClip(-8);
            toggle();
        }
        stack += 2;
    }
}
