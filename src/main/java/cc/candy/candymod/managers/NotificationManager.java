package cc.candy.candymod.managers;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.event.events.player.PlayerDeathEvent;
import cc.candy.candymod.module.render.Notification;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotificationManager extends Manager{
    public List<Notif> notifs = new ArrayList<>();
    public List<EntityPlayer> players = new ArrayList<>();

    private Map<String , Integer> popCounter = new HashMap<>();
    private int scaledWidth , scaledHeight , scaleFactor = 0;

    public void showNotification(String msg)
    {
        if(mc.player == null) return;
        if(!CandyMod.m_module.getModuleWithClass(Notification.class).isEnable) return;

        Notif notif = new Notif(msg);

        //move
        for(Notif n : notifs)
        {
            n.y -= CandyMod.m_font.getHeight() + 40;
        }

        updateResolution();
        notif.y = scaledHeight - 50;
        notif._y = scaledHeight - 50;

        notifs.add(notif);
    }

    public void onUpdate()
    {
        if(mc.world == null) return;
        for(EntityPlayer player : new ArrayList<>(mc.world.playerEntities))
        {
            if(!this.players.contains(player)) showNotification(player.getName() + " is coming towards you!");
        }
        players = new ArrayList<>(mc.world.playerEntities);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event)
    {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);
                if(player.entityId == mc.player.entityId) return;

                Notification notification = (Notification)CandyMod.m_module.getModuleWithClass(Notification.class);
                if(notification.pop.getValue())
                {
                    int pop = countPop(player.getName());

                    if(pop == 1)
                        showNotification(player.getName() + " popped a totem!");
                    else
                        showNotification(player.getName() + " popped " + pop + " totems!");
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(PlayerDeathEvent event) {
        Notification notification = (Notification)CandyMod.m_module.getModuleWithClass(Notification.class);
        if(notification.death.getValue())
        {
            EntityPlayer player = event.player;
            if(player == null) return;
            int pop = getPop(player.getName());
            if(pop == 0)
                showNotification(ChatFormatting.RED + player.getName() + " dead!");
            else
                showNotification(ChatFormatting.RED + player.getName() + " dead after " + pop + " pop!");
			
			if(popCounter.containsKey(player.getName())) popCounter.remove(player.getName());
        }
    }

    public int countPop(String name)
    {
        if(!popCounter.containsKey(name))
        {
            popCounter.put(name , 1);
            return 1;
        }

        popCounter.replace(name , popCounter.get(name) + 1);
        return popCounter.get(name);
    }

    public int getPop(String name)
    {
        if(!popCounter.containsKey(name)) return 0;

        return popCounter.get(name);
    }

    public void onRender2D()
    {
		try
		{
        if(mc.player == null) return;

        for(Notif notification : notifs)
        {
            updateResolution();

            String msg = notification.msg;
            int width = CandyMod.m_font.getWidth(msg);

            RenderUtil.drawRect((float) (scaledWidth - width - 26 + notification.offsetX), notification._y - 21 ,
                    width + 27 , CandyMod.m_font.getHeight() + 12 , ColorUtil.toRGBA(new Color(35 , 35 , 35 , 255)));

            RenderUtil.drawRect((float) (scaledWidth - width - 25 + notification.offsetX), notification._y - 20 ,
                   width + 25 , CandyMod.m_font.getHeight() + 10 , ColorUtil.toRGBA(new Color(45 , 45 , 45 , 255)));

            RenderUtil.drawRect((float) (scaledWidth - width - 26 + notification.offsetX), notification._y - 20 + CandyMod.m_font.getHeight() + 10 ,
                    (width + 26) * ( (notification.max - notification.ticks) / notification.max) , 1 , ColorUtil.toRGBA(new Color(170 , 170 , 170 , 255)));

            RenderUtil.drawString(msg , (float) (scaledWidth - width - 20 + notification.offsetX) , notification._y - 10 - 3 , ColorUtil.toRGBA(255 , 255 , 255) , false , 1.0F);

            if(notification.ticks <= 0) {
                notification.offsetX += (500.0F - notification.offsetX) / 10.0F;
            }
            else {
                notification.ticks--;
                notification.offsetX += (0.0F - notification.offsetX) / 4.0F;
                notification._y += (notification.y - notification._y) / 4.0F;
            }
        }

        notifs = notifs.stream().filter(n -> (n.offsetX < 450 || n.ticks != 0) && !(n._y < -100)).collect(Collectors.toList());
		}
		catch(Exception e) { 
		//ignore
		}
    }

    public void updateResolution() {
        this.scaledWidth = mc.displayWidth;
        this.scaledHeight = mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = mc.isUnicode();
        int i = mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }
        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }
        double scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
        double scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
        this.scaledWidth = MathHelper.ceil(scaledWidthD);
        this.scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    public class Notif
    {
        public String msg;
        public float offsetX = 300.0F;
        public float y = 0.0F;
        public float _y = 0.0F;
        public float ticks = 0;
        public float max = 0;
        public Notif(String msg)
        {
            this.msg = msg;
			int fps = Minecraft.getDebugFPS();
			if(fps == 0) fps = 60;
			int seconds = ((Notification) CandyMod.m_module.getModuleWithClass(Notification.class)).time.getValue();
			this.ticks = seconds * fps;
			this.max = seconds * fps;
        }
    }
}
