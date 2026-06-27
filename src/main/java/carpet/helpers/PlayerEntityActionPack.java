package carpet.helpers;

import carpet.SharedConstants;
import carpet.mixins.accessor.SelectSlotC2SPacketA;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerHandActionC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectSlotC2SPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.List;

import static carpet.utils.PacketHelper.playerHandActionC2SPacket;

public class PlayerEntityActionPack {
    private ServerPlayerEntity player;

    private boolean doesAttack;
    private int attackInterval;
    private int attackCooldown;

    private boolean doesUse;
    private int useInterval;
    private int useCooldown;

    private boolean doesJump;
    private int jumpInterval;
    private int jumpCooldown;

    private BlockPos currentBlock = new BlockPos(-1, -1, -1);
    private int blockHitDelay;
    private boolean isHittingBlock;
    private float curBlockDamageMP;

    private boolean sneaking;
    private boolean sprinting;
    private float forward;
    private float strafing;

    public PlayerEntityActionPack(ServerPlayerEntity playerIn) {
        player = playerIn;
        stop();
    }

    public void copyFrom(PlayerEntityActionPack other) {
        doesAttack = other.doesAttack;
        attackInterval = other.attackInterval;
        attackCooldown = other.attackCooldown;

        doesUse = other.doesUse;
        useInterval = other.useInterval;
        useCooldown = other.useCooldown;

        doesJump = other.doesJump;
        jumpInterval = other.jumpInterval;
        jumpCooldown = other.jumpCooldown;

        currentBlock = other.currentBlock;
        blockHitDelay = other.blockHitDelay;
        isHittingBlock = other.isHittingBlock;
        curBlockDamageMP = other.curBlockDamageMP;

        sneaking = other.sneaking;
        sprinting = other.sprinting;
        forward = other.forward;
        strafing = other.strafing;
    }

    @Override
    public String toString() {
        return (doesAttack ? "t" : "f") + ":" +
                attackInterval + ":" +
                attackCooldown + ":" +
                (doesUse ? "t" : "f") + ":" +
                useInterval + ":" +
                useCooldown + ":" +
                (doesJump ? "t" : "f") + ":" +
                jumpInterval + ":" +
                jumpCooldown + ":" +
                (sneaking ? "t" : "f") + ":" +
                (sprinting ? "t" : "f") + ":" +
                forward + ":" +
                strafing;
    }

    public void fromString(String s) {
        String[] list = s.split(":");
        doesAttack = list[0].equals("t");
        attackInterval = Integer.parseInt(list[1]);
        attackCooldown = Integer.parseInt(list[2]);
        doesUse = list[3].equals("t");
        useInterval = Integer.parseInt(list[4]);
        useCooldown = Integer.parseInt(list[5]);
        doesJump = list[6].equals("t");
        jumpInterval = Integer.parseInt(list[7]);
        jumpCooldown = Integer.parseInt(list[8]);
        sneaking = list[9].equals("t");
        sprinting = list[10].equals("t");
        forward = Float.parseFloat(list[11]);
        strafing = Float.parseFloat(list[12]);
    }

    public PlayerEntityActionPack setAttack(int interval, int offset) {
        if (interval < 1) {
            carpet.SharedConstants.LOG.error("attack interval needs to be positive");
            return this;
        }
        this.doesAttack = true;
        this.attackInterval = interval;
        this.attackCooldown = interval + offset;
        return this;
    }

    public PlayerEntityActionPack setUse(int interval, int offset) {
        if (interval < 1) {
            SharedConstants.LOG.error("use interval needs to be positive");
            return this;
        }
        this.doesUse = true;
        this.useInterval = interval;
        this.useCooldown = interval + offset;
        return this;
    }

    public PlayerEntityActionPack setUseForever() {
        this.doesUse = true;
        this.useInterval = 1;
        this.useCooldown = 1;
        return this;
    }

    public PlayerEntityActionPack setAttackForever() {
        this.doesAttack = true;
        this.attackInterval = 1;
        this.attackCooldown = 1;
        return this;
    }

    public PlayerEntityActionPack setJump(int interval, int offset) {
        if (interval < 1) {
            SharedConstants.LOG.error("jump interval needs to be positive");
            return this;
        }
        this.doesJump = true;
        this.jumpInterval = interval;
        this.jumpCooldown = interval + offset;
        return this;
    }

    public PlayerEntityActionPack setJumpForever() {
        this.doesJump = true;
        this.jumpInterval = 1;
        this.jumpCooldown = 1;
        return this;
    }

    public PlayerEntityActionPack setSneaking(boolean doSneak) {
        sneaking = doSneak;
        player.setSneaking(doSneak);
        if (sprinting && sneaking)
            setSprinting(false);
        return this;
    }

    public PlayerEntityActionPack setSprinting(boolean doSprint) {
        sprinting = doSprint;
        player.setSprinting(doSprint);
        if (sneaking && sprinting)
            setSneaking(false);
        return this;
    }

    public PlayerEntityActionPack setForward(float value) {
        forward = value;
        return this;
    }

    public PlayerEntityActionPack setStrafing(float value) {
        strafing = value;
        return this;
    }

    public boolean look(String where) {
        switch (where) {
            case "north": look(180.0f, 0.0F); return true;
            case "south": look(0.0F, 0.0F); return true;
            case "east": look(-90.0F, 0.0F); return true;
            case "west": look(90.0F, 0.0F); return true;
            case "up": look(player.yaw, -90.0F); return true;
            case "down": look(player.yaw, 90.0F); return true;
        }
        return false;
    }

    public PlayerEntityActionPack look(float yaw, float pitch) {
        setPlayerRotation(yaw, MathHelper.clamp(pitch, -90.0F, 90.0F));
        return this;
    }

    public boolean turn(String where) {
        switch (where) {
            case "left": turn(-90.0F, 0.0F); return true;
            case "right": turn(90.0F, 0.0F); return true;
            case "back": turn(180.0F, 0.0F); return true;
        }
        return false;
    }

    public PlayerEntityActionPack turn(float yaw, float pitch) {
        setPlayerRotation(player.yaw + yaw, MathHelper.clamp(player.pitch + pitch, -90.0F, 90.0F));
        return this;
    }

    public PlayerEntityActionPack stop() {
        this.doesUse = false;
        this.doesAttack = false;
        this.doesJump = false;
        resetBlockRemoving();
        setSneaking(false);
        setSprinting(false);
        forward = 0.0F;
        strafing = 0.0F;
        player.setJumping(false);
        return this;
    }

    public void swapHands() {
        player.networkHandler.handlePlayerHandAction(playerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.SWAP_HELD_ITEMS, null, null));
    }

    public void setSlot(int slot) {
        SelectSlotC2SPacket packet = new SelectSlotC2SPacket();
        ((SelectSlotC2SPacketA) packet).setSlot(slot - 1);
        player.networkHandler.handleSelectSlot(packet);
    }

    public void dropItem(int slot, boolean dropAll) {
        PlayerInventory inv = player.inventory;
        if (slot == -2) { // all
            for (int i = inv.getSize(); i >= 0; i--) {
                if (!inv.getStack(i).isEmpty()) {
                    player.dropItem(inv.removeStack(i, dropAll ? inv.getStack(i).getSize() : 1), false, true);
                }
            }
        } else if (slot == -1) { // mainhand == inv.selectedSlot, but just send packet anyway
            if (dropAll) {
                player.networkHandler.handlePlayerHandAction(playerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.DROP_ALL_ITEMS, null, null));
            } else {
                player.networkHandler.handlePlayerHandAction(playerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.DROP_ITEM, null, null));
            }
        } else {
            player.dropItem(inv.removeStack(slot, dropAll ? inv.getStack(slot).getSize() : 1), false, true);
        }
    }

    public void mount() {
        List<Entity> entities = player.world.getEntities(
                player,
                player.getShape().expand(3.0D, 1.0D, 3.0D),
                other -> !(other instanceof PlayerEntity)
        );
        if (entities.isEmpty()) {
            return;
        }
        Entity closest = entities.get(0);
        double distance = player.getSquaredDistanceTo(closest);
        for (Entity e : entities) {
            double dd = player.getSquaredDistanceTo(e);
            if (dd < distance) {
                distance = dd;
                closest = e;
            }
        }
        player.startRiding(closest, true);
    }

    public void dismount() {
        player.stopRiding();
    }

    public void onUpdate() {
        if (doesJump) {
            if (--jumpCooldown == 0) {
                jumpCooldown = jumpInterval;
                player.setJumping(true);
            } else {
                player.setJumping(false);
            }
        }

        boolean used = false;

        if (doesUse && (--useCooldown) == 0) {
            useCooldown = useInterval;
            used = useOnce();
        }
        if (doesAttack) {
            if ((--attackCooldown) == 0) {
                attackCooldown = attackInterval;
                if (!(used)) attackOnce();
            } else {
                resetBlockRemoving();
            }
        }
        if (forward != 0.0F) {
            player.forwardSpeed = forward * (sneaking ? 0.3F : 1.0F);
        }
        if (strafing != 0.0F) {
            player.sidewaysSpeed = strafing * (sneaking ? 0.3F : 1.0F);
        }
    }

    public void jumpOnce() {
        if (player.onGround) {
            player.jump();
        }
    }

    public void attackOnce() {
        HitResult raytraceresult = mouseOver();
        if (raytraceresult == null) return;

        switch (raytraceresult.type) {
            case ENTITY:
                player.attack(raytraceresult.entity);
                player.swingHand(InteractionHand.MAIN_HAND);
                break;
            case MISS:
                break;
            case BLOCK:
                BlockPos blockpos = raytraceresult.getPos();
                if (player.world.getBlockState(blockpos).getMaterial() != Material.AIR) {
                    onPlayerDamageBlock(blockpos, raytraceresult.face.getOpposite());
                    player.swingHand(InteractionHand.MAIN_HAND);
                    break;
                }
        }
    }

    public boolean useOnce() {
        HitResult raytraceresult = mouseOver();
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack itemstack = player.getHandStack(hand);
            if (raytraceresult != null) {
                switch (raytraceresult.type) {
                    case ENTITY:
                        Entity target = raytraceresult.entity;
                        Vec3d vec3d = new Vec3d(
                                raytraceresult.offset.x - target.x,
                                raytraceresult.offset.y - target.y,
                                raytraceresult.offset.z - target.z);

                        boolean flag = player.canSee(target);
                        double d0 = 36.0D;

                        if (!flag) {
                            d0 = 9.0D;
                        }

                        if (player.getSquaredDistanceTo(target) < d0) {
                            InteractionResult res = player.interact(target, hand);
                            if (res == InteractionResult.SUCCESS) {
                                return true;
                            }
                            res = target.interact(player, vec3d, hand);
                            if (res == InteractionResult.SUCCESS) {
                                return true;
                            }
                        }
                        break;
                    case MISS:
                        break;
                    case BLOCK:
                        BlockPos blockpos = raytraceresult.getPos();

                        if (player.getSourceWorld().getBlockState(blockpos).getMaterial() != Material.AIR) {
                            if (itemstack.isEmpty())
                                continue;
                            float x = (float) raytraceresult.offset.x;
                            float y = (float) raytraceresult.offset.y;
                            float z = (float) raytraceresult.offset.z;

                            InteractionResult res = player.interactionManager.useBlock(
                                    player, player.getSourceWorld(),
                                    itemstack, hand, blockpos, raytraceresult.face,
                                    x, y, z);
                            if (res == InteractionResult.SUCCESS) {
                                player.swingHand(hand);
                                return true;
                            }
                        }
                }
            }
            InteractionResult res = player.interactionManager.useItem(player, player.getSourceWorld(), itemstack, hand);
            if (res == InteractionResult.SUCCESS) {
                return true;
            }
        }
        return false;
    }

    private HitResult rayTraceBlocks(double blockReachDistance) {
        Vec3d eyeVec = player.getEyePosition(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d pointVec = eyeVec.add(lookVec.x * blockReachDistance, lookVec.y * blockReachDistance, lookVec.z * blockReachDistance);
        return player.world.rayTrace(eyeVec, pointVec, false, false, true);
    }

    public HitResult mouseOver() {
        World world = player.getSourceWorld();
        if (world == null) {
            return null;
        }
        HitResult result = null;

        Entity pointedEntity = null;
        double reach = player.isCreative() ? 5.0D : 4.5D;
        result = rayTraceBlocks(reach);
        Vec3d eyeVec = player.getEyePosition(1.0F);
        boolean flag = !player.isCreative();
        if (player.isCreative()) reach = 6.0D;
        double extendedReach = reach;

        if (result != null) {
            extendedReach = result.offset.distanceTo(eyeVec);
            if (world.getBlockState(result.getPos()).getMaterial() == Material.AIR)
                result = null;
        }

        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d pointVec = eyeVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        Vec3d hitVec = null;
        List<Entity> list = world.getEntities(
                player,
                player.getShape().expand(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach).expand(1.0D, 1.0D, 1.0D),
                entity -> entity != null && entity.hasCollision()
        );
        double d2 = extendedReach;

        for (Entity entity : list) {
            Box axisalignedbb = entity.getShape().expand(entity.getExtraHitboxSize());
            HitResult raytraceresult = axisalignedbb.clip(eyeVec, pointVec);

            if (axisalignedbb.contains(eyeVec)) {
                if (d2 >= 0.0D) {
                    pointedEntity = entity;
                    hitVec = raytraceresult == null ? eyeVec : raytraceresult.offset;
                    d2 = 0.0D;
                }
            } else if (raytraceresult != null) {
                double d3 = eyeVec.distanceTo(raytraceresult.offset);

                if (d3 < d2 || d2 == 0.0D) {
                    if (entity.getVehicle() == player.getVehicle()) {
                        if (d2 == 0.0D) {
                            pointedEntity = entity;
                            hitVec = raytraceresult.offset;
                        }
                    } else {
                        pointedEntity = entity;
                        hitVec = raytraceresult.offset;
                        d2 = d3;
                    }
                }
            }
        }

        if (pointedEntity != null && flag && eyeVec.distanceTo(hitVec) > 3.0D) {
            pointedEntity = null;
            result = new HitResult(HitResult.Type.MISS, hitVec, null, new BlockPos(hitVec));
        }

        if (pointedEntity != null && (d2 < extendedReach || result == null)) {
            result = new HitResult(pointedEntity, hitVec);
        }

        return result;
    }

    public boolean clickBlock(BlockPos loc, Direction face) {
        World world = player.getSourceWorld();
        if (player.interactionManager.getGameMode() != GameMode.ADVENTURE) {
            if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
                return false;
            }

            if (!player.abilities.canModifyWorld) {
                ItemStack itemstack = player.getMainHandStack();

                if (itemstack.isEmpty()) {
                    return false;
                }

                if (!itemstack.hasMineBlockOverride(world.getBlockState(loc).getBlock())) {
                    return false;
                }
            }
        }

        if (!world.getWorldBorder().contains(loc)) {
            return false;
        } else {
            if (player.interactionManager.getGameMode() == GameMode.CREATIVE) {
                player.networkHandler.handlePlayerHandAction(playerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.START_DESTROY_BLOCK, loc, face));
                clickBlockCreative(world, loc, face);
                this.blockHitDelay = 5;
            } else if (!this.isHittingBlock || !(currentBlock.equals(loc))) {
                if (this.isHittingBlock) {
                    player.networkHandler.handlePlayerHandAction(playerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBlock, face));
                }

                BlockState blockstate = world.getBlockState(loc);
                player.networkHandler.handlePlayerHandAction(playerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.START_DESTROY_BLOCK, loc, face));
                boolean flag = blockstate.getMaterial() != Material.AIR;

                if (flag && this.curBlockDamageMP == 0.0F) {
                    blockstate.getBlock().startMining(world, loc, player);
                }

                if (flag && world.getBlockState(loc).getMiningSpeed(player, world, loc) >= 1.0F) {
                    this.onPlayerDestroyBlock(loc);
                } else {
                    this.isHittingBlock = true;
                    this.currentBlock = loc;
                    this.curBlockDamageMP = 0.0F;
                    world.updateBlockMiningProgress(player.getNetworkId(), this.currentBlock, (int) (this.curBlockDamageMP * 10.0F) - 1);
                }
            }

            return true;
        }
    }

    private void clickBlockCreative(World world, BlockPos pos, Direction facing) {
        if (!world.extinguishFire(player, pos, facing)) {
            onPlayerDestroyBlock(pos);
        }
    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, Direction directionFacing) {
        if (this.blockHitDelay > 0) {
            --this.blockHitDelay;
            return true;
        }
        World world = player.getSourceWorld();
        if (player.interactionManager.getGameMode() == GameMode.CREATIVE && world.getWorldBorder().contains(posBlock)) {
            this.blockHitDelay = 5;
            player.networkHandler.handlePlayerHandAction(
                    playerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.START_DESTROY_BLOCK, posBlock, directionFacing));
            clickBlockCreative(world, posBlock, directionFacing);
            return true;
        } else if (posBlock.equals(currentBlock)) {
            BlockState blockstate = world.getBlockState(posBlock);

            if (blockstate.getMaterial() == Material.AIR) {
                this.isHittingBlock = false;
                return false;
            } else {
                this.curBlockDamageMP += blockstate.getMiningSpeed(player, world, posBlock);

                if (this.curBlockDamageMP >= 1.0F) {
                    this.isHittingBlock = false;
                    player.networkHandler.handlePlayerHandAction(playerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
                    this.onPlayerDestroyBlock(posBlock);
                    this.curBlockDamageMP = 0.0F;
                    this.blockHitDelay = 5;
                }

                //send to all, even the breaker
                world.updateBlockMiningProgress(-1, this.currentBlock, (int) (this.curBlockDamageMP * 10.0F) - 1);
                return true;
            }
        } else {
            return this.clickBlock(posBlock, directionFacing);
        }
    }

    private boolean onPlayerDestroyBlock(BlockPos pos) {
        World world = player.getSourceWorld();
        if (player.interactionManager.getGameMode() != GameMode.ADVENTURE) {
            if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
                return false;
            }

            if (!player.abilities.canModifyWorld) {
                ItemStack itemstack = player.getMainHandStack();

                if (itemstack.isEmpty()) {
                    return false;
                }

                if (!itemstack.hasMineBlockOverride(world.getBlockState(pos).getBlock())) {
                    return false;
                }
            }
        }

        if (player.interactionManager.getGameMode() == GameMode.CREATIVE
                && !player.getMainHandStack().isEmpty()
                && player.getMainHandStack().getItem() instanceof SwordItem) {
            return false;
        } else {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if ((block instanceof CommandBlock || block instanceof StructureBlock) && !player.canUseMasterBlocks()) {
                return false;
            } else if (blockState.getMaterial() == Material.AIR) {
                return false;
            } else {
                world.doEvent(2001, pos, Block.serialize(blockState));
                block.beforeMinedByPlayer(world, pos, blockState, player);
                boolean flag = world.setBlockState(pos, Blocks.AIR.defaultState(), 11);

                if (flag) {
                    block.onBroken(world, pos, blockState);
                }

                this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());

                if (!(player.interactionManager.getGameMode() == GameMode.CREATIVE)) {
                    ItemStack itemstack1 = player.getMainHandStack();

                    if (!itemstack1.isEmpty()) {
                        itemstack1.mineBlock(world, blockState, pos, player);

                        if (itemstack1.isEmpty()) {
                            player.setHandStack(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                }

                return flag;
            }
        }
    }

    public void resetBlockRemoving() {
        if (this.isHittingBlock) {
            player.networkHandler.handlePlayerHandAction(playerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBlock, Direction.DOWN));
            this.isHittingBlock = false;
            this.curBlockDamageMP = 0.0F;
            player.getSourceWorld().updateBlockMiningProgress(player.getNetworkId(), this.currentBlock, -1);
            player.resetLastAttackedTicks();
            this.currentBlock = new BlockPos(-1, -1, -1);
        }
    }

    private void setPlayerRotation(float yaw, float pitch) {
        player.yaw = yaw % 360.0F;
        player.pitch = pitch % 360.0F;
    }
}
