/*
 ** The MIT License (MIT)
 ** Copyright (c) 2016 Frederik Mikkelsen
 *
 ** Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 ** The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 ** THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fredboat.command.util;

import fredboat.command.fun.TextCommand;

public class MusicHelpCommand extends TextCommand {

    public static final String MUSIC
            ="**This is the list of all the music commands this bot can execute:**.\n"
            +"**;;play**        Plays a track. You can either provide an url to a video or track (;;play <url>) or use it to search on youtube (;;play rick roll).\n"
            +"**;;list**        Display a list of the current tracks in the playlist.\n"
            +"**;;nowplaying**    Displays the currently playing track.\n"
            +"**;;skip**        Skips the current track. Please use in moderation.\n"
            +"**;;stop**        Stops the current track and ***clears*** the playlist. Reserved for moderators.\n"
            +"**;;pause**       Pauses the player.\n"
            +"**;;unpause**     Unpauses the player.\n"
            +"**;;join**        Makes the bot join your current voice channel.\n"
            +"**;;leave**       Makes the bot leave the current voice channel.\n"
            +"**;;repeat**      Toggles repeat mode for the current track.\n"
            +"**;;shuffle**     Toggles shuffle mode for the current queue.\n"
            +"**;;volume**      Changes the volume, volumes are 0-150, default volume is 100.\n"
            +"**;;music**       Posts this message.";

    public MusicHelpCommand() {
        super(MUSIC);
    }
}

//gib dem pats to me
