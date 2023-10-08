package com.cicdez.imagemp;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class Utils {
    public static class FileLogger {
        private static final StringBuilder builder = new StringBuilder();
        private static void log(String msg) {
            builder.append(msg).append("\n");
        }
        
        public static void write() {
            if (!ImageBuilderMod.IS_RUNNING_IN_IDE) return;
            File logs = new File(Minecraft.getMinecraft().mcDataDir, "logs");
            File logged = new File(logs, "imagemp-log.txt");
            try (FileOutputStream stream = new FileOutputStream(logged)) {
                stream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void log(Object msg) {
        ImageBuilderMod.logger.info(msg);
        FileLogger.log(msg + "");
    }
    
    public static String cutFromSides(String s, int fromBegin, int fromEnd) {
        String f = s.substring(fromBegin);
        return f.substring(0, f.length() - fromEnd);
    }
    
    public static String makeFilePath(String[] args, int from) {
        if (args[from].startsWith("\"")) {
            StringBuilder filePath = new StringBuilder();
            if (args[from].endsWith("\"")) {
                filePath = new StringBuilder(cutFromSides(args[from], 1, 1));
            }
            else {
                for (int i = from; i < args.length; i++) {
                    filePath.append(args[i]).append(" ");
                    if (args[i].endsWith("\"")) break;
                }
            }
            return filePath.toString();
        } else return null;
    }
    
    public static BufferedImage scaleImage(BufferedImage original, int width, int height) {
        Image image = original.getScaledInstance(width, height, Image.SCALE_FAST);
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                original.getType());
        bufferedImage.createGraphics().drawImage(image, 0, 0, null);
        return bufferedImage;
    }
}
