package scripts.GrumMediumClueSolver.clues;

import org.tribot.api2007.*;
import org.tribot.api2007.types.RSTile;
import scripts.GrumMediumClueSolver.Variables;

/**
 * Created by Graham on 01/06/2016.
 */
public class Emotes {

    private static int failures=0;

    private static int adamantSqShield = 1183;
    private static int mithrilPlateBody = 1121;
    private static int boneDagger = 8872;

    private static int gamesNecklaceCharge;

    //        if(Inventory.find("Games necklace(1)").length != 0){
//            gamesNecklaceCharge = 1;
//        }
//        if(Inventory.find("Games necklace(2)").length != 0){
//            gamesNecklaceCharge = 2;
//        }
//        if(Inventory.find("Games necklace(3)").length != 0){
//            gamesNecklaceCharge = 3;
//        }
//        if(Inventory.find("Games necklace(4)").length != 0){
//            gamesNecklaceCharge = 4;
//        }
//        if(Inventory.find("Games necklace(5)").length != 0){
//            gamesNecklaceCharge = 5;
//        }
//        if(Inventory.find("Games necklace(6)").length != 0){
//            gamesNecklaceCharge = 6;
//        }
//        if(Inventory.find("Games necklace(7)").length != 0){
//            gamesNecklaceCharge = 7;
//        }
//        if(Inventory.find("Games necklace(8)").length != 0){
//            gamesNecklaceCharge = 8;
//        }
//
//        Inventory.find("Games necklace("+gamesNecklaceCharge+")")[0].click("rub");
//        NPCChat.selectOption("Bar")



    //Cry on the shore of Catherby beach. Laugh before you talk to me,equip an adamant sq shield, a bone dagger and mithril platebody.
    //12027
    //Location: Camelot teleport - Catherby Beach
    //Tested
    public static void clue12027(){
        Inventory.find("Varrock Teleport")[0].click("Break");
        try{
            Thread.sleep(10000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }
        Walking.blindWalkTo(new RSTile(3184, 3435, 0));
        try{
            Thread.sleep(3000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }

        Objects.findNearest(10,"Bank booth")[0].click("Bank");
        try{
            Thread.sleep(3000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }
        Objects.findNearest(10,7409)[0].click("Bank");

        try{
            Thread.sleep(3000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }
        while (Banking.isBankScreenOpen()){
            Banking.withdraw(1,adamantSqShield);
            try{
                Thread.sleep(3000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }
            Banking.withdraw(1,mithrilPlateBody);
            try{
                Thread.sleep(3000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }
            Banking.withdraw(1, boneDagger);
            try{
                Thread.sleep(3000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }
            if(Inventory.find(adamantSqShield).length == 1 && Inventory.find(mithrilPlateBody).length == 1 && Inventory.find(boneDagger).length == 1) {
                Banking.close();
            }
        }
        try{
            Thread.sleep(1000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }
        Inventory.find("Camelot teleport")[0].click("Break");
        try{
            Thread.sleep(10000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }
        while (!Player.getPosition().equals(new RSTile(2853,3428,0))) {
            Walking.blindWalkTo(new RSTile(2853, 3428, 0));
        }

        while (!(Equipment.isEquipped(mithrilPlateBody) && Equipment.isEquipped(adamantSqShield) && Equipment.isEquipped(boneDagger))){
            Inventory.find(mithrilPlateBody)[0].click("Wear");
            try{
                Thread.sleep(1000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }
            Inventory.find(adamantSqShield)[0].click("Wield");
            try{
                Thread.sleep(1000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }
            Inventory.find(boneDagger)[0].click("Wield");
            try{
                Thread.sleep(1000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }
        }


        while(!GameTab.TABS.EMOTES.isOpen()) {
                Interfaces.get(548,33).click("Emotes");
                try{
                    Thread.sleep(3000 + Variables.randomBetween(-100, 100));
                }
                catch (Exception e){
                    //Cheeky
                }
        }
        try{
            Thread.sleep(3000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }
            Interfaces.get(216,1).getChild(16).click("Cry");
            try{
                Thread.sleep(3000 + Variables.randomBetween(-100, 100));
            }
            catch (Exception e){
                //Cheeky
            }


        Interfaces.get(216,1).getChild(9).click("Laugh");

        try{
            Thread.sleep(3000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }

        NPCs.find("Uri")[0].click("Talk-to");

        try{
            Thread.sleep(3000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }

        Inventory.find(12028)[0].click("Open");

        try{
            Thread.sleep(3000 + Variables.randomBetween(-100, 100));
        }
        catch (Exception e){
            //Cheeky
        }

        if(Inventory.find(12027).length == 0){
            System.out.println("We have solved clue 12027!");
            return;
        }
    }
}
