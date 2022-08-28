package com.cssbham.cssbuildcompetition.util;

import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// yoinked from https://github.com/LMBishop/Quests/blob/master/bukkit/src/main/java/com/leonardobishop/quests/bukkit/command/TabHelper.java
public class TabHelper {

    public static List<String> matchTabComplete(String arg, List<String> options) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, options, completions);
        Collections.sort(completions);
        return completions;
    }

}
