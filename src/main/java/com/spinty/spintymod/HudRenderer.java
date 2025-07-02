package com.spinty.spintymod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

/**
 * Обработчик для управления рендерингом HUD-элементов
 */
@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
public class HudRenderer {
    
    // Смещение элементов HUD вниз (меньшее значение)
    private static final int Y_OFFSET = 5;
    
    // Отмена рендеринга шкалы опыта
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderExperienceBar(RenderGuiOverlayEvent.Pre event) {
        // Отменяем рендеринг шкалы опыта
        if (event.getOverlay() == VanillaGuiOverlay.EXPERIENCE_BAR.type()) {
            event.setCanceled(true);
        }
    }
    
    // Перемещение элементов HUD вниз
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onRenderHudElements(RenderGuiOverlayEvent.Pre event) {
        // Перемещаем элементы здоровья, голода, брони и кислорода вниз
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() ||
            event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type() ||
            event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type() ||
            event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()) {
            
            // Получаем текущую матрицу трансформации
            GuiGraphics guiGraphics = event.getGuiGraphics();
            
            // Сохраняем текущее состояние матрицы
            guiGraphics.pose().pushPose();
            
            // Смещаем матрицу вниз
            guiGraphics.pose().translate(0, Y_OFFSET, 0);
        }
    }
    
    // Восстанавливаем матрицу после рендеринга
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderHudElementsPost(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() ||
            event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type() ||
            event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type() ||
            event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()) {
            
            // Получаем текущую матрицу трансформации
            GuiGraphics guiGraphics = event.getGuiGraphics();
            
            // Восстанавливаем матрицу
            guiGraphics.pose().popPose();
        }
    }
} 