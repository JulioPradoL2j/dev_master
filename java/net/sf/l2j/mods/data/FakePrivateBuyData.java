package net.sf.l2j.mods.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.mods.holder.PrivateBuyHolder;

import org.w3c.dom.Document;

 
public class FakePrivateBuyData implements IXmlReader
{
	private final Map<Integer, List<PrivateBuyHolder>> _listItems = new HashMap<>();
	
	protected FakePrivateBuyData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("data/mods/privateBuy.xml");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> {
			forEach(listNode, "items", privateSellNode -> {
				StatsSet privatesellAttributes = parseAttributes(privateSellNode);
				int id = privatesellAttributes.getInteger("id");
				
				forEach(privateSellNode, "item", itemNode -> {
					StatsSet itemAttributes = parseAttributes(itemNode);
					PrivateBuyHolder reward = new PrivateBuyHolder(itemAttributes);
					
					_listItems.computeIfAbsent(id, k -> new ArrayList<>()).add(reward);
				});
			});
		});
	}
	
	public int getRandomPackId()
	{
		List<Integer> packIds = new ArrayList<>(FakePrivateBuyData.getInstance()._listItems.keySet());
		return packIds.get(Rnd.get(packIds.size()));
	}
	
	public Map<Integer, List<PrivateBuyHolder>> getAllPackages()
	{
		return Collections.unmodifiableMap(_listItems);
	}
	
	public List<PrivateBuyHolder> getPrivateBuyId(int boxId)
	{
		return _listItems.getOrDefault(boxId, Collections.emptyList());
	}
	
	public static FakePrivateBuyData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FakePrivateBuyData INSTANCE = new FakePrivateBuyData();
	}
}
