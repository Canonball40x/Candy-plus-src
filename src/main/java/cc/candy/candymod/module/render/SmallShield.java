package cc.candy.candymod.module.render;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;

public class SmallShield extends Module {
    public Setting<Boolean> normalOffset = this.register(new Setting("OffNormal", false));
    public Setting<Float> offset = this.register(new Setting("Offset", Float.valueOf(0.7f), Float.valueOf(1.0f), Float.valueOf(0.0f), v -> this.normalOffset.getValue()));
    public Setting<Float> offX = this.register(new Setting("OffX", Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(-1.0f), v -> this.normalOffset.getValue() == false));
    public Setting<Float> offY = this.register(new Setting("OffY", Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(-1.0f), v -> this.normalOffset.getValue() == false));
    public Setting<Float> mainX = this.register(new Setting("MainX", Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(-1.0f)));
    public Setting<Float> mainY = this.register(new Setting("MainY", Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(-1.0f)));

    public SmallShield(){
        super("SmallShield" , Categories.RENDER , false , false);
    }

    public static SmallShield getINSTANCE(){
        return (SmallShield) CandyMod.m_module.getModuleWithClass(SmallShield.class);
    }

    @Override
    public void onUpdate() {
        if(nullCheck()) return;
        if (this.normalOffset.getValue().booleanValue()) {
            mc.entityRenderer.itemRenderer.equippedProgressOffHand = this.offset.getValue().floatValue();
        }
    }
}
