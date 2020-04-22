package l2jorion.game.community.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import l2jorion.game.community.bb.Forum;
import l2jorion.util.CloseUtil;
import l2jorion.util.database.L2DatabaseFactory;

public class ForumsBBSManager extends BaseBBSManager
{
	private static final String LOAD_FORUMS = "SELECT forum_id FROM forums WHERE forum_type=0";
	
	private final Set<Forum> _forums = ConcurrentHashMap.newKeySet();
	
	private int _lastId = 1;
	
	protected ForumsBBSManager()
	{
		Connection con = null;
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_FORUMS);
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					addForum(new Forum(rs.getInt("forum_id"), null));
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Couldn't load forums root.", e);
		}
		finally
		{
			CloseUtil.close(con);
		}
	}
	
	public void initRoot()
	{
		for (Forum forum : _forums)
		{
			forum.vload();
		}
		
		LOG.info("Loaded {} forums.", _forums.size());
	}
	
	public void addForum(Forum forum)
	{
		if (forum == null)
		{
			return;
		}
		
		_forums.add(forum);
		
		if (forum.getId() > _lastId)
		{
			_lastId = forum.getId();
		}
	}
	
	public Forum createNewForum(String name, Forum parent, int type, int perm, int oid)
	{
		final Forum forum = new Forum(name, parent, type, perm, oid);
		forum.insertIntoDb();
		
		return forum;
	}
	
	public int getANewID()
	{
		return ++_lastId;
	}
	
	public Forum getForumByName(String name)
	{
		return _forums.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
	}
	
	public Forum getForumByID(int id)
	{
		return _forums.stream().filter(f -> f.getId() == id).findFirst().orElse(null);
	}
	
	public static ForumsBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ForumsBBSManager INSTANCE = new ForumsBBSManager();
	}
}