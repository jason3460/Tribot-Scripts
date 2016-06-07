package scripts.GrumMediumClueSolver;

import org.tribot.api2007.Inventory;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import scripts.GrumMediumClueSolver.clues.Clue;
import scripts.GrumMediumClueSolver.farm.Farm;

/**
 * Created by Graham on 01/06/2016.
 */

@ScriptManifest(authors = "Oilborg", name ="Clue_Farmer_9000Pro" , category = "Bank")
public class Main extends Script{

    @Override
    public void run(){
        //TODO: add clues, add spade, work on combat, should be fine, need to bank lobs before clueswitch.
        //TODO: if it starts w/o invent open dont work

        while (true) {
            try {
                if (hasClue()) {
                    System.out.println(textreader.getText());
                    //If we have clue
                    Clue.switchClueID(Inventory.find("Clue Scroll (medium)")[0].getID());
                } else {
                    //If we don't have clue
                    Farm.prepForFarmAndFarm();
                }
            }
            catch (ClueException clueException){
                System.out.println(clueException.getMessage());
                break;
            }
            try{
                Thread.sleep(50);
            }
            catch (Exception e){
                //Cheeky
            }
        }

    }

    private static boolean hasClue(){
        return (Inventory.getCount("Clue Scroll (medium)") == 1);
    }
}
