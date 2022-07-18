package cc.candy.candymod.asm;

import cc.candy.candymod.asm.api.ClassPatch;
import cc.candy.candymod.asm.impl.PatchEntityRenderer;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.ArrayList;
import java.util.List;

public class CandyTransformer implements IClassTransformer {

    public static final List<ClassPatch> patches = new ArrayList<>();

    static {
        patches.add(new PatchEntityRenderer());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null)
            return null;

        for (ClassPatch it : patches) {
            if (it.className.equals(transformedName)) {
                return it.transform(bytes);
            }
        }

        return bytes;
    }

}