package me.zimzaza4.geysercolorreplacer;


import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.zimzaza4.geysercolorreplacer.config.ColorMappings;
import me.zimzaza4.geysercolorreplacer.listener.TextListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.format.TextColor;
import org.geysermc.floodgate.api.FloodgateApi;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Plugin(
        id = "geysercolorreplacer",
        name = "GeyserColorReplacer",
        version = "1.0.0",
        authors = {"zimzaza4"},
        dependencies = {
                @Dependency(id = "floodgate"),
                @Dependency(id = "packetevents")
        }
)

public class GeyserColorReplacer {


    private ProxyServer server;
    public static Logger LOGGER;
    private Path dataPath;

    @Inject
    public GeyserColorReplacer(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, PluginContainer container) {
        this.server = server;
        LOGGER = logger;
        this.dataPath = dataDirectory;
    }

    private ColorMappings colorMappings = new ColorMappings();
    private static GeyserColorReplacer INSTANCE;
    private static FloodgateApi FLOODGATE_API;

    public static boolean isFloodgatePlayer(UUID uuid) {
        if (uuid == null) {
            return false;
        }
        return FLOODGATE_API.isFloodgatePlayer(uuid);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        INSTANCE = this;
        FLOODGATE_API = FloodgateApi.getInstance();
        reload();
        CommandManager commandManager = server.getCommandManager();
        commandManager.register(commandManager.metaBuilder("geysercolorreplacer").aliases("gcp").build(), new SimpleCommand() {
            @Override
            public void execute(Invocation invocation) {
                if (invocation.source().hasPermission("geysercolorreplacer.reload")) {
                    reload();
                    invocation.source().sendRichMessage("Reloaded");
                }
            }
        });

        PacketEvents.getAPI().getEventManager()
                .registerListener(new TextListener(), PacketListenerPriority.LOWEST);
    }

    public static Component replaceColor(Component component) {
        if (component == null) return null;
        if (component instanceof TextComponent textComponent) {
            List<Component> newChild = new ArrayList<>();
            for (Component child : textComponent.children()) {
                newChild.add(replaceColor(child));
            }
            component = textComponent.children(newChild);
        }
        if (component instanceof TranslatableComponent translatableComponent) {

            List<TranslationArgument> newArg = new ArrayList<>();
            for (TranslationArgument arg : translatableComponent.arguments()) {
                if (arg.value() instanceof Component value) {
                    arg = TranslationArgument.component(replaceColor(value));
                }
                newArg.add(arg);
            }
            component = translatableComponent.arguments(newArg);
        }
        if (component.color() != null) {
            TextColor newColor = getInstance().getColorMappings().getMappings().get(component.color());
            if (newColor != null) {
                component = component.color(newColor);
            }
        }
        return component;
    }

    public ColorMappings getColorMappings() {
        return colorMappings;
    }

    public static GeyserColorReplacer getInstance() {
        return INSTANCE;
    }

    public void reload() {
        colorMappings.load(dataPath.resolve("colors.json"));
    }
}
