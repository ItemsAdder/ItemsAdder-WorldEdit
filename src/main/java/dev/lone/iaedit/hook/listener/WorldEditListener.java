package dev.lone.iaedit.we;

import com.sk89q.worldedit.event.extent.EditSessionEvent;
import dev.lone.iaedit.AbstractWorldEditListener;
import org.bukkit.plugin.Plugin;

public class WorldEditListener extends AbstractWorldEditListener
{
    public WorldEditListener(Plugin plugin)
    {
        super(plugin);
    }

    @Override
    protected void handle(EditSessionEvent e)
    {
        e.setExtent(new WeCustomBlocksDelegate(e));
    }
}
