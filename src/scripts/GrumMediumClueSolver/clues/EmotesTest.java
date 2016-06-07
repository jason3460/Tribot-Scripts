package scripts.GrumMediumClueSolver.clues;

import org.tribot.api2007.Interfaces;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

/**
 * Created by Graham on 03/06/2016.
 */

@ScriptManifest(authors = "Oilborg",name = "TestEmotes",category = "Test")
public class EmotesTest extends Script{

    @Override
    public void run(){

        Interfaces.get(548, 33).click("Emotes");
    }
}
