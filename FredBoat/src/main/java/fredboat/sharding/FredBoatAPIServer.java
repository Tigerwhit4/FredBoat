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

package fredboat.sharding;

import net.dv8tion.jda.JDA;
import org.springframework.boot.SpringApplication;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class FredBoatAPIServer {

    protected static String token;
    protected static JDA jda;
    public static FredBoatAPIServer ins = null;
    private final String[] args;
    public static final HashMap<String, String> HEADERS = new HashMap<>();

    public FredBoatAPIServer(JDA jda, String token, String[] args) {
        if (ins != null) {
            throw new IllegalStateException("Only one instance may exist.");
        }

        this.jda = jda;
        this.token = token;
        this.args = args;
        HEADERS.put("authorization", token);
        
        ins = this;
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        return request.getHeader("authorization") != null && request.getHeader("authorization").equals(token);
    }

    public void start() {
        SpringApplication.run(BootController.class, args);
    }

}
