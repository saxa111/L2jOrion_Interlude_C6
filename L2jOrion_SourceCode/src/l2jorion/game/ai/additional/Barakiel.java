package l2jorion.game.ai.additional;

import l2jorion.game.model.quest.Quest;

/*
 * @author m095 (L2EmuRT)
 */

public class Barakiel extends Quest implements Runnable
{
	// Barakiel NpcID
	private static final int BARAKIEL = 25325;
	
	public Barakiel(final int questId, final String name, final String descr)
	{
		super(questId, name, descr);
		
		addEventId(BARAKIEL, Quest.QuestEventType.ON_ATTACK);
	}
	
	@Override
	public void run()
	{
	}
}
