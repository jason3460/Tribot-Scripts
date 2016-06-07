package scripts.MediumClueSolver;

import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

import java.awt.*;


@ScriptManifest(authors={"JDoge"}, category="Combat", name="Medium Clue Farmer")
public class clueFarmer extends Script implements Painting
{
  private final RSTile FARM_SPOT_TILE = new RSTile(3213, 3463, 0);
  private final RSArea FARMING_SPOT = new RSArea(new RSTile[] { new RSTile(3223, 3459, 0),
                                                                new RSTile(3223, 3468, 0),
                                                                new RSTile(3220, 3468, 0),
                                                                new RSTile(3218, 3470, 0),
                                                                new RSTile(3207, 3470, 0),
                                                                new RSTile(3205, 3468, 0),
                                                                new RSTile(3202, 3468, 0),
                                                                new RSTile(3202, 3459, 0) });
  private final RSArea BANK_AREA = new RSArea(new RSTile[] { new RSTile(3186, 3433, 0),
                                                             new RSTile(3186, 3442, 0),
                                                             new RSTile(3180, 3442, 0),
                                                             new RSTile(3180, 3433, 0) });
  private final RSArea FARMING_SPOT_PATH = new RSArea(new RSTile[] { new RSTile(3180, 3433, 0),
                                                                     new RSTile(3180, 3428, 0),
                                                                     new RSTile(3216, 3428, 0),
                                                                     new RSTile(3216, 3438, 0),
                                                                     new RSTile(3214, 3440, 0),
                                                                     new RSTile(3214, 3458, 0),
                                                                     new RSTile(3211, 3458, 0),
                                                                     new RSTile(3211, 3440, 0),
                                                                     new RSTile(3211, 3438, 0),
                                                                     new RSTile(3205, 3438, 0),
                                                                     new RSTile(3205, 3431, 0),
                                                                     new RSTile(3201, 3431, 0),
                                                                     new RSTile(3200, 3432, 0) });
  private final int FOOD_ID = 379;
  private final String[] PICKUP_ITEM_NAMES = { "Clue scroll (medium)"};
  private RSItem[] equipment;
  private boolean haveItems = false;
  private boolean onClue = false;
  private ClueFarmerState SCRIPT_STATE;

  public void run()
  {
    println("Script has been started");
    Mouse.setSpeed(General.random(130, 150));

    int currentAngle = Camera.getCameraAngle();
    if (Math.abs(100 - currentAngle) > 10) {
      Camera.setCameraAngle(100);
    }
    WebWalking.setUseAStar(true);

    Walking.setControlClick(true);
    Walking.setWalkingTimeout(General.random(4000, 6000));

    General.useAntiBanCompliance(true);

    saveEquipment();
    for (int i = 0; i < this.equipment.length; i++)
    {
      int item = this.equipment[i].getID();
      println("Item: " + item);
    }
    for (;;)
    {
      this.SCRIPT_STATE = getState();
      switch (this.SCRIPT_STATE)
      {
        case DO_BANK:
          walkToFarmSpot();
          break;
        case FARM_CLUE:
          combat();
          break;
        case FARM_SPOT_TO_BANK:
          pickupLoot();
          break;
        case PICKUP_CLUE:
          WebWalking.walkToBank();
          break;
        case BANK_TO_FARM_SPOT:
          doBank();
          break;
        case SOLVE_CLUE:
          doClue();
          break;
      }
      sleep(50, 75);
    }
  }

  private ClueFarmerState getState()
  {
    if (!checkInventoryClue())
    {
      if (checkInventoryFood())
      {
        if (!pickupLoot())
        {
          if (atFarmSpot()) {
            return ClueFarmerState.FARM_CLUE;
          }
          return ClueFarmerState.BANK_TO_FARM_SPOT;
        }
        return ClueFarmerState.PICKUP_CLUE;
      }
      if (atFarmSpot()) {
        return ClueFarmerState.FARM_SPOT_TO_BANK;
      }
      return ClueFarmerState.DO_BANK;
    }
    return ClueFarmerState.SOLVE_CLUE;
  }

  private final Font font = new Font("Segoe UI", 0, 12);
  private long startTime = System.currentTimeMillis();

	public void onPaint(Graphics g1) {

      Graphics2D g = (Graphics2D)g1;

		long timeRan = System.currentTimeMillis() - startTime;

		g.setFont(font);

		g.setColor(new Color(0, 0, 0));
		g.drawString("Runtime: " + Timing.msToString(timeRan), 270, 370);
		g.drawString("State: " +SCRIPT_STATE, 270, 385);
	}

  private boolean doBank()
  {
    if (Banking.isBankScreenOpen())
    {
      if (!checkEquipment()) {
        withdrawEquipment();
      } else if (!checkInventoryFood()) {
        withdrawFood();
      }
    }
    else {
      openBank();
    }
    return false;
  }

  private boolean combat()
  {
    if (!checkHealth())
    {
      if (!inCombat()) {
        clickTarget();
      } else {
        sleep(50L);
      }
    }
    else if (checkHealth())
    {
      println("Eating food.");
      eatFood();
    }
    return false;
  }

  private boolean inCombat()
  {
    RSNPC[] guard = NPCs.findNearest(new String[] { "Guard" });
    if (guard.length == 0) {
      return false;
    }
    if (guard[0] == null) {
      return false;
    }
    if ((Player.getRSPlayer().isInCombat()) || (Player.getAnimation() != -1) ||
            (Combat.isUnderAttack()))
    {
      println("In Combat.");
      sleep(30, 60);
      return true;
    }
    return false;
  }

  private RSNPC findTarget()
  {
    RSNPC[] guard = NPCs.findNearest(new String[] { "Guard" });
    if (guard.length == 0) {
      return null;
    }
    if (guard[0] == null) {
      return null;
    }
    println("Finding Target.");
    RSNPC target = guard[0];
    for (int i = 0; i < guard.length; i++)
    {
      target = guard[i];
      if (validTarget(target) != null) {
        return target;
      }
      sleep(200L);
    }
    return null;
  }

  private RSNPC validTarget(RSNPC target)
  {
    if (this.FARMING_SPOT.contains(target.getPosition()))
    {
      if ((target.isOnScreen()) && (!target.isInCombat()) && (!target.isValid())) {
        return target;
      }
      if ((!target.isOnScreen()) && (!target.isInCombat()))
      {
        Walking.walkTo(target.getPosition());
        Camera.turnToTile(target.getPosition());
        General.sleep(450, 600);
        return target;
      }
    }
    return null;
  }

  private boolean clickTarget()
  {
    RSNPC target = findTarget();
    if (target == null) {
      return false;
    }
    if (DynamicClicking.clickRSNPC(target, "Attack ")) {
      Timing.waitCondition(new Condition()
      {
        public boolean active()
        {
          return false;
        }
      }, General.random(1000, 1500));
    }
    return false;
  }

  private boolean checkHealth()
  {
    println("checking health");
    if (Skills.getActualLevel(Skills.SKILLS.HITPOINTS) - Skills.getCurrentLevel(Skills.SKILLS.HITPOINTS) >= 12) {
      return true;
    }
    return false;
  }

  private boolean eatFood()
  {
    RSItem[] food = Inventory.find(new int[] { 379 });
    if ((food != null) && (food.length > 0))
    {
      food[0].click(new String[] { "Eat" });
      sleep(3500, 4000);
      return true;
    }
    return false;
  }

  private boolean pickupLoot()
  {
    RSGroundItem[] items = GroundItems.findNearest(this.PICKUP_ITEM_NAMES);
    if (items.length == 0) {
      return false;
    }
    RSItem[] food = Inventory.find(new int[] { 379 });
    if (food.length == 0) {
      return false;
    }
    if (items.length > 0)
    {
      if ((Player.getRSPlayer().getPosition().distanceTo(items[0].getPosition()) <= 15) && (!Player.getRSPlayer().isInCombat()) && (Player.getRSPlayer().getAnimation() == -1))
      {
        if (!Inventory.isFull())
        {
          items[0].click("Take " + items[0]);
          sleep(300, 700);
          return true;
        }
        food[0].click(new String[] { "Eat" });
        sleep(500, 800);
        items[0].click("Take " + items[0]);
        sleep(300, 700);
        return true;
      }
      return false;
    }
    return false;
  }

  private boolean saveEquipment()
  {
    this.equipment = Equipment.getItems();
    return (this.equipment != null) && (this.equipment.length > 0);
  }

  private boolean checkEquipment()
  {
    RSItem[] equipped = Equipment.getItems();
    if ((((equipped != null ? 1 : 0) & (equipped.length > 0 ? 1 : 0)) != 0) &&
            (equipped == this.equipment)) {
      return true;
    }
    return false;
  }

  private boolean withdrawEquipment()
  {
    for (int i = 0; i < this.equipment.length; i++)
    {
      int item = this.equipment[i].getID();
      Banking.withdraw(1, new int[] { item });
    }
    return false;
  }

  private boolean wearEquipment()
  {
    if (!Banking.isBankScreenOpen()) {
      for (int i = 0; i < this.equipment.length; i++)
      {
        RSItem[] item = Inventory.find(new int[] { this.equipment[i].getID() });
        if ((item != null) && (item.length > 0))
        {
          Clicking.click("Wear", item);
          sleep(100, 500);
        }
      }
    } else {
      Banking.close();
    }
    return false;
  }

  private boolean walkToFarmSpot()
  {
    if ((!atBank()) && (!atFarmWalk()) && (!checkInventoryClue())) {
      varrockTeleport();
    }
    if (WebWalking.walkTo(this.FARM_SPOT_TILE))
    {
      waitUntilIdle(this.FARM_SPOT_TILE);
      return true;
    }
    return false;
  }

  private boolean atFarmWalk()
  {
    RSTile myPos = Player.getPosition();
    return this.FARMING_SPOT_PATH.contains(myPos);
  }

  private boolean atFarmSpot()
  {
    RSTile myPos = Player.getPosition();
    return this.FARMING_SPOT.contains(myPos);
  }

  private boolean atBank()
  {
    RSTile myPos = Player.getPosition();
    return this.BANK_AREA.contains(myPos);
  }

  private boolean openBank()
  {
    Camera.setCameraAngle(General.random(75, 100));
    Camera.setCameraRotation(General.random(230, 280));

    RSObject[] banks = Objects.findNearest(7, new int[] { 11748 });
    if ((banks != null) && (banks.length > 0))
    {
      RSObject bank = banks[0];
      if (DynamicClicking.clickRSObject(bank, "Bank"))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1200, 1800))
        {
          if (Banking.isBankScreenOpen()) {
            return true;
          }
          sleep(20, 80);
          sleep(300, 600);
        }
      }
    }
    return false;
  }

  private boolean atLocation(RSTile location)
  {
    RSTile myPos = Player.getPosition();
    if (myPos.distanceTo(location) <= 3) {
      return true;
    }
    return false;
  }

  private boolean withdrawFood()
  {
    if (Banking.isBankScreenOpen())
    {
      RSItem[] food = Banking.find(new int[] { 379 });
      int count = Inventory.getAll().length;
      if ((food != null) && (food.length > 0)) {
        if (food[0].click(new String[] { "Withdraw-All" }))
        {
          long t = System.currentTimeMillis();
          while (Timing.timeFromMark(t) < General.random(1500, 2500))
          {
            if (Inventory.getAll().length > count) {
              return true;
            }
            sleep(20, 80);
          }
        }
      }
    }
    return false;
  }

  private boolean checkInventoryFood()
  {
    RSItem[] food = Inventory.find(new int[] { 379 });
    return (food != null) && (food.length > 0);
  }

  private boolean checkInventoryClue()
  {
    RSItem[] clue = Inventory.find(new String[] { "Clue scroll (medium)" });
    RSItem[] casket = Inventory.find(new String[] { "Casket (medium)" });
    if ((casket != null) && (casket.length > 0))
    {
      Clicking.click("Open", casket);
      sleep(100, 500);
    }
    return (clue != null) && (clue.length > 0);
  }

  private boolean waitUntilIdle(RSTile tile)
  {
    sleep(400, 800);
    long t = System.currentTimeMillis();
    while (Timing.timeFromMark(t) < General.random(400, 800))
    {
      if (Player.getPosition().distanceTo(tile) <= 1) {
        return true;
      }
      sleep(40, 80);
      if ((Player.isMoving()) || (Player.getAnimation() != -1)) {
        t = System.currentTimeMillis();
      }
    }
    return false;
  }

  private boolean withdrawItem(String itemName)
  {
    RSItem[] item = Banking.find(new String[] { itemName });
    int count = Inventory.getAll().length;
    if ((item != null) && (item.length > 0)) {
      if (item[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: " + itemName);
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    return false;
  }

  private boolean withdrawRingOfDueling()
  {
    RSItem[] ring1 = Banking.find(new String[] { "Ring of dueling(1)" });
    RSItem[] ring2 = Banking.find(new String[] { "Ring of dueling(2)" });
    RSItem[] ring3 = Banking.find(new String[] { "Ring of dueling(3)" });
    RSItem[] ring4 = Banking.find(new String[] { "Ring of dueling(4)" });
    RSItem[] ring5 = Banking.find(new String[] { "Ring of dueling(5)" });
    RSItem[] ring6 = Banking.find(new String[] { "Ring of dueling(6)" });
    RSItem[] ring7 = Banking.find(new String[] { "Ring of dueling(7)" });
    RSItem[] ring8 = Banking.find(new String[] { "Ring of dueling(8)" });

    int count = Inventory.getAll().length;
    if ((ring1 != null) && (ring1.length > 0)) {
      if (ring1[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Ring of dueling(1)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    if ((ring2 != null) && (ring2.length > 0)) {
      if (ring2[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Ring of dueling(2)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    if ((ring3 != null) && (ring3.length > 0)) {
      if (ring3[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Ring of dueling(3)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    if ((ring4 != null) && (ring4.length > 0)) {
      if (ring4[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Ring of dueling(4)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    if ((ring5 != null) && (ring5.length > 0)) {
      if (ring5[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Ring of dueling(5)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    if ((ring6 != null) && (ring6.length > 0)) {
      if (ring6[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Ring of dueling(6)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    if ((ring7 != null) && (ring7.length > 0)) {
      if (ring7[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Ring of dueling(7)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    if ((ring8 != null) && (ring8.length > 0)) {
      if (ring8[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Ring of dueling(8)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    return false;
  }

  private boolean checkRingOfDueling()
  {
    RSItem[] ring1 = Inventory.find(new String[] { "Ring of dueling(1)" });
    RSItem[] ring2 = Inventory.find(new String[] { "Ring of dueling(2)" });
    RSItem[] ring3 = Inventory.find(new String[] { "Ring of dueling(3)" });
    RSItem[] ring4 = Inventory.find(new String[] { "Ring of dueling(4)" });
    RSItem[] ring5 = Inventory.find(new String[] { "Ring of dueling(5)" });
    RSItem[] ring6 = Inventory.find(new String[] { "Ring of dueling(6)" });
    RSItem[] ring7 = Inventory.find(new String[] { "Ring of dueling(7)" });
    RSItem[] ring8 = Inventory.find(new String[] { "Ring of dueling(8)" });
    if ((ring1 != null) && (ring1.length > 0)) {
      return true;
    }
    if ((ring2 != null) && (ring2.length > 0)) {
      return true;
    }
    if ((ring3 != null) && (ring3.length > 0)) {
      return true;
    }
    if ((ring4 != null) && (ring4.length > 0)) {
      return true;
    }
    if ((ring5 != null) && (ring5.length > 0)) {
      return true;
    }
    if ((ring6 != null) && (ring6.length > 0)) {
      return true;
    }
    if ((ring7 != null) && (ring7.length > 0)) {
      return true;
    }
    if ((ring8 != null) && (ring8.length > 0)) {
      return true;
    }
    return false;
  }

  private boolean withdrawGlory()
  {
    RSItem[] glory1 = Banking.find(new String[] { "Amulet of glory(1)" });
    RSItem[] glory2 = Banking.find(new String[] { "Amulet of glory(2)" });
    RSItem[] glory3 = Banking.find(new String[] { "Amulet of glory(3)" });
    RSItem[] glory4 = Banking.find(new String[] { "Amulet of glory(4)" });

    int count = Inventory.getAll().length;
    if ((glory1 != null) && (glory1.length > 0)) {
      if (glory1[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Amulet of Glory(1)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    if ((glory2 != null) && (glory2.length > 0)) {
      if (glory2[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Amulet of Glory(2)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    if ((glory3 != null) && (glory3.length > 0)) {
      if (glory3[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Amulet of Glory(3)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    if ((glory4 != null) && (glory4.length > 0)) {
      if (glory4[0].click(new String[] { "Withdraw-1" }))
      {
        long t = System.currentTimeMillis();
        while (Timing.timeFromMark(t) < General.random(1500, 2500))
        {
          if (Inventory.getAll().length > count)
          {
            println("Withdrawing: Amulet of Glory(4)");
            return true;
          }
          sleep(20, 80);
        }
      }
    }
    return false;
  }

  private boolean checkGlory()
  {
    RSItem[] glory1 = Inventory.find(new String[] { "Amulet of glory(1)" });
    RSItem[] glory2 = Inventory.find(new String[] { "Amulet of glory(2)" });
    RSItem[] glory3 = Inventory.find(new String[] { "Amulet of glory(3)" });
    RSItem[] glory4 = Inventory.find(new String[] { "Amulet of glory(4)" });
    if ((glory1 != null) && (glory1.length > 0)) {
      return true;
    }
    if ((glory2 != null) && (glory2.length > 0)) {
      return true;
    }
    if ((glory3 != null) && (glory3.length > 0)) {
      return true;
    }
    if ((glory4 != null) && (glory4.length > 0)) {
      return true;
    }
    return false;
  }

  private boolean checkInventoryItem(String itemName)
  {
    RSItem[] item = Inventory.find(new String[] { itemName });
    return (item != null) && (item.length > 0);
  }

  private boolean wearItem(String itemName)
  {
    RSItem[] item = Inventory.find(new String[] { itemName });
    if ((item != null) && (item.length > 0))
    {
      Clicking.click("Wear", item);
      println("Equipping: " + itemName);
      sleep(100, 500);
    }
    return false;
  }

  private boolean dig(RSTile digSpot)
  {
    RSItem[] spade = Inventory.find(new String[] { "spade" });
    if ((spade != null) && (spade.length > 0)) {
      if (Player.getPosition().distanceTo(digSpot) <= 1)
      {
        Clicking.click("Dig", spade);
        println("Digging.");
        sleep(100, 500);
      }
      else
      {
        Walking.clickTileMS(digSpot, 1);
      }
    }
    return false;
  }

  private boolean doEmote(String emoteName)
  {
    String str;
    switch ((str = emoteName).hashCode())
    {
      case 66986:
        if (str.equals("Bow")) {}
        break;
      case 2583650:
        if (str.equals("Spin")) {
          break;
        }
        break;
      case 65798035:
        if (str.equals("Dance")) {}
      case 80778450:
        if ((str.equals("Think"))) {
          if ((GameTab.open(GameTab.TABS.EMOTES)) &&
                  (Interfaces.isInterfaceValid(464)))
          {
            RSInterfaceChild thinkInterface = Interfaces.get(464, 5);
            Clicking.click("Think", new Clickable[] { thinkInterface });
            println("Emote: Think");
            if (Player.getAnimation() == -1)
            {
              println("animation is -1");
              return true;
            }
            sleep(50);
            if ((GameTab.open(GameTab.TABS.EMOTES)) &&
                    (Interfaces.isInterfaceValid(464)))
            {
              RSInterfaceChild spinInterface = Interfaces.get(464, 15);
              Clicking.click("Spin", new Clickable[] { spinInterface });
              println("Emote: Spin");
              if (Player.getAnimation() == -1)
              {
                println("animation is -1");
                return true;
              }
              sleep(50);
              if ((GameTab.open(GameTab.TABS.EMOTES)) &&
                      (Interfaces.isInterfaceValid(464)))
              {
                RSInterfaceChild threeInterface = Interfaces.get(464, 3);
                Clicking.click("Bow", new Clickable[] { threeInterface });
                println("Emote: Bow");
                if (Player.getAnimation() == -1)
                {
                  println("animation is -1");
                  return true;
                }
                sleep(50);
                if ((GameTab.open(GameTab.TABS.EMOTES)) &&
                        (Interfaces.isInterfaceValid(464)))
                {
                  RSInterfaceChild fourInterface = Interfaces.get(464, 13);
                  Clicking.click("Dance", new Clickable[] { fourInterface });
                  println("Emote: Dance");
                  if (Player.getAnimation() == -1)
                  {
                    println("animation is -1");
                    return true;
                  }
                  sleep(50);
                }
              }
            }
          }
        }
        break;
    }
    return false;
  }

  private boolean varrockTeleport()
  {
    RSItem[] tab = Inventory.find(new String[] { "Varrock teleport" });
    if ((tab != null) && (tab.length > 0))
    {
      Clicking.click("Break", tab);
      println("Teleporting to Varrock.");
      sleep(1000, 2000);
    }
    return false;
  }

  private boolean camelotTeleport()
  {
    RSItem[] tab = Inventory.find(new String[] { "Camelot teleport" });
    if ((tab != null) && (tab.length > 0))
    {
      Clicking.click("Break", tab);
      println("Teleporting to Camelot.");
      sleep(1000, 2000);
    }
    return false;
  }

  private boolean lumbridgeTeleport()
  {
    RSItem[] tab = Inventory.find(new String[] { "Lumbridge teleport" });
    if ((tab != null) && (tab.length > 0))
    {
      Clicking.click("Break", tab);
      println("Teleporting to Lumbridge.");
      sleep(1000, 2000);
    }
    return false;
  }

  private boolean duelArenaTeleport()
  {
    RSItem[] rings1 = Inventory.find(new String[] { "Ring of dueling(1)" });
    RSItem[] rings2 = Inventory.find(new String[] { "Ring of dueling(2)" });
    RSItem[] rings3 = Inventory.find(new String[] { "Ring of dueling(3)" });
    RSItem[] rings4 = Inventory.find(new String[] { "Ring of dueling(4)" });
    RSItem[] rings5 = Inventory.find(new String[] { "Ring of dueling(5)" });
    RSItem[] rings6 = Inventory.find(new String[] { "Ring of dueling(6)" });
    RSItem[] rings7 = Inventory.find(new String[] { "Ring of dueling(7)" });
    RSItem[] rings8 = Inventory.find(new String[] { "Ring of dueling(8)" });
    if ((rings1 != null) && (rings1.length > 0))
    {
      if (Clicking.click("Rub", rings1))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          RSInterfaceChild duelArenaInterface = Interfaces.get(232, 1);
          Clicking.click("Continue", new Clickable[] { duelArenaInterface });
          println("Teleporting to Duel Arena.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings2 != null) && (rings2.length > 0))
    {
      if (Clicking.click("Rub", rings2))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          RSInterfaceChild duelArenaInterface = Interfaces.get(232, 1);
          Clicking.click("Continue", new Clickable[] { duelArenaInterface });
          println("Teleporting to Duel Arena.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings3 != null) && (rings3.length > 0))
    {
      if (Clicking.click("Rub", rings3))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          RSInterfaceChild duelArenaInterface = Interfaces.get(232, 1);
          Clicking.click("Continue", new Clickable[] { duelArenaInterface });
          println("Teleporting to Duel Arena.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings4 != null) && (rings4.length > 0))
    {
      if (Clicking.click("Rub", rings4))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          RSInterfaceChild duelArenaInterface = Interfaces.get(232, 1);
          Clicking.click("Continue", new Clickable[] { duelArenaInterface });
          println("Teleporting to Duel Arena.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings5 != null) && (rings5.length > 0))
    {
      if (Clicking.click("Rub", rings5))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          RSInterfaceChild duelArenaInterface = Interfaces.get(232, 1);
          Clicking.click("Continue", new Clickable[] { duelArenaInterface });
          println("Teleporting to Duel Arena.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings6 != null) && (rings6.length > 0))
    {
      if (Clicking.click("Rub", rings6))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          RSInterfaceChild duelArenaInterface = Interfaces.get(232, 1);
          Clicking.click("Continue", new Clickable[] { duelArenaInterface });
          println("Teleporting to Duel Arena.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings7 != null) && (rings7.length > 0))
    {
      if (Clicking.click("Rub", rings7))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          RSInterfaceChild duelArenaInterface = Interfaces.get(232, 1);
          Clicking.click("Continue", new Clickable[] { duelArenaInterface });
          println("Teleporting to Duel Arena.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings8 != null) && (rings8.length > 0) &&
            (Clicking.click("Rub", rings8)))
    {
      sleep(300, 700);
      if (Interfaces.isInterfaceValid(232))
      {
        RSInterfaceChild duelArenaInterface = Interfaces.get(232, 1);
        Clicking.click("Continue", new Clickable[] { duelArenaInterface });
        println("Teleporting to Duel Arena.");
        sleep(3000, 5000);
        return true;
      }
    }
    return false;
  }

  private boolean castleWarsTeleport()
  {
    RSItem[] rings1 = Inventory.find(new String[] { "Ring of dueling(1)" });
    RSItem[] rings2 = Inventory.find(new String[] { "Ring of dueling(2)" });
    RSItem[] rings3 = Inventory.find(new String[] { "Ring of dueling(3)" });
    RSItem[] rings4 = Inventory.find(new String[] { "Ring of dueling(4)" });
    RSItem[] rings5 = Inventory.find(new String[] { "Ring of dueling(5)" });
    RSItem[] rings6 = Inventory.find(new String[] { "Ring of dueling(6)" });
    RSItem[] rings7 = Inventory.find(new String[] { "Ring of dueling(7)" });
    RSItem[] rings8 = Inventory.find(new String[] { "Ring of dueling(8)" });
    if ((rings1 != null) && (rings1.length > 0))
    {
      if (Clicking.click("Rub", rings1))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          sleep(500, 1000);
          RSInterfaceChild castleWarsArenaInterface = Interfaces.get(232, 2);
          Clicking.click("Continue", new Clickable[] { castleWarsArenaInterface });
          println("Teleporting to Castle Wars.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings2 != null) && (rings2.length > 0))
    {
      if (Clicking.click("Rub", rings2))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          sleep(500, 1000);
          RSInterfaceChild castleWarsArenaInterface = Interfaces.get(232, 2);
          Clicking.click("Continue", new Clickable[] { castleWarsArenaInterface });
          println("Teleporting to Castle Wars.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings3 != null) && (rings3.length > 0))
    {
      if (Clicking.click("Rub", rings3))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          sleep(500, 1000);
          RSInterfaceChild castleWarsArenaInterface = Interfaces.get(232, 2);
          Clicking.click("Continue", new Clickable[] { castleWarsArenaInterface });
          println("Teleporting to Castle Wars.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings4 != null) && (rings4.length > 0))
    {
      if (Clicking.click("Rub", rings4))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          sleep(500, 1000);
          RSInterfaceChild castleWarsArenaInterface = Interfaces.get(232, 2);
          Clicking.click("Continue", new Clickable[] { castleWarsArenaInterface });
          println("Teleporting to Castle Wars.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings5 != null) && (rings5.length > 0))
    {
      if (Clicking.click("Rub", rings5))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          sleep(500, 1000);
          RSInterfaceChild castleWarsArenaInterface = Interfaces.get(232, 2);
          Clicking.click("Continue", new Clickable[] { castleWarsArenaInterface });
          println("Teleporting to Castle Wars.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings6 != null) && (rings6.length > 0))
    {
      if (Clicking.click("Rub", rings6))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          sleep(500, 1000);
          RSInterfaceChild castleWarsArenaInterface = Interfaces.get(232, 2);
          Clicking.click("Continue", new Clickable[] { castleWarsArenaInterface });
          println("Teleporting to Castle Wars.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings7 != null) && (rings7.length > 0))
    {
      if (Clicking.click("Rub", rings7))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(232))
        {
          sleep(500, 1000);
          RSInterfaceChild castleWarsArenaInterface = Interfaces.get(232, 2);
          Clicking.click("Continue", new Clickable[] { castleWarsArenaInterface });
          println("Teleporting to Castle Wars.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((rings8 != null) && (rings8.length > 0) &&
            (Clicking.click("Rub", rings8)))
    {
      sleep(300, 700);
      if (Interfaces.isInterfaceValid(232))
      {
        sleep(500, 1000);
        RSInterfaceChild castleWarsArenaInterface = Interfaces.get(232, 2);
        Clicking.click("Continue", new Clickable[] { castleWarsArenaInterface });
        println("Teleporting to Castle Wars.");
        sleep(3000, 5000);
        return true;
      }
    }
    return false;
  }

  private boolean edgevilleTeleport()
  {
    RSItem[] amulet1 = Inventory.find(new String[] { "Amulet of glory(1)" });
    RSItem[] amulet2 = Inventory.find(new String[] { "Amulet of glory(2)" });
    RSItem[] amulet3 = Inventory.find(new String[] { "Amulet of glory(3)" });
    RSItem[] amulet4 = Inventory.find(new String[] { "Amulet of glory(4)" });
    if ((amulet1 != null) && (amulet1.length > 0))
    {
      if (Clicking.click("Rub", amulet1))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild edgevilleInterface = Interfaces.get(234, 1);
          Clicking.click("Continue", new Clickable[] { edgevilleInterface });
          println("Teleporting to Edgeville.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet2 != null) && (amulet2.length > 0))
    {
      if (Clicking.click("Rub", amulet2))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild edgevilleInterface = Interfaces.get(234, 1);
          Clicking.click("Continue", new Clickable[] { edgevilleInterface });
          println("Teleporting to Edgeville.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet3 != null) && (amulet3.length > 0))
    {
      if (Clicking.click("Rub", amulet3))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild edgevilleInterface = Interfaces.get(234, 1);
          Clicking.click("Continue", new Clickable[] { edgevilleInterface });
          println("Teleporting to Edgeville.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet4 != null) && (amulet4.length > 0) &&
            (Clicking.click("Rub", amulet4)))
    {
      sleep(300, 700);
      if (Interfaces.isInterfaceValid(234))
      {
        RSInterfaceChild edgevilleInterface = Interfaces.get(234, 1);
        Clicking.click("Continue", new Clickable[] { edgevilleInterface });
        println("Teleporting to Edgeville.");
        sleep(3000, 5000);
        return true;
      }
    }
    return false;
  }

  private boolean karamjaTeleport()
  {
    RSItem[] amulet4 = Inventory.find(new String[] { "Amulet of glory(4)" });
    RSItem[] amulet3 = Inventory.find(new String[] { "Amulet of glory(3)" });
    RSItem[] amulet2 = Inventory.find(new String[] { "Amulet of glory(2)" });
    RSItem[] amulet1 = Inventory.find(new String[] { "Amulet of glory(1)" });
    if ((amulet4 != null) && (amulet4.length > 0))
    {
      if (Clicking.click("Rub", amulet4))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild karamjaInterface = Interfaces.get(234, 2);
          Clicking.click("Continue", new Clickable[] { karamjaInterface });
          println("Teleporting to Karamja.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet3 != null) && (amulet3.length > 0))
    {
      if (Clicking.click("Rub", amulet3))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild karamjaInterface = Interfaces.get(234, 2);
          Clicking.click("Continue", new Clickable[] { karamjaInterface });
          println("Teleporting to Karamja.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet2 != null) && (amulet2.length > 0))
    {
      if (Clicking.click("Rub", amulet2))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild karamjaInterface = Interfaces.get(234, 2);
          Clicking.click("Continue", new Clickable[] { karamjaInterface });
          println("Teleporting to Karamja.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet1 != null) && (amulet1.length > 0) &&
            (Clicking.click("Rub", amulet1)))
    {
      sleep(300, 700);
      if (Interfaces.isInterfaceValid(234))
      {
        RSInterfaceChild karamjaInterface = Interfaces.get(234, 2);
        Clicking.click("Continue", new Clickable[] { karamjaInterface });
        println("Teleporting to Karamja.");
        sleep(3000, 5000);
        return true;
      }
    }
    return false;
  }

  private boolean draynorTeleport()
  {
    RSItem[] amulet1 = Inventory.find(new String[] { "Amulet of glory(1)" });
    RSItem[] amulet2 = Inventory.find(new String[] { "Amulet of glory(2)" });
    RSItem[] amulet3 = Inventory.find(new String[] { "Amulet of glory(3)" });
    RSItem[] amulet4 = Inventory.find(new String[] { "Amulet of glory(4)" });
    if ((amulet1 != null) && (amulet1.length > 0))
    {
      if (Clicking.click("Rub", amulet1))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild draynorInterface = Interfaces.get(234, 3);
          Clicking.click("Continue", new Clickable[] { draynorInterface });
          println("Teleporting to Draynor.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet2 != null) && (amulet2.length > 0))
    {
      if (Clicking.click("Rub", amulet2))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild draynorInterface = Interfaces.get(234, 3);
          Clicking.click("Continue", new Clickable[] { draynorInterface });
          println("Teleporting to Draynor.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet3 != null) && (amulet3.length > 0))
    {
      if (Clicking.click("Rub", amulet3))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild draynorInterface = Interfaces.get(234, 3);
          Clicking.click("Continue", new Clickable[] { draynorInterface });
          println("Teleporting to Draynor.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet4 != null) && (amulet4.length > 0) &&
            (Clicking.click("Rub", amulet4)))
    {
      sleep(300, 700);
      if (Interfaces.isInterfaceValid(234))
      {
        RSInterfaceChild draynorInterface = Interfaces.get(234, 3);
        Clicking.click("Continue", new Clickable[] { draynorInterface });
        println("Teleporting to Draynor.");
        sleep(3000, 5000);
        return true;
      }
    }
    return false;
  }

  private boolean alkharidTeleport()
  {
    RSItem[] amulet1 = Inventory.find(new String[] { "Amulet of glory(1)" });
    RSItem[] amulet2 = Inventory.find(new String[] { "Amulet of glory(2)" });
    RSItem[] amulet3 = Inventory.find(new String[] { "Amulet of glory(3)" });
    RSItem[] amulet4 = Inventory.find(new String[] { "Amulet of glory(4)" });
    if ((amulet1 != null) && (amulet1.length > 0))
    {
      if (Clicking.click("Rub", amulet1))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild alkharidInterface = Interfaces.get(234, 4);
          Clicking.click("Continue", new Clickable[] { alkharidInterface });
          println("Teleporting to Al Kharid.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet2 != null) && (amulet2.length > 0))
    {
      if (Clicking.click("Rub", amulet2))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild alkharidInterface = Interfaces.get(234, 4);
          Clicking.click("Continue", new Clickable[] { alkharidInterface });
          println("Teleporting to Al Kharid.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet3 != null) && (amulet3.length > 0))
    {
      if (Clicking.click("Rub", amulet3))
      {
        sleep(300, 700);
        if (Interfaces.isInterfaceValid(234))
        {
          RSInterfaceChild alkharidInterface = Interfaces.get(234, 4);
          Clicking.click("Continue", new Clickable[] { alkharidInterface });
          println("Teleporting to Al Kharid.");
          sleep(3000, 5000);
          return true;
        }
      }
    }
    else if ((amulet4 != null) && (amulet4.length > 0) &&
            (Clicking.click("Rub", amulet4)))
    {
      sleep(300, 700);
      if (Interfaces.isInterfaceValid(234))
      {
        RSInterfaceChild alkharidInterface = Interfaces.get(234, 4);
        Clicking.click("Continue", new Clickable[] { alkharidInterface });
        println("Teleporting to Al Kharid.");
        sleep(3000, 5000);
        return true;
      }
    }
    return false;
  }

  public void objectAction(int clickattempts, String objectname, String action, int range, int delay)
  {
    RSObject[] objectactionarray = Objects.findNearest(range, new String[] { objectname });
    if ((objectactionarray != null) && (objectactionarray.length > 0))
    {
      RSObject object = objectactionarray[0];

      Timing.waitCondition(new Condition()
      {
        public boolean active()
        {
          General.sleep(100, 150);
          return Player.isMoving();
        }
      }, General.random(9000, 12000));
      if (!object.isOnScreen())
      {
        Walking.walkTo(object.getPosition());

        Timing.waitCondition(new Condition()
        {
          public boolean active()
          {
            General.sleep(100, 150);
            return Player.isMoving();
          }
        }, General.random(9000, 12000));
      }
      object.hover();
      sleep(10, 20);
      for (int ik = 0; ik < clickattempts; ik++)
      {
        if (DynamicClicking.clickRSObject(object, action + " " + objectname))
        {
          sleep(100, 150);
          break;
        }
        Camera.setCameraRotation(Camera.getCameraRotation() + General.random(0, 60));

        sleep(100, 150);
      }
      sleep(delay);
    }
  }

  private boolean checkForNPC(String npcName)
  {
    RSNPC[] npc = NPCs.find(new String[] { npcName });
    if ((npc != null) && (npc.length > 0)) {
      return true;
    }
    return false;
  }

  private boolean talkToNPC(String npcName)
  {
    RSNPC[] npc = NPCs.findNearest(new String[] { npcName });
    if ((npc != null) && (npc.length > 0) && (DynamicClicking.clickRSNPC(npc[0], "Talk"))) {
      return true;
    }
    return false;
  }

  private boolean doClue()
  {
    return false;
  }

  private boolean doBank_2801()
  {
    if (Banking.isBankScreenOpen())
    {
      if (!checkInventoryItem("Lumbridge teleport"))
      {
        withdrawItem("Lumbridge teleport");
      }
      else if (!checkInventoryItem("Varrock teleport"))
      {
        withdrawItem("Varrock teleport");
      }
      else
      {
        Banking.close();
        this.haveItems = true;
      }
    }
    else
    {
      Banking.openBank();
      Banking.depositAllExcept(new String[] {"Clue scroll (medium)" });
    }
    return false;
  }

  private boolean doClue_2801()
  {
    RSTile DIG_SPOT = new RSTile(3161, 3250, 0);
    if ((atBank()) && (this.haveItems))
    {
      lumbridgeTeleport();
      sleep(500, 1500);
      this.onClue = true;
      if (waitUntilIdle(DIG_SPOT))
      {
        WebWalking.walkTo(DIG_SPOT);
      }
      else
      {
        dig(DIG_SPOT);
        sleep(300L);
      }
      this.haveItems = false;
      this.onClue = false;
    }
    else if ((atBank()) && (!this.haveItems))
    {
      doBank_2801();
    }
    else if ((!atBank()) && (!atFarmSpot()) && (!atFarmWalk()) && (!this.haveItems))
    {
      varrockTeleport();
    }
    return false;
  }

  private boolean doBank_2805()
  {
    if (Banking.isBankScreenOpen())
    {
      if (!checkInventoryItem("Sextant"))
      {
        withdrawItem("Sextant");
      }
      else if (!checkInventoryItem("Chart"))
      {
        withdrawItem("Chart");
      }
      else if (!checkInventoryItem("Watch"))
      {
        withdrawItem("Watch");
      }
      else if (!checkInventoryItem("Spade"))
      {
        withdrawItem("Spade");
      }
      else if (!checkGlory())
      {
        withdrawGlory();
      }
      else if (!checkInventoryItem("Varrock teleport"))
      {
        withdrawItem("Varrock teleport");
      }
      else
      {
        Banking.close();
        this.haveItems = true;
      }
    }
    else
    {
      Banking.openBank();
      Banking.depositAllExcept(new String[] {"Clue scroll (medium)", "Sextant", "Chart", "Watch", "Spade" });
    }
    return false;
  }

  private boolean doClue_2805()
  {
    RSTile ROPESWING = new RSTile(2709, 3209, 0);
    RSTile DIG_SPOT = new RSTile(2698, 3207, 0);
    if ((atBank()) && (this.haveItems))
    {
      if (atBank()) {
        karamjaTeleport();
      } else if (waitUntilIdle(ROPESWING)) {
        WebWalking.walkTo(ROPESWING);
      } else {
        objectAction(5, "Ropeswing", "Swing-on", 5, 2000);
      }
      if (waitUntilIdle(DIG_SPOT))
      {
        WebWalking.walkTo(DIG_SPOT);
      }
      else
      {
        dig(DIG_SPOT);
        sleep(300L);
      }
      this.haveItems = false;
    }
    else if ((atBank()) && (!this.haveItems))
    {
      doBank_2805();
    }
    else if ((!atBank()) && (!atFarmSpot()) && (!atFarmWalk()) && (!this.haveItems))
    {
      varrockTeleport();
    }
    return false;
  }

  private boolean doBank_2817()
  {
    if (Banking.isBankScreenOpen())
    {
      if (!checkInventoryItem("Sextant"))
      {
        withdrawItem("Sextant");
      }
      else if (!checkInventoryItem("Chart"))
      {
        withdrawItem("Chart");
      }
      else if (!checkInventoryItem("Watch"))
      {
        withdrawItem("Watch");
      }
      else if (!checkInventoryItem("Spade"))
      {
        withdrawItem("Spade");
      }
      else if (!checkGlory())
      {
        withdrawGlory();
      }
      else if (!checkInventoryItem("Varrock teleport"))
      {
        withdrawItem("Varrock teleport");
      }
      else
      {
        Banking.close();
        this.haveItems = true;
      }
    }
    else
    {
      Banking.openBank();
      Banking.depositAllExcept(new String[] {"Clue scroll (medium)", "Sextant", "Chart", "Watch", "Spade" });
    }
    return false;
  }

  private boolean doClue_2817()
  {
    RSTile DIG_SPOT = new RSTile(2849, 3032, 0);
    if ((atBank()) && (this.haveItems))
    {
      if (atBank())
      {
        karamjaTeleport();
      }
      else if (!waitUntilIdle(DIG_SPOT))
      {
        WebWalking.walkTo(DIG_SPOT);
      }
      else
      {
        dig(DIG_SPOT);
        sleep(2000L);
      }
      this.haveItems = false;
      return true;
    }
    if ((atBank()) && (!this.haveItems)) {
      doBank_2817();
    } else if ((!atBank()) && (!atFarmSpot()) && (!atFarmWalk()) && (!this.haveItems)) {
      varrockTeleport();
    }
    return false;
  }

  private boolean doBank_3588()
  {
    if (Banking.isBankScreenOpen())
    {
      if (!checkInventoryItem("Sextant"))
      {
        withdrawItem("Sextant");
      }
      else if (!checkInventoryItem("Chart"))
      {
        withdrawItem("Chart");
      }
      else if (!checkInventoryItem("Watch"))
      {
        withdrawItem("Watch");
      }
      else if (!checkInventoryItem("Spade"))
      {
        withdrawItem("Spade");
      }
      else if (!checkGlory())
      {
        withdrawGlory();
      }
      else if (!checkInventoryItem("Varrock teleport"))
      {
        withdrawItem("Varrock teleport");
      }
      else
      {
        Banking.close();
        this.haveItems = true;
      }
    }
    else
    {
      Banking.openBank();
      Banking.depositAllExcept(new String[] {"Clue scroll (medium)", "Sextant", "Chart", "Watch", "Spade" });
    }
    return false;
  }

  private boolean doClue_3588()
  {
    RSTile DIG_SPOT = new RSTile(2887, 3155, 0);
    if ((atBank()) && (this.haveItems))
    {
      if (atBank())
      {
        karamjaTeleport();
        this.onClue = true;
      }
      else if (waitUntilIdle(DIG_SPOT))
      {
        WebWalking.walkTo(DIG_SPOT);
      }
      else
      {
        dig(DIG_SPOT);
        sleep(300L);
        this.haveItems = false;
        this.onClue = false;
      }
    }
    else if ((atBank()) && (!this.haveItems)) {
      doBank_3588();
    } else if ((!atBank()) && (!atFarmSpot()) && (!atFarmWalk()) && (!this.haveItems)) {
      varrockTeleport();
    }
    return false;
  }

  private boolean doBank_3594()
  {
    if (Banking.isBankScreenOpen())
    {
      if (!checkInventoryItem("Sextant"))
      {
        withdrawItem("Sextant");
      }
      else if (!checkInventoryItem("Chart"))
      {
        withdrawItem("Chart");
      }
      else if (!checkInventoryItem("Watch"))
      {
        withdrawItem("Watch");
      }
      else if (!checkInventoryItem("Spade"))
      {
        withdrawItem("Spade");
      }
      else if (!checkInventoryItem("Camelot teleport"))
      {
        withdrawItem("Camelot teleport");
      }
      else if (!checkInventoryItem("Varrock teleport"))
      {
        withdrawItem("Varrock teleport");
      }
      else
      {
        Banking.close();
        this.haveItems = true;
      }
    }
    else
    {
      Banking.openBank();
      Banking.depositAllExcept(new String[] {"Clue scroll (medium)", "Sextant", "Chart", "Watch", "Spade" });
    }
    return false;
  }

  private boolean doClue_3594()
  {
    RSTile DIG_SPOT = new RSTile(2416, 3515, 0);
    if ((atBank()) && (this.haveItems))
    {
      if (atBank())
      {
        karamjaTeleport();
        this.onClue = true;
      }
      else if (waitUntilIdle(DIG_SPOT))
      {
        WebWalking.walkTo(DIG_SPOT);
      }
      else
      {
        dig(DIG_SPOT);
        sleep(300L);
        this.haveItems = false;
        this.onClue = false;
      }
    }
    else if ((atBank()) && (!this.haveItems)) {
      doBank_3594();
    } else if ((!atBank()) && (!atFarmSpot()) && (!atFarmWalk()) && (!this.haveItems)) {
      varrockTeleport();
    }
    return false;
  }

  private boolean doBank_3618()
  {
    if (Banking.isBankScreenOpen())
    {
      if (!checkRingOfDueling())
      {
        withdrawRingOfDueling();
      }
      else if (!checkInventoryItem("Varrock teleport"))
      {
        withdrawItem("Varrock teleport");
      }
      else
      {
        Banking.close();
        this.haveItems = true;
      }
    }
    else
    {
      Banking.openBank();
      Banking.depositAllExcept(new String[] {"Clue scroll (medium)" });
    }
    return false;
  }

  private boolean doClue_3618()
  {
    RSTile CAVE_ENTRANCE = new RSTile(2630, 2997, 0);
    RSTile TALK_SPOT = new RSTile(2648, 9393, 0);
    if ((atBank()) && (this.haveItems))
    {
      castleWarsTeleport();
      sleep(500, 1500);
      this.onClue = true;
      if (!atLocation(CAVE_ENTRANCE)) {
        WebWalking.walkTo(CAVE_ENTRANCE);
      } else {
        objectAction(5, "Cave entrance", "Enter", 5, 2000);
      }
      if (!atLocation(TALK_SPOT))
      {
        WebWalking.walkTo(TALK_SPOT);
      }
      else
      {
        talkToNPC("Fycie");
        sleep(500, 1000);
        NPCChat.clickContinue(true);
        this.haveItems = false;
        this.onClue = false;
      }
    }
    else if ((atBank()) && (!this.haveItems))
    {
      doBank_3618();
    }
    else if ((!atBank()) && (!atFarmSpot()) && (!atFarmWalk()) && (!this.haveItems) && (!this.onClue))
    {
      varrockTeleport();
      WebWalking.walkToBank();
    }
    return false;
  }

  private boolean doBank_7309()
  {
    if (Banking.isBankScreenOpen())
    {
      if (Inventory.getAll().length > 1)
      {
        Banking.depositAllExcept(new String[] { "Clue scroll (medium)" });
      }
      else if (!checkInventoryItem("Sextant"))
      {
        withdrawItem("Sextant");
      }
      else if (!checkInventoryItem("Chart"))
      {
        withdrawItem("Chart");
      }
      else if (!checkInventoryItem("Watch"))
      {
        withdrawItem("Watch");
      }
      else if (!checkInventoryItem("Spade"))
      {
        withdrawItem("Spade");
      }
      else if (!checkGlory())
      {
        withdrawGlory();
      }
      else if (!checkInventoryItem("Varrock teleport"))
      {
        withdrawItem("Varrock teleport");
      }
      else
      {
        Banking.close();
        this.haveItems = true;
      }
    }
    else
    {
      Banking.openBank();
      Banking.depositAllExcept(new String[] {"Clue scroll (medium)", "Sextant", "Chart", "Watch", "Spade" });
    }
    return false;
  }

  private boolean doClue_7309()
  {
    RSTile WOODEN_LOG = new RSTile(2905, 3049, 0);
    RSTile DIG_SPOT = new RSTile(2896, 3119, 0);
    if ((atBank()) && (this.haveItems))
    {
      if (atBank()) {
        karamjaTeleport();
      }
      if (!waitUntilIdle(WOODEN_LOG)) {
        WebWalking.walkTo(WOODEN_LOG);
      } else {
        objectAction(5, "A wooden log", "Cross", 5, 4000);
      }
      if (!waitUntilIdle(DIG_SPOT))
      {
        Walking.blindWalkTo(DIG_SPOT);
      }
      else
      {
        dig(DIG_SPOT);
        sleep(2000L);
      }
      this.haveItems = false;
      return true;
    }
    if ((atBank()) && (!this.haveItems)) {
      doBank_7309();
    }
    return false;
  }

  private boolean doBank_10270()
  {
    if (Banking.isBankScreenOpen())
    {
      if (!checkInventoryItem("Mithril chainbody"))
      {
        withdrawItem("Mithril chainbody");
      }
      else if (!checkInventoryItem("Green d'hide chaps"))
      {
        withdrawItem("Green d'hide chaps");
      }
      else if (!checkInventoryItem("Ruby amulet"))
      {
        withdrawItem("Ruby amulet");
      }
      else if (!checkRingOfDueling())
      {
        withdrawRingOfDueling();
      }
      else if (!checkInventoryItem("Varrock teleport"))
      {
        withdrawItem("Varrock teleport");
      }
      else
      {
        Banking.close();
        this.haveItems = true;
      }
    }
    else
    {
      Banking.openBank();
      Banking.depositAllExcept(new String[] {"Clue scroll (medium)", "Mithril chainbody", "Green d'hide chaps", "Ruby amulet" });
    }
    return true;
  }

  private boolean doClue_10270()
  {
    RSTile OBSERVATORY_CAVE_STAIRS = new RSTile(2457, 3186, 0);
    RSTile OBSERVATORY_STAIRS = new RSTile(2335, 9350, 0);
    RSTile OBSERVATORY_MIDDLE_TELESCOPE = new RSTile(2439, 3161, 0);
    if ((atBank()) && (this.haveItems))
    {
      castleWarsTeleport();

      WebWalking.walkTo(OBSERVATORY_CAVE_STAIRS);
      waitUntilIdle(OBSERVATORY_CAVE_STAIRS);

      objectAction(5, "Stairs", "Climb-down", 5, 100);

      WebWalking.walkTo(OBSERVATORY_STAIRS);

      objectAction(5, "Stairs", "Climb up", 5, 100);

      Walking.clickTileMS(OBSERVATORY_MIDDLE_TELESCOPE, 1);

      wearItem("Mithril chainbody");
      wearItem("Green d'hide chaps");
      wearItem("Ruby amulet");

      doEmote("Think");

      checkForNPC("Uri");

      doEmote("Spin");

      talkToNPC("Uri");

      this.haveItems = false;
    }
    else if ((atBank()) && (!this.haveItems))
    {
      doBank_10270();
    }
    return false;
  }

  private boolean doBank_12049()
  {
    if (Banking.isBankScreenOpen())
    {
      if (!checkInventoryItem("Sextant"))
      {
        withdrawItem("Sextant");
      }
      else if (!checkInventoryItem("Chart"))
      {
        withdrawItem("Chart");
      }
      else if (!checkInventoryItem("Watch"))
      {
        withdrawItem("Watch");
      }
      else if (!checkInventoryItem("Spade"))
      {
        withdrawItem("Spade");
      }
      else if (!checkInventoryItem("Camelot teleport"))
      {
        withdrawItem("Camelot teleport");
      }
      else if (!checkInventoryItem("Varrock teleport"))
      {
        withdrawItem("Varrock teleport");
      }
      else
      {
        Banking.close();
        this.haveItems = true;
      }
    }
    else
    {
      Banking.openBank();
      Banking.depositAllExcept(new String[] {"Clue scroll (medium)", "Sextant", "Chart", "Watch", "Spade", "Camelot teleport", "Varrock teleport" });
    }
    return false;
  }

  private boolean doC2lue_12049()
  {
    RSTile LOG_LOCATION = new RSTile(2606, 3475, 0);
    RSTile DIG_SPOT = new RSTile(2585, 3505, 0);
    if ((atBank()) && (this.haveItems))
    {
      camelotTeleport();
      sleep(500, 1500);
      this.onClue = true;
      if (!atLocation(LOG_LOCATION)) {
        WebWalking.walkTo(LOG_LOCATION);
      } else {
        objectAction(5, "Log balance", "Walk-across", 5, 4000);
      }
      if (!atLocation(DIG_SPOT))
      {
        WebWalking.walkTo(DIG_SPOT);
      }
      else
      {
        dig(DIG_SPOT);
        sleep(2000L);
        this.onClue = false;
        this.haveItems = false;
      }
    }
    else if ((atBank()) && (!this.haveItems))
    {
      doBank_12049();
    }
    else if ((!atBank()) && (!atFarmSpot()) && (!atFarmWalk()) &&
            (!this.haveItems) && (!this.onClue))
    {
      varrockTeleport();
      WebWalking.walkToBank();
    }
    return false;
  }
}
