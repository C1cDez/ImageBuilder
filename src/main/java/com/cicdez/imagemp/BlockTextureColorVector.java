package com.cicdez.imagemp;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Objects;

public class BlockTextureColorVector {
    private final Block block;
    private final double red, green, blue;
    
    private BlockTextureColorVector(Block block, double red, double green, double blue) {
        this.block = block;
        this.red = MathHelper.clamp(red, 0, 1);
        this.green = MathHelper.clamp(green, 0, 1);
        this.blue = MathHelper.clamp(blue, 0, 1);
    }
    
    public Block getBlock() {
        return block;
    }
    
    public double getRed() {
        return red;
    }
    public double getGreen() {
        return green;
    }
    public double getBlue() {
        return blue;
    }
    
    public boolean isNull() {
        return block == null;
    }
    
    public static double distance(BlockTextureColorVector vector, double redf, double greenf, double bluef) {
        double dred = vector.getRed() - redf,
                dgreen = vector.getGreen() - greenf,
                dblue = vector.getBlue() - bluef;
        return Math.sqrt(dred * dred + dgreen * dgreen + dblue * dblue);
    }
    
    public static BlockTextureColorVector create(Block block) throws Exception {
        try (InputStream stream = TextureUtils.getTexture(block)) {
            if (stream != null) {
                BufferedImage texture = ImageIO.read(stream);
                double[] average = TextureUtils.getAverageColorOnTexture(texture);
                return new BlockTextureColorVector(block, average[0] / 255, average[1] / 255, average[2] / 255);
            } else return null;
        }
    }
    
    public static BlockTextureColorVector nullable(double red, double green, double blue) {
        return new BlockTextureColorVector(null, red, green, blue);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof BlockTextureColorVector) {
            BlockTextureColorVector vector = (BlockTextureColorVector) o;
            return vector.red == this.red && vector.green == this.green && vector.blue == this.blue;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(block, red, green, blue);
    }
    
    @Override
    public String toString() {
        return red + "," + green + "," + blue;
    }
    public static BlockTextureColorVector fromString(String str) {
        String id = str.split("=")[0];
        String[] values = str.split("=")[1].split(",");
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:" + id));
        Utils.log("Loading: " + block);
        return new BlockTextureColorVector(block, Double.parseDouble(values[0]),
                Double.parseDouble(values[1]), Double.parseDouble(values[2]));
    }
}
