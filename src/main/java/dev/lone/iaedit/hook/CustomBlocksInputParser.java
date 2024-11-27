package dev.lone.iaedit;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.ItemsAdder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Stream;

public class CustomBlocksInputParser<P> extends InputParser<Pattern>
{
    static Constructor<?> BASEBLOCK_CONSTRUCTOR;
    static
    {
        for (Constructor<?> constructor : BaseBlock.class.getDeclaredConstructors())
        {
            if(constructor.getParameterCount() == 2 && constructor.getParameterTypes()[1] == CompoundTag.class)
            {
                BASEBLOCK_CONSTRUCTOR = constructor;
                BASEBLOCK_CONSTRUCTOR.setAccessible(true);
                break;
            }
        }

        if(BASEBLOCK_CONSTRUCTOR == null)
            Main.instance.getLogger().severe("Failed to find BaseBlock constructor. Try to update WorldEdit/FAWE.");
    }

    public CustomBlocksInputParser(WorldEdit worldEdit)
    {
        super(worldEdit);
    }

    public static boolean isCustomBlockType(BlockType blockType)
    {
        return blockType == BlockTypes.NOTE_BLOCK ||
                blockType == BlockTypes.MUSHROOM_STEM ||
                blockType == BlockTypes.BROWN_MUSHROOM_BLOCK ||
                blockType == BlockTypes.RED_MUSHROOM_BLOCK ||
                blockType == BlockTypes.CHORUS_PLANT ||
                blockType == BlockTypes.TRIPWIRE ||
                blockType == BlockTypes.SPAWNER
        ;
    }

    @Override
    public Stream<String> getSuggestions(String input)
    {
        if (input.isEmpty())
            return Stream.empty();
        return ItemsAdder.getNamespacedBlocksNamesInConfig(input).stream();
    }

    @Override
    public BaseBlock parseFromInput(String input, ParserContext context)
    {
        if(BASEBLOCK_CONSTRUCTOR == null)
            return null;

        try
        {
            CustomBlock customBlock = CustomBlock.getInstance(input);

            if (customBlock == null)
                return null;

            try
            {
                BlockType blockType;
                if (customBlock.getBaseBlockData() == null) // Dirty, it may cause issues.
                    blockType = BlockTypes.SPAWNER; // Dirty, it may cause issues.
                else
                    blockType = BlockTypes.get("minecraft:" + customBlock.getBaseBlockData().getMaterial().toString().toLowerCase(Locale.ROOT));

                BlockState blockState;
                if (blockType == null)
                    return null;

                blockState = blockType.getDefaultState();

                HashMap<String, Tag> attributes = new HashMap<>();
                attributes.put("IABlock", new StringTag(input));

                return (BaseBlock) BASEBLOCK_CONSTRUCTOR.newInstance(blockState, new CompoundTag(attributes));
            }
            catch (IllegalAccessException | InvocationTargetException | InstantiationException e)
            {
                return null;
            }
        }
        catch (ClassCastException ignored) {}
        return null;
    }
}