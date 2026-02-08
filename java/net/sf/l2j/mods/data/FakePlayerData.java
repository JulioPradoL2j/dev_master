package net.sf.l2j.mods.data;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.mods.holder.FakeItem;
import net.sf.l2j.mods.holder.FakeTemplate;

import org.w3c.dom.Document;
 
public class FakePlayerData implements IXmlReader
{
	private final Map<Integer, FakeTemplate> _templates = new HashMap<>();
	
	private FakePlayerData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("data/mods/fakes.xml");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		// <fakes>
		forEach(doc, "fakes", fakesNode -> {
			// <fake>
			forEach(fakesNode, "fake", fakeNode -> {
				final StatsSet set = new StatsSet();
				
				set.set("classId", parseInteger(fakeNode.getAttributes(), "classId"));
				set.set("title", parseString(fakeNode.getAttributes(), "title", ""));
				
				// ===== STATS =====
				forEach(fakeNode, "stats", statsNode -> {
					set.set("level", parseInteger(statsNode.getAttributes(), "level", 1));
				});
				
				// ===== APPEARANCE =====
				final StatsSet appearance = new StatsSet();
				forEach(fakeNode, "appearance", appNode -> {
					appearance.set("hairStyle", parseInteger(appNode.getAttributes(), "hairStyle", 0));
					appearance.set("hairColor", parseInteger(appNode.getAttributes(), "hairColor", 0));
					appearance.set("face", parseInteger(appNode.getAttributes(), "face", 0));
					appearance.set("sex", parseString(appNode.getAttributes(), "sex", "male"));
				});
				set.set("appearance", appearance);
				
				final FakeTemplate template = new FakeTemplate(set);
				
				// ===== ITEMS =====
				forEach(fakeNode, "items", itemsNode -> {
					forEach(itemsNode, "item", itemNode -> {
						final int id = parseInteger(itemNode.getAttributes(), "id", 0);
						final int count = parseInteger(itemNode.getAttributes(), "count", 1);
						final boolean equipped = parseBoolean(itemNode.getAttributes(), "isEquipped", false);
						
						template.addItem(new FakeItem(id, count, equipped));
					});
				});
				
				_templates.put(template.getClassId(), template);
			});
		});
	}
	
	public FakeTemplate getTemplate(int classId)
	{
		return _templates.get(classId);
	}
	
	public Collection<FakeTemplate> getTemplates()
	{
		return _templates.values();
	}
	
	public static FakePlayerData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final FakePlayerData INSTANCE = new FakePlayerData();
	}
	
}