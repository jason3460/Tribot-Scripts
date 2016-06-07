package scripts.GrumMediumClueSolver;

import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;

import java.util.Random;

/**
 * Created by Graham on 02/06/2016.
 */
public class Variables extends Script{

    @Override
    public void run(){}


    private static  RSTile varrockCentre = new RSTile(3211,3428,0);
    private static RSTile varrockBank = new RSTile(3183,3427,0);
    //Guards infront of varrock castle
    private static RSTile npcLocation01 = new RSTile(3212,3462,0);




    public static RSTile getVarrockCentre() {
        return varrockCentre;
    }

    public static RSTile getVarrockBank() {
        return varrockBank;
    }

    public static RSTile getNpcLocation01() {
        return npcLocation01;
    }

    public static int randomBetween(int min, int max){
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
