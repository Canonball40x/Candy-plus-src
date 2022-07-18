package cc.candy.candymod.mixin;

import cc.candy.candymod.asm.CandyAccessTransformer;
import cc.candy.candymod.asm.CandyTransformer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({"cc.candy.candymod.asm"})
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("Candy")
@IFMLLoadingPlugin.SortingIndex(value = 1001)
public class CandyMixin implements IFMLLoadingPlugin
{
    private static boolean isObfuscatedEnvironment = false;

    public CandyMixin()
    {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.candy.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }
	
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                CandyTransformer.class.getName()
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass() {
        return CandyAccessTransformer.class.getName();
    }
}
