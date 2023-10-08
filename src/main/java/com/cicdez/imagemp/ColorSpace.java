package com.cicdez.imagemp;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class ColorSpace {
    private static final Stack<BlockTextureColorVector> VECTORS = new Stack<>();
    
    public static void init() throws Exception {
        Utils.log("Initializing Color Space");
        loadMinecraftBlocks();
        
        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            BlockTextureColorVector vector = BlockTextureColorVector.create(block);
            if (vector != null && !VECTORS.contains(vector)) VECTORS.push(vector);
        }
    }
    
    private static void loadMinecraftBlocks() throws IOException {
        InputStream stream = ImageBuilderMod.class.getResourceAsStream("/embeddings.txt");
        if (stream != null) {
            new BufferedReader(new InputStreamReader(stream)).lines().forEach(line -> {
                BlockTextureColorVector vector = BlockTextureColorVector.fromString(line);
                VECTORS.push(vector);
            });
            stream.close();
        }
    }
    private static void storeMinecraftBlocks() throws Exception {
        Path embeddings = Paths.get("C:/Users/C1cDez/mymods/ImageMapArtBuilder/ImageMapArtBuilder_1.12.2/" +
                "src/main/resources/embedding.txt");
        PrintStream stream = new PrintStream(Files.newOutputStream(embeddings), true);
        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            BlockTextureColorVector vector = BlockTextureColorVector.create(block);
            stream.println(vector);
        }
        stream.close();
    }
    
    
    public static Stack<BlockTextureColorVector> vectors() {
        return VECTORS;
    }
    
    private static final List<Block> EXCEPTIONS = Lists.newArrayList(Blocks.MOB_SPAWNER);
    public static void putExceptionBlock(Block block, ICommandSender sender) {
        EXCEPTIONS.add(block);
        sender.sendMessage(new TextComponentString("'" + block + "' is now exception"));
    }
    
    private static final Map<BlockTextureColorVector, BlockTextureColorVector>
            USED_VECTORS_CACHE = new HashMap<>(),
            USED_VECTORS_CACHE_FULL = new HashMap<>();
    public static BlockTextureColorVector calculateNearest(double redf, double greenf, double bluef, boolean mustFull) {
        BlockTextureColorVector supposedVector = BlockTextureColorVector.nullable(redf, greenf, bluef);
        if (!mustFull) {
            if (USED_VECTORS_CACHE.containsKey(supposedVector)) return USED_VECTORS_CACHE.get(supposedVector);
        } else {
            if (USED_VECTORS_CACHE_FULL.containsKey(supposedVector)) return USED_VECTORS_CACHE_FULL.get(supposedVector);
        }
        
        BlockTextureColorVector nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (BlockTextureColorVector vector : VECTORS) {
            if (EXCEPTIONS.contains(vector.getBlock())) continue;
            double dist = BlockTextureColorVector.distance(vector, redf, greenf, bluef);
            if (dist < nearestDistance) {
                if (mustFull) {
                    boolean full = vector.getBlock().isFullCube(vector.getBlock().getDefaultState());
                    if (full) {
                        nearestDistance = dist;
                        nearest = vector;
                    } else continue;
                } else {
                    nearestDistance = dist;
                    nearest = vector;
                }
            }
        }
        if (nearest != null) {
            if (!mustFull) USED_VECTORS_CACHE.put(supposedVector, nearest);
            else USED_VECTORS_CACHE_FULL.put(supposedVector, nearest);
        }
        return nearest;
    }
    
    public static void build(World world, BlockPos destPos, int size) {
        ImageBuilderMod.DO_BLOCK_SUPPRESSION = true;
        
        //Axes
        for (int x = 0; x < size + 1; x++) {
            world.setBlockState(destPos.add(x, 0, 0), Blocks.REDSTONE_BLOCK.getDefaultState(), 2);
        }
        for (int y = 0; y < size + 1; y++) {
            world.setBlockState(destPos.add(0, y, 0), Blocks.EMERALD_BLOCK.getDefaultState(), 2);
        }
        for (int z = 0; z < size + 1; z++) {
            world.setBlockState(destPos.add(0, 0, z), Blocks.LAPIS_BLOCK.getDefaultState(), 2);
        }
        world.setBlockState(destPos, Blocks.COAL_BLOCK.getDefaultState(), 2);
        
        //Blocks
        for (BlockTextureColorVector vector : VECTORS) {
            int x = (int) (vector.getRed() * size), y = (int) (vector.getGreen() * size),
                    z = (int) (vector.getBlue() * size);
            BlockPos pos = destPos.add(1, 1, 1).add(x, y, z);
            System.out.println(vector.getBlock().getRegistryName());
            world.setBlockState(pos, vector.getBlock().getDefaultState(), 2);
        }
    }
}
