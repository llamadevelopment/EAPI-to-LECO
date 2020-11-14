package net.lldv.eapitoleco;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import me.onebone.economyapi.EconomyAPI;
import net.lldv.llamaeconomy.LlamaEconomy;

import java.util.Map;
import java.util.UUID;

/**
 * @author LlamaDevelopment
 * @project EAPI-to-LECO
 * @website http://llamadevelopment.net/
 */
public class EAPITOLECO extends PluginBase implements Listener {

    private boolean transfered = false;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        if (this.getConfig().getBoolean("done")) {
            this.getLogger().info("EAPI to LECO is done! You can now remove EconomyAPI & EAPI to LECO");
            this.transfered = true;
            final Plugin plugin = this.getServer().getPluginManager().getPlugin("EconomyAPI");
            if (plugin != null) {
                this.getServer().getPluginManager().disablePlugin(plugin);
            }
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getLogger().info("§aStarting Transfer in 5 Seconds...");
        this.getServer().getScheduler().scheduleDelayedTask(this, this::doIt, 100);
    }

    private void doIt() {
        this.getLogger().info("§aStarting transfer process. This can take a while.");

        int done = 1;
        final int count = EconomyAPI.getInstance().getAllMoney().size();

        for (Map.Entry<String, Double> entry : EconomyAPI.getInstance().getAllMoney().entrySet()) {
            final String player = this.getServer().getOfflinePlayer(UUID.fromString(entry.getKey())).getName();
            if (player != null) {
                this.getLogger().info("§eTransferring Player " + player + "...");
                LlamaEconomy.getAPI().createAccount(player, entry.getValue());
                this.getLogger().info("§aDone! (" + done + "/" + count + ")");
            } else this.getLogger().info("Skipped an invalid player. (" + done + "/" + count + ")");
            done++;
        }

        this.getConfig().set("done", true);
        this.getConfig().save();

        this.getLogger().info("§aEverything was transferred. Please reboot the Server and remove EconomyAPI & EAPI to LECO!");
    }

    @EventHandler
    public void on(PlayerPreLoginEvent event) {
        if (!this.transfered)
            event.setKickMessage("§cPlease check the console! EAPI to LECO is still requires your attention.");
            event.setCancelled();
    }


}
