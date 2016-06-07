package scripts.GrumMediumClueSolver.clues;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSTile;
import scripts.GrumMediumClueSolver.ClueException;
import scripts.GrumMediumClueSolver.Variables;

/**
 * Created by Graham on 01/06/2016.
 */
public class Cryptics {

    public static void clueSS() throws ClueException{
        System.out.println("Need to more data to solve clue ID"+Inventory.find("Clue scroll (medium)")[0].getID());
        throw new ClueException("Insuffient Data!");
    }

    //A town with a different sort of night life is your destination. Search for some crates in one of the houses.
    //3609
    //Location: Canafis
    //Tested
    public static void clue3609(){
        while(Inventory.find(3609).length != 0) {
            Inventory.find(12779)[0].click("Break");
            RSTile rsTile2 = new RSTile(3499, 3505, 0);
            try{
                Thread.sleep(3000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }
            while (!Player.getPosition().equals(new RSTile(3497, 3486, 0))) {
                Walking.walkTo(new RSTile(3497, 3486, 0));
            }
            while (!Player.getPosition().equals(new RSTile(3496, 3495, 0))) {
                Walking.walkTo(new RSTile(3496, 3495, 0));
            }
            while (!Player.getPosition().equals(rsTile2)) {
                Walking.walkTo(rsTile2);
            }

            while (Player.getPosition().distanceToDouble(rsTile2) < 5) {
                org.tribot.api2007.Objects.findNearest(5, "Crate")[0].click("Search");
                if(Inventory.find(3609).length == 0){
                    System.out.println("We have solved clue 3609!");
                    return;
                }
                try{
                    Thread.sleep(3000 + Variables.randomBetween(-100, 100));
                }
                catch (Exception e){
                    //Cheeky
                }
            }


        }
    }

}
