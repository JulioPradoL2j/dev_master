package net.sf.l2j.event.bossevent;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.util.Broadcast;

public class KTBManager
{
	protected static final Logger _log = Logger.getLogger(KTBManager.class.getName());
	
	private static final SimpleDateFormat FORMAT_FULL = new SimpleDateFormat("EEE dd/MM HH:mm:ss z yyyy");
	
	private ScheduledFuture<?> _future; // future atual do task
	private KTBStartTask _task; // task reutilizado
	
	private KTBManager()
	{
		if (KTBConfig.KTB_EVENT_ENABLED)
		{
			KTBEvent.init();
			scheduleEventStart();
			_log.info("Kill The Boss Engine: is Started.");
		}
		else
		{
			_log.info("Kill The Boss Engine: Engine is disabled.");
		}
	}
	
	/*
	 * ========================= SCHEDULER (NEXT START) =========================
	 */
	public void scheduleEventStart()
	{
		try
		{
			cancelCurrentFuture();
			
			Calendar next = computeNextStartTime();
			if (next == null)
			{
				_log.warning("KTBEventEngine: No valid start time found. Check KTB_EVENT_INTERVAL.");
				return;
			}
			
			long now = System.currentTimeMillis();
			long startMs = next.getTimeInMillis();
			long delayMs = startMs - now;
			if (delayMs < 0)
				delayMs = 0;
			
			_log.info("KTBEventEngine: Next registration at " + FORMAT_FULL.format(next.getTime()) + " (in " + (delayMs / 1000) + "s)");
			
			_task = new KTBStartTask(startMs, Step.START_REG);
			_future = ThreadPool.schedule(_task, delayMs);
		}
		catch (Exception e)
		{
			_log.warning("KTBEventEngine: Error scheduling next start: " + e.getMessage());
		}
	}
	
	private static Calendar computeNextStartTime()
	{
		if (KTBConfig.KTB_EVENT_TIMES == null || KTBConfig.KTB_EVENT_TIMES.isEmpty())
			return null;
		
		Calendar now = Calendar.getInstance();
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		
		Calendar next = null;
		
		for (LocalTime time : KTBConfig.KTB_EVENT_TIMES)
		{
			Calendar candidate = Calendar.getInstance();
			candidate.set(Calendar.HOUR_OF_DAY, time.getHour());
			candidate.set(Calendar.MINUTE, time.getMinute());
			candidate.set(Calendar.SECOND, 0);
			candidate.set(Calendar.MILLISECOND, 0);
			
			if (candidate.before(now))
				candidate.add(Calendar.DAY_OF_MONTH, 1);
			
			if (next == null || candidate.before(next))
				next = candidate;
		}
		
		return next;
	}
	
	public String getNextTime()
	{
		Calendar c = computeNextStartTime();
		return (c != null) ? FORMAT_FULL.format(c.getTime()) : "Erro";
	}
	
	/*
	 * ========================= EVENT FLOW =========================
	 */
	public void startReg()
	{
		// registration open
		if (!KTBEvent.startParticipation())
		{
			Broadcast.gameAnnounceToOnlinePlayers("Kill The Boss: Event was cancelled.");
			_log.warning("KTBEventEngine: Error spawning event npc for participation.");
			scheduleEventStart();
			return;
		}
		
		Broadcast.gameAnnounceToOnlinePlayers("Kill The Boss: Joinable in " + KTBConfig.KTB_NPC_LOC_NAME + "!");
		if (KTBConfig.ALLOW_EVENT_KTB_COMMANDS)
			Broadcast.gameAnnounceToOnlinePlayers("Kill The Boss: Command: .ktbjoin / .ktbleave / .ktbinfo");
		
		// agenda o fim da registration -> START_EVENT
		long startMs = System.currentTimeMillis() + KTBConfig.KTB_EVENT_PARTICIPATION_TIME;
		scheduleStep(startMs, Step.START_EVENT);
	}
	
	public void startEvent()
	{
		// start fight
		if (!KTBEvent.startFight())
		{
			Broadcast.gameAnnounceToOnlinePlayers("Kill The Boss: Event cancelled due to lack of Participation.");
			_log.info("KTBEventEngine: Lack of registration, abort event.");
			scheduleEventStart();
			return;
		}
		
		KTBEvent.sysMsgToAllParticipants("Teleporting in " + KTBConfig.KTB_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
		
		// agenda fim do evento -> END_EVENT
		long endMs = System.currentTimeMillis() + (60000L * KTBConfig.KTB_EVENT_RUNNING_TIME);
		scheduleStep(endMs, Step.END_EVENT);
	}
	
	public void raidKilled()
	{
		Broadcast.gameAnnounceToOnlinePlayers(KTBEvent.calculateRewards());
		KTBEvent.sysMsgToAllParticipants("Teleporting back town in " + KTBConfig.KTB_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
		KTBEvent.stopFight();
		
		cancelCurrentFuture();
		scheduleEventStart();
	}
	
	public void endEvent()
	{
		Broadcast.gameAnnounceToOnlinePlayers("Kill The Boss: You all failed against the raid boss.");
		KTBEvent.sysMsgToAllParticipants("Teleporting back town in " + KTBConfig.KTB_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
		KTBEvent.stopFight();
		
		scheduleEventStart();
	}
	
	public void skipDelay()
	{
		if (_task == null)
			return;
		
		cancelCurrentFuture();
		// executa imediatamente o passo atual
		_task.setStartTime(System.currentTimeMillis());
		_future = ThreadPool.schedule(_task, 0);
	}
	
	/*
	 * ========================= INTERNAL SCHEDULING =========================
	 */
	private void scheduleStep(long startTimeMs, Step step)
	{
		cancelCurrentFuture();
		
		_task = new KTBStartTask(startTimeMs, step);
		long delayMs = startTimeMs - System.currentTimeMillis();
		if (delayMs < 0)
			delayMs = 0;
		
		_future = ThreadPool.schedule(_task, delayMs);
	}
	
	private void cancelCurrentFuture()
	{
		if (_future != null)
		{
			_future.cancel(false);
			_future = null;
		}
	}
	
	private enum Step
	{
		START_REG,
		START_EVENT,
		END_EVENT
	}
	
	/*
	 * ========================= TASK WITH ANNOUNCEMENTS =========================
	 */
	class KTBStartTask implements Runnable
	{
		private long _startTime;
		private final Step _step;
		
		public KTBStartTask(long startTime, Step step)
		{
			_startTime = startTime;
			_step = step;
		}
		
		public void setStartTime(long startTime)
		{
			_startTime = startTime;
		}
		
		@Override
		public void run()
		{
			long remainingMs = _startTime - System.currentTimeMillis();
			long remainingSec = Math.round(remainingMs / 1000.0);
			
			if (remainingSec > 0)
			{
				announce(remainingSec);
				
				long nextMsgSec = computeNextAnnounceDelaySeconds(remainingSec);
				long nextDelayMs = nextMsgSec * 1000L;
				
				// re-agenda a si mesmo para o próximo anúncio
				_future = ThreadPool.schedule(this, nextDelayMs);
				return;
			}
			
			switch (_step)
			{
				case START_REG:
					if (KTBEvent.isInactive())
						startReg();
					else
						scheduleEventStart();
					break;
				
				case START_EVENT:
					if (KTBEvent.isParticipating())
						startEvent();
					else
						scheduleEventStart();
					break;
				
				case END_EVENT:
					
					if (KTBEvent.isStarted())
						endEvent();
					else
						scheduleEventStart();
					break;
			}
		}
		
		private long computeNextAnnounceDelaySeconds(long remainingSec)
		{
			
			if (remainingSec > 3600)
				return remainingSec - 3600;
			if (remainingSec > 1800)
				return remainingSec - 1800;
			if (remainingSec > 900)
				return remainingSec - 900;
			if (remainingSec > 600)
				return remainingSec - 600;
			if (remainingSec > 300)
				return remainingSec - 300;
			if (remainingSec > 60)
				return remainingSec - 60;
			if (remainingSec > 5)
				return remainingSec - 5;
			return remainingSec;
		}
		
		private void announce(long timeSec)
		{
			
			if (timeSec >= 3600 && timeSec % 3600 == 0)
			{
				if (KTBEvent.isParticipating())
					Broadcast.gameAnnounceToOnlinePlayers("Kill The Boss: " + (timeSec / 3600) + " hour(s) until registration is closed!");
				else if (KTBEvent.isStarted())
					KTBEvent.sysMsgToAllParticipants((timeSec / 3600) + " hour(s) until event is finished!");
			}
			else if (timeSec >= 60)
			{
				if (KTBEvent.isParticipating())
					Broadcast.gameAnnounceToOnlinePlayers("Kill The Boss: " + (timeSec / 60) + " minute(s) until registration is closed!");
				else if (KTBEvent.isStarted())
					KTBEvent.sysMsgToAllParticipants((timeSec / 60) + " minute(s) until the event is finished!");
			}
			else
			{
				if (KTBEvent.isParticipating())
					Broadcast.gameAnnounceToOnlinePlayers("Kill The Boss: " + timeSec + " second(s) until registration is closed!");
				else if (KTBEvent.isStarted())
					KTBEvent.sysMsgToAllParticipants(timeSec + " second(s) until the event is finished!");
			}
		}
	}
	
	public static KTBManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final KTBManager _instance = new KTBManager();
	}
}
