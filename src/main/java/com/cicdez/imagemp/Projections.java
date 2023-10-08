package com.cicdez.imagemp;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;
import java.util.function.Function;

public final class Projections {
    public static void save(int width, int height, Function<BlockTextureColorVector, Double> xImage,
                            Function<BlockTextureColorVector, Double> yImage) throws Exception {
        Path path = Paths.get("C:/Users/C1cDez/mymods/ImageMapArtBuilder/" +
                "ImageMapArtBuilder_1.12.2/projection.png");
        BufferedImage image = make2dProjection(width, height, xImage, yImage);
        ImageIO.write(image, "png", Files.newOutputStream(path));
    }
    
    public static BufferedImage make2dProjection(int width, int height, Function<BlockTextureColorVector, Double> xImage,
                                                 Function<BlockTextureColorVector, Double> yImage) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);
        
        for (BlockTextureColorVector vector : ColorSpace.vectors()) {
            int x = (int) (xImage.apply(vector) * width), y = (int) (yImage.apply(vector) * height);
            try (InputStream textureStream = TextureUtils.getTexture(vector.getBlock())) {
                if (textureStream != null) {
                    BufferedImage texture = ImageIO.read(textureStream);
                    int textureWidth = texture.getWidth(), textureHeight = texture.getHeight();
                    if (textureHeight != textureWidth) {
                        textureHeight = textureWidth;
                    }
                    graphics.drawImage(texture, x - textureWidth / 2, y - textureHeight / 2,
                            textureWidth, textureHeight, null);
                }
            }
        }
        return image;
    }
}
