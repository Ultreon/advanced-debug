package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.util.TargetUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.RED;

public class EntityPage extends DebugPage {
    private final Minecraft mc = Minecraft.getInstance();

    public EntityPage() {
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, IDebugRenderContext ctx) {
        EntityHitResult entityHit = TargetUtils.entity();
        if (entityHit != null) {
            Entity entity = entityHit.getEntity();
            EntityType<? extends Entity> type = entity.getType();
            Component customName = entity.getCustomName();

            ctx.left(GRAY + "Entity Type");
            ctx.left("Height", type.getHeight());
            ctx.left("Name", type.getDescription().getString());
            ctx.left("Size", getSize(type.getDimensions().width, type.getDimensions().height));
            ctx.left("Internal Name", type.arch$registryName());
            ctx.left();

            ctx.left(GRAY + "Entity");
            ctx.left("Air", entity.getAirSupply());
            ctx.left("Max Air", entity.getMaxAirSupply());
            ctx.left("Eye Height", entity.getEyeHeight());
            ctx.left("Look Angle", entity.getLookAngle());
            ctx.left("Riding Entity", entity.getVehicle());
            ctx.left("Riding Offset", entity.getMyRidingOffset());
            ctx.left("Entity UUID", entity.getUUID());
            ctx.left("Entity ID", entity.getId());
            ctx.left("Entity Name", entity.getName().getString());
            ctx.left("Custom Name", customName == null ? null : customName.getString());
            ctx.left("Nametag Visible", entity.isCustomNameVisible());
            ctx.left("Silent", entity.isSilent());
            ctx.left("Attackable", entity.isAttackable());
            ctx.left("Invulnerable", entity.isInvulnerable());
            ctx.left("Invisible", entity.isInvisible());
            ctx.left("Sneaking", entity.isCrouching());
            ctx.left("Sprinting", entity.isSprinting());
            ctx.left("Swimming", entity.isSwimming());
            ctx.left("Pushable", entity.isPushable());
            ctx.left("Pushed By Fluid", entity.isPushedByFluid());

            if (entity instanceof LivingEntity living) {
                ctx.right(GRAY + "Living Entity");
                ctx.right("Health", living.getHealth());
                ctx.right("Max. Health", living.getMaxHealth());
                ctx.right("Main Hand", living.getItemInHand(InteractionHand.MAIN_HAND));
                ctx.right("Off Hand", living.getItemInHand(InteractionHand.OFF_HAND));
                ctx.right("Arrow Count", living.getArrowCount());
                ctx.right("Absorption Amount", living.getAbsorptionAmount());
                ctx.right("Speed", living.getSpeed());
                ctx.right("Last Used Hand", living.getUsedItemHand());
                ctx.right("Attacking Entity", living.getKillCredit());
                ctx.right("Item Usage Ticks Remaining", living.getUseItemRemainingTicks());
                ctx.right("Last Attacked Mob", living.getLastHurtMob());
                ctx.right("Rope Position", living.getRopeHoldPosition(mc.getFrameTime()));
                ctx.right("Pose", living.getPose());
                ctx.right("Scale", living.getScale());
                ctx.right("Armor Value", living.getArmorValue());
            } else if (entity instanceof ItemEntity itemEntity) {
                ctx.right(GRAY + "Item Entity");
                ctx.right("Owner", itemEntity.getOwner());
                ctx.right("Item", itemEntity.getItem());
                ctx.right("Spin", itemEntity.getSpin(minecraft.getFrameTime()));
                ctx.right("Age", itemEntity.getAge());
            }
        } else {
            // not looking at a block, or too far away from one to tell
            ctx.top(RED + "<No Entity Was Found>");
        }
    }
}
