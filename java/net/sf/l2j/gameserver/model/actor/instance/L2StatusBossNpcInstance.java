package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedBossSpawn; // Adicione a importação
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;

public class L2StatusBossNpcInstance extends L2NpcInstance
{
    public L2StatusBossNpcInstance(int objectId, NpcTemplate template)
    {
        super(objectId, template);
    }
    
    @Override
    public void showChatWindow(Player player)
    {
        // Aqui você chama o método para exibir a lista de bosses (com página 1 por padrão)
        VoicedBossSpawn.showBossListWindow(player, 1);
    }
}
