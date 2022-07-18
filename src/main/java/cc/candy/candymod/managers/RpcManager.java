package cc.candy.candymod.managers;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.combat.CevBreaker;
import cc.candy.candymod.module.combat.CivBreaker;
import cc.candy.candymod.module.combat.PistonAura;
import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

//99% salhack
public class RpcManager extends Manager{
    private Thread _thread = null;

    public void enable(cc.candy.candymod.module.misc.DiscordRPC module)
    {
        DiscordRPC lib = DiscordRPC.INSTANCE;
        String applicationId = "871752800470728724";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        lib.Discord_UpdatePresence(presence);
        presence.largeImageText = "";
        _thread = new Thread(() ->
        {
            while (!Thread.currentThread().isInterrupted())
            {
                lib.Discord_RunCallbacks();
                if(module.girl.getValue()) presence.largeImageKey = "tomari";
                else presence.largeImageKey = "icon";

                presence.details = "Enjoying Candy+";
                presence.state = getState();
                lib.Discord_UpdatePresence(presence);
                try
                {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored)
                {
                }
            }
        }, "RPC-Callback-Handler");

        _thread.start();
    }

    public void disable()
    {
        DiscordRPC.INSTANCE.Discord_Shutdown();
        _thread = null;
    }

    public String getState()
    {
        if(mc.player == null) return "Main Menu";
        String state = "HP : " + Math.round(mc.player.getHealth() + mc.player.getAbsorptionAmount()) + " / " + Math.round(mc.player.getMaxHealth() + mc.player.getAbsorptionAmount());
        Module piston = CandyMod.m_module.getModuleWithClass(PistonAura.class);
        Module cev = CandyMod.m_module.getModuleWithClass(CevBreaker.class);
        Module civ = CandyMod.m_module.getModuleWithClass(CivBreaker.class);

        if(piston == null || cev == null || civ == null) return state;
        if(piston.isEnable) state = "Pushing crystal";
        if(cev.isEnable) state = "Breaking ceil";
        if(civ.isEnable) state = "Attacking side";
        return state;
    }
}
