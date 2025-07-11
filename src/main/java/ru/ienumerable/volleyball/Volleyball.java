package ru.ienumerable.volleyball;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ienumerable.volleyball.canvas.MenuFunctionListener;
import ru.ienumerable.volleyball.ball.BallsContainer;
import ru.ienumerable.volleyball.commands.BallSummonCMD;
import ru.ienumerable.volleyball.commands.CommandsCMD;
import ru.ienumerable.volleyball.skin.SkullsContainer;
import ru.ienumerable.volleyball.tools.update.Updater;

public final class Volleyball extends JavaPlugin {

    private static Volleyball instance;

    private static NamespacedKey ballKey;
    private static NamespacedKey ballRandomKey;

    private static final SkullsContainer skullsContainer = new SkullsContainer();;

    private static final TCContainer tcContainer = new TCContainer();

    private static final BallsContainer ballsContainer = new BallsContainer();

    private static final Updater updater = new Updater();

    @Override
    public void onEnable() {


        instance = this;
        ballKey = new NamespacedKey(instance, "BBNB_Ball");
        ballRandomKey = new NamespacedKey(instance, "BBNB_Ball_random");

        saveResource("skins.yml", false);
        saveResource("config.yml", false);

        loadConfig();

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);


        CommandsCMD commandsCMD = new CommandsCMD();
        getCommand("ballskin").setExecutor(commandsCMD);
        getCommand("ballsreload").setExecutor(commandsCMD);

        getCommand("ballsummon").setExecutor(new BallSummonCMD());
        getCommand("ballsummon").setTabCompleter(new BallSummonCMD());

        updater.startScheduler();

    }

    @Override
    public void onDisable() {

        ballsContainer.dropAllBalls();

    }

    public static void loadConfig(){

        skullsContainer.parseSkulls();
        Config.init();

    }

    public static Volleyball getInstance() {
        return instance;
    }

    public static NamespacedKey getBallKey() {
        return ballKey;
    }
    public static NamespacedKey getBallRandomKey() {
        return ballRandomKey;
    }

    public static TCContainer getTcContainer() {
        return tcContainer;
    }

    public static BallsContainer getBallsContainer(){
        return ballsContainer;
    }

    public static SkullsContainer getSkullsContainer(){
        return skullsContainer;
    }

    public static Updater getUpdater() {
        return updater;
    }

}
