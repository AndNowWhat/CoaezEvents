package net.botwithus;

import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.game.*;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.inventories.Backpack;
import net.botwithus.rs3.game.inventories.Equipment;

import java.util.*;

import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class CoaezEvents extends LoopingScript {

    public BotState botState = BotState.IDLE;
    public BotState lastActivityState = BotState.IDLE;
    public final Random random = new Random();
    private ScriptConfig config;
    public final CoaezEventGraphicsContext sgc;

    private final Coordinate bankLocation = new Coordinate(605, 1701, 0);
    private final Coordinate excavationLocation = new Coordinate(574, 1738, 0);
    private final Area bankArea = new Area.Circular(bankLocation, 10);
    private final Area excavationArea = new Area.Circular(excavationLocation, 15);
    private final Coordinate summoningLocation = new Coordinate(586, 1742, 0);
    private final Area summoningArea = new Area.Circular(summoningLocation, 15);
    private final Coordinate stairsLocation = new Coordinate(3323,3378,0);
    private final Area stairsArea = new Area.Circular(stairsLocation, 5);
    private final Coordinate tomeLocation = new Coordinate(3318, 3378, 1);
    private final Area tomeArea = new Area.Circular(tomeLocation, 3);
    private final Coordinate eepLocation = new Coordinate(580,1736,0);
    private final Area eepArea = new Area.Circular(eepLocation, 10);


    private static final int MAZE_PORTAL = 131372;
    private static final int BONE_CLUB = 57608;
    private static final int ZOMBIE_IMPLING = 31307;
    private static final int ZOMBIE = 31305;
    private static final int SKELETON = 31306;
    private static final int GATE = 131376;
    private static final int FENCE = 131377;
    private static final int SKARAXXI = 31304;
    private static final Coordinate ENTRANCE_MIN = new Coordinate(698, 1726, 0);
    private static final Coordinate ENTRANCE_MAX = new Coordinate(703, 1727, 0);

    public boolean forceCollectionTurnIn = false;

    public enum BotState {
        SUMMONING,
        ARCHAEOLOGY,
        THIEVING,
        PUMPKIN,
        TURNINCOLLECTIONS,
        MAZE,
        FIR_WOODCUTTING,
        TOY_CRAFTING,
        SNOWBALL_FLETCHING,
        ICE_FISHING,
        HOT_CHOCOLATE,
        IDLE
    }

    public boolean destroyArchBook;
    public boolean identifyAncientRemains;
    private long lastAnimationChangeTime = 0;
    private int lastAnimationId = -1;
    public boolean chaseSprite;
    public int ancientRemainsCount = 27;
    public int thievingDelay = 10;
    public int interactionCount = 0;
    public int maxInteractionsBeforePause = random.nextInt(26) + 15;
    public int minWaitTime = 10;
    public int maxWaitTime = 60;
    public int tomeCount = 20;
    public boolean handInTomes = false;
    public boolean useMaizeLootTokens;
    public boolean turnInCollections = true;
    private long lastAnimationTime = 0;
    private final int ANIMATION_ID_RITUAL = 6298;
    private final long RITUAL_TIMEOUT_MS = 5000;
    private final Coordinate mazeStartLocation = new Coordinate(624,1715,0);
    private final Area mazeEntranceArea = new Area.Circular(mazeStartLocation, 5);
    boolean usedDoor = false;

    private final Coordinate implingCoords = new Coordinate(707,1726,0);
    private final Area implingArea = new Area.Rectangular(new Coordinate(698,1726,0), new Coordinate(712,1731,0));
    private final Area innerBossArea = new Area.Rectangular(new Coordinate(702,1732,0), new Coordinate(178,1736,0));
    private final Area fenceArea = new Area.Rectangular(new Coordinate(708, 1731, 0), new Coordinate(711, 1732, 0));


    private long lastAnimationFletchingTime = 0;
    private static final int FLETCHING_TIMEOUT = 3000;
    private long lastAnimationCookingTime = 0;
    private boolean needFirewood = false;
    private boolean needMilk = false;
    private boolean needSugar = false;
    private boolean needChocolate = false;
    private boolean needSpice = false;
    private long lastSnowballInteraction = -1;
    private long IDLE_TIMEOUT = 3000;
    private static final int UNPAINTED_MARIONETTE = 57928;
    private static final int PAINTED_MARIONETTE = 57929;
    private static final int COMPLETE_MARIONETTE = 57931;
    private static final int INCOMPLETE_MARIONETTE = 57927;


    enum TransferOptionType {
        ONE(2, 33882205),
        FIVE(3, 33882208),
        TEN(4, 33882211),
        ALL(7, 33882215),
        X(5, 33882218);

        private final int varbitValue;
        private final int componentValue;

        TransferOptionType(int varbitValue, int componentValue) {
            this.varbitValue = varbitValue;
            this.componentValue = componentValue;
        }

        public int getComponentValue() {
            return this.componentValue;
        }

        public int getVarbitValue() {
            return this.varbitValue;
        }
    }
    public CoaezEvents(String s, ScriptConfig config, ScriptDefinition scriptDefinition) {
        super(s, config, scriptDefinition);
        this.config = config;
        subscribe(ChatMessageEvent.class, this::onChatMessage);
        this.sgc = new CoaezEventGraphicsContext(this.getConsole(), this);
    }

    private void onChatMessage(ChatMessageEvent chatMessageEvent) {
        String message = chatMessageEvent.getMessage();
        if (message.contains("You have reached the Spooky token soft cap")) {
            println("Reached max points, stopping collection turn-ins.");
            turnInCollections = false;
        }
        if (message.contains("Could you fetch some more firewood")) {
            println("Need to collect firewood!");
            needFirewood = true;
        }
        else if (message.contains("Let's add a splash of milk")) {
            println("Need to add milk!");
            needMilk = true;
        }
        else if (message.contains("We need a sprinkle of sugar")) {
            println("Need to add sugar!");
            needSugar = true;
        }
        else if (message.contains("need another handful of chocolate chunks")) {
            println("Need to add chocolate!");
            needChocolate = true;
        }
        else if (message.contains("Flavour's a little plain") || message.contains("add another dash of spice")) {
            println("Need to add spice!");
            needSpice = true;
        }
    }

    @Override
    public void onLoop() {
        Player player = getLocalPlayer();

        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            Execution.delay(random.nextLong(2500, 5500))    ;
            return;
        }

        ResultSet<Component> results = ComponentQuery.newQuery(1473).componentIndex(5).itemName("Christmas wrapping paper booster").option("Consume").results();
        Component result = results.first();
        if(result != null) {
            result.interact("Consume");
        }
        if(forceCollectionTurnIn) {
            handleForceCollectionTurnIn(player);
            return;
        }

        sgc.saveConfig();

        if(useMaizeLootTokens){
            handleMaizeMazeLootTokens();
        }

        switch (botState) {
            case HOT_CHOCOLATE:
                handleHotChocolate(player);
                return;
            case ICE_FISHING:
                handleIceFishing(player);
                return;
            case SNOWBALL_FLETCHING:
                handleSnowballFletching(player);
                return;
            case FIR_WOODCUTTING:
                handleFirWoodcutting(player);
                return;
            case TOY_CRAFTING:
                handleToyCrafting(player);
                return;
            case MAZE:
                handleMaze(player);
                return;
            case TURNINCOLLECTIONS:
                handleCollectionTurnIn(player);
                break;
            case PUMPKIN:
                handlePumpkin(player);
                break;
            case THIEVING:
                handleThieving(player);
                break;
            case SUMMONING:
                handleSummoning(player);
                break;
            case ARCHAEOLOGY:
                handleArchaeology(player);
                break;
            default:
                Execution.delay(random.nextLong(1000, 3000));
                break;
        }
    }

    private void handleHotChocolate(Player player) {
        if (needFirewood) {
            println("Need firewood, searching...");
            EntityResultSet<SceneObject> firewoodResults = SceneObjectQuery.newQuery()
                    .name("Firewood")
                    .option("Take")
                    .results();

            SceneObject firewood = firewoodResults.nearest();
            if (firewood != null) {
                println("Found firewood at: " + firewood.getCoordinate());
                firewood.interact("Take");
                Execution.delayUntil(5000, () -> Backpack.contains("Firewood"));
                needFirewood = false;
                println("Got firewood: " + Backpack.contains("Firewood"));
                return;
            } else {
                println("No firewood found!");
            }
        }

        if (needMilk) {
            println("Need milk, searching...");
            EntityResultSet<SceneObject> milkResults = SceneObjectQuery.newQuery()
                    .name("Milk")
                    .option("Take")
                    .results();

            SceneObject milk = milkResults.nearest();
            if (milk != null) {
                println("Found milk at: " + milk.getCoordinate());
                milk.interact("Take");
                Execution.delayUntil(5000, () -> Backpack.contains("Milk"));
                needMilk = false;
                println("Got milk: " + Backpack.contains("Milk"));
                return;
            } else {
                println("No milk found!");
            }
        }

        if (needSugar) {
            println("Need sugar, searching...");
            EntityResultSet<SceneObject> sugarResults = SceneObjectQuery.newQuery()
                    .name("Sugar")
                    .option("Take")
                    .results();

            SceneObject sugar = sugarResults.nearest();
            if (sugar != null) {
                println("Found sugar at: " + sugar.getCoordinate());
                sugar.interact("Take");
                Execution.delayUntil(5000, () -> Backpack.contains("Sugar"));
                needSugar = false;
                println("Got sugar: " + Backpack.contains("Sugar"));
                return;
            } else {
                println("No sugar found!");
            }
        }

        if (needChocolate) {
            println("Need chocolate, searching...");
            EntityResultSet<SceneObject> chocolateResults = SceneObjectQuery.newQuery()
                    .name("Chocolate")
                    .option("Take")
                    .results();

            SceneObject chocolate = chocolateResults.nearest();
            if (chocolate != null) {
                println("Found chocolate at: " + chocolate.getCoordinate());
                chocolate.interact("Take");
                Execution.delayUntil(5000, () -> Backpack.contains("Chocolate chunks"));
                needChocolate = false;
                println("Got chocolate: " + Backpack.contains("Chocolate chunks"));
                return;
            } else {
                println("No chocolate found!");
            }
        }

        if (needSpice) {
            println("Need spice, searching...");
            EntityResultSet<SceneObject> spiceResults = SceneObjectQuery.newQuery()
                    .name("Spices")
                    .option("Take")
                    .results();

            SceneObject spice = spiceResults.nearest();
            if (spice != null) {
                println("Found spice at: " + spice.getCoordinate());
                spice.interact("Take");
                Execution.delayUntil(5000, () -> Backpack.contains("Spice"));
                needSpice = false;
                println("Got spice: " + Backpack.contains("Spice"));
                return;
            } else {
                println("No spice found!");
            }
        }


        // Update last animation time if we're currently animating
        if (player.getAnimationId() != -1) {
            lastAnimationCookingTime = System.currentTimeMillis();
            return;
        }

        long timeSinceLastAnim = System.currentTimeMillis() - lastAnimationCookingTime;
        if (player.getAnimationId() == -1 && timeSinceLastAnim >= random.nextInt(1000, 2000)) {
            println("Player is idle and timeout reached (" + timeSinceLastAnim + "ms), searching for pot...");
            EntityResultSet<SceneObject> potResults = SceneObjectQuery.newQuery()
                    .id(131826)
                    .option("Assist Aoife")
                    .results();

            SceneObject pot = potResults.nearest();
            if (pot != null) {
                println("Found pot at: " + pot.getCoordinate());
                pot.interact("Assist Aoife");
                println("Interacted with pot");
                Execution.delay(random.nextLong(600, 1200));
            } else {
                println("No pot found!");
            }
        }
    }

    private void handleIceFishing(Player player) {
        if (Backpack.isFull()) {
            EntityResultSet<SceneObject> barrelResults = SceneObjectQuery.newQuery()
                    .name("Barrel of fish")
                    .option("Deposit all")
                    .results();

            SceneObject barrel = barrelResults.nearest();
            if (barrel != null) {
                println("Depositing fish...");
                barrel.interact("Deposit all");
                Execution.delayUntil(5000, () -> !Backpack.isFull());
                return;
            }
        }

        if (player.getAnimationId() == -1 && !Backpack.isFull()) {
            EntityResultSet<Npc> fishingSpotResults = NpcQuery.newQuery()
                    .name("Icy fishing spot")
                    .option("Catch")
                    .results();
            Npc fishingSpot = fishingSpotResults.nearest();
            if (fishingSpot != null) {
                println("Starting to fish...");
                fishingSpot.interact("Catch");
                Execution.delay(random.nextLong(3000, 6000));

            }
        } else {
            Execution.delay(random.nextLong(1000, 3000));
        }
    }

    private void handleToyCrafting(Player player) {
        if (Backpack.contains(COMPLETE_MARIONETTE)) {
            EntityResultSet<Npc> maeveResults = NpcQuery.newQuery()
                    .name("Maeve")
                    .option("Talk to")
                    .results();

            Npc maeve = maeveResults.nearest();
            if (maeve != null) {
                println("Turning in completed marionettes...");
                maeve.interact("Talk to");
                Execution.delay(random.nextLong(2000, 4000));
            }
            return;
        }

        if (Backpack.contains(PAINTED_MARIONETTE) && Backpack.contains("Marionette handle")) {
            EntityResultSet<SceneObject> finishingBenchResults = SceneObjectQuery.newQuery()
                    .name("Finishing bench")
                    .option("Assemble")
                    .results();

            SceneObject finishingBench = finishingBenchResults.nearest();
            if (finishingBench != null) {
                println("Assembling marionettes...");
                finishingBench.interact("Assemble");
                Execution.delayUntil(90000, () -> !Backpack.contains(PAINTED_MARIONETTE) || !Backpack.contains("Marionette handle"));
                Execution.delay(random.nextLong(600, 2000));
            }
            return;
        }

        if (Backpack.contains(UNPAINTED_MARIONETTE)) {
            EntityResultSet<SceneObject> paintingBenchResults = SceneObjectQuery.newQuery()
                    .name("Painting bench")
                    .option("Paint")
                    .results();

            SceneObject paintingBench = paintingBenchResults.nearest();
            if (paintingBench != null) {
                println("Painting marionettes...");
                paintingBench.interact("Paint");
                Execution.delayUntil(30000, () -> !Backpack.contains(UNPAINTED_MARIONETTE));
                Execution.delay(random.nextLong(600, 2000));
            }
            return;
        }

        if (Backpack.contains("Dry fir wood")) {
            EntityResultSet<SceneObject> carvingBenchResults = SceneObjectQuery.newQuery()
                    .name("Carving bench")
                    .option("Carve")
                    .results();

            SceneObject carvingBench = carvingBenchResults.nearest();
            if (carvingBench != null) {
                println("Carving wood...");
                carvingBench.interact("Carve");
                Execution.delayUntil(120000, () -> !Backpack.contains("Dry fir wood"));
                Execution.delay(random.nextLong(600, 2000));
            }
            return;
        }

        boolean shouldGetWood = Backpack.isEmpty() || hasOnlySingleMarionette();
        println("Inventory check - Should get wood: " + shouldGetWood);

        if (shouldGetWood) {
            EntityResultSet<SceneObject> crateResults = SceneObjectQuery.newQuery()
                    .name("Crate of wood")
                    .option("Take from")
                    .results();

            SceneObject crate = crateResults.nearest();
            if (crate != null) {
                println("Taking wood from crate...");
                crate.interact("Take from");
                Execution.delayUntil(15000, Backpack::isFull);
                Execution.delay(random.nextLong(600, 2000));
            }
        }
    }

    private boolean hasOnlySingleMarionette() {
        int marionettes = 0;
        for (Item item : Backpack.container().getItems()) {
            if (item != null) {
                if (item.getId() == UNPAINTED_MARIONETTE ||
                        item.getId() == PAINTED_MARIONETTE ||
                        item.getId() == COMPLETE_MARIONETTE ||
                        item.getId() == INCOMPLETE_MARIONETTE) {
                    marionettes++;
                    println("Found marionette item: " + item.getId());
                }
            }
        }

        println("Found " + marionettes + " marionette items");
        return marionettes == 1;
    }

    private void handleFirWoodcutting(Player player) {

        if (!Backpack.isFull() && player.getAnimationId() == -1) {
            EntityResultSet<SceneObject> treeResults = SceneObjectQuery.newQuery()
                    .name("Fir")
                    .option("Chop down")
                    .results();

            SceneObject tree = treeResults.nearest();
            if (tree != null && !tree.isHidden()) {
                println("Chopping fir tree...");
                tree.interact("Chop down");
                Execution.delayUntil(random.nextInt(3000, 6000), Backpack::isFull);
            }
        }

        if (Backpack.contains("Fir logs") && Backpack.isFull()) {
            EntityResultSet<SceneObject> stumpResults = SceneObjectQuery.newQuery()
                    .name("Log splitting stump")
                    .option("Split logs")
                    .results();

            SceneObject stump = stumpResults.nearest();
            if (stump != null) {
                println("Splitting fir logs...");
                stump.interact("Split logs");
                Execution.delayUntil(90000, () -> !Backpack.contains("Fir logs"));
                Execution.delay(random.nextLong(600, 2000));
            }
            return;
        }
        if (Backpack.contains("Split fir logs")) {
            EntityResultSet<SceneObject> stockpileResults = SceneObjectQuery.newQuery()
                    .name("Split log stockpile")
                    .option("Deposit all")
                    .results();

            SceneObject stockpile = stockpileResults.nearest();
            if (stockpile != null) {
                println("Depositing split logs...");
                stockpile.interact("Deposit all");
                Execution.delayUntil(10000, () -> !Backpack.contains("Split fir logs"));
            }
            return;
        }

        Execution.delay(random.nextLong(600, 2000));
    }

    private void handleSnowballFletching(Player player) {
        EntityResultSet<Npc> smokeyResults = NpcQuery.newQuery()
                .name("Smokey")
                .option("Pelt snowball")
                .results();
        Npc smokey = smokeyResults.nearest();
        if (smokey != null) {
            println("Found Smokey! Equipping snowballs if needed and cooling him...");

            if (!Equipment.contains("Snowball")) {
                if(Backpack.contains("Snowball")){
                    Backpack.interact("Snowball", "Wield");
                }
            }

            smokey.interact("Pelt snowball");
            Execution.delay(random.nextLong(1500, 2500));
            return;
        }

        if (player.getAnimationId() != -1) {
            lastSnowballInteraction = System.currentTimeMillis();
            return;
        }

        if (System.currentTimeMillis() - lastSnowballInteraction < IDLE_TIMEOUT) {
            return;
        }

        EntityResultSet<SceneObject> snowPileResults = SceneObjectQuery.newQuery()
                .name("Pile of snow")
                .option("Create snowball")
                .results();

        SceneObject snowPile = snowPileResults.nearest();
        if (snowPile != null) {
            println("Creating snowballs...");
            snowPile.interact("Create snowball");
            lastSnowballInteraction = System.currentTimeMillis();
        }
    }
    private void handleMaze(Player player) {
        if (!mazeEntranceArea.contains(player.getCoordinate()) && !usedDoor) {
            println("Moving to maze entrance at " + mazeEntranceArea);
            Movement.walkTo(mazeEntranceArea.getCoordinate().getX(), mazeEntranceArea.getCoordinate().getY(), false);
            Execution.delayUntil(5000, () -> mazeEntranceArea.contains(player));
            return;
        }

        EntityResultSet<SceneObject> mazeDoorResults = SceneObjectQuery.newQuery().name("Maize Maze portal").option("Enter").results();
        SceneObject mazeEntranceDoor = mazeDoorResults.nearest();

        if (mazeEntranceDoor != null && !usedDoor) {
            println("Entering the maze...");
            mazeEntranceDoor.interact("Enter");
            Execution.delayUntil(5000, () -> player.getCoordinate().getRegionId() != 2330);
            Execution.delay(random.nextLong(2000, 3000));
            usedDoor = true;
        }

        if (!implingArea.contains(player.getCoordinate())) {
            println("Not in the impling area, navigate to the area manually.");
            Execution.delay(random.nextLong(2000, 3000));
            return;
        }

        if (!Backpack.contains("Bone club", 10)) {
            EntityResultSet<Npc> results = NpcQuery.newQuery().name("Zombie impling").option("Catch").inside(implingArea).results();
            Npc impling = results.nearest();

            if (impling != null && impling.interact("Catch")) {
                println("Catching a Zombie impling...");
                Execution.delay(random.nextLong(1200, 2000));
                Execution.delayUntil(3000, () -> player.getAnimationId() == -1);
            } else {
                println("No Zombie impling found, waiting...");
                Execution.delay(random.nextLong(100, 200));
            }
        } else {
            Execution.delayUntil(4000, () -> getLocalPlayer().getAnimationId() == -1);
            println("Collected 10 Bone clubs. Preparing to jump over the fence...");
            EntityResultSet<SceneObject> fenceResults = SceneObjectQuery.newQuery().name("Fence obstacle").option("Jump over").inside(fenceArea).results();
            SceneObject fence = fenceResults.nearest();

            if (fence != null && fence.interact("Jump over")) {
                println("Jumping over the fence...");
                Execution.delayUntil(8000, () -> player.getAnimationId() == 19972);
                Execution.delayUntil(2000, () -> player.getAnimationId() != 19972);
            }

            println("Preparing to fight the boss...");

            while (Backpack.contains("Bone club")) {
                println("Using Bone Clubs to spook the boss...");
                EntityResultSet<Npc> bossResults = NpcQuery.newQuery().name("Solak-o'-lantern").option("Spook (melee)").results();
                Npc boss = bossResults.nearest();

                if (boss != null) {
                    if (boss.interact("Spook (melee)")) {
                        println("Spooked Solak");
                        Execution.delay(random.nextLong(1000, 1200));
                    }
                } else {
                    println("Could not find Skaraxxi!");
                    break;
                }
            }

            println("Out of Bone Clubs or boss defeated!");
            Execution.delay(random.nextLong(5000, 8000));
            usedDoor = false;
        }
    }

    private void waitForStillness() {
        Execution.delay(random.nextLong(1500, 2000));
        Player player = getLocalPlayer();
        while (player != null && (player.isMoving() || player.getAnimationId() != -1)) {
            Execution.delay(random.nextLong(500, 1000));
        }
    }

    private void navigateWithEnemyAvoidance(Player player, Area targetArea) {
        if (!player.isMoving() && player.getAnimationId() != -1) {
            Npc enemy = NpcQuery.newQuery()
                    .id(ZOMBIE).id(SKELETON)
                    .results()
                    .nearest();

            if (enemy != null && enemy.distanceTo(player) <= 2) {
                println("Spooking enemy!");
                enemy.interact("Spook");
                Execution.delay(random.nextLong(2500, 3000));
            }

            Coordinate destination = targetArea.getRandomWalkableCoordinate();
            Movement.walkTo(destination.getX(), destination.getY(), false);
            println("Navigating to " + destination);

            Npc impling = NpcQuery.newQuery()
                    .id(ZOMBIE_IMPLING)
                    .results()
                    .nearest();

            if (impling != null) {
                println("Catching zombie impling!");
                impling.interact("Catch");
                Execution.delay(random.nextLong(500, 1000));
            }
        }

        Execution.delay(random.nextLong(250, 1000));
    }

    private void handleMaizeMazeLootTokens() {
        String[] collectionItems = {
                "Maize Maze loot token (double)", "Maize Maze loot token (triple)"
        };

        for (String item : collectionItems) {
            if (Backpack.contains(item)) {
                Backpack.interact(item, "Redeem All");
                Execution.delay(random.nextLong(600, 1200));
                break;
            }
        }
    }

    private void handleCollectionTurnIn(Player player) {

        if(!bankArea.contains(player)){
            Movement.walkTo(bankLocation.getX(), bankLocation.getY(), true);
            Execution.delay(random.nextLong(6000, 10000));
        }
        if (!Bank.isOpen()) {
            println("Opening the bank...");
            Bank.open();
            Execution.delayUntil(5000, Bank::isOpen);
            Bank.depositAll();
            Execution.delay(random.nextLong(600, 1200));
        } else {
            println("Can't open bank");
            return;
        }

        String[] firstCollectionItems = {
                "Soiled storybook", "Polished horn", "Dusty snow globe", "Ruined letter", "Strange coins"
        };

        String[] secondCollectionItems = {
                "Ancient skull", "Ancient torso", "Ancient left arm", "Ancient right arm", "Ancient left leg", "Ancient right leg"
        };

        int firstCollectionItemCount = firstCollectionItems.length;
        int secondCollectionItemCount = secondCollectionItems.length;

        println("Calculating available collections from the bank...");
        int firstCollectionAvailable = calculateAvailableCollections(firstCollectionItems, firstCollectionItemCount);
        int secondCollectionAvailable = calculateAvailableCollections(secondCollectionItems, secondCollectionItemCount);

        println("You have enough items to complete " + firstCollectionAvailable + " first collections.");
        println("You have enough items to complete " + secondCollectionAvailable + " second collections.");

        int totalFreeSpace = 28;

        while (firstCollectionAvailable > 0 || secondCollectionAvailable > 0) {
            int maxCollectionsForFirst = Math.min(totalFreeSpace / firstCollectionItemCount, firstCollectionAvailable);
            int maxCollectionsForSecond = Math.min(totalFreeSpace / secondCollectionItemCount, secondCollectionAvailable);

            if (maxCollectionsForFirst == 0 && maxCollectionsForSecond == 0) {
                println("No more collections to withdraw. Stopping.");
                setBotState(BotState.IDLE);
                return;
            }

            int collectionsToWithdrawFirst = Math.min(maxCollectionsForFirst, totalFreeSpace / firstCollectionItemCount);
            int collectionsToWithdrawSecond = Math.min(maxCollectionsForSecond, (totalFreeSpace - collectionsToWithdrawFirst * firstCollectionItemCount) / secondCollectionItemCount);

            println("Withdrawing " + collectionsToWithdrawFirst + " first collections and " + collectionsToWithdrawSecond + " second collections.");
            if (collectionsToWithdrawFirst > 0) {
                withdrawMaxCollectionItems(firstCollectionItems, collectionsToWithdrawFirst);
                totalFreeSpace -= collectionsToWithdrawFirst * firstCollectionItemCount;
                firstCollectionAvailable -= collectionsToWithdrawFirst;
            }

            if (collectionsToWithdrawSecond > 0) {
                withdrawMaxCollectionItems(secondCollectionItems, collectionsToWithdrawSecond);
                totalFreeSpace -= collectionsToWithdrawSecond * secondCollectionItemCount;
                secondCollectionAvailable -= collectionsToWithdrawSecond;
            }

            Bank.close();

            if (collectionsToWithdrawFirst > 0) {
                println("Turning in first collection.");
                handleCollectionSubmission(player);
            }

            if (collectionsToWithdrawSecond > 0) {
                println("Turning in second collection.");
                handleSecondCollectionSubmission(player);
            }

            return;
        }

        println("Finished turning in collections.");
        setBotState(BotState.IDLE);
    }

    private int calculateAvailableCollections(String[] collectionItems, int collectionItemCount) {
        int minAvailable = Integer.MAX_VALUE;

        for (String item : collectionItems) {
            int itemCount = Bank.getCount(item);
            println("Found " + itemCount + " of " + item + " in the bank.");
            minAvailable = Math.min(minAvailable, itemCount);
        }

        int collectionsAvailable = minAvailable;
        println("You can complete " + collectionsAvailable + " full collections based on available items.");

        return collectionsAvailable;
    }

    private boolean withdrawMaxCollectionItems(String[] items, int fullCollectionCount) {
        int itemsPerCollection = items.length;
        int maxCollectionsInInventory = 28 / itemsPerCollection;

        int collectionsToWithdraw = Math.min(fullCollectionCount, maxCollectionsInInventory);
        println("Withdrawing " + collectionsToWithdraw + " of each item for the collections.");

        for (String item : items) {  // Loop through each item
            println("Withdrawing " + collectionsToWithdraw + " of " + item);
            if (!Bank.withdraw(item, TransferOptionType.X.getVarbitValue())) {
                println("Failed to withdraw " + collectionsToWithdraw + " of " + item);
                return false;
            }
            Execution.delay(random.nextLong(200, 400));
        }

        return true;
    }

    private void handlePumpkin(Player player) {
        println("Handling pumpkin interaction. Interaction count: " + interactionCount + " / " + maxInteractionsBeforePause);

        EntityResultSet<Npc> pumpkinResults = NpcQuery.newQuery()
                .name("Smashing Pumpkin")
                .option("Smash")
                .results();
        Npc pumpkin = pumpkinResults.nearest();

        if (pumpkin != null) {
            println("Smashing pumpkin...");
            pumpkin.interact("Smash");
            Execution.delay(random.nextLong(2300, 2400));

            interactionCount++;

            println("Interaction #" + interactionCount + " completed.");

            if (interactionCount >= maxInteractionsBeforePause) {
                long randomWait = random.nextLong(minWaitTime * 1000, maxWaitTime * 1000);
                println("Pausing for " + randomWait / 1000 + " seconds after " + interactionCount + " interactions.");

                Execution.delay(randomWait);

                interactionCount = 0;
                maxInteractionsBeforePause = random.nextInt(26) + 15;

                println("Next pause will be after " + maxInteractionsBeforePause + " interactions.");
            }
        } else {
            println("No Smashing Pumpkin found.");
        }
    }

    private void handleThieving(Player player) {

        if(Backpack.contains("Ancient remains", ancientRemainsCount) && identifyAncientRemains && !Backpack.isFull()){
            Backpack.interact("Ancient remains", "Identify all");
            Execution.delay(random.nextLong(1200, 1800));
        }

        if (turnInCollections) {
            if (backpackContainsSecondCollectionItems()) {
                handleSecondCollectionSubmission(player);
                return;
            }
        }

        if (Backpack.isFull()) {
            println("Backpack is full. Moving to bank to deposit.");
            if (!bankArea.contains(player.getCoordinate())) {
                println("Player is not in the bank area, moving to bank...");
                Movement.walkTo(bankLocation.getX(), bankLocation.getY(), true);
                Execution.delay(random.nextLong(6000, 10000));
                return;
            }
            if (bankArea.contains(player.getCoordinate())) {
                println("Player is in the bank area, opening the bank...");
                Bank.open();
                Execution.delayUntil(10000, Bank::isOpen);
                if(Bank.isOpen()){
                    Execution.delay(random.nextLong(1000, 2000));
                    Bank.depositAllExcept("Complete tome");
                    Bank.close();
                    return;
                }
            }
            return;
        }

        int currentAnimationId = player.getAnimationId();

        if (currentAnimationId != lastAnimationId) {
            lastAnimationId = currentAnimationId;
            lastAnimationChangeTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - lastAnimationChangeTime >= (thievingDelay * 1000)) {
            if (currentAnimationId == -1) {
                EntityResultSet<SceneObject> lootableObjects = SceneObjectQuery.newQuery()
                        .option("Loot")
                        .results();
                SceneObject nearestLootable = lootableObjects.nearest();

                if (nearestLootable != null) {
                    println("Lootable object found. Interacting...");
                    nearestLootable.interact("Loot");
                    Execution.delay(random.nextLong(2000, 4000));
                } else {
                    println("No lootable objects nearby.");
                }
            } else {
                if (currentAnimationId == 24887) {
                    println("No loot animation detected for " + (thievingDelay) + " seconds. Searching for new loot...");
                    EntityResultSet<SceneObject> lootableObjects = SceneObjectQuery.newQuery()
                            .option("Loot")
                            .results();
                    SceneObject nearestLootable = lootableObjects.nearest();
                    if (nearestLootable != null) {
                        println("New lootable object found. Interacting...");
                        nearestLootable.interact("Loot");
                        Execution.delay(random.nextLong(6000, 8000));
                    } else {
                        println("No other lootable objects found.");
                    }
                } else {
                    println("Player is already interacting. Waiting...");
                    Execution.delay(random.nextLong(1000, 2000));
                }
            }
        } else {
            Execution.delay(random.nextLong(600, 1200));
        }
    }

    private void handleSummoning(Player player) {

        if (!summoningArea.contains(player)) {
            println("Moving to summoning area");
            moveTo(summoningArea.getRandomWalkableCoordinate());
        }

        if (Backpack.contains("Ancient remains", ancientRemainsCount) && identifyAncientRemains) {
            Backpack.interact("Ancient remains", "Identify all");
            Execution.delay(random.nextLong(1200, 1800));
        }

        if (turnInCollections) {
            if (backpackContainsSecondCollectionItems()) {
                handleSecondCollectionSubmission(player);
                return;
            }
        }

        if (Backpack.isFull()) {
            println("Backpack is full. Moving to bank to load the last preset.");
            if (!bankArea.contains(player.getCoordinate())) {
                println("Player is not in the bank area, moving to bank...");
                Movement.walkTo(bankLocation.getX(), bankLocation.getY(), true);
                Execution.delay(random.nextLong(6000, 10000));
                return;
            }
            if (bankArea.contains(player.getCoordinate())) {
                println("Player is in the bank area, opening the bank...");
                Bank.open();
                Execution.delayUntil(10000, Bank::isOpen);
                if (Bank.isOpen()) {
                    Execution.delay(random.nextLong(1000, 2000));
                    Bank.depositAllExcept("Complete tome");
                    Bank.close();
                    return;
                }
            }
            return;
        }

        EntityResultSet<SceneObject> unlitCandleResults = SceneObjectQuery.newQuery()
                .name("Candle")
                .option("Light")
                .results();
        SceneObject unlitCandle = unlitCandleResults.nearest();

        if (unlitCandle != null) {
            println("Unlit candle found. Lighting...");
            unlitCandle.interact("Light");
            Execution.delay(random.nextLong(2000, 4000));
            return;
        }

        EntityResultSet<SceneObject> summoningCircleResults = SceneObjectQuery.newQuery()
                .name("Summoning circle")
                .option("Summon")
                .results();
        SceneObject summoningCircle = summoningCircleResults.nearest();

        if (summoningCircle != null) {
            long currentTime = System.currentTimeMillis();

            if (player.getAnimationId() == ANIMATION_ID_RITUAL) {
                println("Player is already performing the ritual...");
                lastAnimationTime = currentTime;
            } else {
                if (currentTime - lastAnimationTime > RITUAL_TIMEOUT_MS) {
                    println("Ritual not detected for over 5 seconds. Restarting the summoning ritual...");
                    summoningCircle.interact("Summon");
                    Execution.delayUntil(4000, () -> player.getAnimationId() == ANIMATION_ID_RITUAL);
                    lastAnimationTime = currentTime;
                }
            }
        }
    }

    private void handleArchaeology(Player player) {
        handleDialogue();

        if (Backpack.contains("Complete tome", tomeCount) && handInTomes) {
            handInTomesProcedure(player);
        }

        handleAncientRemains();

        if (turnInCollections) {
            if (backpackContainsCollectionItems()) {
                handleCollectionSubmission(player);
                return;
            }

            if (backpackContainsSecondCollectionItems()) {
                handleSecondCollectionSubmission(player);
                return;
            }
        }

        if (Backpack.isFull()) {
            handleFullBackpack(player);
            return;
        }

        if (!excavationArea.contains(player.getCoordinate())) {
            returnToExcavationSite(player);
            return;
        }

        handleChaseSprite(player);
    }


    private void handleDialogue() {
        if (Interfaces.isOpen(1189)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 77922323);
        }
    }

    private void handInTomesProcedure(Player player) {
        println("Complete tome count reached, handing in tomes");

        stopPlayerAnimation(player);

        teleportToWarsRetreat(player);

        moveToTomeArea(player);

        interactWithDeskToStudyTomes(player);

        teleportBackToEvent();
    }

    private void stopPlayerAnimation(Player player) {
        if (player.getAnimationId() != -1) {
            println("Trying to stop interaction");
            Movement.walkTo(player.getCoordinate().getX(), player.getCoordinate().getY(), false);
            Execution.delayUntil(1200, () -> player.getAnimationId() == -1);
        }
    }

    private void teleportToWarsRetreat(Player player) {
        if (player.getAnimationId() == -1 && ActionBar.containsAbility("War's Retreat Teleport")) {
            if (ActionBar.useAbility("War's Retreat Teleport")) {
                Execution.delayUntil(10000, () -> player.getCoordinate().getRegionId() == 13214);
            }
        }
    }

    private void moveToTomeArea(Player player) {
        if (!tomeArea.contains(player)) {
            println("Moving to tome area");
            moveTo(tomeArea.getRandomWalkableCoordinate());
            println("Moved to tome area");
        }
    }

    private void interactWithDeskToStudyTomes(Player player) {
        if (tomeArea.contains(player)) {
            println("Interacting with desk to study the tome...");
            SceneObject desk = SceneObjectQuery.newQuery().name("Desk").option("Study").results().nearest();
            if (desk != null && desk.interact("Study")) {
                println("Interacted with desk to study");
                Execution.delayUntil(60000, () -> !Backpack.contains("Complete tome"));
            }
            println("All tomes studied.");
        }
    }

    private void teleportBackToEvent() {
        println("Attempting to teleport back to the event...");
        ResultSet<Component> communityComponent = ComponentQuery.newQuery(1431).componentIndex(0).results();
        Component communityButton = communityComponent.first();

        if (communityButton != null && !communityButton.isHidden()) {
            interactWithCommunityComponent(communityButton);
        } else {
            println("Community component not found or not visible.");
        }
    }

    private void interactWithCommunityComponent(Component communityButton) {
        if (MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 4, 93782016)) {
            println("Open the community window");
            Execution.delayUntil(5000, () -> Interfaces.isOpen(1289));
            Execution.delay(random.nextLong(600, 1200));

            ResultSet<Component> teleportComponent = ComponentQuery.newQuery(1289).componentIndex(28).results();
            Component teleportButton = teleportComponent.first();

            if (teleportButton != null && !teleportButton.isHidden()) {
                println("Teleport button found and visible. Interacting...");
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 84475932);
                Execution.delay(random.nextLong(1200, 1800));
                println("Teleportation back to the event complete.");
            } else {
                println("Teleport button not found or not visible.");
            }
        }
    }

    private void handleAncientRemains() {
        if (Backpack.contains("Ancient remains", ancientRemainsCount) && identifyAncientRemains) {
            Backpack.interact("Ancient remains", "Identify all");
            Execution.delay(random.nextLong(1200, 1800));

        }
    }

    private void handleFullBackpack(Player player) {
        println("Backpack is full. Moving to bank to load the last preset.");
        if (!bankArea.contains(player.getCoordinate())) {
            println("Player is not in the bank area, moving to bank...");
            Movement.walkTo(bankLocation.getX(), bankLocation.getY(), true);
            Execution.delay(random.nextLong(6000, 10000));
        } else {
            openAndUseBank();
        }
    }

    private void openAndUseBank() {
        println("Player is in the bank area, opening the bank...");
        Bank.open();
        Execution.delayUntil(10000, Bank::isOpen);
        if (Bank.isOpen()) {
            Execution.delay(random.nextLong(1000, 2000));
            Bank.depositAllExcept("Complete tome");
            Bank.close();
        }
    }

    private void returnToExcavationSite(Player player) {
        println("Returning to excavation site at " + excavationLocation);
        Movement.walkTo(excavationLocation.getX(), excavationLocation.getY(), true);
        Execution.delay(random.nextLong(3000, 5000));
    }

    private void handleChaseSprite(Player player) {
        if (chaseSprite) {
            SpotAnimation archaeologySpot = SpotAnimationQuery.newQuery().animations(7307).results().nearest();
            if (archaeologySpot != null) {
                interactWithSpotAnimation(player, archaeologySpot);
            } else {
                interactWithNearestAncientRemains();
            }
        } else {
            interactWithNearestMysteryRemains(player);
        }
    }



    private void interactWithSpotAnimation(Player player, SpotAnimation archaeologySpot) {
        Coordinate spotCoordinate = archaeologySpot.getCoordinate();
        double distanceToSpot = player.getCoordinate().distanceTo(spotCoordinate);

        if (player.getAnimationId() == 33350 && distanceToSpot <= 1) {
            Execution.delay(random.nextLong(2000, 4000));
            return;
        }

        if (player.getAnimationId() == -1 || distanceToSpot > 1) {
            println("Interacting with 'Mystery remains' at: " + spotCoordinate);
            SceneObject mysteryRemains = SceneObjectQuery.newQuery().name("Mystery remains").option("Excavate").on(spotCoordinate).results().nearest();
            if (mysteryRemains != null) {
                mysteryRemains.interact("Excavate");
                Execution.delay(random.nextLong(3000, 5000));
            }
        }
    }

    private void interactWithNearestAncientRemains() {
        println("No archaeology SpotAnimation found nearby. Interacting with 'Ancient remains'.");
        SceneObject mysteryRemains = SceneObjectQuery.newQuery().name("Mystery remains").option("Excavate").results().nearest();
        if (mysteryRemains != null) {
            mysteryRemains.interact("Excavate");
            Execution.delay(random.nextLong(3000, 5000));
        }
    }

    private void interactWithNearestMysteryRemains(Player player) {
        if (player.getAnimationId() == -1) {
            println("Interacting with the nearest 'Mystery remains'.");
            interactWithNearestAncientRemains();
        } else {
            Execution.delay(random.nextLong(3000, 5000));
        }
    }


    private boolean backpackContainsCollectionItems() {
        String[] collectionItems = {
                "Soiled storybook", "Polished horn", "Dusty snow globe", "Ruined letter", "Strange coins"
        };

        for (String item : collectionItems) {
            if (!Backpack.contains(item)) {
                return false;
            }
        }
        return true;
    }

    private boolean backpackContainsSecondCollectionItems() {
        String[] secondCollectionItems = {
                "Ancient skull", "Ancient torso", "Ancient left arm", "Ancient right arm", "Ancient left leg", "Ancient right leg"
        };

        for (String item : secondCollectionItems) {
            if (!Backpack.contains(item)) {
                return false;
            }
        }
        return true;
    }

    private void handleCollectionSubmission(Player player) {
        if (!eepArea.contains(player.getCoordinate())) {
            println("Moving to Eep...");
            Movement.walkTo(eepLocation.getX(), eepLocation.getY(), true);
            Execution.delayUntil(random.nextLong(2000, 3000), player::isMoving);
            Execution.delayUntil(random.nextLong(6000, 10000),() -> !player.isMoving());
        }

        println("Found collection items in backpack. Finding NPC 'Eep'...");
        EntityResultSet<Npc> eepResults = NpcQuery.newQuery().name("Eep").option("Talk to").results();
        Npc eep = eepResults.nearest();

        if (eep != null) {
            println("Interacting with NPC 'Eep'...");
            int attempts = 0;
            boolean interactionSuccess = false;

            while (attempts < 3 && !interactionSuccess) {
                interactionSuccess = eep.interact("Collections");
                if (!interactionSuccess) {
                    println("Failed to interact with NPC, retrying... (" + (attempts + 1) + "/3)");
                    attempts++;
                    Execution.delay(random.nextLong(1000, 2000));
                }
            }

            if (interactionSuccess) {
                println("Opening collections window...");
                if (Execution.delayUntil(10000, () -> Interfaces.isOpen(656))) {
                    println("Collections interface opened.");

                    while (backpackContainsCollectionItems() && Interfaces.isOpen(656)) {
                        ResultSet<Component> contributeAllComponent = ComponentQuery.newQuery(656)
                                .componentIndex(24)
                                .results();

                        Component confirmButton = contributeAllComponent.first();
                        if (confirmButton != null && !confirmButton.isHidden()) {
                            println("Found 'Contribute All' button. Attempting to interact...");
                            MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 0, 42991641);
                            Execution.delay(random.nextLong(1200, 1800));
                            println("Collection submitted.");
                        } else {
                            println("Failed to find or interact with 'Contribute All' button.");
                            break;
                        }
                    }

                    println("No more collection items in inventory.");
                } else {
                    println("Failed to open collections interface.");
                }
            } else {
                println("NPC interaction failed after 3 attempts.");
            }
        } else {
            println("NPC 'Eep' not found.");
        }
    }

    private void handleSecondCollectionSubmission(Player player) {
        if (!eepArea.contains(player.getCoordinate())) {
            println("Moving to Eep...");
            Movement.walkTo(eepLocation.getX(), eepLocation.getY(), true);
            Execution.delayUntil(random.nextLong(2000, 3000), player::isMoving);
            Execution.delayUntil(random.nextLong(6000, 10000),() -> !player.isMoving());
        }

        println("Found second collection items in backpack. Finding NPC 'Eep'...");

        EntityResultSet<Npc> eepResults = NpcQuery.newQuery()
                .name("Eep")
                .option("Talk to")
                .results();
        Npc eep = eepResults.nearest();

        if (eep != null) {
            println("Interacting with NPC 'Eep'...");
            int attempts = 0;
            boolean interactionSuccess = false;

            while (attempts < 3 && !interactionSuccess) {
                interactionSuccess = eep.interact("Collections");
                if (!interactionSuccess) {
                    println("Failed to interact with NPC, retrying... (" + (attempts + 1) + "/3)");
                    attempts++;
                    Execution.delay(random.nextLong(1000, 2000));
                }
            }

            if (interactionSuccess) {
                println("Opening collections window...");
                if (Execution.delayUntil(15000, () -> Interfaces.isOpen(656))) {
                    println("Collections interface opened, switching to second collection...");

                    Execution.delay(random.nextLong(600, 1200));

                    ResultSet<Component> secondCollectionTab = ComponentQuery.newQuery(656)
                            .componentIndex(30)
                            .results();
                    Component secondTab = secondCollectionTab.first();
                    if (secondTab != null && !secondTab.isHidden()) {
                        println("Second collection tab is visible. Attempting to switch...");
                        MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 1, 42991647);
                        Execution.delay(random.nextLong(1200, 1800));

                        while (backpackContainsSecondCollectionItems()) {
                            ResultSet<Component> contributeAllComponent = ComponentQuery.newQuery(656)
                                    .componentIndex(24)
                                    .results();
                            Component confirmButton = contributeAllComponent.first();
                            if (confirmButton != null && !confirmButton.isHidden()) {
                                println("Found 'Contribute All' button. Attempting to confirm...");
                                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 0, 42991641);
                                Execution.delay(random.nextLong(1200, 1800));
                                println("Second collection submitted.");
                            } else {
                                println("Failed to find or interact with 'Contribute All' button.");
                                break;
                            }
                        }

                        println("No more second collection items in inventory.");
                    } else {
                        println("Failed to find or interact with second collection tab.");
                    }
                } else {
                    println("Failed to open collections interface.");
                }
            } else {
                println("NPC interaction failed after 3 attempts.");
            }
        } else {
            println("NPC 'Eep' not found.");
        }
    }

    private void handleForceCollectionTurnIn(Player player) {
        if(!bankArea.contains(player)){
            Movement.walkTo(bankLocation.getX(), bankLocation.getY(), true);
            Execution.delay(random.nextLong(6000, 10000));
        }
        if (!Bank.isOpen()) {
            println("Opening the bank...");
            Bank.open();
            Execution.delayUntil(5000, Bank::isOpen);
            Bank.depositAll();
            Execution.delay(random.nextLong(600, 1200));
        }

        String[] firstCollectionItems = {
                "Soiled storybook", "Polished horn", "Dusty snow globe", "Ruined letter", "Strange coins"
        };

        String[] secondCollectionItems = {
                "Ancient skull", "Ancient torso", "Ancient left arm", "Ancient right arm", "Ancient left leg", "Ancient right leg"
        };

        int firstCollectionItemCount = firstCollectionItems.length;
        int secondCollectionItemCount = secondCollectionItems.length;

        println("Calculating available collections from the bank...");
        int firstCollectionAvailable = calculateAvailableCollections(firstCollectionItems, firstCollectionItemCount);
        int secondCollectionAvailable = calculateAvailableCollections(secondCollectionItems, secondCollectionItemCount);

        println("Collections available - First: " + firstCollectionAvailable + ", Second: " + secondCollectionAvailable);

        if (firstCollectionAvailable == 0 && secondCollectionAvailable == 0) {
            println("No collections available to turn in.");
            setBotState(lastActivityState);
            forceCollectionTurnIn = false;
            return;
        }

        int totalFreeSpace = 28;
        while (firstCollectionAvailable > 0 || secondCollectionAvailable > 0) {
            int maxCollectionsForFirst = Math.min(totalFreeSpace / firstCollectionItemCount, firstCollectionAvailable);
            int maxCollectionsForSecond = Math.min(totalFreeSpace / secondCollectionItemCount, secondCollectionAvailable);

            int collectionsToWithdrawFirst = Math.min(maxCollectionsForFirst, totalFreeSpace / firstCollectionItemCount);
            int collectionsToWithdrawSecond = Math.min(maxCollectionsForSecond, (totalFreeSpace - collectionsToWithdrawFirst * firstCollectionItemCount) / secondCollectionItemCount);

            if (collectionsToWithdrawFirst > 0) {
                withdrawMaxCollectionItems(firstCollectionItems, collectionsToWithdrawFirst);
                firstCollectionAvailable -= collectionsToWithdrawFirst;
            }

            if (collectionsToWithdrawSecond > 0) {
                withdrawMaxCollectionItems(secondCollectionItems, collectionsToWithdrawSecond);
                secondCollectionAvailable -= collectionsToWithdrawSecond;
            }

            Bank.close();

            if (collectionsToWithdrawFirst > 0) {
                handleCollectionSubmission(player);
            }

            if (collectionsToWithdrawSecond > 0) {
                handleSecondCollectionSubmission(player);
            }

            if (firstCollectionAvailable == 0 && secondCollectionAvailable == 0) {
                println("All collections turned in.");
                setBotState(lastActivityState);
                forceCollectionTurnIn = false;
                return;
            }

            if(!bankArea.contains(player)){
                Movement.walkTo(bankLocation.getX(), bankLocation.getY(), true);
                Execution.delay(random.nextLong(6000, 10000));
            }
            if (!Bank.isOpen()) {
                Bank.open();
                Execution.delayUntil(5000, Bank::isOpen);
            }
        }
    }


    public boolean moveTo(Coordinate location) {
        Player player = getLocalPlayer();
        if (location.distanceTo(player.getCoordinate()) < 1) return true;
        NavPath path = NavPath.resolve(location);
        TraverseEvent.State moveState = Movement.traverse(path);
        return moveState == TraverseEvent.State.FINISHED;
    }

    public void setBotState(BotState state) {
        this.botState = state;
        println("Bot state changed to: " + state);
    }
    public ScriptConfig getConfig() {
        return config;
    }

    @Override
    public CoaezEventGraphicsContext getGraphicsContext() {
        return sgc;
    }
}