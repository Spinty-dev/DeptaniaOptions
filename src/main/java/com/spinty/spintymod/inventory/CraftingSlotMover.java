package com.spinty.spintymod.inventory;

import com.spinty.spintymod.Main;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Второй подход: Перемещение слотов крафта за пределы экрана
 * Заменяет оригинальные слоты на новые с координатами за пределами экрана
 */
@Mod.EventBusSubscriber(modid = Main.MODID)
public class CraftingSlotMover {

    // Константы для перемещения слотов за экран
    private static final int OFF_SCREEN_X = -9999;
    private static final int OFF_SCREEN_Y = -9999;

    @SubscribeEvent(priority = EventPriority.HIGH) // Выполняется после CraftingSlotDisabler
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof InventoryMenu inventoryMenu) {
            Main.LOGGER.debug("Перемещение слотов крафта за пределы экрана (подход 2)");
            
            // Заменяем слоты крафта на новые с координатами за пределами экрана
            int replacedCount = 0;
            for (int i = 0; i < inventoryMenu.slots.size(); i++) {
                Slot slot = inventoryMenu.slots.get(i);
                if (slot.container instanceof CraftingContainer && !(slot instanceof OffscreenCraftingSlot)) {
                    // Сохраняем оригинальные координаты в отладочных целях
                    int originalX = slot.x;
                    int originalY = slot.y;
                    
                    // Создаем новый слот с координатами за пределами экрана
                    OffscreenCraftingSlot offscreenSlot = new OffscreenCraftingSlot(
                            slot.container, slot.getContainerSlot(), OFF_SCREEN_X, OFF_SCREEN_Y);
                    
                    // Заменяем оригинальный слот
                    inventoryMenu.slots.set(i, offscreenSlot);
                    replacedCount++;
                    
                    Main.LOGGER.debug("Слот крафта {} перемещен с ({}, {}) на ({}, {})", 
                            i, originalX, originalY, OFF_SCREEN_X, OFF_SCREEN_Y);
                }
                
                // Также перемещаем слот результата крафта
                if (slot instanceof ResultSlot && !(slot instanceof OffscreenResultSlot)) {
                    // Сохраняем оригинальные координаты
                    int originalX = slot.x;
                    int originalY = slot.y;
                    
                    // Создаем новый слот результата с координатами за пределами экрана
                    OffscreenResultSlot offscreenResultSlot = new OffscreenResultSlot(
                            slot.container, slot.getContainerSlot(), OFF_SCREEN_X, OFF_SCREEN_Y);
                    
                    // Заменяем оригинальный слот
                    inventoryMenu.slots.set(i, offscreenResultSlot);
                    replacedCount++;
                    
                    Main.LOGGER.debug("Слот результата крафта {} перемещен с ({}, {}) на ({}, {})", 
                            i, originalX, originalY, OFF_SCREEN_X, OFF_SCREEN_Y);
                }
            }
            
            if (replacedCount > 0) {
                Main.LOGGER.debug("Всего перемещено {} слотов крафта", replacedCount);
            }
        }
    }
    
    /**
     * Дополнительный обработчик для случаев, когда слоты могут быть восстановлены
     * при обновлении GUI или других событиях
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof net.minecraft.client.gui.screens.inventory.InventoryScreen) {
            var player = net.minecraft.client.Minecraft.getInstance().player;
            if (player != null && player.containerMenu instanceof InventoryMenu inventoryMenu) {
                // Повторно заменяем слоты крафта
                int replacedCount = 0;
                for (int i = 0; i < inventoryMenu.slots.size(); i++) {
                    Slot slot = inventoryMenu.slots.get(i);
                    // Перемещаем слоты крафта
                    if (slot.container instanceof CraftingContainer && !(slot instanceof OffscreenCraftingSlot)) {
                        // Создаем новый слот с координатами за пределами экрана
                        OffscreenCraftingSlot offscreenSlot = new OffscreenCraftingSlot(
                                slot.container, slot.getContainerSlot(), OFF_SCREEN_X, OFF_SCREEN_Y);
                        
                        // Заменяем оригинальный слот
                        inventoryMenu.slots.set(i, offscreenSlot);
                        replacedCount++;
                    }
                    
                    // Перемещаем слот результата крафта
                    if (slot instanceof ResultSlot && !(slot instanceof OffscreenResultSlot)) {
                        // Создаем новый слот результата с координатами за пределами экрана
                        OffscreenResultSlot offscreenResultSlot = new OffscreenResultSlot(
                                slot.container, slot.getContainerSlot(), OFF_SCREEN_X, OFF_SCREEN_Y);
                        
                        // Заменяем оригинальный слот
                        inventoryMenu.slots.set(i, offscreenResultSlot);
                        replacedCount++;
                    }
                }
                
                if (replacedCount > 0) {
                    Main.LOGGER.debug("Повторно перемещено {} слотов крафта при обновлении экрана", replacedCount);
                }
            }
        }
    }
    
    /**
     * Специальный слот с координатами за пределами экрана
     */
    private static class OffscreenCraftingSlot extends Slot {
        public OffscreenCraftingSlot(net.minecraft.world.Container container, int index, int x, int y) {
            super(container, index, x, y);
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            return false; // Для дополнительной безопасности также запрещаем размещение предметов
        }
        
        @Override
        public boolean mayPickup(Player player) {
            return false; // Запрещаем взятие предметов
        }
        
        @Override
        public boolean isActive() {
            return false; // Слот неактивен
        }
    }
    
    /**
     * Специальный слот результата с координатами за пределами экрана
     */
    private static class OffscreenResultSlot extends Slot {
        public OffscreenResultSlot(net.minecraft.world.Container container, int index, int x, int y) {
            super(container, index, x, y);
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
        
        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
        
        @Override
        public boolean isActive() {
            return false;
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