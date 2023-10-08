package com.cicdez.imagemp;

import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ImageBuilderCommand extends CommandBase {
    private static final String NAME = "imagebuilder";
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + NAME + " <fraction/width;height> <mode[" + ImageBuildingMode.COLLECTION.stream()
                .map(ImageBuildingMode::toString).collect(Collectors.joining("/")) +
                "]> <full/every> <\"pathToFile\">";
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            if (args.length != 0) {
                if (args[0].equals("help")) {
                    sender.sendMessage(new TextComponentString(getUsage(sender)));
                    return;
                }
                if (args[0].equals("cs")) {
                    ColorSpace.build(sender.getEntityWorld(), sender.getPosition(), Integer.parseInt(args[1]));
                    return;
                }
                if (args[0].equals("dbs")) {
                    ImageBuilderMod.DO_BLOCK_SUPPRESSION = Boolean.parseBoolean(args[1]);
                    sender.sendMessage(new TextComponentString("Block Suppression is now " +
                            (ImageBuilderMod.DO_BLOCK_SUPPRESSION ? "Enabled" : "Disabled")));
                    return;
                }
                if (args[0].equals("-ex")) {
                    for (int index = 1; index < args.length; index++) {
                        ColorSpace.putExceptionBlock(ForgeRegistries.BLOCKS.getValue(
                                new ResourceLocation(args[index])), sender);
                    }
                    return;
                }
                String[] size = args[0].split(";");
                double fraction = -1;
                int width = -1, height = -1;
                if (size.length == 1) fraction = Double.parseDouble(size[0]);
                else if (size.length == 2) {
                    width = Integer.parseInt(size[0]);
                    height = Integer.parseInt(size[1]);
                }
                
                ImageBuildingMode mode = ImageBuildingMode.byName(args[1]);
                boolean mustFull = args[2].equals("full");
                String filePath = Utils.makeFilePath(args, 3);
                
                sender.sendMessage(new TextComponentString(
                        "File: " + filePath + ", " +
                                "Fraction: " + fraction + ", " +
                                "Size: " + width + "x" + height + ", " +
                                "Mode: " + mode
                ));
                
                long before = System.currentTimeMillis();
                if (width != -1 && height != -1)
                    ImageBuilding.build(filePath, width, height, mode, mustFull, sender);
                else if (fraction != -1)
                    ImageBuilding.build(filePath, fraction, mode, mustFull, sender);
                else throw new WrongUsageException("Can't build image");
                long after = System.currentTimeMillis();
                
                sender.sendMessage(new TextComponentString("Image built in " + (after - before) + "ms"));
            } else throw new WrongUsageException("No arguments!");
        } catch (Throwable throwable) {
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            sender.sendMessage(new TextComponentString(TextFormatting.RED + writer.toString()));
        }
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
                                          String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 2: getListOfStringsMatchingLastWord(args, ImageBuildingMode.COLLECTION);
            case 3: Collections.singletonList("\"");
            default: return super.getTabCompletions(server, sender, args, targetPos);
        }
    }
}
