package cc.candy.candymod.module.render;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemViewModel extends Module {
    public Setting<Float> fov = register(new Setting("Fov" , 120F , 300F , 0F));

    public ItemViewModel(){
        super("ItemViewModel" , Categories.RENDER , false , false);
    }
    @SubscribeEvent
    public void onEntityViewRender(EntityViewRenderEvent.FOVModifier event){
        event.setFOV(fov.getValue());
    }
}
