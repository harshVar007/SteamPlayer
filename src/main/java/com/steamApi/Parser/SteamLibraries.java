package com.steamApi.Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.steamApi.Config.EnvConfig;
import com.steamApi.Model.Game;

public class SteamLibraries {

    private static EnvConfig envConfig = new EnvConfig();

    public static Map<String, String> getLibraryPath(Path steamRoot) throws Exception {
        
        Properties props = envConfig.getEnvProps();

        

        Game g = new Game();

        //Path vdf = steamRoot.resolve("steamapps/libraryfolders.vdf");

        List<Path> acfFiles = Files.list(Path.of(props.getProperty("STEAM_APPS_PATH"))).toList();

        //System.out.println("Reading .acf files from " + "/home/harshvardhan/.local/share/Steam/steamapps/");
        // String content = Files.readString(vdf);

        Map<String, String> res = new HashMap<>();

        for (Path path : acfFiles) {
            
            if(path.toString().contains(".acf")){

                String data = Files.readString(path);

                Pattern sizePattern = Pattern.compile("\"(SizeOnDisk)\"\\s+\"(\\d+)\"" , Pattern.MULTILINE);
                Matcher sizeMatcher = sizePattern.matcher(data);

                Pattern appidPattern = Pattern.compile("\"(appid)\"\\s+\"(\\d+)\"" , Pattern.MULTILINE);
                Matcher appidMatcher = appidPattern.matcher(data);

                while(appidMatcher.find() && sizeMatcher.find()){

                String appId = appidMatcher.group(2);
                Long appSize = Long.parseLong(sizeMatcher.group(2));
                

                System.out.println(appId + " : " + appSize);

                if(appSize > 0L)
                {
                g.setGameSize(String.format("%s", bytestoGigabytes(appSize).toString().substring(0, 5)));
                res.put(appId, g.getGameSize());

                }
                


            }

        }

        // Pattern libPattern = Pattern
        //         .compile("\"\\d+\"\\s*\\{([\\s\\S]*?)\\}", Pattern.MULTILINE);

        // Matcher libMatcher = libPattern.matcher(content);

        // while (libMatcher.find()) {

        //     String block = libMatcher.group(1);
        //     String path = extract(block, "path");

        //     if (path == null)
        //         continue;

        //     //Path libraryPath = Paths.get(path.replace("\\\\", "\\"));

        //     // extract apps block
        //     Pattern appsPattern = Pattern.compile("\"apps\"\\s*\\{([\\s*\"\\d]*)");

        //     Matcher appsMatcher = appsPattern.matcher(block);
        //     if (!appsMatcher.find()) {
        //         System.out.println("Couldn't find apps block");
        //         continue;
        //     }

        //     String appsBlock = appsMatcher.group(1);
        //     Pattern appPattern = Pattern.compile("\"(\\d+)\"\\s+\"(\\d+)\"");
        //     Matcher appMatcher = appPattern.matcher(appsBlock);

        //     while (appMatcher.find()) {
        //         String appId = appMatcher.group(1);
        //         long appSizeBytes = Long.parseLong(appMatcher.group(2));

        //         if(appSizeBytes > 0)
        //         g.setGameSize(String.format("%s", bytestoGigabytes(appSizeBytes).toString().substring(0, 5)));

        //         res.put(appId, g.getGameSize());

        //     }

        // }

        
    }
    
    return res;

}
    // private static String extract(String block, String key) {
    //     Pattern p = Pattern.compile("\"" + key + "\"\\s+\"([^\"]+)\"");
    //     Matcher m = p.matcher(block);
    //     return m.find() ? m.group(1) : null;

    // }

    public static Double bytestoGigabytes(Long bytes){
        return (double) (bytes/(1024.0*1024.0*1024.0));
    }

}

//     public static void main(String[] args) {

//         try {
//             SteamLibraries.getLibraryPath(Path.of("C:/Program Files (x86)/Steam"));
//         } catch (Exception ex) {
//         }
//     }
// }
