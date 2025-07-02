package com.spinty.spintymod.inventory;

import com.spinty.spintymod.Main;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Третий подход: Блокировка результатов крафта
 * Отменяет события крафта и очищает результаты крафта
 */
@Mod.EventBusSubscriber(modid = Main.MODID)
public class CraftingResultBlocker {

    // Счетчик тиков для оптимизации проверок
    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL = 5; // Проверять каждые 5 тиков

    /**
     * Отменяем событие крафта предмета
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        // Проверяем, что это крафт в инвентаре игрока (не в верстаке)
        if (event.getEntity().containerMenu instanceof net.minecraft.world.inventory.InventoryMenu) {
            Main.LOGGER.debug("Блокировка результата крафта в инвентаре игрока (подход 3)");
            
            // Отменить крафт нельзя напрямую, но можно очистить результат
            if (event.getCrafting() != null && !event.getCrafting().isEmpty()) {
                // Логируем заблокированный предмет
                Main.LOGGER.debug("Заблокирован крафт предмета: {}", 
                        event.getCrafting().getDisplayName().getString());
                
                // Очищаем слот результата крафта
                clearCraftingResult(event.getEntity());
            }
        }
    }
    
    /**
     * Заменяем слот результата крафта на неактивный слот
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof net.minecraft.world.inventory.InventoryMenu inventoryMenu) {
            Main.LOGGER.debug("Отключение слота результата крафта в инвентаре игрока");
            
            // Ищем и заменяем слот результата крафта
            for (int i = 0; i < inventoryMenu.slots.size(); i++) {
                Slot slot = inventoryMenu.slots.get(i);
                if (slot instanceof ResultSlot) {
                    // Заменяем на неактивный слот
                    DisabledResultSlot disabledSlot = new DisabledResultSlot(slot);
                    inventoryMenu.slots.set(i, disabledSlot);
                    Main.LOGGER.debug("Слот результата крафта {} отключен", i);
                    break;
                }
            }
        }
    }
    
    /**
     * Дополнительный обработчик для проверки и очистки результата крафта
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Оптимизация: проверяем только каждые CHECK_INTERVAL тиков
            tickCounter++;
            if (tickCounter % CHECK_INTERVAL != 0) {
                return;
            }
            
            if (event.player.containerMenu instanceof net.minecraft.world.inventory.InventoryMenu) {
                // Периодически проверяем и очищаем результат крафта
                clearCraftingResult(event.player);
            }
        }
    }
    
    /**
     * Очищает слот результата крафта в инвентаре игрока
     */
    private static void clearCraftingResult(Player player) {
        if (player.containerMenu instanceof net.minecraft.world.inventory.InventoryMenu inventoryMenu) {
            // Ищем слот результата крафта
            for (int i = 0; i < inventoryMenu.slots.size(); i++) {
                if (inventoryMenu.slots.get(i) instanceof ResultSlot || 
                    inventoryMenu.slots.get(i) instanceof DisabledResultSlot) {
                    // Проверяем, есть ли в слоте предмет
                    ItemStack currentStack = inventoryMenu.slots.get(i).getItem();
                    if (!currentStack.isEmpty()) {
                        Main.LOGGER.debug("Очищен результат крафта: {}", 
                                currentStack.getDisplayName().getString());
                        
                        // Очищаем слот результата крафта
                        inventoryMenu.slots.get(i).set(ItemStack.EMPTY);
                    }
                    break;
                }
            }
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
            // Не вызываем super.set(stack)
        }
        
        @Override
        public ItemStack getItem() {
            return ItemStack.EMPTY; // Всегда возвращаем пустой стек
        }
    }
} 