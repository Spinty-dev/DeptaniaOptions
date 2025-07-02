package com.spinty.spintymod;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

public class RepairItem extends Item {
    private final float repairPercentage;

    public RepairItem(Properties properties, float repairPercentage) {
        super(properties);
        this.repairPercentage = repairPercentage;
    }

    public float getRepairPercentage() {
        return repairPercentage;
    }
} 