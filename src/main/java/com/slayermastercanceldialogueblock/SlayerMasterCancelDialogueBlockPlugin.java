package com.slayermastercanceldialogueblock;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
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
	private static final int CHATBOX_MULTI_INIT = 58;

	@Inject
	private Client client;

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
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == CHATBOX_MULTI_INIT)
		{
			replaceCancelOption();
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuAction() != MenuAction.WIDGET_CONTINUE
			|| WidgetUtil.componentToInterface(event.getParam1()) != InterfaceID.CHATMENU)
		{
			return;
		}

		Widget widget = event.getWidget();
		if (widget != null && isBlockedOption(widget.getText()))
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

			child.setText(BLOCKED_OPTION_TEXT);
			child.setOnKeyListener((Object[]) null);
			child.setHasListener(false);
			log.debug("Replaced slayer cancel dialogue option");
		}
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
