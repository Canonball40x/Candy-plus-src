package cc.candy.candymod.module.misc;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;

public class DiscordRPC extends Module {
    public Setting<Boolean> girl = register(new Setting("Girl" , false));

    public DiscordRPC()
    {
        super("DiscordRPC" , Categories.MISC , false , false);
    }

    @Override
    public void onEnable()
    {
        CandyMod.m_rpc.enable(this);
    }

    @Override
    public void onDisable()
    {
        CandyMod.m_rpc.disable();
    }
}
