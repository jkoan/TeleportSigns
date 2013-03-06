package de.zh32.teleportsigns;

import de.zh32.teleportsigns.ping.Ping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.PersistenceException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleportSigns extends JavaPlugin {
    
    public Map<String, Location> locs = new HashMap<String, Location>();
    public List<TeleportSign> signs = new ArrayList<>();
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Update(this), 60L, 100L);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Ping.getInstance().loadConfig();
        Ping.getInstance().startPing();
        setupDB();
        loadSigns();
        
    }
    
    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(TeleportSign.class);
        return list;
    }

    public void loadSigns() {
        signs = this.getDatabase().find(TeleportSign.class).findList();
    }

    private void setupDB() {
        try {
            getDatabase().find(TeleportSign.class).findRowCount();
        } catch (PersistenceException ex) {
            Bukkit.getLogger().log(Level.INFO, "Installing database for {0} due to first time usage", getDescription().getName());
            installDDL();
        }
    }
    
    public String getServerName(String s) {
        return s.split("#")[1];
    }
}