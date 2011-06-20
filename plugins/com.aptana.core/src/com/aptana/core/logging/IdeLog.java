/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.IDebugScopes;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;

/**
 * Utility for logging IDE messages.
 * 
 * @author Spike Washburn
 */
public final class IdeLog
{

	private static Map<Plugin, List<IStatus>> earlyMessageCache = new HashMap<Plugin, List<IStatus>>();
	private static boolean caching = true;
	private static StatusLevel level = StatusLevel.ERROR;

	public static enum StatusLevel
	{
		OFF, ERROR, WARNING, INFO
	};

	/**
	 * Flushes any cached logging messages
	 */
	public static void flushCache()
	{
		caching = false;

		for (Map.Entry<Plugin, List<IStatus>> entry : earlyMessageCache.entrySet())
		{
			for (IStatus status : entry.getValue())
			{
				if (status.getSeverity() == IStatus.ERROR || isDebugging(entry.getKey(), status.getSeverity(), null))
				{
					log(entry.getKey(), status.getSeverity(), status.getMessage(), null, status.getException());
				}
			}
		}

		earlyMessageCache.clear();
	}

	/**
	 * Private constructor not to be used.
	 */
	private IdeLog()
	{

	}

	/**
	 * Get whether Studio is in debug mode
	 * 
	 * @return - true if debugging
	 */
	public static boolean isInDebugMode(int debugLevel)
	{
		if (caching)
		{
			return true;
		}
		else
		{
			if (level.equals(StatusLevel.OFF))
			{
				return false;
			}

			return level.compareTo(getStatusLevel(debugLevel)) >= 0;
		}
	}

	/**
	 * Returns the current severity preference
	 * 
	 * @return
	 */
	public static StatusLevel getCurrentSeverity()
	{
		return level;
	}

	/**
	 * Sets the current severity preference
	 */
	public static void setCurrentSeverity(StatusLevel severity)
	{
		level = severity;
	}

	/**
	 * Returns the current severity preference
	 * 
	 * @return
	 */
	public static StatusLevel getSeverityPreference()
	{
		try
		{
			return Enum.valueOf(
					StatusLevel.class,
					Platform.getPreferencesService().getString(CorePlugin.PLUGIN_ID,
							ICorePreferenceConstants.PREF_DEBUG_LEVEL, StatusLevel.ERROR.toString(), null));
		}
		catch (IllegalArgumentException ex)
		{
			return StatusLevel.ERROR;
		}
	}

	/**
	 * Is the particular scope in question enabled
	 * 
	 * @param plugin
	 * @param scope
	 * @return
	 */
	public static boolean isScopeEnabled(String scope)
	{
		if (scope != null)
		{
			return EclipseUtil.debugOptionActive(scope);
		}
		return true;
	}

	/**
	 * Are we debugging? a) we have debugging preference level met (i.e. logging INFO and listening to INFO) b) Plugin
	 * is debugging, and we
	 * 
	 * @return
	 */
	public static boolean isDebugging(Plugin plugin, int severity, String scope)
	{
		boolean inSeverity = isInDebugMode(severity);
		if (!inSeverity)
		{
			return false;
		}

		return !Platform.inDebugMode() || isScopeEnabled(scope);
	}

	/**
	 * Converts the IStatus level into something we get.
	 * 
	 * @param status
	 * @return
	 */
	private static StatusLevel getStatusLevel(int status)
	{
		switch (status)
		{
			case IStatus.INFO:
			{
				return StatusLevel.INFO;
			}
			case IStatus.WARNING:
			{
				return StatusLevel.WARNING;
			}
			case IStatus.ERROR:
			{
				return StatusLevel.ERROR;
			}
			default:
			{
				return StatusLevel.OFF;
			}
		}
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logError(Plugin plugin, String message, Throwable th)
	{
		log(plugin, IStatus.ERROR, message, null, th);
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logError(Plugin plugin, String message, String scope)
	{
		log(plugin, IStatus.ERROR, message, scope, null);
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 * @param th
	 */
	public static void logError(Plugin plugin, String message, Throwable th, String scope)
	{
		if (isDebugging(plugin, IStatus.ERROR, scope))
		{
			log(plugin, IStatus.ERROR, message, scope, th);
		}
		else
		{
			logTrace(plugin, IStatus.ERROR, message, scope, th);
		}
	}

	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logWarning(Plugin plugin, String message)
	{
		logWarning(plugin, message, null, null);
	}

	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logWarning(Plugin plugin, String message, String scope)
	{
		logWarning(plugin, message, null, scope);
	}

	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 * @param th
	 */
	public static void logWarning(Plugin plugin, String message, Throwable th, String scope)
	{
		if (isDebugging(plugin, IStatus.WARNING, scope))
		{
			log(plugin, IStatus.WARNING, message, scope, th);
		}
		else
		{
			logTrace(plugin, IStatus.INFO, message, scope, th);
		}
	}


	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 */
	public static void logInfo(Plugin plugin, String message)
	{
		logInfo(plugin, message, null, null);
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 */
	public static void logInfo(Plugin plugin, String message, String scope)
	{
		logInfo(plugin, message, null, scope);
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 * @param th
	 */
	public static void logInfo(Plugin plugin, String message, Throwable th, String scope)
	{
		if (isDebugging(plugin, IStatus.INFO, scope))
		{
			log(plugin, IStatus.INFO, message, scope, th);
		}
		else
		{
			logTrace(plugin, IStatus.INFO, message, scope, th);
		}
	}

	/**
	 * Trace this message out anyway if we are in debug mode
	 * 
	 * @param plugin
	 * @param message
	 * @param scope
	 * @param th
	 */
	private static void logTrace(Plugin plugin, int severity, String message, String scope, Throwable th)
	{
		if (isScopeEnabled(IDebugScopes.LOGGER))
		{
			boolean inSeverity = isInDebugMode(severity);

			String cause = StringUtil.EMPTY;
			if (!inSeverity)
			{
				cause = "Not logging items of current severity."; //$NON-NLS-1$
			}
			else if (!isScopeEnabled(scope))
			{
				cause = "Scope not enabled."; //$NON-NLS-1$
			}

			String traceMessage = StringUtil
					.format("(Build {0}) Skipping log of {1} {2} {3}. Cause: {4}", new String[] { EclipseUtil.getPluginVersion(plugin), //$NON-NLS-1$
									getLabel(severity), scope, message, cause });

			if (plugin != null)
			{
				Status logStatus = new Status(severity, plugin.getBundle().getSymbolicName(), IStatus.OK, traceMessage,
						th);
				plugin.getLog().log(logStatus);
			}
			else
			{
				System.err.println(traceMessage); // CHECKSTYLE:ON
			}
		}

	}

	/**
	 * Logs an item to the current plugin's log
	 * 
	 * @param plugin
	 * @param status
	 */
	public static void log(Plugin plugin, IStatus status)
	{
		log(plugin, status, null);
	}

	/**
	 * Logs an item to the current plugin's log
	 * 
	 * @param plugin
	 * @param status
	 * @param scope
	 */
	public static void log(Plugin plugin, IStatus status, String scope)
	{

		Throwable th = status.getException();
		if (plugin == null)
		{
			// CHECKSTYLE:OFF
			System.err.println(status.getMessage()); // CHECKSTYLE:ON
			if (th != null)
			{
				// CHECKSTYLE:OFF
				th.printStackTrace(); // CHECKSTYLE:ON
			}
			return;
		}

		if (!EclipseUtil.isPluginLoaded(plugin))
		{
			// CHECKSTYLE:OFF
			System.err.println(status.getMessage()); // CHECKSTYLE:ON
			return;
		}

		if (caching)
		{
			List<IStatus> statusMessages = null;
			if (earlyMessageCache.containsKey(plugin))
			{
				statusMessages = earlyMessageCache.get(plugin);
			}
			else
			{
				statusMessages = new ArrayList<IStatus>();
				earlyMessageCache.put(plugin, statusMessages);
			}
			statusMessages.add(status);
		}
		else
		{
			plugin.getLog().log(status);
		}
		if (status.getSeverity() == IStatus.ERROR && isDebugging(plugin, IStatus.ERROR, scope))
		{
			// dump the error to stderr so the devteam knows it happened
			// TODO: we should create a debug-mode flag that sets a custom
			// logger for all plugins.
			// CHECKSTYLE:OFF
			System.err.println(status.getMessage()); // CHECKSTYLE:ON

			if (th != null)
			{
				// CHECKSTYLE:OFF
				th.printStackTrace(); // CHECKSTYLE:ON
			}
		}

	}

	/**
	 * Logs an item to the current plugin's log
	 * 
	 * @param plugin
	 * @param status
	 * @param message
	 * @param th
	 */
	public static void log(Plugin plugin, int severity, String message, String scope, Throwable th)
	{
		String tempMessage = buildMessage(plugin, severity, message, scope, th);
		String symbolicName = CorePlugin.PLUGIN_ID;
		if (plugin != null && plugin.getBundle() != null)
		{
			symbolicName = plugin.getBundle().getSymbolicName();
		}
		Status logStatus = new Status(severity, symbolicName, IStatus.OK, tempMessage, th);
		log(plugin, logStatus, scope);
	}

	/**
	 * Constructs the message to log
	 * 
	 * @param plugin
	 * @param severity
	 * @param message
	 * @param scope
	 * @param th
	 * @return
	 */
	public static String buildMessage(Plugin plugin, int severity, String message, String scope, Throwable th)
	{
		if (scope == null)
		{
			scope = StringUtil.EMPTY;
		}

		String tempMessage = StringUtil.format(
				"(Build {0}) {1} {2} {3}", new String[] { EclipseUtil.getPluginVersion(plugin), //$NON-NLS-1$
						getLabel(severity), scope, message });

		return tempMessage;
	}

	/**
	 * @param status
	 * @return
	 */
	private static String getLabel(int status)
	{
		switch (status)
		{
			case IStatus.INFO:
			{
				return Messages.IdeLog_INFO;
			}
			case IStatus.WARNING:
			{
				return Messages.IdeLog_WARNING;
			}
			case IStatus.ERROR:
			{
				return Messages.IdeLog_ERROR;
			}
			default:
			{
				return Messages.IdeLog_UNKNOWN;
			}
		}
	}
}
