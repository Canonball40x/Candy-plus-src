package cc.candy.candymod.module;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.event.events.player.PlayerDeathEvent;
import cc.candy.candymod.managers.Manager;
import cc.candy.candymod.managers.NotificationManager;
import cc.candy.candymod.module.misc.DiscordRPC;
import cc.candy.candymod.module.render.Notification;
import cc.candy.candymod.setting.Bind;
import cc.candy.candymod.setting.EnumConverter;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Module implements Util {

    public String name = "";
    public Categories category = Categories.MISC;
    public boolean hide = false;
    public boolean isEnable = false;
    public Bind key = new Bind();
    public List<Setting> settings = new ArrayList<>();

    public Module(String name , Categories category , int defaultKey , boolean hide , boolean defaultStatus)
    {
        this.name = name;
        this.category = category;
        setKey(defaultKey);
        this.hide = hide;
        isEnable = defaultStatus;
    }

    public Module(String name , Categories category , boolean hide , boolean defaultStatus)
    {
        this.name = name;
        this.category = category;
        setKey(-1);
        this.hide = hide;
        isEnable = defaultStatus;
    }

    //events
    public void onUpdate()
    {
    }

    public void onTick()
    {
    }

    public void onConnect()
    {
    }

    public void onRender2D()
    {
    }

    public void onRender3D()
    {
    }

    public void onRender3D(float ticks)
    {
    }

    public void onPacketSend(PacketEvent.Send event){}

    public void onEnable()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable()
    {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public void onPlayerDeath(PlayerDeathEvent event) {

    }

    public void onPacketReceive(PacketEvent.Receive event) {}

    public void onTotemPop(EntityPlayer entity){}

    //?
    public boolean isEnable()
    {
        return isEnable;
    }

    public boolean toggle()
    {
        if(!isEnable) {
            enable();
        }
        else {
            disable();
        }

        return isEnable;
    }

    public void enable()
    {
        if(!isEnable) {
			SendMessage(this.name + " : " + ChatFormatting.GREEN + "Enabled");

            isEnable = true;
            onEnable();
        }
    }

    public void disable()
    {
        if(isEnable) {
            SendMessage(this.name + " : " + ChatFormatting.RED + "Disabled");

            isEnable = false;
            onDisable();
        }
    }

    public Setting register(Setting setting)
    {
        settings.add(setting);
        return setting;
    }

    public boolean nullCheck()
    {
        if(mc.player == null || mc.world == null)
            return true;

        return false;
    }

    public void setKey(int Nkey)
    {
        key.setKey(Nkey);
    }

    public void saveConfig() throws IOException
    {
        Gson gson = new Gson();
        Map<String , Object> mappedSettings = new HashMap<String , Object>();
        for(Setting s : settings)
        {
            if(s.value instanceof Enum)
            {
                mappedSettings.put(s.name , EnumConverter.currentEnum((Enum)s.value) + "N");
                continue;
            }
            if(s.value instanceof Color)
            {
                Color color = (Color)s.value;
                mappedSettings.put(s.name , color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha() + "C");
                continue;
            }
            if(s.value instanceof Integer)
            {
                mappedSettings.put(s.name , (Integer)s.value + "I");
                continue;
            }
            mappedSettings.put(s.name , s.value);
        }
        mappedSettings.put("ismoduleenabled" , isEnable);
        mappedSettings.put("keybindcode" , key.getKey());
        String json = gson.toJson(mappedSettings);
        File config = new File("candy/" + category.name().toLowerCase() + "/" + name.toLowerCase() + ".json");
        FileWriter writer = new FileWriter(config);
        writer.write(json);
        writer.close();
    }

    public void loadConfig() throws Exception
    {
        Path path = Paths.get("candy/" + category.name().toLowerCase() + "/" + name.toLowerCase() + ".json");
        if(!Files.exists(path))
        {
            return;
        }
        String context = readAll(path);
        Gson gson = new Gson();
        Map<String , Object> mappedSettings = gson.fromJson(context, Map.class);
        mappedSettings.forEach((name , value) -> setConfig(name , value));
    }

    public void setConfig(String name , Object value)
    {
        if(Objects.equals(name, "ismoduleenabled"))
        {
            isEnable = (Boolean)value;
            if(isEnable) {
                MinecraftForge.EVENT_BUS.register(this);
                if(this instanceof DiscordRPC) onEnable();
            }

            return;
        }
        if(Objects.equals(name, "keybindcode"))
        {
            key.setKey(((Double)value).intValue());
            return;
        }

        List<Setting> settings = new ArrayList<>(this.settings);
        for (int i = 0; i < settings.size(); i++)
        {
            Setting setting = settings.get(i);
            String n = setting.name;
            if(Objects.equals(n, name))
            {
                char c = value.toString().charAt(value.toString().length() - 1);
                if(c == 'N')
                {
                    String enumValue = value.toString().replace("N" , "");
                    setting.setEnum(Integer.parseInt(enumValue));
                    continue;
                }
                if(c == 'C')
                {
                    String[] color = value.toString().replace("C" , "").split(",");
                    setting.setValue(new Color(Integer.parseInt(color[0]) , Integer.parseInt(color[1]) , Integer.parseInt(color[2]) , Integer.parseInt(color[3])));
                    continue;
                }
                if(c == 'I')
                {
                    String intValue = value.toString().replace("I" , "");
                    setting.setValue((Double.valueOf(intValue)).intValue());
                    continue;
                }
                if(value instanceof Double)
                {
                    setting.setValue(((Double)value).floatValue());
                    continue;
                }
                setting.setValue(value);
            }
        }
        this.settings = new ArrayList<>(settings);
    }

    public static String readAll(Path path) throws IOException {
        return Files.lines(path)
                .reduce("", (prev, line) ->
                        prev + line + System.getProperty("line.separator"));
    }

    public enum Categories
    {
        COMBAT ,
        //CRASH ,
        EXPLOIT ,
        MISC ,
        MOVEMENT ,
        RENDER ,
        HUB
    }

    public void sendMessage(String str)
    {
		Notification notif = (Notification) CandyMod.m_module.getModuleWithClass(Notification.class);
        if(notif.isEnable && notif.message.getValue())
            CandyMod.m_notif.showNotification(str);
        else
            mc.player.sendMessage(new Manager.ChatMessage(ChatFormatting.RED + "[" + CandyMod.NAME + "] " + ChatFormatting.GRAY + str));
    }

    private void SendMessage(String str)
    {
        if(GUICheck()) {
            Notification notif = (Notification) CandyMod.m_module.getModuleWithClass(Notification.class);
            if(notif.isEnable && notif.togglE.getValue())
                CandyMod.m_notif.showNotification(str);
            else
                mc.player.sendMessage(new Manager.ChatMessage(ChatFormatting.RED + "[" + CandyMod.NAME + "] " + ChatFormatting.GRAY + str));
        }
    }

    private boolean GUICheck()
    {
        return !(this.name.toLowerCase().equals("clickgui"));
    }

    public static class ChatMessage
            extends TextComponentBase {
        private final String text;

        public ChatMessage(String text) {
            Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
            Matcher matcher = pattern.matcher(text);
            StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                String replacement = matcher.group().substring(1);
                matcher.appendReplacement(stringBuffer, replacement);
            }
            matcher.appendTail(stringBuffer);
            this.text = stringBuffer.toString();
        }

        public String getUnformattedComponentText() {
            return this.text;
        }

        public ITextComponent createCopy() {
            return null;
        }

        public ITextComponent shallowCopy() {
            return new Manager.ChatMessage(this.text);
        }
    }
}
