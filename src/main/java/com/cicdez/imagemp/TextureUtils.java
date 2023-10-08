package com.cicdez.imagemp;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class TextureUtils {
    private static final List<String> MINECRAFT_IDS = Lists.newArrayList("minecraft", "mcp", "FML", "forge");
    
    public static InputStream getTexture(Block block) throws IOException {
        String id = getTextureId(block);
        Utils.log(id);
        if (id != null) {
            ResourceLocation resource = new ResourceLocation(id);
            if (MINECRAFT_IDS.contains(resource.getResourceDomain())) return null;
            
            ZipFile jar = getModJar(block);
            Utils.log("'" + id + "' is in '" + jar.toString() + "'");
            String resourcePath = "assets/" + resource.getResourceDomain() + "/textures/" + resource.getResourcePath() + ".png";
            ZipEntry entry = jar.getEntry(resourcePath);
            if (entry != null) return jar.getInputStream(entry);
        }
        return null;
    }
    
    private static ZipFile getModJar(Block block) throws IOException {
        ResourceLocation resource = block.getRegistryName();
        String modId = resource.getResourceDomain();
        if (MINECRAFT_IDS.contains(modId)) {
            if (ImageBuilderMod.IS_RUNNING_IN_IDE) {
                return ImageBuilderMod.MINECRAFT_JAR;
            } else throw new IOException("Unable to access minecraft.jar");
        } else {
            ModContainer mod = Loader.instance().getIndexedModList().get(modId);
            return new ZipFile(mod.getSource());
        }
    }
    
    private static String getTextureId(Block block) {
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBlockState state = block.getDefaultState();
        List<BakedQuad> list = dispatcher.getModelForState(state).getQuads(state, EnumFacing.NORTH, 0);
        if (!list.isEmpty()) {
            TextureAtlasSprite sprite = list.get(0).getSprite();
            return sprite.getIconName();
        }
        return null;
    }
    
    public static double[] getAverageColorOnTexture(BufferedImage image) {
        double redSum = 0, greenSum = 0, blueSum = 0;
        double pixelsCount = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                if (color.getAlpha() != 0) {
                    redSum += color.getRed();
                    greenSum += color.getGreen();
                    blueSum += color.getBlue();
                    pixelsCount++;
                }
            }
        }
        return new double[] {redSum / pixelsCount, greenSum / pixelsCount, blueSum / pixelsCount};
    }
}
