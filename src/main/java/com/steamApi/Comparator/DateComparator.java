package com.steamApi.Comparator;


import java.util.Comparator;

import com.steamApi.Model.Game;


public class DateComparator implements Comparator<Game> {

    @Override
    public int compare(Game arg0, Game arg1) {
       
        String date1 = arg0.getLastPlayed();
        String date2 = arg1.getLastPlayed();

        if (date1.equals("Never") && date2.equals("Never"))
            return 0;

        if (date1.equals("Never"))
            return 1;

        if (date2.equals("Never"))
            return -1;

        return date2.compareTo(date1);
    }

    
}
