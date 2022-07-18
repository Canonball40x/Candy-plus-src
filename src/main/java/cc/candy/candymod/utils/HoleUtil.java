package cc.candy.candymod.utils;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class HoleUtil implements Util{
    private static BlockPos[] surroundOffsets = BlockUtil.toBlockPos(PlayerUtil.getOffsets(0, true));

    public static boolean isObbyHole(BlockPos pos){
        for(BlockPos offset : surroundOffsets){
            if(BlockUtil.getBlock(pos.add(offset)) != Blocks.OBSIDIAN) return false;
        }
        return true;
    }

    public static boolean isBedrockHole(BlockPos pos){
        for(BlockPos offset : surroundOffsets){
            if(BlockUtil.getBlock(pos.add(offset)) != Blocks.BEDROCK) return false;
        }
        return true;
    }
}
