package cc.candy.candymod.module.render;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;

public class Notification extends Module {
    public Setting<Integer> time = register(new Setting("Time" , 2 , 5 , 1));

    public Setting<Boolean> togglE = register(new Setting("Toggle" , false));
	public Setting<Boolean> message = register(new Setting("Message" , true));
    public Setting<Boolean> player = register(new Setting("Player" , true));
    public Setting<Boolean> pop = register(new Setting("Totem" , true));
    public Setting<Boolean> death = register(new Setting("Death" , true));

    public Notification()
    {
        super("Notification" , Categories.RENDER , false , false);
    }
}
