package cc.candy.candymod.hud.modules;

import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.RenderUtil;

public class PlayerModel extends Hud {
    public Setting<Float> scale = register(new Setting("Scale" , 50F , 100F , 30F));

    public PlayerModel(){
        super("PlayerModel" , 100 , 150);
    }

    @Override
    public void onRender(){
        this.width = (mc.player.width + 0.5F) * scale.getValue() + 10;
        this.height = (mc.player.height + 0.5F) * scale.getValue();
        RenderUtil.renderEntity(mc.player , x.getValue() + width - 30 , y.getValue() + height - 20 , scale.getValue());
    }
}
