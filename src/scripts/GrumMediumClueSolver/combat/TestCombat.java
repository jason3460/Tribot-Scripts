package scripts.GrumMediumClueSolver.combat;

import org.tribot.api2007.*;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;

import java.util.Random;

/**
 * Created by Graham on 01/06/2016.
 */

public class TestCombat extends Script{

    @Override
    public void run(){
        while(true){
            getNewGuard();
            if(needToEat(12)){
                eat();
            }
            loot();
            try{
                Thread.sleep(50);
            }
            catch (Exception e){
                //Cheeky
            }

            if(Inventory.find("Clue Scroll (medium)").length != 0) {

                System.out.println("We found the clue scroll!");
                break;
            }
        }
    }

    public static void combatMaster9000(){
        while(true){
            getNewGuard();
            if(needToEat(12)){
                eat();
            }
            loot();
            try{
                Thread.sleep(50);
            }
            catch (Exception e){
                //Cheeky
            }

            if(Inventory.find("Clue Scroll (medium)").length != 0) {

                System.out.println("We found the clue scroll!");
                break;
            }
        }
    }

    private static int guardID1 = 3010;
    private static int guardID2 = 3011;

    private static boolean needToEat(int food){
        if(Player.getRSPlayer().getHealth() <= Player.getRSPlayer().getMaxHealth()-food+randomBetween(0,4)){
            return true;
        }
        return false;
    }

    private static boolean isGuard(int NPCID){
        return (NPCID == guardID1 || NPCID == guardID2);
    }

    private static int randomBetween(int min, int max){
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }


    private static void getNewGuard(){
        if(Player.getRSPlayer().isInCombat()){
            return;
        }
        for(RSNPC rsnpc : NPCs.findNearest(guardID1,guardID2)){
            if (isGuard(rsnpc.getID())){

                while(!rsnpc.isOnScreen()){
                    RSTile NPCTile = rsnpc.getPosition();
                    Walking.walkTo(NPCTile);
                    try{
                        Thread.sleep(3000 + randomBetween(-100,100));
                    }
                    catch (Exception e){
                        //Cheeky
                    }

                    Camera.turnToTile(rsnpc.getPosition());

                    try{
                        Thread.sleep(3000 + randomBetween(-100,100));
                    }
                    catch (Exception e){
                        //Cheeky
                    }

                }

                if(rsnpc.isInCombat()){
                    return;
                }

                rsnpc.click("Attack");

                try{
                    Thread.sleep(3000 + randomBetween(-100,100));
                }
                catch (Exception e){
                    //Cheeky
                }


//                while(rsnpc.click("Attack")){
//                    try{
//                        Thread.sleep(3000 + randomBetween(-100,100));
//                    }
//                    catch (Exception e){
//                        //Cheeky
//                    }
//                }

//                if(Player.getAnimation() == -1){
//                    System.out.println("Attack Failed!");
//                    getNewGuard();
//                }

            }

            if(Player.getRSPlayer().isInCombat()){
                return;
            }

        }
    }

    private static int foodID1 = 379;

    private static void eat(){
        RSItem[] lobster = Inventory.find(foodID1);
        for(RSItem rsItem : lobster){
            if(rsItem.getID() == foodID1){
                rsItem.click("Eat");
            }
        }
    }

    private static void loot(){

        RSGroundItem[] RsGroundItems = GroundItems.find("Clue scroll (medium)");

        if(RsGroundItems.length != 0){
            RsGroundItems[0].click("Take");
            try{
                        Thread.sleep(3000 + randomBetween(-100,100));
                    }
                    catch (Exception e){
                        //Cheeky
                    }
        }


    }
}