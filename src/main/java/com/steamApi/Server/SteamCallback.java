
package com.steamApi.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.function.Consumer;

import com.sun.net.httpserver.HttpServer;

public class SteamCallback {

    private HttpServer server;

    public void start(Consumer<String> onSteamIdRecieved) throws IOException{

        System.out.println("Server Started with PID : "  + ProcessHandle.current().pid() + "\nPort 8088");

        server = HttpServer.create(new InetSocketAddress(8088), 1);

            server.createContext("/callback", exchange->{

                URI requestURI = exchange.getRequestURI();
                String queryVar = requestURI.getQuery();

                String steamId = extractSteamId(queryVar);
                //System.out.printf("SteamID : %s\n", steamId);

                String response = """
                     <html>
                     <style>
                     body{
                     
                     background-color : #445566;
                     color : white;

                     }
                     </style>
                  <body>
                    <h2>Login successful</h2>
                    <p>You can close the tab.</p>
                  </body>

                  <script>
                  

                  </script>

                </html>
                        """;

                exchange.sendResponseHeaders(200, response.getBytes().length);
                
                try(OutputStream os = exchange.getResponseBody()) {
                    os.write(
                        response.getBytes()
                    );
                } catch (Exception e) {
                }

                server.stop(0);
                System.out.println("\nServer Stopped");

                if(steamId !=null){
                    onSteamIdRecieved.accept(steamId);
                }
                
            });

        server.start();
    }

    private static String extractSteamId(String queryVar) {

        for(String param : queryVar.split("&")){

            if(param.startsWith("openid.claimed_id=")){
                String value = param.substring(param.lastIndexOf("/") + 1);
                return value;

            }
        }
        return null;
    }

}