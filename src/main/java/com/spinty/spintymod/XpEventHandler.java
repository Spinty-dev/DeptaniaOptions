package com.spinty.spintymod;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class XpEventHandler {

    // Отключение опыта от мобов
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        event.setCanceled(true);
        event.setDroppedExperience(0);
    }
    
    // Отключение получения опыта от любых источников
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        event.setCanceled(true);
        event.setAmount(0);
    }
    
    // Отключение опыта при добыче руд и других блоков
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        Player player = event.getEntity();
        if (player != null) {
            // Опыт не меняется при добыче блоков
            player.skipDropExperience();
        }
    }
    
    // Дополнительная защита от сфер опыта
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPickupXp(PlayerXpEvent.PickupXp event) {
        event.setCanceled(true);
        ExperienceOrb orb = event.getOrb();
        if (orb != null) {
            orb.value = 0;
            orb.discard();
        }
    }
} 