package dev.lone.iaedit.fawe;

import com.sk89q.worldedit.event.extent.EditSessionEvent;
import dev.lone.iaedit.AbstractWorldEditListener;
import org.bukkit.plugin.Plugin;

public class FaweListener extends AbstractWorldEditListener
{
    public FaweListener(Plugin plugin)
    {
        super(plugin);
    }

    @Override
    protected void handle(EditSessionEvent e)
    {
        e.setExtent(new FaweCustomBlocksDelegate(e));
    }
}
