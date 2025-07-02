package com.spinty.spintymod;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class AnvilEventHandler {

    // Отключение стоимости опыта для наковальни и переименования предметов
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        // Отключаем переименование
        if (!event.getName().isEmpty() && !event.getName().equals(event.getLeft().getHoverName().getString())) {
            event.setCanceled(true);
            return;
        }
        
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        
        // Проверяем, не является ли это нашим специальным рецептом
        if (isSpecialRecipe(left, right)) {
            // Не применяем изменения стоимости для специальных рецептов
            return;
        }
        
        // Убираем стоимость опыта для всех остальных рецептов
        event.setCost(0);
        
        // Обработка предметов для ремонта
        // Если правый предмет - наш предмет для ремонта
        if (right.getItem() instanceof RepairItem repairItem && left.isDamageableItem()) {
            int maxDamage = left.getMaxDamage();
            int currentDamage = left.getDamageValue();
            
            if (currentDamage > 0) {
                ItemStack result = left.copy();
                int repairAmount = (int) (maxDamage * repairItem.getRepairPercentage());
                int newDamage = Math.max(0, currentDamage - repairAmount);
                
                result.setDamageValue(newDamage);
                event.setOutput(result);
                event.setCost(0);
                event.setMaterialCost(1);
                
                Main.LOGGER.debug("Ремонт предмета: {} на {}% (с {} до {})", 
                    left.getDisplayName().getString(), 
                    (int)(repairItem.getRepairPercentage() * 100),
                    currentDamage,
                    newDamage);
            }
        }
    }
    
    /**
     * Проверяет, является ли комбинация предметов специальным рецептом
     */
    private static boolean isSpecialRecipe(ItemStack left, ItemStack right) {
        // Проверка рецепта: кость -> костная мука
        if (left.getItem() == Items.BONE && right.isEmpty()) {
            return true;
        }
        
        // Проверка рецепта: shattered_skull -> костная мука
        var shatteredSkull = ForgeRegistries.ITEMS.getValue(new ResourceLocation("born_in_chaos_v1:shattered_skull"));
        if (shatteredSkull != null && left.getItem() == shatteredSkull && right.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    // Отмена события ремонта наковальней (для отключения опыта)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAnvilRepair(AnvilRepairEvent event) {
        // Отменяем шанс поломки наковальни
        event.setBreakChance(0);
    }
} 