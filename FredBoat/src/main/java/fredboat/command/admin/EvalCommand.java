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

package fredboat.command.admin;

import fredboat.audio.PlayerRegistry;
import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.ICommandOwnerRestricted;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.*;

public class EvalCommand extends Command implements ICommandOwnerRestricted {

    //Thanks Dinos!
    private ScriptEngine engine;

    public EvalCommand() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval("var imports = new JavaImporter(java.io, java.lang, java.util);");

        } catch (ScriptException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, User author, Message message, String[] args) {
        JDA jda = guild.getJDA();
        String msg = message.getContent();

        channel.sendTyping();

        final String source = message.getRawContent().substring(args[0].length() + 1);

        engine.put("jda", jda);
        engine.put("api", jda);
        engine.put("channel", channel);
        engine.put("vc", PlayerRegistry.getExisting(guild) != null ? PlayerRegistry.getExisting(guild).getChannel() : null);
        engine.put("author", author);
        engine.put("bot", jda.getSelfInfo());
        engine.put("message", message);
        engine.put("guild", guild);
        engine.put("player", PlayerRegistry.getExisting(guild));

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = service.schedule(() -> {

            Object out = null;
            try {
                out = engine.eval(
                        "(function() {"
                        + "with (imports) {\n" + source + "\n}"
                        + "})();");

            } catch (Exception ex) {
                channel.sendMessage("`"+ex.getMessage()+"`");
                return;
            }

            String outputS;
            if (out == null) {
                outputS = ":ok_hand::skin-tone-3:";
            } else if (out.toString().contains("\n")) {
                outputS = "\nEval: ```\n" + out.toString() + "```";
            } else {
                outputS = "\nEval: `" + out.toString() + "`";
            }

            channel.sendMessage("```java\n"+source+"```" + "\n" + outputS);

        }, 0, TimeUnit.MILLISECONDS);

        Thread script = new Thread("Eval") {
            @Override
            public void run() {
                try {
                    future.get(10, TimeUnit.SECONDS);

                } catch (TimeoutException ex) {
                    future.cancel(true);
                    channel.sendMessage("Task exceeded time limit.");
                } catch (Exception ex) {
                    channel.sendMessage("`"+ex.getMessage()+"`");
                }
            }
        };
        script.start();
    }
}
