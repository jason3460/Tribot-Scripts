package scripts.GrumMediumClueSolver;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.script.Script;

/**
 * Created by Graham on 05/06/2016.
 */
public class textreader  extends Script{

    @Override
    public void run(){}

    public static String getText() throws ClueException{
        String text="";
        if(Inventory.find("Clue scroll (medium)").length == 0){
            throw new ClueException(("No clue in inventory!"));
        }else{
            Inventory.find("Clue scroll (medium)")[0].click("Read");
            try{
                Thread.sleep(3000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }
            if(Interfaces.get(203).isValid()) {
                text = Interfaces.get(203, 2).getText();
            }else{
                getText();
            }

        }
        return text;
    }

}
