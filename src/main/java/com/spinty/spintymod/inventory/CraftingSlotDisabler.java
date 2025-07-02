package com.spinty.spintymod.inventory;

import com.spinty.spintymod.Main;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Первый подход: Отключение слотов крафта на уровне ядра
 * Блокирует взаимодействие со слотами крафта путем замены их на нефункциональные слоты
 */
@Mod.EventBusSubscriber(modid = Main.MODID)
public class CraftingSlotDisabler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof InventoryMenu inventoryMenu) {
            Main.LOGGER.debug("Отключение слотов крафта в инвентаре игрока (подход 1)");
            
            // Находим и заменяем слоты крафта на заблокированные слоты
            for (int i = 0; i < inventoryMenu.slots.size(); i++) {
                Slot slot = inventoryMenu.slots.get(i);
                // Отключаем слоты крафта
                if (slot.container instanceof CraftingContainer) {
                    // Создаем новый слот, который не позволяет взаимодействовать с ним
                    DisabledCraftingSlot disabledSlot = new DisabledCraftingSlot(slot);
                    inventoryMenu.slots.set(i, disabledSlot);
                    Main.LOGGER.debug("Слот крафта {} заблокирован", i);
                }
                
                // Отключаем слот результата крафта
                if (slot instanceof ResultSlot) {
                    // Создаем новый слот, который не позволяет взаимодействовать с ним
                    DisabledResultSlot disabledSlot = new DisabledResultSlot(slot);
                    inventoryMenu.slots.set(i, disabledSlot);
                    Main.LOGGER.debug("Слот результата крафта {} заблокирован", i);
                }
            }
        }
    }
    
    /**
     * Специальный слот, который блокирует любое взаимодействие
     */
    private static class DisabledCraftingSlot extends Slot {
        
        public DisabledCraftingSlot(Slot originalSlot) {
            super(originalSlot.container, originalSlot.getContainerSlot(), originalSlot.x, originalSlot.y);
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            return false; // Запрещаем размещение предметов
        }
        
        @Override
        public boolean mayPickup(Player player) {
            return false; // Запрещаем взятие предметов
        }
        
        @Override
        public void onTake(Player player, ItemStack stack) {
            // Ничего не делаем при попытке взять предмет
        }
        
        @Override
        public boolean isActive() {
            return false; // Слот неактивен
        }
        
        @Override
        public void set(ItemStack stack) {
            // Игнорируем попытки установить предмет в слот
            // Не вызываем super.set(stack)
        }
    }
    
    /**
     * Специальный слот результата, который блокирует любое взаимодействие
     */
    private static class DisabledResultSlot extends Slot {
        
        public DisabledResultSlot(Slot originalSlot) {
            super(originalSlot.container, originalSlot.getContainerSlot(), originalSlot.x, originalSlot.y);
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            return false; // Запрещаем размещение предметов
        }
        
        @Override
        public boolean mayPickup(Player player) {
            return false; // Запрещаем взятие предметов
        }
        
        @Override
        public void onTake(Player player, ItemStack stack) {
            // Ничего не делаем при попытке взять предмет
        }
        
        @Override
        public boolean isActive() {
            return false; // Слот неактивен
        }
        
        @Override
        public void set(ItemStack stack) {
            // Игнорируем попытки установить предмет в слот
        }
        
        @Override
        public ItemStack getItem() {
            return ItemStack.EMPTY; // Всегда возвращаем пустой стек
        }
    }
} 