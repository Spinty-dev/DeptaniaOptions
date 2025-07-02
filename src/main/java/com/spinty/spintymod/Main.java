package com.spinty.spintymod;

import com.spinty.spintymod.inventory.CraftingResultBlocker;
import com.spinty.spintymod.inventory.CraftingSlotDisabler;
import com.spinty.spintymod.inventory.CraftingSlotMover;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public class Main
{
    public static final String MODID = "spintymod";
    public static final String MOD_VERSION = "1.3.11";
    public static final Logger LOGGER = LogManager.getLogger();
    
    // Регистр для предметов
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    
    // Регистрация предметов для ремонта
    public static final RegistryObject<Item> REPAIR_ITEM_25 = ITEMS.register("repair_item_25", 
            () -> new RepairItem(new Item.Properties(), 0.25f));
    
    public static final RegistryObject<Item> REPAIR_ITEM_100 = ITEMS.register("repair_item_100", 
            () -> new RepairItem(new Item.Properties(), 1.0f));
    
    // Регистр для креатив таба
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    // Креатив таб для предметов мода
    public static final RegistryObject<CreativeModeTab> SPINTY_TAB = CREATIVE_MODE_TABS.register("spinty_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.spintymod"))
                    .icon(() -> new ItemStack(REPAIR_ITEM_100.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(REPAIR_ITEM_25.get());
                        output.accept(REPAIR_ITEM_100.get());
                    })
                    .build());
    
    public Main()
    {
        LOGGER.info("Инициализация мода DeptaniaOptions версии " + MOD_VERSION);
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Регистрируем предметы
        ITEMS.register(modEventBus);
        
        // Регистрируем креатив таб
        CREATIVE_MODE_TABS.register(modEventBus);
        
        // Регистрируем обработчики событий
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new XpEventHandler());
        MinecraftForge.EVENT_BUS.register(new HudRenderer());
        // DeathHandler отключен
        
        // Обработчики для отключения крафта в инвентаре игрока регистрируются автоматически
        // через аннотацию @Mod.EventBusSubscriber
        LOGGER.info("Активированы три способа отключения крафта в инвентаре игрока");
        
        // Логирование добавленных рецептов наковальни
        LOGGER.info("Добавлены рецепты наковальни:");
        LOGGER.info(" - Кость -> 4 костной муки");
        LOGGER.info(" - born_in_chaos_v1:shattered_skull -> 10 костной муки");
        
        LOGGER.info("Мод DeptaniaOptions успешно загружен");
    }
} 