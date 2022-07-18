package cc.candy.candymod.module.movement;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.MathUtil;
import cc.candy.candymod.utils.PlayerUtil;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HoleTP2 extends Module {
    public Setting<Integer> split = register(new Setting("Split" , 5 , 15 , 1));
    public Setting<Float> y = register(new Setting("Y" , 10F , 15F , 0F));
    public Setting<Boolean> stopMotion = register(new Setting("StopMotion" , true));
    public Setting<Boolean> packet = register(new Setting("Packet" , true));
    public Setting<Boolean> up = register(new Setting("Up" , false));
    public Setting<Boolean> trap = register(new Setting("TrapMode" , false));
    public Setting<Boolean> calc = register(new Setting("Calc" , false));
    public Setting<Float> range = register(new Setting("Range" , 5.0F , 20.0F , 1.0F , v -> calc.getValue()));
    public Setting<Float> speed = register(new Setting("Speed" , 5.0F , 10.0F , 1.5F));
    public Setting<fall> fallType = register(new Setting("Fall" , fall.Motion));
    public Setting<Float> fallSpeed = register(new Setting("FallSpeed" , 4.0F , 15.0F , 1.0F , v -> fallType.getValue() == fall.Motion));

    public enum fall{
        Packet ,
        Motion
    }

    public HoleTP2(){
        super("HoleTP2" , Categories.MOVEMENT , false , false);
    }

    public Vec3d hole;
    public Vec3d py;

    @Override
    public void onEnable(){
        if(nullCheck()) return;
        if(!setHole()) {
            sendMessage("Cannot find hole! disabling");
            disable();
        }
        py = new Vec3d(getPlayerPos().x , y.getValue() , getPlayerPos().z);
    }

    @Override
    public void onTick(){
        if(nullCheck()) return;

        if (mc.player.rotationYawHead != mc.player.rotationYaw && packet.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(
                    mc.player.rotationYawHead, mc.player.rotationPitch, mc.player.onGround));
            return;
        }

        int i = 0;
        Vec3d pos = getPlayerPos();
        while(true){
            if(i > split.getValue()) break;

            if(stopMotion.getValue()){
                mc.player.motionX *= 0.0F;
                mc.player.motionY *= 0.0F;
                mc.player.motionZ *= 0.0F;
            }

            //y
            if(Math.abs(py.y - pos.y) > 1.1F){
                double offset = (py.y - pos.y) / speed.getValue();
                pos = pos.add(0 , offset , 0);
                if(packet.getValue()){
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.x , pos.y , pos.z , false));
                    if(up.getValue()) mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.x , pos.y + 1337.0 , pos.z , true));
                }
                else{
                    mc.player.setPosition(pos.x , pos.y , pos.z);
                }
            }
            //x
            else if(Math.abs(hole.x - pos.x) > 1.1F || Math.abs(hole.z - pos.z) > 1.1F){
                double offsetX = (hole.x - pos.x) / speed.getValue();
                double offsetZ = (hole.z - pos.z) / speed.getValue();
                pos = pos.add(offsetX , 0 , offsetZ);
                teleport(pos);
            }
            else {
                teleport(new Vec3d(hole.x , pos.y , hole.z));
                if(fallType.getValue() == fall.Motion){
                    mc.player.motionY = fallSpeed.getValue() * -1;
                }
                else{
                    while(Math.abs((hole.y - pos.y)) < 1.1F){
                        double offset = (hole.y - pos.y) / speed.getValue();
                        pos = pos.add(0 , offset , 0);
                        teleport(pos);
                    }
                }
                disable();
                break;
            }

            i++;
        }
    }

    public void teleport(Vec3d pos){
        if(packet.getValue()){
            mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.x , pos.y , pos.z , false));
            if(up.getValue() && !trap.getValue()) mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.x , pos.y + 1337.0 , pos.z , true));
        }
        else{
            mc.player.setPosition(pos.x , pos.y , pos.z);
        }
    }

    public boolean setHole(){
        List<BlockPos> holes;
        if(calc.getValue()) holes = CandyMod.m_hole.calcHoles();
        else holes = CandyMod.m_hole.calcHoles(range.getValue());
        if(holes == null || holes.size() == 0) return false;
        BlockPos _hole = holes.stream().filter(h -> mc.player.getDistance(h.getX() + 0.5 , h.getY() , h.getZ() + 0.5) > 0.5)
                .min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
        if(_hole == null) return false;
        hole = new Vec3d(_hole.getX() + 0.5 , _hole.getY() , _hole.getZ() + 0.5);
        return true;
    }

    public double range(Vec3d a , Vec3d b){
        return a.distanceTo(b);
    }

    public Vec3d getPlayerPos(){
        return new Vec3d(mc.player.posX , mc.player.posY , mc.player.posZ);
    }
}
