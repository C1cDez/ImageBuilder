package com.cicdez.imagemp;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class ImageBuilding {
    public static void build(String filePath, int width, int height, ImageBuildingMode mode,
                             boolean mustFull, ICommandSender sender) {
        System.out.println("Starting Building Image '" + filePath + "' " + width + "x" + height + " " + mode);
        BufferedImage image = getImage(filePath);
        BufferedImage scaled = Utils.scaleImage(image, width, height);
        build(scaled, sender.getEntityWorld(), sender.getPosition(), mode, mustFull);
    }
    public static void build(String filePath, double fraction, ImageBuildingMode mode, boolean mustFull, ICommandSender sender) {
        System.out.println("Starting Building Image '" + filePath + "' " + fraction + " " + mode);
        BufferedImage image = getImage(filePath);
        BufferedImage scaled = Utils.scaleImage(image,
                (int) (image.getWidth() * fraction), (int) (image.getHeight() * fraction));
        build(scaled, sender.getEntityWorld(), sender.getPosition(), mode, mustFull);
    }
    
    private static void build(BufferedImage image, World world, BlockPos pos, ImageBuildingMode mode, boolean mustFull) {
        int width = image.getWidth(), height = image.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = new Color(image.getRGB(x, y));
                BlockTextureColorVector vector = ColorSpace.calculateNearest((double) color.getRed() / 255,
                        (double) color.getGreen() / 255, (double) color.getBlue() / 255, mustFull);
                Block block = vector.getBlock();
                BlockPos blockPos = mode.blockPosFromImage(x, y, width, height);
                world.setBlockState(pos.add(blockPos), block.getDefaultState(), 2);
            }
        }
    }
    
    public static BufferedImage getImage(String filePath) {
        try {
            return ImageIO.read(Files.newInputStream(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
