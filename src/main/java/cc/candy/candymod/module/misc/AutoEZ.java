package cc.candy.candymod.module.misc;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.event.events.player.PlayerDeathEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.combat.*;
import cc.candy.candymod.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoEZ extends Module {
    public Setting<Boolean> floppaGod = register(new Setting("FloppaGod" , false));

    public AutoEZ()
    {
        super("AutoEZ" , Categories.MISC , false , false);
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(nullCheck()) return;
        EntityPlayer player = event.player;
        if(player.getHealth() > 0.0F) return;
        //PistonAura
        Module piston = CandyMod.m_module.getModuleWithClass(PistonAura.class);
		Module pistonRe = CandyMod.m_module.getModuleWithClass(PistonAuraRewrite.class);
        Module pistonRe2 = CandyMod.m_module.getModuleWithClass(PistonAuraRewrite2.class);
        Module pistonRe3 = CandyMod.m_module.getModuleWithClass(PistonAuraRewrite3.class);
        if((piston.isEnable || pistonRe.isEnable || pistonRe2.isEnable || pistonRe3.isEnable)
                && (player.equals(PistonAura.target) || player.equals(PistonAuraRewrite.target) || player.equals(PistonAuraRewrite2.target) || player.equals(PistonAuraRewrite3.target)))
        {
            EZ();
            return;
        }
        Module cev = CandyMod.m_module.getModuleWithClass(CevBreaker.class);
        if(cev.isEnable && player.equals(CevBreaker.target))
        {
            EZ();
            return;
        }
        Module civ = CandyMod.m_module.getModuleWithClass(CivBreaker.class);
        if(civ.isEnable && player.equals(CivBreaker.target))
        {
            EZ();
            return;
        }
    }

    public void EZ() {
        if(floppaGod.getValue())
            sendChat("you just got nae nae'd by FloppaGod");
        else
            sendChat("you just got ez'd by candy+");
    }

    public void sendChat(String str)
    {
        mc.player.connection.sendPacket(new CPacketChatMessage(str));
    }
}
