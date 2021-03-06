/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Frederik Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fredboat.command.music;

import fredboat.audio.GuildPlayer;
import fredboat.audio.PlayerRegistry;
import fredboat.audio.VideoSelection;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.IMusicCommand;
import fredboat.util.YoutubeAPI;
import fredboat.util.YoutubeVideo;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.Message.Attachment;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import org.json.JSONException;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayCommand extends Command implements IMusicCommand {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PlayCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        if (!message.getAttachments().isEmpty()) {
            GuildPlayer player = PlayerRegistry.get(guild.getId());
            player.setCurrentTC(channel);
            
            for (Attachment atc : message.getAttachments()) {
                player.queue(atc.getUrl(), channel, invoker);
            }
            
            player.setPause(false);
            
            return;
        }

        if (args.length < 2) {
            //channel.sendMessage("Proper syntax: ;;play <url-or-search-terms>");
            handleNoArguments(guild, channel, invoker, message);
            return;
        }

        //Search youtube for videos and let the user select a video
        if (!args[1].startsWith("http")) {
            searchForVideos(guild, channel, invoker, message, args);
            return;
        }

        GuildPlayer player = PlayerRegistry.get(guild.getId());
        player.setCurrentTC(channel);

        player.queue(args[1], channel, invoker);
        player.setPause(false);

        try {
            message.deleteMessage();
        } catch (Exception ex) {

        }
    }

    private void handleNoArguments(Guild guild, TextChannel channel, User invoker, Message message) {
        GuildPlayer player = PlayerRegistry.get(guild.getId());
        if (player.isQueueEmpty()) {
            channel.sendMessage("The player is not currently playing anything. Use the following syntax to add a song:\n;;play <url-or-search-terms>");
        } else if (player.isPlaying()) {
            channel.sendMessage("The player is already playing.");
        } else if (player.getUsersInVC().isEmpty()) {
            channel.sendMessage("There are no users in the voice chat.");
        } else {
            player.play();
            channel.sendMessage("The player will now play.");
        }
    }

    private void searchForVideos(Guild guild, TextChannel channel, User invoker, Message message, String[] args) {
        Matcher m = Pattern.compile("\\S+\\s+(.*)").matcher(message.getRawContent());
        m.find();
        String query = m.group(1);
        
        //Now remove all punctuation
        query = query.replaceAll("[.,/#!$%\\^&*;:{}=\\-_`~()]", "");

        Message outMsg = channel.sendMessage("Searching YouTube for `{q}`...".replace("{q}", query));

        ArrayList<YoutubeVideo> vids = null;
        try {
            vids = YoutubeAPI.searchForVideos(query);
        } catch (JSONException e) {
            channel.sendMessage("An error occurred when searching YouTube. Consider linking directly to audio sources instead.\n```\n;;play <url>```");
            log.debug("YouTube search exception", e);
            return;
        }

        if (vids.isEmpty()) {
            outMsg.updateMessage("No results for `{q}`".replace("{q}", query));
        } else {
            MessageBuilder builder = new MessageBuilder();
            builder.appendString("**Please select a video with the `;;select n` command:**");

            int i = 1;
            for (YoutubeVideo vid : vids) {
                builder.appendString("\n**")
                        .appendString(String.valueOf(i))
                        .appendString(":** ")
                        .appendString(vid.name)
                        .appendString(" (")
                        .appendString(vid.getDurationFormatted())
                        .appendString(")");
                i++;
            }

            outMsg.updateMessage(builder.build().getRawContent());

            GuildPlayer player = PlayerRegistry.get(guild.getId());
            player.setCurrentTC(channel);
            player.selections.put(invoker.getId(), new VideoSelection(vids, outMsg));
        }
    }

}
