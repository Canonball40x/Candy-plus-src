package cc.candy.candymod.managers;

import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.event.events.player.PlayerDeathEvent;
import cc.candy.candymod.hud.modules.*;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.combat.*;
import cc.candy.candymod.module.exploit.*;
import cc.candy.candymod.module.misc.*;
import cc.candy.candymod.module.movement.*;
import cc.candy.candymod.module.render.*;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModuleManager extends Manager{

    public List<Module> modules = new ArrayList<>();

    @Override
    public void load()
    {
        //combat
        register(new PistonAura());
        register(new Velocity());
        register(new Blocker());
        register(new SelfAnvil());
        register(new CevBreaker());
        register(new CivBreaker());
        register(new AutoPush());
        register(new AutoMend());
        register(new AutoCity());
        register(new PistonAuraRewrite());
        register(new PistonAuraRewrite2());
        register(new PistonAuraRewrite3());
        register(new OyveyAura());
        register(new AntiBurrow());
        register(new AutoTotem());
        register(new HoleFill());
        register(new BowSpam());
        register(new CrystalAura());
        register(new SelfAnvil2());

        //crash
        //register(new BoatCrash());
        //register(new InvalidPosCrash());
        //register(new LoginCrash());
        //register(new OffhandCrash());
        //register(new SignCrash());

        //exploit
        register(new InstantMine());
        register(new SilentPickel());
        register(new XCarry());
        register(new Burrow());
        register(new NoMiningTrace());
        register(new PingBypass());
        register(new TrapPhase());

        //misc
        register(new DonkeyNotifier());
        register(new FakePlayer());
        register(new ChatSuffix());
        register(new NoteSpam());
        register(new DiscordRPC());
        register(new PopAnnouncer());
        register(new AutoEZ());
        register(new ESP());
        register(new Refill());
        register(new AutoDrop());
        register(new Spammer());

        //movement
        register(new HoleTP());
        register(new Speed());
        register(new Blink());
        register(new TPCart());
        register(new NoWeb());
        register(new NoSlowdown());
        register(new PhaseWalk());

        //render
        register(new Afterglow());
        register(new BreadCrumbs());
        register(new ClickGUI());
        register(new BurrowESP());
        register(new NoOverlay());
        register(new CustomFov());
        register(new Notification());
        register(new CandyCrystal());
        register(new HoleESP());
        register(new CityESP());
        register(new HUDEditor());
        register(new ItemViewModel());
        register(new SmallShield());
        register(new EnchantmentColor());

        //hub
        register(new Watermark());
        register(new InventoryViewer());
        register(new ModuleList());
        register(new Welcomer());
        register(new PvPResources());
        register(new PlayerList());
        register(new CombatInfo());
        //watermark killer
        register(new PlayerModel());
        register(new TargetHUD());
    }

    public void register(Module module)
    {
        modules.add(module);
    }

    //events
    public void onUpdate()
    {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onUpdate());
    }

    public void onTick()
    {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onTick());
    }

    public void onConnect()
    {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onConnect());
    }

    public void onRender2D()
    {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onRender2D());
    }

    public void onRender3D()
    {
        modules.stream().filter(m -> m.isEnable).forEach(m ->
        {
            mc.profiler.startSection(m.name);
            m.onRender3D();
            mc.profiler.endSection();
        });
    }

    public void onRender3D(float ticks)
    {
        modules.stream().filter(m -> m.isEnable).forEach(m ->
        {
            mc.profiler.startSection(m.name);
            m.onRender3D(ticks);
            mc.profiler.endSection();
        });
    }

    public void onPacketSend(PacketEvent.Send event)
    {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onPacketSend(event));
    }

    public void onPacketReceive(PacketEvent.Receive event)
    {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onPacketReceive(event));
    }

    public void onTotemPop(EntityPlayer player)
    {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onTotemPop(player));
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
        modules.stream().filter(m -> m.isEnable).forEach(m -> m.onPlayerDeath(event));
    }

    public void onKeyInput(int key)
    {
        //module event
        modules.stream().filter(m -> m.key.getKey() == key).
                forEach(m -> m.toggle());
    }

    //utils
    public List<Module> getModulesWithCategories(Module.Categories c)
    {
        List<Module> moduleList = new ArrayList<>();

        for(Module m : modules) {
            if (m.category == c) moduleList.add(m);
        }

        return moduleList;
    }

    public Module getModuleWithName(String name)
    {
        Module r = null;
        for(Module m : modules)
        {
            if(Objects.equals(m.name, name)) r = m;
        }

        return r;
    }

    public Module getModuleWithClass(Class clazz)
    {
        Module r = null;
        for(Module m : modules)
        {
            if(m.getClass() == clazz) r = m;
        }

        return r;
    }


}
