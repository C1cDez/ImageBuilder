package com.cicdez.imagemp;

import net.minecraft.crash.CrashReport;
import net.minecraft.init.Blocks;
import net.minecraft.util.ReportedException;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.zip.ZipFile;

@Mod(modid = ImageBuilderMod.MODID, name = ImageBuilderMod.NAME, version = ImageBuilderMod.VERSION)
public class ImageBuilderMod {
    public static final String MODID = "imagemp";
    public static final String NAME = "Image Map Art Mod";
    public static final String VERSION = "1.0.0";
    
    public static final boolean IS_RUNNING_IN_IDE = false;
    public static final String MINECRAFT_PATH = "C:/Users/C1cDez/AppData/Roaming/.minecraft/versions/" +
            "1.12.2-forge-14.23.5.2860/1.12.2-forge-14.23.5.2860.jar";
    public static final ZipFile MINECRAFT_JAR;
    
    static {
        try {
            MINECRAFT_JAR = new ZipFile(new File(MINECRAFT_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean DO_BLOCK_SUPPRESSION = false;

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Image Builder Mod initialized");
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Utils.log("Post Init mod");
        
        try {
            ColorSpace.init();
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            Utils.log(writer.toString());
        }
        
//        Utils.FileLogger.write();
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new ImageBuilderCommand());
    }
    
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
            System.out.println(DO_BLOCK_SUPPRESSION);
            if (DO_BLOCK_SUPPRESSION) {
                event.setCanceled(true);
            }
        }
    }
}
