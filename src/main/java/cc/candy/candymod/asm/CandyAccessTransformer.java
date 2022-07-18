package cc.candy.candymod.asm;

import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

public class CandyAccessTransformer extends AccessTransformer {
    public CandyAccessTransformer() throws IOException {
        super("candy_asm.cfg");
    }
}