package net.sf.l2j.gameserver.datatables.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.model.holder.HerbDropHolder;
import net.sf.l2j.gameserver.model.item.DropCategory;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.templates.StatsSet;

import org.w3c.dom.Document;

public class HerbDropData implements IXmlReader
{
	private final Map<Integer, List<DropCategory>> _groups = new HashMap<>();
	private final Map<Integer, HerbDropHolder> _npcDrops = new HashMap<>();
	
	public HerbDropData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_groups.clear();
		_npcDrops.clear();
		
		parseFile("./data/xml/herbs_droplist.xml");
		
		LOGGER.info("HerbDropData: Loaded {" + _groups.size() + "} herb groups.");
		LOGGER.info("HerbDropData: Loaded {" + _npcDrops.size() + "} npc overrides.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", root -> {
			// ====== grupos base (formato antigo) ======
			forEach(root, "group", groupNode -> {
				final StatsSet gset = parseAttributes(groupNode);
				final int groupId = gset.getInteger("id", 0);
				if (groupId <= 0)
					return;
				
				final List<DropCategory> categories = _groups.computeIfAbsent(groupId, k -> new ArrayList<>());
				
				forEach(groupNode, "item", itemNode -> {
					final StatsSet set = parseAttributes(itemNode);
					
					final int itemId = set.getInteger("id", 0);
					final int categoryType = set.getInteger("category", 0);
					final int chance = set.getInteger("chance", 0);
					
					if (itemId <= 0 || chance <= 0)
						return;
					
					if (ItemTable.getInstance().getTemplate(itemId) == null)
					{
						LOGGER.warning("HerbDropData: Undefined itemId " + itemId + " in group " + groupId);
						return;
					}
					
					final DropData dd = new DropData();
					dd.setItemId(itemId);
					dd.setMinDrop(1);
					dd.setMaxDrop(1);
					dd.setChance(chance);
					
					DropCategory cat = categories.stream().filter(c -> c.getCategoryType() == categoryType).findFirst().orElse(null);
					
					if (cat == null)
					{
						cat = new DropCategory(categoryType);
						categories.add(cat);
					}
					
					cat.addDropData(dd, false);
				});
			});
			
			// ====== overrides por NPC ======
			forEach(root, "npc", npcNode -> {
				final StatsSet npcSet = parseAttributes(npcNode);
				final HerbDropHolder holder = new HerbDropHolder(npcSet);
				
				forEach(npcNode, "item", itemNode -> holder.addItem(parseAttributes(itemNode)));
				
				_npcDrops.put(holder.getNpcId(), holder);
			});
		});
	}
	
	public HerbDropHolder getOverride(int npcId)
	{
		return _npcDrops.get(npcId);
	}
	
	public List<DropCategory> getGroup(int groupId)
	{
		return _groups.get(groupId);
	}
	
	public List<DropCategory> resolveDrops(int npcId, int templateGroupId)
	{
		final HerbDropHolder ov = getOverride(npcId);
		
		// default antigo
		if (ov == null)
			return safeList(getGroup(templateGroupId));
		
		final int baseGroup = (ov.getBaseGroup() > 0) ? ov.getBaseGroup() : templateGroupId;
		
		// clone para não corromper cache global
		final List<DropCategory> result = new ArrayList<>();
		
		if (!ov.isReplace())
		{
			for (DropCategory baseCat : safeList(getGroup(baseGroup)))
				result.add(cloneCategory(baseCat));
		}
		
		// adiciona extras (merge ou replace)
		for (StatsSet set : ov.getItems())
		{
			final int itemId = set.getInteger("id", 0);
			final int categoryType = set.getInteger("category", 0);
			final int chance = set.getInteger("chance", 0);
			
			if (itemId <= 0 || chance <= 0)
				continue;
			
			if (ItemTable.getInstance().getTemplate(itemId) == null)
			{
				LOGGER.warning("HerbDropData: Undefined itemId " + itemId + " in npc " + npcId);
				continue;
			}
			
			final DropData dd = new DropData();
			dd.setItemId(itemId);
			dd.setMinDrop(1);
			dd.setMaxDrop(1);
			dd.setChance(chance);
			
			DropCategory cat = result.stream().filter(c -> c.getCategoryType() == categoryType).findFirst().orElse(null);
			
			if (cat == null)
			{
				cat = new DropCategory(categoryType);
				result.add(cat);
			}
			
			cat.addDropData(dd, false);
		}
		
		return result;
	}
	
	private static List<DropCategory> safeList(List<DropCategory> list)
	{
		return (list != null) ? list : List.of();
	}
	
	private static DropCategory cloneCategory(DropCategory src)
	{
		final DropCategory dst = new DropCategory(src.getCategoryType());
		for (DropData d : src.getAllDrops())
		{
			final DropData dd = new DropData();
			dd.setItemId(d.getItemId());
			dd.setMinDrop(d.getMinDrop());
			dd.setMaxDrop(d.getMaxDrop());
			dd.setChance(d.getChance());
			dst.addDropData(dd, false);
		}
		return dst;
	}
	
	public static HerbDropData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HerbDropData INSTANCE = new HerbDropData();
	}
}
