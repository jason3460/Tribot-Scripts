package scripts.GrumMediumClueSolver.farm;
import org.tribot.api.rs3.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.script.Script;
import scripts.GrumMediumClueSolver.Variables;
import scripts.GrumMediumClueSolver.combat.TestCombat;

/**
 * Created by Graham on 01/06/2016.
 */

public class Farm extends Script{

    @Override
    public void run(
    ){}

    public static void prepForFarmAndFarm(){
        if(Player.getPosition().distanceToDouble(Variables.getVarrockBank()) > 7.00){
            Inventory.find("Varrock teleport")[0].click("Break");
            Walking.walkTo(Variables.getVarrockBank());
        }
        Objects.findNearest(10,"Bank booth")[0].click("Bank");
        try{
            Thread.sleep(3000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }
        while (Banking.isBankScreenOpen()){
            Banking.depositAllExcept(562,556,8013,561,8009,8011,8010,8008,8007,3853,1712,2552,12779);
            Banking.withdraw(20, 379);
            Banking.close();
        }
        while(Inventory.getCount("Clue scroll (medium)") == 0 && Inventory.getCount(379) != 0) {
            while (Player.getPosition().distanceToDouble(Variables.getNpcLocation01()) < 12) {
                TestCombat.combatMaster9000();
            }
            Walking.walkTo(Variables.getNpcLocation01());
        }


    }
}