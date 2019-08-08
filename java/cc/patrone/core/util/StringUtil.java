package cc.patrone.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {

    public static String buildString(String[] args, int start) {
        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    public static List<String> getList(final String string) {
        final List<String> list = new ArrayList<String>();
        final String[] split;
        final String[] items = split = string.split(":");
        for (final String item : split) {
            list.add(item);
        }
        return list;
    }

    public static String listToString(final List<String> list) {
        String output = "";
        for (int i = 0; i < list.size(); ++i) {
            if (i + 1 < list.size()) {
                output = output + list.get(i) + ":";
            }
            else {
                output += list.get(i);
            }
        }
        return output;
    }
}
