package com.spinty.spintymod;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Обработчик для рецептов на наковальне
 */
@Mod.EventBusSubscriber(modid = Main.MODID)
public class AnvilRecipeHandler {

    // Используем приоритет NORMAL, чтобы выполнять после AnvilEventHandler с приоритетом HIGHEST
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        
        // Если левый слот пустой или уже есть результат, выходим
        if (left.isEmpty() || event.getOutput() != null && !event.getOutput().isEmpty()) {
            return;
        }
        
        // Рецепт: кость -> 4 костной муки
        if (left.getItem() == Items.BONE && right.isEmpty()) {
            ItemStack result = new ItemStack(Items.BONE_MEAL, 4);
            event.setOutput(result);
            event.setCost(1); // Стоимость опыта
            event.setMaterialCost(1); // Количество потребляемых предметов
            Main.LOGGER.debug("Создан рецепт наковальни: кость -> 4 костной муки");
            return;
        }
        
        // Рецепт: shattered_skull -> 10 костной муки
        // Получаем предмет по его ID
        var shatteredSkull = ForgeRegistries.ITEMS.getValue(new ResourceLocation("born_in_chaos_v1:shattered_skull"));
        
        if (shatteredSkull != null && left.getItem() == shatteredSkull && right.isEmpty()) {
            ItemStack result = new ItemStack(Items.BONE_MEAL, 10);
            event.setOutput(result);
            event.setCost(1); // Стоимость опыта
            event.setMaterialCost(1); // Количество потребляемых предметов
            Main.LOGGER.debug("Создан рецепт наковальни: shattered_skull -> 10 костной муки");
            return;
        }
    }
} 