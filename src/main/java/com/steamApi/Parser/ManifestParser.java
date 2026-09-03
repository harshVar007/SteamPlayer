package com.steamApi.Parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManifestParser {


    public static long getGameSize(Path manifestPath) throws FileNotFoundException, IOException
    {

        String content = Files.readString(manifestPath);

        Pattern p = Pattern.compile("\"SizeOnDisk\"\\s+\"(\\d+)\"");
        Matcher m = p.matcher(content);

        if(m.find()){
            return Long.parseLong(m.group(1));
        }

        return 0;
    
    }

    

}
