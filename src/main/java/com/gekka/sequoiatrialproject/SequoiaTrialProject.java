package com.gekka.sequoiatrialproject;

import com.gekka.sequoiatrialproject.command.GuildCommand;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequoiaTrialProject implements ModInitializer {
	public static final String MOD_ID = "sequoiatrialproject";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ClientCommandRegistrationCallback.EVENT.register(GuildCommand::register);
	}
}