package net.sf.l2j.mods.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.RecipeTable;
import net.sf.l2j.gameserver.model.L2ManufactureItem;
import net.sf.l2j.gameserver.model.L2ManufactureList;
import net.sf.l2j.gameserver.model.actor.Player.StoreType;
import net.sf.l2j.gameserver.model.item.RecipeList;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.RecipeShopMsg;
import net.sf.l2j.mods.actor.FakePlayer;
 
public interface ICrafter
{
	class CraftState
	{
		boolean crafting;
		long craftUntil;
		long nextAllowedCraft;
	}
	
	ConcurrentMap<FakePlayer, CraftState> STATES = new ConcurrentHashMap<>();
	
	long MIN_CRAFT_TIME = 5 * 60000; // 5 min
	long MAX_CRAFT_TIME = 5 * 60000; // 5 min
	
	long MIN_COOLDOWN = 30 * 60000; // 30 min
	long MAX_COOLDOWN = 60 * 60000; // 60 min
	
	public static int getRandomCommonInterludeRecipe()
	{
		return Rnd.get(1, 871); // inclui 1 e 871
	}
	
	public static List<RecipeList> getValidRecipes()
	{
		List<RecipeList> list = new ArrayList<>();
		
		for (RecipeList recipe : RecipeTable.getInstance().getAllRecipes())
		{
			int recipeId = getRandomCommonInterludeRecipe();
			
			if (recipe.getId() == recipeId)
			{
				list.add(recipe);
				break;
			}
			
		}
		return list;
	}
	
	default void handleManufacture(FakePlayer player)
	{
		if (player.isDead())
			return;
		
		if (!player.isInsideZone(ZoneId.TOWN))
			return;
		
		if (player.isBusy() || player.getTarget() != null)
			return;
		
		if (player.isPrivateSelling() || player.isPrivateBuying())
			return;
		
		CraftState st = STATES.computeIfAbsent(player, p -> new CraftState());
		long now = System.currentTimeMillis();
		
		// Loja ativa
		if (player.isPrivateManufactureing())
		{
			if (now >= st.craftUntil)
			{
				stopCraft(player, st);
			}
			return;
		}
		
		// Cooldown
		if (now < st.nextAllowedCraft)
			return;
		
		startCraft(player, st);
	}
	
	private static void startCraft(FakePlayer player, CraftState st)
	{
		L2ManufactureList list = new L2ManufactureList();
		
		int maxRecipesPerShop = Rnd.get(1, 4);
		
		List<RecipeList> available = new ArrayList<>(player.getDwarvenRecipeBook());
		Collections.shuffle(available);
		
		List<RecipeList> selectedRecipes = available.stream().limit(maxRecipesPerShop).collect(Collectors.toList());
		
		for (RecipeList recipe : selectedRecipes)
		{
			long price = calculateRecipePrice(recipe);
			list.add(new L2ManufactureItem(recipe.getId(), (int) price));
		}
		if (list.getList().isEmpty())
			return;
		
		list.setStoreName(buildCraftShopName(selectedRecipes));
		
		player.setCreateList(list);
		player.setStoreType(StoreType.MANUFACTURE);
		player.sitDown();
		player.broadcastUserInfo();
		player.broadcastPacket(new RecipeShopMsg(player));
		player.setPrivateManufactureing(true);
		long craftTime = Rnd.get(MIN_CRAFT_TIME, MAX_CRAFT_TIME);
		st.crafting = true;
		st.craftUntil = System.currentTimeMillis() + craftTime;
	}
	
	private static long calculateRecipePrice(RecipeList recipe)
	{
		var item = ItemTable.getInstance().getTemplate(recipe.getItemId());
		if (item == null)
			return 1000;
		
		long base = Math.max(1000, item.getReferencePrice());
		return base * Rnd.get(120, 180) / 100;
	}
	
	private static void stopCraft(FakePlayer player, CraftState st)
	{
		player.setCreateList(null);
		player.setStoreType(StoreType.NONE);
		player.standUp();
		player.broadcastUserInfo();
		player.setPrivateManufactureing(false);
		st.crafting = false;
		st.craftUntil = 0;
		st.nextAllowedCraft = System.currentTimeMillis() + Rnd.get(MIN_COOLDOWN, MAX_COOLDOWN);
		
	}
	
	private static String buildCraftShopName(List<RecipeList> recipes)
	{
		if (recipes.isEmpty())
			return "Craft Shop";
		
		List<String> names = new ArrayList<>();
		
		for (RecipeList recipe : recipes)
		{
			String baseName = extractBaseRecipeName(recipe);
			
			if (!names.contains(baseName))
				names.add(baseName);
			
			if (names.size() >= 4)
				break;
		}
		
		if (names.size() == 1)
			return names.get(0) + " Craft";
		
		return String.join(" / ", names);
	}
	
	private static String extractBaseRecipeName(RecipeList recipe)
	{
		String name = recipe.getRecipeName();
		if (name == null || name.isEmpty())
			return "Craft";
		
		// remove mk_
		if (name.startsWith("mk_"))
			name = name.substring(3);
		
		// pega apenas a primeira palavra
		String[] parts = name.split("_");
		if (parts.length == 0)
			return "Craft";
		
		String base = parts[0];
		
		// Capitaliza
		return Character.toUpperCase(base.charAt(0)) + base.substring(1);
	}
	
}
