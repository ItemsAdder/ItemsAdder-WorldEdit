package dev.lone.iaedit.fawe;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import dev.lone.iaedit.AbstractCustomBlocksDelegate;
import dev.lone.iaedit.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class FaweCustomBlocksDelegate extends AbstractCustomBlocksDelegate
{
    public FaweCustomBlocksDelegate(EditSessionEvent e)
    {
        super(e);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(int x, int y, int z, T baseBlock) throws WorldEditException
    {
        Result result = setBlock0(x, y, z, baseBlock);
        if (result == Result.YES)
            return true;
        return getExtent().setBlock(x, y, z, baseBlock);
    }

    public void placeCustomBlock(Location location, String namespacedId)
    {
        Bukkit.getScheduler().runTask(Main.instance, () -> placeCustomBlock0(location, namespacedId));
    }
}
