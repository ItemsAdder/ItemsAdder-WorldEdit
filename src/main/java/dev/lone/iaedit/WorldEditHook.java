package dev.lone.iaedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.function.pattern.Pattern;
import dev.lone.iaedit.hook.delegate.FaweCustomBlocksDelegate;
import dev.lone.iaedit.hook.delegate.WeCustomBlocksDelegate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Hook
{
    WorldEditPlugin worldEditPlugin;
    AbstractWorldEditListener listener;
    CustomBlocksInputParser customBlocksInputParser;

    public void init(Plugin plugin)
    {
        boolean isFawe = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null;

        worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        listener = AbstractWorldEditListener.init(plugin, isFawe);
        customBlocksInputParser = new CustomBlocksInputParser<Pattern>(WorldEdit.getInstance());

        if(isFawe)
        {
            com.fastasyncworldedit.core.configuration.Settings.settings().EXTENT.ALLOWED_PLUGINS.add(WeCustomBlocksDelegate.class.getCanonicalName());
            com.fastasyncworldedit.core.configuration.Settings.settings().EXTENT.ALLOWED_PLUGINS.add(FaweCustomBlocksDelegate.class.getCanonicalName());
        }
    }

    public void register()
    {
        WorldEdit.getInstance().getEventBus().register(listener);
        WorldEdit.getInstance().getBlockFactory().register(customBlocksInputParser);
    }

    public void unregister()
    {
        WorldEdit.getInstance().getEventBus().unregister(listener);
    }
}
