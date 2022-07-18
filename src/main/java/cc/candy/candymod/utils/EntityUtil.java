package cc.candy.candymod.utils;

import cc.candy.candymod.CandyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityUtil implements Util{
    public static BlockPos getFlooredPos(Entity e) {
        return new BlockPos(Math.floor(e.posX), Math.floor(e.posY), Math.floor(e.posZ));
    }

    public static Entity getPredictedPosition(Entity entity, double x) {
        if (x == 0.0) {
            return entity;
        }
        EntityPlayer e = null;
        double motionX = entity.posX - entity.lastTickPosX;
        double motionY = entity.posY - entity.lastTickPosY;
        double motionZ = entity.posZ - entity.lastTickPosZ;
        boolean shouldPredict = false;
        boolean shouldStrafe = false;
        double motion = Math.sqrt(Math.pow(motionX, 2.0) + Math.pow(motionZ, 2.0) + Math.pow(motionY, 2.0));
        if (motion > 0.1) {
            shouldPredict = true;
        }
        if (!shouldPredict) {
            return entity;
        }
        if (motion > 0.31) {
            shouldStrafe = true;
        }
        int i = 0;
        while ((double)i < x) {
            if (e == null) {
                if (EntityUtil.isOnGround(0.0, 0.0, 0.0, entity)) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                } else {
                    motionY -= 0.08;
                    motionY *= (double)0.98f;
                }
                e = EntityUtil.placeValue(motionX, motionY, motionZ, (EntityPlayer)entity);
            } else {
                if (EntityUtil.isOnGround(0.0, 0.0, 0.0, e)) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                } else {
                    motionY -= 0.08;
                    motionY *= (double)0.98f;
                }
                e = EntityUtil.placeValue(motionX, motionY, motionZ, e);
            }
            ++i;
        }
        return e;
    }

    public static boolean isOnGround(double x, double y, double z, Entity entity) {
        double d3 = y;
        List list1 = Minecraft.getMinecraft().world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(x, y, z));
        if (y != 0.0) {
            int l = list1.size();
            for (int k = 0; k < l; ++k) {
                y = ((AxisAlignedBB)list1.get(k)).calculateYOffset(entity.getEntityBoundingBox(), y);
            }
        }
        return d3 != y && d3 < 0.0;
    }

    public static EntityPlayer placeValue(double x, double y, double z, EntityPlayer entity) {
        List list1 = Minecraft.getMinecraft().world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(x, y, z));
        if (y != 0.0) {
            int l = list1.size();
            for (int k = 0; k < l; ++k) {
                y = ((AxisAlignedBB)list1.get(k)).calculateYOffset(entity.getEntityBoundingBox(), y);
            }
            if (y != 0.0) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0, y, 0.0));
            }
        }
        if (x != 0.0) {
            int l5 = list1.size();
            for (int j5 = 0; j5 < l5; ++j5) {
                x = EntityUtil.calculateXOffset(entity.getEntityBoundingBox(), x, (AxisAlignedBB)list1.get(j5));
            }
            if (x != 0.0) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(x, 0.0, 0.0));
            }
        }
        if (z != 0.0) {
            int i6 = list1.size();
            for (int k5 = 0; k5 < i6; ++k5) {
                z = EntityUtil.calculateZOffset(entity.getEntityBoundingBox(), z, (AxisAlignedBB)list1.get(k5));
            }
            if (z != 0.0) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0, 0.0, z));
            }
        }
        return entity;
    }

    public static double calculateXOffset(AxisAlignedBB other, double offsetX, AxisAlignedBB this1) {
        if (other.maxY > this1.minY && other.minY < this1.maxY && other.maxZ > this1.minZ && other.minZ < this1.maxZ) {
            double d0;
            if (offsetX > 0.0 && other.maxX <= this1.minX) {
                double d1 = this1.minX - other.maxX;
                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0 && other.minX >= this1.maxX && (d0 = this1.maxX - other.minX) > offsetX) {
                offsetX = d0;
            }
        }
        return offsetX;
    }

    public static double calculateZOffset(AxisAlignedBB other, double offsetZ, AxisAlignedBB this1) {
        if (other.maxX > this1.minX && other.minX < this1.maxX && other.maxY > this1.minY && other.minY < this1.maxY) {
            double d0;
            if (offsetZ > 0.0 && other.maxZ <= this1.minZ) {
                double d1 = this1.minZ - other.maxZ;
                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0 && other.minZ >= this1.maxZ && (d0 = this1.maxZ - other.minZ) > offsetZ) {
                offsetZ = d0;
            }
        }
        return offsetZ;
    }

    public static boolean isLiving(Entity e) {
        return e instanceof EntityLivingBase;
    }

    public static boolean isPassive(Entity e) {
        if (e instanceof EntityWolf && ((EntityWolf)((Object)e)).isAngry()) {
            return false;
        }
        if (e instanceof EntityAmbientCreature) {
            return true;
        } else if (e instanceof EntityAnimal || e instanceof EntityAgeable || e instanceof EntityTameable || e instanceof EntitySquid) {
            return true;
        }
        if (e instanceof EntityIronGolem) if (((EntityIronGolem) ((Object) e)).getAttackTarget() == null) return true;
        return false;
    }

    public static void attackEntity(final Entity entity, final boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }
    }

    public static float getHealth(final Entity entity) {
        if (entity.isEntityAlive()) {
            final EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }

    public static void setTimer(float speed) {
        mc.timer.tickLength = 50.0f / speed;
    }

    public static Block isColliding(double posX, double posY, double posZ) {
        Block block = null;
        if (mc.player != null) {
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ) : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ);
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                }
            }
        }
        return block;
    }

    public static void resetTimer() {
        mc.timer.tickLength = 50;
    }

    public static boolean isInLiquid() {
        if (mc.player != null) {
            if (mc.player.fallDistance >= 3.0f) {
                return false;
            }
            boolean inLiquid = false;
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }
                        inLiquid = true;
                    }
                }
            }
            return inLiquid;
        }
        return false;
    }

    public static boolean isFullArmor(EntityPlayer entity) {
        boolean fullArmor = true;
        int diamondItems = 0;
        boolean hasBlast = false;
        for (ItemStack is : entity.getArmorInventoryList()) {
            if (is.isEmpty()) {
                fullArmor = false;
                break;
            } else {
                if (is.getItem() == Items.DIAMOND_HELMET) diamondItems++;
                if (is.getItem() == Items.DIAMOND_CHESTPLATE) diamondItems++;
                if (is.getItem() == Items.DIAMOND_LEGGINGS) diamondItems++;
                if (is.getItem() == Items.DIAMOND_BOOTS) diamondItems++;
                NBTTagList enchants = is.getEnchantmentTagList();
                List<String> enchantments = new ArrayList<>();
                if (enchants != null) {
                    for (int index = 0; index < enchants.tagCount(); ++index) {
                        short id = enchants.getCompoundTagAt(index).getShort("id");
                        short level = enchants.getCompoundTagAt(index).getShort("lvl");
                        Enchantment enc = Enchantment.getEnchantmentByID(id);
                        if (enc != null) {
                            enchantments.add(enc.getTranslatedName(level));
                        }
                    }
                }
                if (enchantments.contains("Blast Protection IV")) hasBlast = true;
            }
        }
        return fullArmor && diamondItems == 4 && hasBlast;
    }

    public static boolean isInHole(Entity entity) {
        return EntityUtil.isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }

    public static boolean isBlockValid(BlockPos blockPos) {
        return EntityUtil.isBedrockHole(blockPos) || EntityUtil.isObbyHole(blockPos) || EntityUtil.isBothHole(blockPos);
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        BlockPos[] touchingBlocks;
        for (BlockPos pos : touchingBlocks = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = EntityUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.getBlock() == Blocks.OBSIDIAN) continue;
            return false;
        }
        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        BlockPos[] touchingBlocks;
        for (BlockPos pos : touchingBlocks = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = EntityUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.getBlock() == Blocks.BEDROCK) continue;
            return false;
        }
        return true;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        BlockPos[] touchingBlocks;
        for (BlockPos pos : touchingBlocks = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = EntityUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && (touchingState.getBlock() == Blocks.BEDROCK || touchingState.getBlock() == Blocks.OBSIDIAN))
                continue;
            return false;
        }
        return true;
    }

}
