package com.spinty.spintymod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

/**
 * Обработчик для отображения пустой шкалы брони
 */
@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
public class EmptyArmorBarRenderer {
    
    private static final ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.ARMOR_LEVEL.type()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        if (player == null || player.getArmorValue() > 0) {
            return;
        }
        
        // Проверяем, что игрок не в креативе и не в спектаторе
        GameType gameType = mc.gameMode.getPlayerMode();
        if (gameType == GameType.CREATIVE || gameType == GameType.SPECTATOR) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        
        // Координаты для отображения шкалы брони
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        int left = width / 2 - 91;
        
        // Вычисляем количество строк здоровья и корректируем позицию шкалы брони
        int maxHealth = (int) Math.ceil(player.getMaxHealth());
        int rows = Math.max(1, (int) Math.ceil(maxHealth / 20.0));
        
        // Нелинейное смещение с учетом "сплющивания" строк здоровья
        int offset;
        if (rows <= 1) {
            offset = 0;
        } else if (rows == 2) {
            offset = 10;
        } else if (rows == 3) {
            offset = 18;
        } else if (rows == 4) {
            offset = 25;
        } else if (rows == 5) {
            offset = 28;
        } else if (rows == 6) {
            offset = 30;
        } else if (rows == 7) {
            offset = 32;
        } else if (rows == 8) {
            offset = 30; // Уменьшено на 4 пикселя от ожидаемых 34 (32+2)
        } else if (rows == 9) {
            offset = 30; // Уменьшено еще на 2 пикселя от ожидаемых 36 (34+2)
        } else {
            // Для 10+ строк - сжатие прекращается
            offset = 30; // Фиксированное значение для всех последующих строк
        }
        
        int top = height - 49 - offset;
        
        // Отрисовка пустых иконок брони
        for (int i = 0; i < 10; i++) {
            int x = left + i * 8;
            int y = top;
            
            // Координаты текстуры пустой иконки брони (в текстуре icons.png)
            int textureX = 16;
            int textureY = 9;
            
            // Отрисовка пустой иконки брони
            guiGraphics.blit(ICONS, x, y, textureX, textureY, 9, 9);
        }
    }
} 