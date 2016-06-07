package scripts.GrumMediumClueSolver.clues;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSTile;
import scripts.GrumMediumClueSolver.Variables;

/**
 * Created by Graham on 01/06/2016.
 */
public class Maps {

    //Rimmington Chemical Place
    //3602
    //Location: Home Teleport - Rimmington
    //Tested
    public static void clue3602(){
        while(Inventory.find(3602).length != 0){
            Inventory.find(8013)[0].click("Break");
            try{
                Thread.sleep(10000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }
            Objects.findNearest(5,"Portal")[0].click("Enter");
            try{
                Thread.sleep(3000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }

            Walking.blindWalkTo(new RSTile(2924,3213,0));
            try{
                Thread.sleep(2000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }

            while(!Player.getPosition().equals(new RSTile(2924,3210,0))){
                Walking.walkTo(new RSTile(2924,3210,0));
                try{
                    Thread.sleep(3000 + Variables.randomBetween(-100, 100));
                }
                catch (Exception e){
                    //Cheeky
                }
            }
            while (Inventory.find(3603).length == 0 && Player.getPosition().equals(new RSTile(2924,3210,0))) {
                Inventory.find(952)[0].click("Dig");
                try{
                    Thread.sleep(1000 + Variables.randomBetween(-100, 100));
                }
                catch (Exception e){
                    //Cheeky
                }
            }
            while(Inventory.find(3603).length == 1){
                Inventory.find(3603)[0].click("Open");
                try{
                    Thread.sleep(1000 + Variables.randomBetween(-100, 100));
                }
                catch (Exception e){
                    //Cheeky
                }
            }

        }
        System.out.println("We have solved clue 3602!");
    }

}
