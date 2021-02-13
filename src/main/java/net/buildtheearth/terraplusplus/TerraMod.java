package net.buildtheearth.terraplusplus;

import net.buildtheearth.terraplusplus.control.TerraCommand;
import net.buildtheearth.terraplusplus.control.TerraTeleport;
import net.buildtheearth.terraplusplus.provider.EarthWorldProvider;
import net.buildtheearth.terraplusplus.provider.GenerationEventDenier;
import net.buildtheearth.terraplusplus.provider.WaterDenier;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

@Mod(modid = TerraMod.MODID,
        dependencies = "required-after:cubicchunks; required-after:cubicgen",
        acceptableRemoteVersions = "*",
        useMetadata = true)
public class TerraMod {
    public static final String MODID = TerraConstants.MOD_ID;
    public static final String VERSION = "0.1";
    public static final String USERAGENT = TerraMod.MODID + '/' + TerraMod.VERSION;
    public static final boolean CUSTOM_PROVIDER = false; //could potentially interfere with other mods and is relatively untested, leaving off for now

    public static Logger LOGGER = new SimpleLogger("[terra++ bootstrap]", Level.INFO, true, false, true, false, "[yyyy/MM/dd HH:mm:ss:SSS]", null, new PropertiesUtil("log4j2.simplelog.properties"), System.out);

    //set custom provider
    private static void setupProvider() {
        DimensionType type = DimensionType.register("earth", "_earth", 0, EarthWorldProvider.class, true);
        DimensionManager.init();
        DimensionManager.unregisterDimension(0);
        DimensionManager.registerDimension(0, type);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        EarthWorldType.create();

        // This is just a handy shortcut when creating new BTE worlds on the client not needed on the server
        // It is critical that this happens after the EarthWorldType is registered
        if (Side.CLIENT == event.getSide()) {
            BTEWorldType.create();
        }

        if (CUSTOM_PROVIDER) {
            setupProvider();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (TerraConfig.threeWater) {
            MinecraftForge.EVENT_BUS.register(WaterDenier.class);
        }
        if (Side.CLIENT == event.getSide()) {
            MinecraftForge.EVENT_BUS.register(BTEWorldType.class);
        }
        MinecraftForge.TERRAIN_GEN_BUS.register(GenerationEventDenier.class);

        PermissionAPI.registerNode(TerraConstants.controlCommandNode + "tpll", DefaultPermissionLevel.OP, "Allows a player to do /tpll");
        PermissionAPI.registerNode(TerraConstants.controlCommandNode + "terra", DefaultPermissionLevel.OP, "Allows access to terra commands");
        PermissionAPI.registerNode(TerraConstants.othersCommandNode, DefaultPermissionLevel.OP, "Allows a player to control another player in terra commands");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new TerraTeleport());
        event.registerServerCommand(new TerraCommand());
    }
}
