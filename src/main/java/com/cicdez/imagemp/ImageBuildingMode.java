package com.cicdez.imagemp;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ImageBuildingMode {
    VERTICAL_NORTH {
        @Override
        public BlockPos blockPosFromImage(int x, int y, int width, int height) {
            return new BlockPos(x, height - y, 0);
        }
    },
    VERTICAL_WEST {
        @Override
        public BlockPos blockPosFromImage(int x, int y, int width, int height) {
            return new BlockPos(0, height - y, x);
        }
    },
    HORIZONTAL {
        @Override
        public BlockPos blockPosFromImage(int x, int y, int width, int height) {
            return new BlockPos(x, 0, y);
        }
    },
    ;
    ImageBuildingMode() {
    }
    
    public BlockPos blockPosFromImage(int x, int y, int width, int height) {
        throw new UnsupportedOperationException();
    }
    
    public static void makePlateIfHorizontal(World world, int x, int y, int high, int width, int height) {
        for (int xf = x; xf < width; xf++) {
            for (int yf = y; yf < height; yf++) {
                world.setBlockState(new BlockPos(xf, high, yf), Blocks.BARRIER.getDefaultState());
            }
        }
    }
    
    @Override
    public String toString() {
        return name();
    }
    
    private static final Map<String, ImageBuildingMode> MODES = Arrays.stream(values()).collect(
            Collectors.toMap(ImageBuildingMode::toString, Function.identity()));
    public static ImageBuildingMode byName(String name) {
        return MODES.get(name);
    }
    
    public static final Collection<ImageBuildingMode> COLLECTION = Arrays.asList(values());
}
