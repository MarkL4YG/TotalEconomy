package com.erigitic.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;

import java.io.IOException;
import java.io.StringWriter;

public class TestSerializeCommand implements CommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TestSerializeCommand.class);

    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .arguments(GenericArguments.none())
                .executor(new TestSerializeCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(itemStack -> {
                StringWriter writer = new StringWriter();

                try {
                    DataContainer container = itemStack.toContainer();
                    DataFormats.HOCON.writeTo(writer, container);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                logger.warn("Item in hand: \n{}", writer.toString());
            });
        }
        return CommandResult.empty();
    }
}
