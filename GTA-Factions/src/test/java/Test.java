import com.google.common.collect.Lists;
import me.gtacraft.plugins.gangs.Gang;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Connor on 7/1/14. Designed for the GTA-Factions project.
 */

public class Test {

    public static void main(String[] args) {
        List<String> l = Arrays.asList("1a", "2a", "3a", "4a", "5a", "6a", "7a", "8a", "9a", "10a",
                                       "1b", "2b", "3b", "4b", "5b", "6b", "7b", "8b", "9b", "10b",
                                       "1c", "2c", "3c", "4c", "5c", "6c", "7c", "8c", "9c", "10c",
                                       "1d", "2d", "3d", "4d", "5d", "6d", "7d", "8d", "9d", "10d",
                                       "1e", "2e", "3e", "4e", "5e", "6e", "7e", "8e", "9e", "10e",
                                       "1f", "2f");

        int page = 7;
        page--;

        List<String> range = Lists.newArrayList();
        for (int i = page*8; i < (page*8)+8; i++) {
            if (i >= l.size())
                break;

            System.out.println(i);
            range.add(l.get(i));
        }

        System.out.println(range);
    }
}
