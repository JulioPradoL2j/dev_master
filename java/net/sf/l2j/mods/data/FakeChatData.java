package net.sf.l2j.mods.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.network.clientpackets.Say2;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

public class FakeChatData implements IXmlReader
{
	public enum ChatContext
	{
		ANY,
		CITY,
		FARM,
		COMBAT,
		DEAD
	}
	
	public final static class ChatTypeResolver
	{
		private ChatTypeResolver()
		{
		}
		
		public static int resolve(String name)
		{
			if (name == null)
				return Say2.ALL;
			
			String normalized = name.toUpperCase();
			
			// Aliases amigáveis para XML
			switch (normalized)
			{
				case "HERO":
				case "HERO_VOICE":
				case "HEROCHAT":
					return Say2.HERO_VOICE;
				
				case "TELL":
					return Say2.TELL;
				
				case "ALL":
					return Say2.ALL;
				
				case "TRADE":
					return Say2.TRADE;
					
				case "GLOBAL":
					return Say2.SHOUT;
			}
			
			// fallback pelo CHAT_NAMES oficial
			for (int i = 0; i < Say2.CHAT_NAMES.length; i++)
			{
				if (Say2.CHAT_NAMES[i].equalsIgnoreCase(normalized))
					return i;
			}
			
			throw new IllegalArgumentException("Invalid chat type: " + name);
		}
		
	}
	
	private static class ChatGroup
	{
		final int chance;
		final List<String> messages = new ArrayList<>();
		
		ChatGroup(int chance)
		{
			this.chance = chance;
		}
	}
	
	// chatType -> lang -> context -> groups
	private final Map<Integer, Map<String, Map<ChatContext, List<ChatGroup>>>> _data = new HashMap<>();
	
	private FakeChatData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_data.clear();
		parseFile("data/mods/fakeChat.xml");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc.getDocumentElement(), "chat", chatNode -> {
			NamedNodeMap chatAttrs = chatNode.getAttributes();
			String typeName = parseString(chatAttrs, "type", "ALL");
			int chatType = ChatTypeResolver.resolve(typeName);
			
			Map<String, Map<ChatContext, List<ChatGroup>>> langs = _data.computeIfAbsent(chatType, k -> new HashMap<>());
			
			forEach(chatNode, "language", langNode -> {
				NamedNodeMap langAttrs = langNode.getAttributes();
				String lang = parseString(langAttrs, "id", "pt_BR");
				
				Map<ChatContext, List<ChatGroup>> contexts = langs.computeIfAbsent(lang, k -> new HashMap<>());
				
				forEach(langNode, "context", ctxNode -> {
					NamedNodeMap ctxAttrs = ctxNode.getAttributes();
					ChatContext ctx = ChatContext.valueOf(parseString(ctxAttrs, "id", "ANY").toUpperCase());
					
					List<ChatGroup> groups = contexts.computeIfAbsent(ctx, k -> new ArrayList<>());
					
					forEach(ctxNode, "group", groupNode -> {
						int chance = parseInteger(groupNode.getAttributes(), "chance", 100);
						ChatGroup group = new ChatGroup(chance);
						
						forEach(groupNode, "msg", msgNode -> {
							String text = msgNode.getTextContent().trim();
							if (!text.isEmpty())
								group.messages.add(text);
						});
						
						if (!group.messages.isEmpty())
							groups.add(group);
					});
				});
			});
		});
	}
	
	public String getRandomResponse(int chatType, String lang, ChatContext ctx, Set<String> used)
	{
		Map<String, Map<ChatContext, List<ChatGroup>>> langs = _data.get(chatType);
		if (langs == null || langs.isEmpty())
			return null;
		
		// fallback de idioma
		Map<ChatContext, List<ChatGroup>> contexts = langs.getOrDefault(lang, langs.get(DEFAULT_LANG));
		
		if (contexts == null)
			return null;
		
		List<ChatGroup> groups = contexts.getOrDefault(ctx, contexts.get(ChatContext.ANY));
		
		if (groups == null || groups.isEmpty())
			return null;
		
		for (int i = 0; i < groups.size(); i++)
		{
			ChatGroup group = groups.get(Rnd.get(groups.size()));
			if (Rnd.get(100) > group.chance)
				continue;
			
			List<String> available = new ArrayList<>();
			for (String msg : group.messages)
				if (!used.contains(msg))
					available.add(msg);
				
			if (!available.isEmpty())
				return Rnd.get(available);
		}
		return null;
	}
	
	public Set<String> getAvailableLanguages(int chatType)
	{
		Map<String, ?> langs = _data.get(chatType);
		return langs != null ? langs.keySet() : Set.of(DEFAULT_LANG);
	}
	
	private static final String DEFAULT_LANG = "pt_BR";
	
	public static FakeChatData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final FakeChatData INSTANCE = new FakeChatData();
	}
}
