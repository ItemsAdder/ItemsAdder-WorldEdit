package dev.lone.iaedit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements org.bukkit.event.Listener, CommandExecutor
{
    public static Main instance;
    private Hook hook;

    @Override
    public void onEnable()
    {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, this);

        hook = new Hook();
        hook.init(this);
        hook.register();
    }

    @Override
    public void onDisable()
    {
        hook.unregister();
    }

    @EventHandler
    private void onIADisabled(PluginDisableEvent e)
    {
        if(!e.getPlugin().getName().equals("ItemsAdder"))
            return;

        hook.unregister();
    }

    @EventHandler
    private void onIAEnabled(PluginEnableEvent e)
    {
        if(!e.getPlugin().getName().equals("ItemsAdder"))
            return;

        hook.register();
    }
}
