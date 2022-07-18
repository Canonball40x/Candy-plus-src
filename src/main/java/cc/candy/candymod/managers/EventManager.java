package cc.candy.candymod.managers;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.event.events.player.PlayerDeathEvent;
import cc.candy.candymod.utils.RenderUtil3D;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

public class EventManager extends Manager{
    //update event
    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if(!nullCheck()) {
            CandyMod.m_notif.onUpdate();
            CandyMod.m_rotate.updateRotations();
            CandyMod.m_hole.update();
            //call module event
            CandyMod.m_module.onUpdate();
        }
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        //call login event
        CandyMod.m_module.onConnect();
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        //call logout event
        CandyMod.Info("Saving configs...");
        ConfigManager.saveConfigs();
        CandyMod.Info("Successfully save configs!");
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event)
    {
        //if(nullCheck())
        //   return;

        //call tick event
        CandyMod.m_module.onTick();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL , receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if(Keyboard.getEventKeyState())
        CandyMod.m_module.onKeyInput(Keyboard.getEventKey());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            ScaledResolution resolution = new ScaledResolution(mc);
            CandyMod.m_module.onRender2D();
            CandyMod.m_notif.onRender2D();
            GlStateManager.color(1.0F , 1.0F , 1.0F , 1.0F);
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) return;
        if (mc.player == null || mc.world == null) return;

        mc.profiler.startSection("candy");
        mc.profiler.startSection("setup");
        RenderUtil3D.prepare();
        mc.profiler.endSection();

        /*
        for (Module module : ModuleManager.getModules()) {
            if (!module.isEnabled()) continue;
            getProfiler().startSection(module.getName());
            module.onWorldRender(event1);
            getProfiler().endSection();
        }

         */

        CandyMod.m_module.onRender3D();
        CandyMod.m_module.onRender3D(event.getPartialTicks());

        mc.profiler.startSection("release");
        RenderUtil3D.release();
        mc.profiler.endSection();
        mc.profiler.endSection();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event)
    {
        CandyMod.m_module.onPacketSend(event);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event)
    {
        CandyMod.m_module.onPacketReceive(event);

        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);
                CandyMod.m_module.onTotemPop(player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(PlayerDeathEvent event) {
        CandyMod.m_module.onPlayerDeath(event);
    }
}
