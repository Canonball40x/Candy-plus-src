package cc.candy.candymod.module.combat;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.BlockUtil;
import cc.candy.candymod.utils.InventoryUtil;
import cc.candy.candymod.utils.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class SelfAnvil extends Module {
    public Setting<Float> blockDelay = register(new Setting("BlockDelay" , 0F , 25F , 0F));
    public Setting<Float> anvilDelay = register(new Setting("AnvilDelay" , 0F , 25F , 0F));
    public Setting<Boolean> packetPlace = register(new Setting("PacketPlace" , false));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch" , false));

    private int progress = 0;
    private Timer blockT , anvilT = null;
    private BlockPos base = null;

    public SelfAnvil()
    {
        super("SelfAnvil" , Categories.COMBAT , false , false);
    }

    @Override
    public void onEnable()
    {
        if(nullCheck()) return;
        if(BlockUtil.getBlock(new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ) ) != Blocks.AIR) {
            sendMessage("you are already in block! disabling...");
            disable();
        }
    }
    @Override
    public void onDisable()
    {
        reset();
    }

    @Override
    public void onTick()
    {
		if(nullCheck()) return;
        int anvil = InventoryUtil.findHotbarBlock(Blocks.ANVIL);
        int obby = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        if(anvil == -1 || obby == -1 | nullCheck()) return;

        BlockPos mypos = new BlockPos(mc.player.posX , mc.player.posY , mc.player.posZ);

        if(base == null) {
            BlockPos[] offsets = new BlockPos[]
                    {
                            new BlockPos(1, 0, 0),
                            new BlockPos(-1, 0, 0),
                            new BlockPos(0, 0, 1),
                            new BlockPos(0, 0, -1)
                    };
            for (BlockPos offset :
                    offsets) {
                BlockPos pos = mypos.add(offset.add(0 , 1 , 0));
                if (BlockUtil.canPlaceBlock(pos) ||
                        (BlockUtil.getBlock(pos) == Blocks.OBSIDIAN || BlockUtil.getBlock(pos) == Blocks.BEDROCK)) {
                    base = pos;
                }
            }
        }

        if(base == null) return;

        //obby
        if(blockT == null && progress <= 1)
        {
            blockT = new Timer();
        }
        if(progress <= 1)
        {
            if(blockT.passedX(blockDelay.getValue())) {
                setItem(obby);

                if (progress == 0) {
                    if(BlockUtil.getBlock(base.add(0, 0, 0)) == Blocks.AIR)
                        BlockUtil.placeBlock(base.add(0, 0, 0), packetPlace.getValue());
                }
                if (progress == 1) {
                    if(BlockUtil.getBlock(base.add(0, 1, 0)) == Blocks.AIR)
                        BlockUtil.placeBlock(base.add(0, 1, 0), packetPlace.getValue());
                }

                blockT = new Timer();
                progress++;
            }
        }

        //place
        if(anvilT == null && progress == 2)
        {
            anvilT = new Timer();
        }
        if(progress == 2)
        {
            if(anvilT.passedX(blockDelay.getValue())) {
                setItem(anvil);
                BlockUtil.placeBlock(mypos.add(0, 2, 0), packetPlace.getValue());
                disable();
                reset();
            }
        }
		
		restoreItem();
    }

    public void reset()
    {
        base = null;
        anvilT = null;
        blockT = null;
        progress = 0;
    }

    private EnumHand oldhand = null;
    private int oldslot = -1;

    public void setItem(int slot)
    {
        if(silentSwitch.getValue()) {
            oldhand = null;
            if(mc.player.isHandActive()) {
                oldhand = mc.player.getActiveHand();
            }
            oldslot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        }
        else {
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public void restoreItem()
    {
        if(oldslot != -1 && silentSwitch.getValue())
        {
            if(oldhand != null) {
                mc.player.setActiveHand(oldhand);
            }
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            oldslot = -1;
            oldhand = null;
        }
    }
}
