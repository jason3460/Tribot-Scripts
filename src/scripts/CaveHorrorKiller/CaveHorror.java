package scripts.CaveHorrorKiller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Walking;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceMaster;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;
import org.tribot.api2007.*;

@ScriptManifest(authors = { "JDoge" }, category = "Combat", name = "Cave Horror Killer")
public class CaveHorror extends Script implements Painting{

private final RSTile ENERGY_BARRIER_TILE = new RSTile(3660, 3509, 0),
		
					 PORT_TILE = new RSTile(3660, 3507, 0),
					 PORT_TILE2 = new RSTile(3659, 3507, 0),
		
					 DOCK_TILE1 = new RSTile(3669, 3503, 0), 
					 DOCK_TILE2 = new RSTile(3681, 3503, 0),
					 DOCK_TILE3 = new RSTile(3689, 3496, 0), 
					 DOCK_TILE4 = new RSTile(3700, 3496, 0),
					 DOCK_TILE5 = new RSTile(3709, 3496, 0),
				
					 BANK_TILE1 = new RSTile(3680, 2964, 0), 
					 BANK_TILE2 = new RSTile(3680, 2974, 0),
					 BANK_TILE3 = new RSTile(3680, 2982, 0),
		
					 JUNGLE_TILE1 = new RSTile(3680, 2991, 0),
					 JUNGLE_TILE2 = new RSTile(3681, 2999, 0),
					 JUNGLE_TILE3 = new RSTile(3686, 3008, 0),
					 JUNGLE_TILE4 = new RSTile(3697, 3009, 0),
					 JUNGLE_TILE5 = new RSTile(3708, 3008, 0),
					 JUNGLE_TILE6 = new RSTile(3720, 3006, 0),
					 JUNGLE_TILE7 = new RSTile(3732, 3005, 0),
					 JUNGLE_TILE8 = new RSTile(3743, 3003, 0),
					 JUNGLE_TILE9 = new RSTile(3754, 3000, 0),
					 JUNGLE_TILE10 = new RSTile(3763, 2995, 0),
					 JUNGLE_TILE11 = new RSTile(3761, 2988, 0),
					 JUNGLE_TILE12 = new RSTile(3754, 2981, 0),
		
					 CAVE_ENTRANCE_TILE = new RSTile(3749, 2973, 0),
					
					 CAVE_TILE = new RSTile(3748, 9373, 0),
		
					 CAVE_TILE1 = new RSTile(3738, 9383, 0),
					 CAVE_TILE2 = new RSTile(3736, 9393, 0),
					 CAVE_TILE3 = new RSTile(3737, 9403, 0),
					 CAVE_TILE4 = new RSTile(3747, 9413, 0),
					 CAVE_TILE5 = new RSTile(3750, 9426, 0),
					 CAVE_TILE6 = new RSTile(3759, 9439, 0),
					 CAVE_TILE7 = new RSTile(3766, 9452, 0),
					 CAVE_TILE8 = new RSTile(3778, 9460, 0),
		
					 COMBAT_SPOT_TILE = new RSTile(3786, 9459, 0);

	private final RSArea ECTOFUNTUS_AREA =	 	new RSArea(new RSTile[] { new RSTile(3653, 3524, 0), new RSTile(3653, 3515, 0), 
												new RSTile(3657, 3513, 0), new RSTile(3662, 3513, 0), new RSTile(3665, 3515, 0), 
												new RSTile(3665, 3524, 0), new RSTile(3661, 3526, 0), new RSTile(3657, 3526, 0) }),
	
						 CAVE_HORRORS_AREA = 	new RSArea(new RSTile[] { new RSTile(3789, 9466, 0), new RSTile(3785, 9466, 0), 
											 	new RSTile(3778, 9466, 0), new RSTile(3756, 9467, 0), new RSTile(3742, 9465, 0), 
											 	new RSTile(3746, 9451, 0), new RSTile(3760, 9444, 0), new RSTile(3773, 9445, 0), 
											 	new RSTile(3781, 9445, 0), new RSTile(3784, 9442, 0), new RSTile(3787, 9440, 0), 
											 	new RSTile(3794, 9441, 0), new RSTile(3803, 9449, 0), new RSTile(3801, 9462, 0) }), 

						 FIRST_BOAT_AREA = 		new RSArea(new RSTile[] { new RSTile(3712, 3504, 1), new RSTile(3716, 3504, 1), 
								 		   		new RSTile(3716, 3495, 1), new RSTile(3712, 3495, 1) }), 
						
						 SECOND_BOAT_AREA =		new RSArea(new RSTile[] { new RSTile(3676, 2950, 1), new RSTile(3676, 2946, 1), 
								 				new RSTile(3685, 2946, 1), new RSTile(3685, 2950, 1) }), 
						
						 FIRST_DOCK_AREA = 		new RSArea(new RSTile[] { new RSTile(3710, 3498, 0), new RSTile(3710, 3495, 0), 
								 		   		new RSTile(3703, 3495, 0), new RSTile(3703, 3498, 0) }), 
						
						 SECOND_DOCK_AREA =		new RSArea(new RSTile[] { new RSTile(3690, 2955, 0), new RSTile(3690, 2953, 0), 
								 				new RSTile(3675, 2953, 0), new RSTile(3675, 2955, 0) }), 
						
						 BANK_AREA = 			new RSArea(new RSTile[] { new RSTile(3685, 2979, 0), new RSTile(3677, 2979, 0), 
								 	 			new RSTile(3677, 2985, 0), new RSTile(3685, 2985, 0) }), 
						
						 CAVE_ENTRANCE_AREA = 	new RSArea(new RSTile[] { new RSTile(3753, 2970, 0), new RSTile(3747, 2970, 0), new RSTile(3747, 2977, 0), new RSTile(3753, 2977, 0) });
						
	private final RSTile[] 	DOCK_TILES = new RSTile[] { DOCK_TILE1, DOCK_TILE2, DOCK_TILE3, DOCK_TILE4, DOCK_TILE5 }, 
							BANK_TILES = new RSTile[] { BANK_TILE1, BANK_TILE2, BANK_TILE3 }, 
							JUNGLE_TILES = new RSTile[] { JUNGLE_TILE1, JUNGLE_TILE2, JUNGLE_TILE3, 
							JUNGLE_TILE4, JUNGLE_TILE5, JUNGLE_TILE6, 
							JUNGLE_TILE7, JUNGLE_TILE8, JUNGLE_TILE9, 
							JUNGLE_TILE10, JUNGLE_TILE11, JUNGLE_TILE12,
							CAVE_ENTRANCE_TILE },
							CAVE_TILES = new RSTile[] { CAVE_TILE1, CAVE_TILE2, CAVE_TILE3, CAVE_TILE4, 
							CAVE_TILE5, CAVE_TILE6, CAVE_TILE7, CAVE_TILE8, COMBAT_SPOT_TILE };

	private final int 	FILLED_ECTOPHIAL_ID = 4251, ENERGY_BARRIER_ID = 16105,
						GANGPLANK_ID = 11209, GANGPLANK2_ID = 11212,
						CANDLE_LANTERN_ID = 4531, FOOD_ID = 379, CAVE_ID = 3650,
						ANGLE = 100;

	private final String[] PICKUP_ITEM_NAMES = new String[] { "Black mask (10)", "Clue scroll (hard)", "Rune dagger", "Nature Rune", 
															  "Nature Talisman", "Herb", "Teak logs", "Kwuarm seed", "Toadflax seed", 
															  "Snapdragon seed", "Avantoe seed", "Irit seed", "Lantadyme seed", 
															  "Dwarf weed seed", "Cadantine seed", "Torstol seed", "Limpwurt root", 
															  "Loop half of key", "Tooth half of key", "Runite bar", "Rune spear", 
															  "Rune battleaxe", "Rune 2h sword", "Uncut diamond", "Silver ore", 
															  "Rune sq shield", "Steel arrow", "Rune arrow", "Law rune", "Death rune", 
															  "Uncut dragonstone", "Dragonstone", "Rune kiteshield", "Dragon med helm", 
															  "Shield left half", "Dragon spear" };
	
	private final int[] DONT_DEPOSIT_IDS = new int[] { FILLED_ECTOPHIAL_ID, CANDLE_LANTERN_ID, FOOD_ID };

	private CaveHorrorState SCRIPT_STATE;

	public boolean inCave;
	
	private CameraUtils cameraUtils = new CameraUtils();
	private CommonUtils commonUtils = new CommonUtils(cameraUtils);
	private KMUtils utils = new KMUtils(cameraUtils, commonUtils);
		
	private ABCUtil abc;
	
	@Override
	public void run() {
		println("Script has been started");
		Mouse.setSpeed(General.random(130, 150));

		// Setting angle fully up
		int currentAngle = Camera.getCameraAngle();
		if (Math.abs(ANGLE - currentAngle) > 10) {
			Camera.setCameraAngle(ANGLE);
		}

		Walking.setControlClick(true);
		Walking.setWalkingTimeout(General.random(4000, 6000));
		
		General.useAntiBanCompliance(true);
		this.abc = new ABCUtil();
		
		while (true) {
			SCRIPT_STATE = getState();

			switch (SCRIPT_STATE) {
			case ECTOFUNTUS_TO_BANK:
				goToBank();
				break;
				
			case CAVE_TO_ECTOFUNTUS:
				inCave = false;
				useEctophial();
				sleep(5000);
				break;

			case DO_BANK:
				doBank();
				sleep(1000, 1500);
				break;

			case BANK_TO_CAVE:	
				walkToCave();
				break;

			case ENTER_CAVE:
				goIntoCave();
				inCave = true;
				break;

			case WALK_TO_COMBAT_SPOT:
				walkToCombatSpot();
				break;

			case KILLING_CAVE_HORROR:
				//killing();
				combat();
				break;
				
			case PICKING_UP_LOOT:
				pickupLoot();
				break;
			}
			sleep(50, 75);
		}
	}

	private CaveHorrorState getState() {
		if (checkInventoryFood()) {
			if (inCave()) {
				if (!pickupLoot()) {
					if (atCombatSpot()) {
						return CaveHorrorState.KILLING_CAVE_HORROR;
					} else {
						return CaveHorrorState.WALK_TO_COMBAT_SPOT;
					}
				} else {
					return CaveHorrorState.PICKING_UP_LOOT;
				}
			} else {
				if (atCave()) {
					return CaveHorrorState.ENTER_CAVE;
				} else {
					return CaveHorrorState.BANK_TO_CAVE;
				}
			}
		} else {
			if (inCave()) {
				return CaveHorrorState.CAVE_TO_ECTOFUNTUS;
			} else {
				if (atBank()) {
					return CaveHorrorState.DO_BANK;
				} else {
					return CaveHorrorState.ECTOFUNTUS_TO_BANK;
				}
			}
		}
	}


//	public void onPaint(Graphics g) {
//
//		Graphics2D gg = (Graphics2D)g;
//		gg.drawImage(img, 0, 304, null);
//
//		long timeRan = System.currentTimeMillis() - startTime;
//		int currentLvl = Skills.getActualLevel(SKILLS.RANGED);
//		int gainedLvl = currentLvl - startLvl;
//		int gainedXP = Skills.getXP(SKILLS.RANGED) - startXP;
//		int xpToLevel = Skills.getXPToNextLevel(SKILLS.RANGED);
//		int xpPerHour = (int)(gainedXP * 3600000d / timeRan);
//
//		g.setFont(font);
//
//		g.setColor(new Color(0, 0, 0));
//		g.drawString("Runtime: " + Timing.msToString(timeRan), 270, 370);
//		g.drawString("State: " +SCRIPT_STATE, 270, 385);
//		g.drawString("Current lvl: " + currentLvl + " (+" + gainedLvl + ")", 270, 400);
//		g.drawString("XP Gained(XP/H): " + gainedXP + "(" + xpPerHour + ")", 270, 415);
//		g.drawString("XP TNL: " + xpToLevel, 270, 430);
//		g.drawString("Black Masks Looted: " + blackMasksLooted, 270, 445);
//	}
	
	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch(IOException e) {
			return null;
		}
	}

	//private final Image img = getImage("http://tryimg.com/4/cuwu.png");
	
	private final Image img = getImage("http://s10.postimg.org/g49bz36w9/Cave_Horro_Paint.png");
		
	private int startLvl = Skills.getActualLevel(SKILLS.RANGED);
	private int startXP = Skills.getXP(SKILLS.RANGED);
	long startTime = System.currentTimeMillis();
	
	private final RenderingHints antialiasing = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private final Color color1 = new Color(51, 102, 255, 175);
    private final Color color2 = new Color(0, 0, 0);
    private final BasicStroke stroke1 = new BasicStroke(1);
    private final Font font1 = new Font("Segoe UI", Font.BOLD, 12);
    private final Font font2 = new Font("Segoe UI", 0, 12);

    public void onPaint(Graphics g1) {
        Graphics2D g = (Graphics2D)g1;
        g.setRenderingHints(antialiasing);
        g.drawImage(img, 0, 304, null);
        
        long timeRan = System.currentTimeMillis() - startTime;
		int currentLvl = Skills.getActualLevel(SKILLS.RANGED);
		int gainedLvl = currentLvl - startLvl;
		int gainedXP = Skills.getXP(SKILLS.RANGED) - startXP;
		int xpToLevel = Skills.getXPToNextLevel(SKILLS.RANGED);
		int xpPerHour = (int)(gainedXP * 3600000d / timeRan);
		int kills = gainedXP / 220;
		int killsPerHour = xpPerHour / 220;

        g.setColor(color1);
        g.fillRoundRect(316, 24, 195, 96, 3, 16);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(316, 24, 195, 96, 3, 16);
        g.setColor(color1);
        g.setFont(font2);
        g.setColor(color2);
        
        g.setFont(font1);
        g.drawString("Runtime: ", 320, 40);
        g.setFont(font2);
        g.drawString(""+ Timing.msToString(timeRan), 375, 40);
        
        g.setFont(font1);
        g.drawString("State: ", 320, 55);
        g.setFont(font2);
        g.drawString(""+ SCRIPT_STATE, 357, 55);
        
        g.setFont(font1);
        g.drawString("Current lvl: ", 320, 70);
        g.setFont(font2);
        g.drawString(""+ currentLvl + "(+" + gainedLvl + ")", 385, 70);
        
        g.setFont(font1);
        g.drawString("XP Gained(/h): ", 320, 85);
        g.setFont(font2);
        g.drawString(""+ gainedXP + "("+ xpPerHour + ")", 403, 85);
        
        g.setFont(font1);
        g.drawString("XP TNL: ", 320, 100);
        g.setFont(font2);
        g.drawString(""+ xpToLevel, 365, 100);
        
        g.setFont(font1);
        g.drawString("Kills(/h): ", 320, 115);
        g.setFont(font2);
        g.drawString("" +kills + "(" + killsPerHour + ")", 370, 115);
    }

	//deposits loot and withdraws food and ecto/lantern if needed
	private boolean doBank() {
		if(Banking.isBankScreenOpen()) {
			if(Inventory.getAll().length > 2) {
				depositItems();
			} else {
				if(!checkInventoryEcto()) {
					withdrawEctophial();
				} else {
					if(!checkInventoryLantern()) {
						withdrawCandleLantern();
					} else {
						if (!checkInventoryFood()) {
							withdrawFood();
						}
					}
				}
			}
		} else {
			openBank();
		}
		return false;
	}
	
	//goes to the bank
	private boolean goToBank() {
			if(atEctofuntus()) {
				sleep(500, 700);
				walkToBarrier();
				return true;
			} else {
				if(atBarrier()) {
					useBarrier();
					return true;
				} else {
					if(atPort()) {
						walkToDock();
						return true;
					} else {
						if(atDock()) {
							useGangplank();
							return true;
						} else {
							if(atBoat() && !haveBillInterface()) {
								talkToBill();
								return true;
							} else {
								if(haveBillInterface()) {
									billInterface();
									return true;
								} else {
									if(atSecondBoat()) {
										sleep(5000);
										useSecondGangplank();
										return true;
									} else {
										if(atSecondDock()) {
											walkToBank();
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		return false;
	}
	
	//used for combat
	private boolean combat() {
		//if(!checkRun()) {
			if(!checkHealth()) {
				if(!inCombat()) {
					clickTarget();
				} else {
					//abc.performXPCheck(SKILLS.RANGED);
					sleep(500);
				}
			} else if (checkHealth()){
				println("Eatting food.");
				eatFood();
			}
//		} else {
//			utils.toggleRun(true);
//		}
		return false;
	}
	
	//used to check if we are in combat
	private boolean inCombat() {
		RSNPC[] caveHorror = NPCs.findNearest("Cave horror");
		int abcRandom = General.random(1, 50);
		if(caveHorror.length == 0) { // caveHorror == null
			return false;
		}
		if(caveHorror[0] == null) {
			return false;
		}
		if(Player.getRSPlayer().isInCombat() || Player.getAnimation() != -1 || Combat.isUnderAttack()) {
			println("In Combat.");
			//abc.performXPCheck(SKILLS.RANGED);
			sleep(300, 600);
			if(abcRandom == 1) {
				abc.performXPCheck(SKILLS.RANGED);
				println("Performing ABC - XP Check");
			} else if(abcRandom == 25){
				println("Performing ABC - Random Right Click");
				abc.performRandomRightClick();
				//Mouse.leaveGame();
			} else if(abcRandom == 50) {
				println("Performing ABC - Examine Object");
				abc.performExamineObject();
				//Mouse.leaveGame();
			} else {
				Mouse.leaveGame();
				println("Not performing ABC");
			}
			return true;
		}


		return false;
	}

	//used to find a target
	private RSNPC findTarget() {
		RSNPC[] caveHorror = NPCs.findNearest("Cave horror");
		if (caveHorror.length == 0) { //caveHorror == null
			return null;
		}
		if(caveHorror[0] == null) {
			return null;
		}
		println("Finding Target.");
		RSNPC target = caveHorror[0];

		for (int i = 0; i < caveHorror.length; i++) {
			target = caveHorror[i];
			if (validTarget(target) != null) {
				return target;
			}
			sleep(200);
		}
		return null;
	}
	
	//checks if target is valid
	private RSNPC validTarget(RSNPC target) {
		if(CAVE_HORRORS_AREA.contains(target.getPosition())) {
			if(target.isOnScreen()) {
				return target;
			} else {
				Walking.walkTo(target.getPosition());
				cameraUtils.pitchCameraAsync(General.random(23, 60));
				cameraUtils.rotateCameraToTileAsync(target.getPosition());
				commonUtils.sleep(450, 600);
				return target;
			}
		}
		return null;
	}
	
	//used to attack the target
	private boolean clickTarget() {
		RSNPC target = findTarget();
		if(target == null) {
			return false;
		}

		 int clickAttempts = 5;  
		  for(int i=0; i<clickAttempts;i++){
		   if(DynamicClicking.clickRSNPC(target, "Attack")){
		    sleep(100, 150);
		    break;
		   }
		   sleep(100, 150);
		  }
		  return false;
		 }
	
	@SuppressWarnings("unused")
	private boolean checkRun() {
		if (Game.getRunEnergy() <= General.random(30, 90)) {
			return true;
		}
		return true;
	}
	
	//used for combat
//	private boolean killing() {
//
//		RSNPC[] caveHorror = NPCs.findNearest("Cave horror");
//
//		if (Game.getRunEnergy() > General.random(10, 100)) {
//			utils.toggleRun(true);
//		}
//		
//		if ((Player.getRSPlayer().isInCombat() || Player.getAnimation() != -1) && (!checkHealth())) {
//			// println("In Combat");
//			sleep(300, 600);
//			return true;
//		} else {
//			if ((!checkHealth()) || (Player.getRSPlayer().isInCombat() && (!checkHealth()))) {
//				if (caveHorror.length > 0) {
//					if (caveHorror[0].isOnScreen() && CAVE_HORRORS_AREA.contains(caveHorror[0].getPosition())){
//						if (commonUtils.clickModel(caveHorror[0].getModel(), "Attack")) {
//							sleep(500, 800);
//						}
//					} else if (CAVE_HORRORS_AREA.contains(caveHorror[0].getPosition())) {
//						cameraUtils.pitchCameraAsync(General.random(23, 60));
//						cameraUtils.rotateCameraToTileAsync(caveHorror[0].getPosition());
//						commonUtils.sleep(450, 600);
//						return true;
//					} else {
//						sleep(750, 1150);
//						return true;
//					}
//
//					long timer = System.currentTimeMillis();
//					while (caveHorror[0].isValid()) {
//						if (Player.getRSPlayer().isInCombat()) {
//							timer = System.currentTimeMillis();
//						}
//						// Target is still legit but character got stuck
//						else {
//							if (System.currentTimeMillis() - timer > General.random(2612, 2919)) {
//								if (!caveHorror[0].isOnScreen()) {
//									Camera.turnToTile(caveHorror[0].getPosition());
//								}
//								if (Player.getPosition().distanceTo(caveHorror[0]) > 3) {
//									Walking.walkTo(caveHorror[0]);
//									commonUtils.sleep(150, 300);
//								}
//								if (!Player.isMoving()&& caveHorror[0].getHealth() == 0 && caveHorror[0].isInCombat()) {
//									if (commonUtils.clickModel(caveHorror[0].getModel(), "Attack")) {
//										timer = System.currentTimeMillis();
//									}
//								}
//							}
//						}
//						return true;
//					}
//				}
//			} else if (checkHealth()) {
//				println("Eatting food");
//				eatFood();
//				sleep(2100);
//				return true;
//			}			
//			}
//		return false;
//	}

	private boolean checkHealth() {			
		println("checking health");
		if(Skills.getActualLevel(SKILLS.HITPOINTS)-Skills.getCurrentLevel(SKILLS.HITPOINTS)>=12) {
			return true;
		}
		return false;
	}
	
	private boolean eatFood() {
		RSItem[] food = Inventory.find(FOOD_ID);
		if (food != null && food.length > 0) {
			 food[0].click("Eat");
			 sleep(3500, 4000);
			return true;
		}
		return false;
	}
	
	private boolean pickupLoot() {
		RSGroundItem[] items = GroundItems.findNearest(PICKUP_ITEM_NAMES);
		if(items.length == 0) {
			return false;
		}
		
		RSItem[] food = Inventory.find(FOOD_ID);
		if(food.length == 0) {
			return false;
		}
	
			if(items.length > 0) {
				if(Player.getRSPlayer().getPosition().distanceTo(items[0].getPosition()) <= 15 ) {
				if(!Inventory.isFull()) {
					utils.pickUpLoot(items[0]);
					sleep(1000, 1500);
					return true;
				} else {
					food[0].click("Eat");
					sleep(500, 800);
					utils.pickUpLoot(items[0]);
					sleep(1000, 1500);
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	//used to walk to the combat spot within the cave
	private boolean walkToCombatSpot() {
		
		if (utils.walkPath(CAVE_TILES, false)) {
			commonUtils.waitUntilIdle(700, 1000);
			return true;
		}
		return false;
	}

	//used to check we are in the combat spot
	private boolean atCombatSpot() {
		RSTile myPos = Player.getPosition();
		return myPos.distanceTo(COMBAT_SPOT_TILE) <= 30;
	}

	//used to check we have arrived at the island
	private boolean atSecondBoat(){
		return SECOND_BOAT_AREA.contains(Player.getPosition());
	}

	//used to walk to the bank
	private boolean walkToBank() {
		Camera.setCameraAngle(General.random(90, 100));
		Camera.setCameraRotation(General.random(0, 25));
		if (Walking.walkPath(BANK_TILES)) {
			waitUntilIdle(BANK_TILE3);
			return true;
		}
		return false;
	}

	//used to check if we are at the bank
	private boolean atBank() {
		return BANK_AREA.contains(Player.getPosition());
	}

	//used to open the bank
	private boolean openBank() {
		
		Camera.setCameraAngle(General.random(90, 100));
		Camera.setCameraRotation(General.random(0, 25));
		
		RSObject[] banks = Objects.findNearest(7, 11338);
		if (banks != null && banks.length > 0) {
			RSObject bank = banks[0];
			if (DynamicClicking.clickRSObject(bank, "Bank")) {
				long t = System.currentTimeMillis();
				while (Timing.timeFromMark(t) < General.random(1200, 1800)) {
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

	//used to depoist junk/drops in bank
	private boolean depositItems() {
		if (Banking.isBankScreenOpen()) {
			Banking.depositAllExcept(DONT_DEPOSIT_IDS);
			return true;
		}
		return false;
	}

	
	//used to withdraw ecto from the bank
	private boolean withdrawEctophial() {
		if (Banking.isBankScreenOpen()) {
			RSItem[] ectophial = Banking.find(FILLED_ECTOPHIAL_ID);
			int count = Inventory.getAll().length;

			if (ectophial != null && ectophial.length > 0
					&& ectophial[0].click("Withdraw-1")) {
				long t = System.currentTimeMillis();
				while (Timing.timeFromMark(t) < General.random(1500, 2500)) {
					if (Inventory.getAll().length > count) {
						return true;
					}
					sleep(20, 80);
				}
			}
		}
		return false;
	}
	
	//used to withdraw a candle lantern
	private boolean withdrawCandleLantern() {
		if (Banking.isBankScreenOpen()) {
			RSItem[] lantern = Banking.find(CANDLE_LANTERN_ID);
			int count = Inventory.getAll().length;
			if (lantern != null && lantern.length > 0 && lantern[0].click("Withdraw-1")) {
				long t = System.currentTimeMillis();
				while (Timing.timeFromMark(t) < General.random(1500, 2500)) {
					if (Inventory.getAll().length > count) {
						return true;
					}
					sleep(20, 80);
				}
			}
		}
		return false;
	}
		
	//used to withdraw food
	private boolean withdrawFood() {
		if (Banking.isBankScreenOpen()) {
			RSItem[] food = Banking.find(FOOD_ID);
			int count = Inventory.getAll().length;
			if (food != null && food.length > 0 && food[0].click("Withdraw-All")) {
				long t = System.currentTimeMillis();
				while (Timing.timeFromMark(t) < General.random(1500, 2500)) {
					if (Inventory.getAll().length > count) {
						return true;
					}
					sleep(20, 80);
				}
			}
		}
		return false;
	}
	
	//used to check we have a ecto in our inventory
	private boolean checkInventoryEcto() {
		RSItem[] ecto = Inventory.find(FILLED_ECTOPHIAL_ID);
		return ecto != null && ecto.length > 0;
	}

	//used to check if we have a lantern in our inventory
	private boolean checkInventoryLantern() {
		RSItem[] lantern = Inventory.find(CANDLE_LANTERN_ID);
		return lantern != null && lantern.length > 0;
	}

	//used to check we have food in our inventory
	private boolean checkInventoryFood(){
		RSItem[] food = Inventory.find(FOOD_ID);
		return food != null && food.length > 0;
	}

	//used to walk to the cave
	private boolean walkToCave() {
		if (Walking.walkPath(JUNGLE_TILES)) {
			waitUntilIdle(CAVE_ENTRANCE_TILE);
			return true;
		}
		return false;
	}

	//used to enter the cave
	private boolean enterCave() {
		RSObject[] cave = Objects.findNearest(4, CAVE_ID);
		if (cave != null && cave.length > 0) {
			if (!cave[0].isOnScreen()) {
				Camera.turnToTile(cave[0].getPosition());
			}

			if (cave[0].isOnScreen()) {
				if (DynamicClicking.clickRSObject(cave[0], "Enter")) {
					sleep(800, 1200);
					return true;
				}
			}
		}
		return false;
	}

	//used to check if we are at the cave
	private boolean atCave() {
		return CAVE_ENTRANCE_AREA.contains(Player.getPosition());
	}

	//used to talk to bill
	private boolean talkToBill() {
		RSNPC[] bill = NPCs.findNearest("Bill Teach");
		if (bill != null && bill.length > 0 && DynamicClicking.clickRSNPC(bill[0], "Talk-to")) {
			waitUntilIdle(bill[0].getPosition());
			return true;
		}
		return false;
	}

	//used to finish the bill interface
	private boolean billInterface() {
		if (NPCChat.getClickContinueInterface() != null) {
			NPCChat.clickContinue(true);
			sleep(300, 500);
			if (NPCChat.getSelectOptionInterfaces() != null) {
				NPCChat.selectOption("Yes Cap'n.", true);
				sleep(300, 500);
				if (NPCChat.getClickContinueInterface() != null) {
					NPCChat.clickContinue(true);
					sleep(300, 500);
					NPCChat.clickContinue(true);
					sleep(300, 500);
					NPCChat.clickContinue(true);
					sleep(300, 500);
					NPCChat.clickContinue(true);
					sleep(2000, 2500);
					return true;
				}
			}
		}
		return false;
	}

	//used to check if we have the bill interface
	private boolean haveBillInterface(){
		RSInterfaceChild billInterface = Interfaces.get(242, 2);
		return billInterface != null && !billInterface.isHidden();
	}

	//used to get on the boat
	private boolean useGangplank() {
		RSObject[] gangplank = Objects.findNearest(10, GANGPLANK_ID);
		
		
		if (gangplank != null && gangplank.length > 0) {
			if (DynamicClicking.clickRSObject(gangplank[0], "Cross")) {
				waitUntilIdle(gangplank[0].getPosition());
				sleep(800, 1200);
				return true;
			} else {
				cameraUtils.pitchCameraAsync(General.random(95, 100));
				cameraUtils.rotateCameraToTileAsync(gangplank[0].getPosition());
				commonUtils.sleep(450, 600);
				return false;
			}
		}
		return false;
	}

	//used to get off the boat
	private boolean useSecondGangplank() {
		RSObject[] gangplank = Objects.findNearest(10, GANGPLANK2_ID);
		
		
		Camera.setCameraRotation(General.random(345, 360));
		if (gangplank != null && gangplank.length > 0) {
			if (DynamicClicking.clickRSObject(gangplank[0], "Cross")) {
				waitUntilIdle(gangplank[0].getPosition());
				sleep(800, 1200);
				return true;
			} else {
				cameraUtils.pitchCameraAsync(General.random(95, 100));
				cameraUtils.rotateCameraToTileAsync(gangplank[0].getPosition());
				commonUtils.sleep(450, 600);
				return false;
			}
		}
		return false;
	}

	//used to check if we are at the boat
	private boolean atBoat() {
		return FIRST_BOAT_AREA.contains(Player.getPosition());
	}

	//used to walk to the dock from the barrier in port
	private boolean walkToDock() {
		if (utils.walkPath(DOCK_TILES, false)) {
			commonUtils.waitUntilIdle(600, 1000);
			//waitUntilIdle(DOCK_TILE5);
			return true;
		}
		return false;
	}

	//used to check if we are at the first dock
	private boolean atDock() {
		return FIRST_DOCK_AREA.contains(Player.getPosition());
	}

	//used to check if we are at the second dock
	private boolean atSecondDock() {
		return SECOND_DOCK_AREA.contains(Player.getPosition());
	}

	//used to pass the barrier into the port
	private boolean useBarrier() {
		RSObject[] barrier = Objects.findNearest(2, ENERGY_BARRIER_ID);
		if (barrier != null && barrier.length > 0) {
			if (DynamicClicking.clickRSObject(barrier[0], "Pay-toll(2-Ecto)")) {
				waitUntilIdle(PORT_TILE);
				sleep(1200, 1500);
				return true;
			} else {
				Camera.turnToTile(barrier[0].getPosition());
				useBarrier();
			}
		}
		return false;
	}

	//used to walk to barrier from ectofuntus
	private boolean walkToBarrier() {
		return walkToTile(ENERGY_BARRIER_TILE);
	}

	//used to check if we are at the barrier
	private boolean atBarrier() {
		RSTile myPos = Player.getPosition();
		return myPos.distanceTo(ENERGY_BARRIER_TILE) <= 1;
	}

	//used to bank by using ectophial
	private boolean useEctophial() {
		RSItem[] ectophial = Inventory.find(FILLED_ECTOPHIAL_ID);
		if (ectophial != null && ectophial.length > 0) {
			MoveMouseTo(ectophial[0].getArea());
			if (Timing.waitUptext("Empty", 1000)) {
				Mouse.click(1);
			}
		}
		return false;
	}

	//used to check if we are at the ectofuntus
	private boolean atEctofuntus() {
		if(ECTOFUNTUS_AREA.contains(Player.getPosition())){
			inCave = false;
			return true;
		}
		return false;
	}

	//used to check if we passed the barrier into port
	private boolean atPort() {
		RSTile myPos = Player.getPosition();
		return myPos.distanceTo(PORT_TILE) <= 0 || myPos.distanceTo(PORT_TILE2) <= 0;
	}

	private void MoveMouseTo(Point p, int randX, int randY) {
		Point point = new Point(p.x + General.random(-randX, randX) / 2, p.y
				+ General.random(-randY, randY) / 2);
		Mouse.move(point);
	}

	private void MoveMouseTo(Rectangle r) {
		MoveMouseTo(new Point((int) r.getCenterX(), (int) r.getCenterY()),
				(int) (r.getMaxX() - r.getMinX()) / 2,
				(int) (r.getMaxY() - r.getMinY()) / 2);
	}

	//used to walk to tiles
	private boolean walkToTile(RSTile tile) {
		if (Walking.walkTo(tile)) {
			waitUntilIdle(tile);
			return true;
		}
		return false;
	}

	private void waitUntilIdle(RSTile tile) {
		sleep(400, 800);
		long t = System.currentTimeMillis();

		while (Timing.timeFromMark(t) < General.random(400, 800)) {
			if (Player.getPosition().distanceTo(tile) <= 1) {
				break;
			}

			sleep(40, 80);

			if (Player.isMoving() || Player.getAnimation() != -1) {
				t = System.currentTimeMillis();
			}
		}
	}

	//used to enter cave
	public void goIntoCave() {
		if (clickNextOpen()) {
			NPCChat.clickContinue(true);
			sleep(800, 1200);
		} else if (optionInterfaceOpen()) {
			RSInterfaceMaster master = Interfaces.get(228);
			if (master != null) {
				RSInterfaceChild child = master.getChild(1);
				if (child != null) {
					child.click("Continue");

					sleep(800, 1200);
				}
			}
		} else {
			enterCave();
		}
	}
	
	public boolean clickNextOpen() {
		return NPCChat.getClickContinueInterface() != null;
	}

	public boolean optionInterfaceOpen() {
		return Interfaces.get(228) != null;
	}
	
	//check if in cave
	private boolean inCave(){
		RSTile myPos = Player.getPosition();
		if(myPos.distanceTo(CAVE_TILE) <= 100){
			inCave = true;
			return true;
		}
		return false;
	}
}
