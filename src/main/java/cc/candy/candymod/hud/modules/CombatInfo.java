package cc.candy.candymod.hud.modules;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.combat.*;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;

import java.awt.*;

public class CombatInfo extends Hud {
    public Setting<Boolean> shadow = register(new Setting("Shadow" , true));
    public Setting<Color> color = register(new Setting("Color" , new Color(255 , 255 , 255 , 255)));
    public Setting<Boolean> mend = register(new Setting("AutoMend" , true));
    public Setting<Boolean> blocker = register(new Setting("Blocker" , true));
    public Setting<Boolean> cev = register(new Setting("CevBreaker" , true));
    public Setting<Boolean> civ = register(new Setting("CivBreaker" , true));
    public Setting<Boolean> holefill = register(new Setting("HoleFill" , true));
    public Setting<Boolean> oyveyaura = register(new Setting("OyveyAura" , true));
    public Setting<Boolean> pa = register(new Setting("PistonAura" , true));

    public CombatInfo() {
        super("CombatInfo" , 50 , 10);
    }

    @Override
    public void onRender(){
        try{
            Module mend = getModule(AutoMend.class);
            Module blocker = getModule(Blocker.class);
            Module cev = getModule(CevBreaker.class);
            Module civ = getModule(CivBreaker.class);
            Module holefill = getModule(HoleFill.class);
            Module oyveyaura = getModule(OyveyAura.class);
            Module pa = getModule(PistonAura.class);
            Module pa2 = getModule(PistonAuraRewrite.class);
            Module pa3 = getModule(PistonAuraRewrite2.class);

            float width = 0.0F;
            float height = 0.0F;
            if(this.mend.getValue()) {
                float _width = drawModuleInfo(mend , height);
                if(width < _width) width = _width;
                height += RenderUtil.getStringHeight(1.0F) + 5;
            }
            if(this.blocker.getValue()) {
                float _width = drawModuleInfo(blocker , height);
                if(width < _width) width = _width;
                height += RenderUtil.getStringHeight(1.0F) + 5;
            }
            if(this.cev.getValue()) {
                float _width = drawModuleInfo(cev , height);
                if(width < _width) width = _width;
                height += RenderUtil.getStringHeight(1.0F) + 5;
            }
            if(this.civ.getValue()) {
                float _width = drawModuleInfo(civ , height);
                if(width < _width) width = _width;
                height += RenderUtil.getStringHeight(1.0F) + 5;
            }
            if(this.holefill.getValue()) {
                float _width = drawModuleInfo(holefill , height);
                if(width < _width) width = _width;
                height += RenderUtil.getStringHeight(1.0F) + 5;
            }
            if(this.oyveyaura.getValue()) {
                float _width = drawModuleInfo(oyveyaura , height);
                if(width < _width) width = _width;
                height += RenderUtil.getStringHeight(1.0F) + 5;
            }
            if(this.pa.getValue()) {
                float _width = RenderUtil.drawString(pa.name + " : " + (pa.isEnable || pa2.isEnable || pa3.isEnable ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF") , this.x.getValue() , this.y.getValue() + height , ColorUtil.toRGBA(color.getValue()) , shadow.getValue() , 1.0F);
                if(width > _width) width = _width;
                height += RenderUtil.getStringHeight(1.0F) + 5;
            }

            this.width = width - x.getValue();
            this.height = height;
        }
        catch (Exception ignored){

        }
    }

    public float drawModuleInfo(Module module , float offset){
        return RenderUtil.drawString(module.name + " : " + (module.isEnable ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF") , this.x.getValue() , this.y.getValue() + offset , ColorUtil.toRGBA(color.getValue()) , shadow.getValue() , 1.0F);
    }

    public Module getModule(Class clazz){
        return CandyMod.m_module.getModuleWithClass(clazz);
    }
}
