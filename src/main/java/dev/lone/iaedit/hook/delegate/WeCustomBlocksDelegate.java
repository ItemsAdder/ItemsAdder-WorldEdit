package dev.lone.iaedit.we;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import dev.lone.iaedit.AbstractCustomBlocksDelegate;
import org.bukkit.Location;

public class WeCustomBlocksDelegate extends AbstractCustomBlocksDelegate
{
    public WeCustomBlocksDelegate(EditSessionEvent e)
    {
        super(e);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 pos, T baseBlock) throws WorldEditException
    {
        Result result = setBlock0(pos.getX(), pos.getY(), pos.getZ(), baseBlock);
        if (result == Result.YES)
            return true;
        return getExtent().setBlock(pos, baseBlock);
    }

    @Override
    public void placeCustomBlock(Location location, String namespacedId)
    {
        placeCustomBlock0(location, namespacedId);
    }
}
