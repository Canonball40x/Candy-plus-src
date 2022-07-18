package cc.candy.candymod.module.misc;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.*;
import cc.candy.candymod.utils.*;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NoteSpam extends Module {
    private Setting<Integer> range = register(new Setting("Range" , 7 , 10 , 1));
    private Setting<Integer> max = register(new Setting("MaxBlock" , 30 , 150 , 1));
    private Setting<Color> color = register(new Setting("RenderColor" , new Color(255 , 10 , 10 , 55)));

    private List<BlockPos> notes = new ArrayList<>();

    public NoteSpam()
    {
        super("NoteSpam" , Categories.MISC , false , false);
    }
    
    @Override
    public void onTick()
    {
		if(nullCheck()) return;
		
        //search noteblock
        int counter = 0;

        notes = new ArrayList<>();

        List<BlockPos> posList = BlockUtil.getSphere(PlayerUtil.getPlayerPos(mc.player) , range.getValue() , range.getValue() , false , true , 0);
        for (BlockPos b : posList) {
            if(BlockUtil.getBlock(b) == Blocks.NOTEBLOCK) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, b, EnumFacing.UP));
                counter++;

                if(counter > max.getValue())
                    return;
            }
        }

    }

    @Override
    public void onRender3D()
    {
        for (BlockPos note: notes) {
            RenderUtil3D.drawBox(note , 1.0F , color.getValue(), GeometryMasks.Quad.ALL);
        }
    }
}
