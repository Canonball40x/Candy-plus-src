package cc.candy.candymod.module.render;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Freecam extends Module {
    public Setting<modes> mode = register(new Setting("Mode" , modes.Camera));

    public Freecam(){
        super("FreeCam" , Categories.RENDER , false , false);
    }

    public enum modes
    {
        Normal,
        Camera,
    }

    private Entity riding;
    private EntityOtherPlayerMP Camera;
    private Vec3d position;
    private float yaw;
    private float pitch;

    @Override
    public void onEnable()
    {
        super.onEnable();

        if (mc.world == null)
            return;

        if (mode.getValue() == modes.Normal)
        {
            riding = null;

            if (mc.player.getRidingEntity() != null)
            {
                this.riding = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
            }

            Camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            Camera.copyLocationAndAnglesFrom(mc.player);
            Camera.prevRotationYaw = mc.player.rotationYaw;
            Camera.rotationYawHead = mc.player.rotationYawHead;
            Camera.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(-69, Camera);

            this.position = mc.player.getPositionVector();
            this.yaw = mc.player.rotationYaw;
            this.pitch = mc.player.rotationPitch;

            mc.player.noClip = true;
        }
        else // camera
        {
            Camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            Camera.copyLocationAndAnglesFrom(mc.player);
            Camera.prevRotationYaw = mc.player.rotationYaw;
            Camera.rotationYawHead = mc.player.rotationYawHead;
            Camera.inventory.copyInventory(mc.player.inventory);
            Camera.noClip = true;
            mc.world.addEntityToWorld(-69, Camera);
            mc.setRenderViewEntity(Camera);
        }
    }
}
