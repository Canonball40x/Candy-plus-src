package cc.candy.candymod.event.events.render;

import cc.candy.candymod.event.CandyEvent;
import cc.candy.candymod.managers.Manager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public class RenderEntityModelEvent extends CandyEvent {
    public ModelBase modelBase;
    public Entity entity;
    public float limbSwing;
    public float limbSwingAmount;
    public float age;
    public float headYaw;
    public float headPitch;
    public float scale;

    public RenderEntityModelEvent(ModelBase modelBase, Entity entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch, float scale) {
        this.modelBase = modelBase;
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.age = age;
        this.headYaw = headYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }
}
