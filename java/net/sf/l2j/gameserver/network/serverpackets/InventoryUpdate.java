package net.sf.l2j.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.model.item.instance.ItemInfo;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;

/**
 * @author Advi
 */
public class InventoryUpdate extends L2GameServerPacket
{
	private List<ItemInfo> _items;
	
	public InventoryUpdate()
	{
		_items = new ArrayList<>();
	}
	
	public InventoryUpdate(List<ItemInfo> items)
	{
		_items = items;
	}
	
	public void addItem(ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item));
	}
	
	public void addNewItem(ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item, 1));
	}
	
	public void addModifiedItem(ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item, 2));
	}
	
	public void addRemovedItem(ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item, 3));
	}
	
	public void addItems(List<ItemInstance> items)
	{
		if (items != null)
			for (ItemInstance item : items)
				if (item != null)
					_items.add(new ItemInfo(item));
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x27);
		writeH(_items.size());
		
		for (ItemInfo temp : _items)
		{
			if (temp == null || temp.getItem() == null)
				continue;
			
			Item item = temp.getItem();
			
			writeH(temp.getChange());
			writeH(item.getType1());
			writeD(temp.getObjectId());
			writeD(item.getItemId());
			writeD(temp.getCount());
			writeH(item.getType2());
			writeH(temp.getCustomType1());
			
			writeH(temp.getEquipped());
			writeD(item.getBodyPart());
			
			writeH(temp.getEnchant());
			writeH(temp.getCustomType2());
			writeD(temp.getAugmentationBoni());
			writeD(temp.getMana());
		}
		_items.clear();
		_items = null;
	}
}