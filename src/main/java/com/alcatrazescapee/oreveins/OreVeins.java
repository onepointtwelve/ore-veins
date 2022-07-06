/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins;


import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.alcatrazescapee.oreveins.cmd.CommandClearWorld;
import com.alcatrazescapee.oreveins.cmd.CommandFindVeins;
import com.alcatrazescapee.oreveins.cmd.CommandVeinInfo;
import com.alcatrazescapee.oreveins.vein.VeinRegistry;
import com.alcatrazescapee.oreveins.world.WorldGenReplacer;
import com.alcatrazescapee.oreveins.world.WorldGenVeins;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(modid = OreVeins.MOD_ID, version = OreVeins.VERSION, dependencies = OreVeins.DEPENDENCIES, acceptableRemoteVersions = "*")
public class OreVeins
{
    public static final String MOD_ID = OreVeinsGenerated.MOD_ID;
    public static final String MOD_NAME = OreVeinsGenerated.MOD_NAME;
    public static final String VERSION = OreVeinsGenerated.MOD_VERSION;

    private static final String FORGE_MIN = OreVeinsGenerated.FORGE_MIN;
    private static final String FORGE_MAX = OreVeinsGenerated.FORGE_MAX;

    public static final String DEPENDENCIES = OreVeinsGenerated.DEPENDENCIES;

    private static Logger log;

    public static Logger getLog()
    {
        return log;
    }

    // This is necessary in order to catch the NewRegistry Event
    public OreVeins()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MOD_ID))
        {
            ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
            WorldGenVeins.resetChunkRadius();
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
        log.debug("If you can see this, debug logging is working :)");

        VeinRegistry.preInit(event.getModConfigurationDirectory());

        GameRegistry.registerWorldGenerator(new WorldGenVeins(), 1);
        MinecraftForge.ORE_GEN_BUS.register(new WorldGenReplacer());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        VeinRegistry.reloadVeins();
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        if (OreVeinsConfig.DEBUG_COMMANDS)
        {
            event.registerServerCommand(new CommandClearWorld());
            event.registerServerCommand(new CommandVeinInfo());
            event.registerServerCommand(new CommandFindVeins());
        }
    }
}
