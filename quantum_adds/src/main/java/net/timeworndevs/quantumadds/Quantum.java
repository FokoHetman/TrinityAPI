package net.timeworndevs.quantumadds;

import com.google.gson.JsonArray;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.timeworndevs.quantumadds.block.ModBlocks;
import net.timeworndevs.quantumadds.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.timeworndevs.quantumadds.util.ParseJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static net.minecraft.server.command.CommandManager.*;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Quantum implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "quantumadds";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier locate(String name) {
        return new Identifier(MOD_ID, name);
    }

    public static final ItemGroup RADIATION = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.GEIGER_COUNTER))
            .displayName(Text.translatable("itemGroup.quantum.radiation"))
            .entries((context, entries) -> {
            })
            .build();
    public static final ItemGroup BUILD_BLOCKS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.NUCLEAR_WASTE))
            .displayName(Text.translatable("itemGroup.quantum.building_blocks"))
            .entries((context, entries) -> {
            })
            .build();
    public static final ItemGroup QUANTUM = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.NUCLEAR_WASTE))
            .displayName(Text.translatable("itemGroup.quantum.quantum"))
            .entries((context, entries) -> {
            })
            .build();


    /*public Identifier getFabricId() {
        return new Identifier(Quantum.MOD_ID, "radiation_data");
    }
    public void reload(ResourceManager manager) {
        Map<Identifier, Resource> data = manager.findResources("radiation_data", path -> path.toString().endsWith(".json"));

        BiConsumer<Identifier, Resource> read = (i, resource) -> {
            Quantum.LOGGER.info(i + resource.toString());
        };
        data.forEach(read);
    }*/
    public static Map<String, JsonArray> radiation_data;

    @Override
    public void onInitialize() {
        

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("clearrad")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    // For versions below 1.19, replace "Text.literal" with "new LiteralText".
                    // For versions below 1.20, remode "() ->" directly.
                    context.getSource().sendFeedback(() -> Text.literal("Clearing.."), true);
                    return 1;
                })));
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener((new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("tutorial", "my_resources");
            }

            @Override
            public void reload(ResourceManager manager) {
                Map<Identifier, Resource> data = manager.findResources("radiation_data", path -> path.toString().endsWith(".json"));

                BiConsumer<Identifier, Resource> read = (i, resource) -> {
                    try(InputStream stream = manager.getResource(i).get().getInputStream()) {
                        Scanner s = new Scanner(stream).useDelimiter("\\A");
                        String result = s.hasNext() ? s.next() : "";

                        radiation_data = ParseJson.parseJson(result);

                    } catch(Exception e) {

                        LOGGER.error("Error occurred while loading resource json" + i.toString(), e);
                    }
                };
                data.forEach(read);
            }
        }));

        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Computing wave-functions...");
        ModBlocks.registerBlocks();
        ModBlocks.registerBlockItems();
        LOGGER.info("Analyzing external dimensions...");
        ModItems.registerItems();
        //ModRecipes.registerRecipes();
        LOGGER.info("Testing radiation...");
        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "radiation"), RADIATION);
        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "building_blocks"), BUILD_BLOCKS);
        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "quantum"), QUANTUM);
        LOGGER.info("Wormhole established!");
    }
}