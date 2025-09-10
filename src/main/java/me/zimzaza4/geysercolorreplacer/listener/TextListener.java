package me.zimzaza4.geysercolorreplacer.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.ChatType;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.zimzaza4.geysercolorreplacer.GeyserColorReplacer;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.zimzaza4.geysercolorreplacer.GeyserColorReplacer.replaceColor;

public class TextListener implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!GeyserColorReplacer.isFloodgatePlayer(event.getUser().getUUID())) {
            return;
        }
        if (event.getPacketType() == PacketType.Play.Server.SYSTEM_CHAT_MESSAGE) {
            WrapperPlayServerSystemChatMessage message = new WrapperPlayServerSystemChatMessage(event);
            message.setMessage(replaceColor(message.getMessage()));
            message.write();
        }
        if (event.getPacketType() == PacketType.Play.Server.CHAT_MESSAGE) {
            WrapperPlayServerChatMessage message = new WrapperPlayServerChatMessage(event);
            ChatMessage chatMessage = message.getMessage();
            if (chatMessage instanceof ChatMessage_v1_19_3 messageV1193) {

                ChatType.Bound bound = messageV1193.getChatFormatting();
                bound.setName(replaceColor(bound.getName()));
                bound.setTargetName(replaceColor(bound.getTargetName()));
                messageV1193.setChatFormatting(bound);
                messageV1193.getUnsignedChatContent().ifPresent(component -> {
                    messageV1193.setUnsignedChatContent(replaceColor(component));
                });
            }
            message.write();
        }
        if (event.getPacketType() == PacketType.Play.Server.ACTION_BAR) {
            WrapperPlayServerActionBar actionBar = new WrapperPlayServerActionBar(event);
            actionBar.setActionBarText(replaceColor(actionBar.getActionBarText()));
            actionBar.write();
        }if (event.getPacketType() == PacketType.Play.Server.SET_TITLE_TEXT) {
            WrapperPlayServerSetTitleText title = new WrapperPlayServerSetTitleText(event);
            title.setTitle(replaceColor(title.getTitle()));
            title.write();
        }

        if (event.getPacketType() == PacketType.Play.Server.SET_TITLE_SUBTITLE) {
            WrapperPlayServerSetTitleSubtitle subtitle = new WrapperPlayServerSetTitleSubtitle(event);
            subtitle.setSubtitle(replaceColor(subtitle.getSubtitle()));
            subtitle.write();
        }
        if (event.getPacketType() == PacketType.Play.Server.DISGUISED_CHAT) {
            WrapperPlayServerDisguisedChat message = new WrapperPlayServerDisguisedChat(event);
            message.setMessage(replaceColor(message.getMessage()));
            message.write();
        }
        if (event.getPacketType() == PacketType.Play.Server.BOSS_BAR) {
            WrapperPlayServerBossBar bossBar = new WrapperPlayServerBossBar(event);
            bossBar.setTitle(replaceColor(bossBar.getTitle()));
            bossBar.write();
        }
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            WrapperPlayServerWindowItems items = new WrapperPlayServerWindowItems(event);
            for (ItemStack item : items.getItems()) {
                processItem(item);
            }
            items.getCarriedItem().ifPresent(this::processItem);
            items.write();
        }
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            WrapperPlayServerSetSlot item = new WrapperPlayServerSetSlot(event);
            processItem(item.getItem());
            item.write();
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(event);
            for (EntityData<?> entityData : metadata.getEntityMetadata()) {
                if (entityData.getType() == EntityDataTypes.ADV_COMPONENT) {
                    EntityData<Component> componentEntityData = (EntityData<Component>) entityData;
                    componentEntityData.setValue(replaceColor((Component) entityData.getValue()));
                }
                if (entityData.getType() == EntityDataTypes.OPTIONAL_ADV_COMPONENT) {
                    EntityData<Optional<Component>> componentEntityData = (EntityData<Optional<Component>>) entityData;
                    componentEntityData.getValue().ifPresent(component -> componentEntityData.setValue(Optional.of(replaceColor(component))));
                }
            }
            metadata.write();
        }
        if (event.getPacketType() == PacketType.Play.Server.SCOREBOARD_OBJECTIVE) {
            WrapperPlayServerScoreboardObjective objective = new WrapperPlayServerScoreboardObjective(event);
            objective.setDisplayName(replaceColor(objective.getDisplayName()));
            objective.write();
        }
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_SCORE) {
            WrapperPlayServerUpdateScore updateScore = new WrapperPlayServerUpdateScore(event);
            updateScore.setEntityDisplayName(replaceColor(updateScore.getEntityDisplayName()));
            updateScore.write();
        }
    }

    public void processItem(ItemStack item) {
        item.getComponent(ComponentTypes.CUSTOM_NAME).ifPresent(component -> {
            item.setComponent(ComponentTypes.CUSTOM_NAME, replaceColor(component));
        });

        item.getComponent(ComponentTypes.LORE).ifPresent(lore -> {
            List<Component> components = new ArrayList<>();
            for (Component line : lore.getLines()) {
                components.add(replaceColor(line));
            }
            lore.setLines(components);
            item.setComponent(ComponentTypes.LORE, lore);
        });
    }
}
