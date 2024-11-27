package dev.lone.iaedit;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.List;
import java.util.Map;

public abstract class AbstractCustomBlocksDelegate extends AbstractDelegateExtent
{
    protected final EditSessionEvent e;

    public AbstractCustomBlocksDelegate(EditSessionEvent e)
    {
        super(e.getExtent());
        this.e = e;
    }

    public <T extends BlockStateHolder<T>> Result setBlock0(int x, int y, int z, T baseBlock) throws WorldEditException
    {
        World world = Bukkit.getWorld(e.getWorld().getName());
        Location loc = new Location(world, x,y,z);
        if(ItemsAdder.isCustomBlock(world.getBlockAt(loc)))
            ItemsAdder.removeCustomBlock(loc);

        if (CustomBlocksInputParser.isCustomBlockType(baseBlock.getBlockType()))
        {
            BaseBlock bb = baseBlock.toBaseBlock();

            if(bb.getNbtData() != null)
            {
                Map<String, Tag> nbt = bb.getNbtData().getValue();
                // //set
                if(nbt.containsKey("IABlock"))
                {
                    String namespacedId = (String) nbt.get("IABlock").getValue();
                    placeCustomBlock(loc, namespacedId);
                    return Result.YES;
                }
                // //paste
                else if(baseBlock.getBlockType() == BlockTypes.SPAWNER)
                {
                    // RETURN ONLY IF IT'S CUSTOM BLOCK
                    if(nbt.containsKey("SpawnData"))
                    {
                        CompoundTag entityTag = (CompoundTag) ((Map<?, ?>) nbt.get("SpawnData").getValue()).get("entity");
                        if(entityTag != null)
                        {
                            if(entityTag.containsKey("id") && entityTag.containsKey("ArmorItems")
                                    && entityTag.getString("id").equals("minecraft:armor_stand"))
                            {
                                List armorItemsList = (List) entityTag.getValue().get("ArmorItems").getValue();
                                if(armorItemsList.size() == 4)
                                {
                                    CompoundTag helmetItemTag = (CompoundTag) armorItemsList.get(3);
                                    CompoundTag tagValue = (CompoundTag) helmetItemTag.getValue().get("tag");
                                    IntTag customModelDataTag = (IntTag) tagValue.getValue().get("CustomModelData");
                                    Integer cmd = customModelDataTag.getValue();

                                    // Stupidly slow
                                    for (String namespacedId : ItemsAdder.getNamespacedBlocksNamesInConfig(null))
                                    {
                                        CustomBlock customBlock = CustomBlock.getInstance(namespacedId);
                                        if(customBlock.getItemStack().getItemMeta().getCustomModelData() == cmd.intValue())
                                        {
                                            placeCustomBlock(loc, namespacedId);
                                            return Result.YES;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else // //paste
            {
                // Stupidly slow
                String bbToString = bb.getAsString();
                for (String namespacedId : ItemsAdder.getNamespacedBlocksNamesInConfig(null))
                {
                    BlockData customBlockData = CustomBlock.getBaseBlockData(namespacedId);
                    if (customBlockData == null)
                        break;

                    String customBlockDataStr = customBlockData.getAsString(false);
                    if (customBlockDataStr.equals(bbToString))
                    {
                        placeCustomBlock(loc, namespacedId);
                        return Result.YES;
                    }
                }
            }
        }

        return Result.FALLBACK;
    }

    public abstract void placeCustomBlock(Location location, String namespacedId);

    public void placeCustomBlock0(Location location, String namespacedId)
    {
        ItemsAdder.placeCustomBlock(
                location,
                ItemsAdder.getCustomItem(namespacedId)
        );
        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            if (location.getWorld() != null)
            {
                BlockData blockData = location.getBlock().getBlockData();
                location.getWorld().getPlayers().forEach(player -> {
                    if (player.getLocation().distance(location) <= 64)
                        player.sendBlockChange(location, blockData);
                });
            }
        }, 5L);
    }

    public enum Result
    {
        FALLBACK,
        YES,
        NO
    }
}
