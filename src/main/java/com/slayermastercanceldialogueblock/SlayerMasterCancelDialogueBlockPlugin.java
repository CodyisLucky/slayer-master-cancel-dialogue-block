package com.slayermastercanceldialogueblock;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Slayer Master Cancel Dialogue Block",
	description = "Replaces the post-assignment \"Can I cancel that task?\" chat option with a blocked notice.",
	tags = {"slayer", "task", "dialogue"}
)
public class SlayerMasterCancelDialogueBlockPlugin extends Plugin
{
	private static final String CANCEL_OPTION_PREFIX = "Can I cancel that task";
	private static final String BLOCKED_OPTION_TEXT = "Cancel option blocked by plugin";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Override
	protected void startUp() throws Exception
	{
		log.debug("Slayer Master Cancel Dialogue Block started");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("Slayer Master Cancel Dialogue Block stopped");
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() != InterfaceID.CHATMENU)
		{
			return;
		}

		clientThread.invokeLater(this::replaceCancelOption);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (isBlockedOptionClick(event))
		{
			event.consume();
			log.debug("Consumed slayer cancel dialogue click");
		}
	}

	private void replaceCancelOption()
	{
		Widget options = client.getWidget(InterfaceID.Chatmenu.OPTIONS);
		if (options == null)
		{
			return;
		}

		replaceMatching(options.getDynamicChildren());
		replaceMatching(options.getStaticChildren());
		replaceMatching(options.getNestedChildren());
	}

	private void replaceMatching(Widget[] children)
	{
		if (children == null)
		{
			return;
		}

		for (Widget child : children)
		{
			if (child == null || !isCancelTaskOption(child.getText()))
			{
				continue;
			}

			child.setHidden(false);
			child.setText(BLOCKED_OPTION_TEXT);
			child.setOnOpListener((Object[]) null);
			child.setOnKeyListener((Object[]) null);
			child.clearActions();
			child.setHasListener(false);
			log.debug("Replaced slayer cancel dialogue option");
		}
	}

	private boolean isBlockedOptionClick(MenuOptionClicked event)
	{
		if (isBlockedOption(event.getMenuOption()) || isBlockedOption(event.getMenuTarget()))
		{
			return true;
		}

		Widget widget = event.getWidget();
		return widget != null && isBlockedOption(widget.getText());
	}

	private static boolean isBlockedOption(String text)
	{
		return isCancelTaskOption(text) || isBlockedNotice(text);
	}

	private static boolean isCancelTaskOption(String text)
	{
		String plain = plainText(text);
		return plain.startsWith(CANCEL_OPTION_PREFIX);
	}

	private static boolean isBlockedNotice(String text)
	{
		return BLOCKED_OPTION_TEXT.equals(plainText(text));
	}

	private static String plainText(String text)
	{
		if (text == null || text.isEmpty())
		{
			return "";
		}

		return Text.removeTags(text).replace('\u00A0', ' ').trim();
	}
}
