package com.slayermastercanceldialogueblock;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SlayerMasterCancelDialogueBlockPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SlayerMasterCancelDialogueBlockPlugin.class);
		RuneLite.main(args);
	}
}
