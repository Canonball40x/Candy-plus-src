package cc.candy.candymod.event.events.world;

import cc.candy.candymod.event.CandyEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockEvent extends CandyEvent {
    public BlockPos pos;
    public EnumFacing facing;
    public int stage = 0;

    public BlockEvent(int stage, BlockPos pos, EnumFacing facing) {
        this.stage = stage;
        this.pos = pos;
        this.facing = facing;
    }
}
