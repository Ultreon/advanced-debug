package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.client.menu.DebugRenderContext;
import com.ultreon.mods.advanceddebug.util.TargetUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.EntityHitResult;

import static net.minecraft.ChatFormatting.RED;

public class ItemEntityPage extends EntityPage {
    public ItemEntityPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, DebugRenderContext ctx) {
        EntityHitResult entityHit = TargetUtils.entity();
        if (entityHit.getEntity() instanceof ItemEntity entity) {
            ctx.left("Thrower", entity.getThrower());
            ctx.left("Owner", entity.getOwner());
            ctx.left("Item", entity.getItem());

            ctx.right("Attackable", entity.isAttackable());
        } else {
            // not looking at a block, or too far away from one to tell
            ctx.top(RED + "<No Entity Was Found>");
        }
    }
}
