package net.samagames.tsbot.database;

import net.samagames.tsbot.TSBot;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

/*
 * This file is part of SamaBot.
 *
 * SamaBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaBot.  If not, see <http://www.gnu.org/licenses/>.
 */
class Subscriber extends JedisPubSub
{
    private final HashMap<String, HashSet<IPacketsReceiver>> packetsReceivers = new HashMap<>();

    public void registerReceiver(String channel, IPacketsReceiver receiver)
    {
        HashSet<IPacketsReceiver> receivers = packetsReceivers.get(channel);
        if (receivers == null)
            receivers = new HashSet<>();
        receivers.add(receiver);
        this.subscribe(channel);
        packetsReceivers.put(channel, receivers);
    }

    @Override
    public void onMessage(String channel, String message)
    {
        try
        {
            HashSet<IPacketsReceiver> receivers = packetsReceivers.get(channel);
            if (receivers != null)
                receivers.forEach((IPacketsReceiver receiver) -> receiver.receive(channel, message));
            else
                TSBot.LOGGER.log(Level.WARNING, "{PubSub} Received message on a channel, but no packetsReceivers were found. (channel: " + channel + ", message: " + message + ")");
        } catch (Exception ignored)
        {
            ignored.printStackTrace();
        }

    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {}
}
