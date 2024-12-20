package net.botwithus;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.rs3.script.config.ScriptConfig;

import static net.botwithus.rs3.script.ScriptConsole.println;

public class CoaezEventGraphicsContext extends ScriptGraphicsContext {

    private final CoaezEvents coaezHalloweenEvent;
    private CoaezEvents.BotState lastBotState;
    private CoaezEvents.BotState lastActivityBotState;
    private boolean lastIdentifyAncientRemains;
    private boolean lastChaseSprite;
    private int lastAncientRemainsCount;
    private int lastThievingDelay;
    private boolean lastHandInTomes;
    private int lastTomeCount;
    private boolean lastForceCollectionTurnIn;
    private boolean lastUseMaizeLootTokens;
    private boolean lastTurnInCollections;
    private boolean lastBuySpecialBox;

    private int selectedLampSkillIndex = 0;
    private int selectedStarSkillIndex = 0;
    private static final String[] skillOptions = {
            "Attack", "Constitution", "Mining", "Strength", "Agility", "Smithing", "Defense",
            "Herblore", "Fishing", "Ranged", "Thieving", "Cooking", "Prayer", "Crafting",
            "Firemaking", "Magic", "Fletching", "Woodcutting", "Runecrafting", "Slayer",
            "Farming", "Construction", "Hunter", "Summoning", "Dungeoneering", "Divination",
            "Invention", "Archaeology", "Necromancy"
    };
    public CoaezEventGraphicsContext(ScriptConsole scriptConsole, CoaezEvents script) {
        super(scriptConsole);
        this.coaezHalloweenEvent = script;
        loadConfig();

        lastBotState = coaezHalloweenEvent.botState;
        lastActivityBotState = coaezHalloweenEvent.lastActivityState;
        lastIdentifyAncientRemains = coaezHalloweenEvent.identifyAncientRemains;
        lastChaseSprite = coaezHalloweenEvent.chaseSprite;
        lastAncientRemainsCount = coaezHalloweenEvent.ancientRemainsCount;
        lastThievingDelay = coaezHalloweenEvent.thievingDelay;
        lastHandInTomes = coaezHalloweenEvent.handInTomes;
        lastTomeCount = coaezHalloweenEvent.tomeCount;
        lastForceCollectionTurnIn = coaezHalloweenEvent.forceCollectionTurnIn;
        lastUseMaizeLootTokens = coaezHalloweenEvent.useMaizeLootTokens;
        lastTurnInCollections = coaezHalloweenEvent.turnInCollections;
        lastBuySpecialBox = coaezHalloweenEvent.buySpecialBox;
    }

    public boolean hasStateChanged() {
        boolean changed = false;

        if (lastBotState != coaezHalloweenEvent.botState ||
                lastActivityBotState != coaezHalloweenEvent.lastActivityState ||
                lastIdentifyAncientRemains != coaezHalloweenEvent.identifyAncientRemains ||
                lastChaseSprite != coaezHalloweenEvent.chaseSprite ||
                lastAncientRemainsCount != coaezHalloweenEvent.ancientRemainsCount ||
                lastThievingDelay != coaezHalloweenEvent.thievingDelay ||
                lastHandInTomes != coaezHalloweenEvent.handInTomes ||
                lastTomeCount != coaezHalloweenEvent.tomeCount ||
                lastForceCollectionTurnIn != coaezHalloweenEvent.forceCollectionTurnIn ||
                lastUseMaizeLootTokens != coaezHalloweenEvent.useMaizeLootTokens ||
                lastTurnInCollections != coaezHalloweenEvent.turnInCollections ||
                lastBuySpecialBox != coaezHalloweenEvent.buySpecialBox) {

            lastBotState = coaezHalloweenEvent.botState;
            lastActivityBotState = coaezHalloweenEvent.lastActivityState;
            lastIdentifyAncientRemains = coaezHalloweenEvent.identifyAncientRemains;
            lastChaseSprite = coaezHalloweenEvent.chaseSprite;
            lastAncientRemainsCount = coaezHalloweenEvent.ancientRemainsCount;
            lastThievingDelay = coaezHalloweenEvent.thievingDelay;
            lastHandInTomes = coaezHalloweenEvent.handInTomes;
            lastTomeCount = coaezHalloweenEvent.tomeCount;
            lastForceCollectionTurnIn = coaezHalloweenEvent.forceCollectionTurnIn;
            lastUseMaizeLootTokens = coaezHalloweenEvent.useMaizeLootTokens;
            lastTurnInCollections = coaezHalloweenEvent.turnInCollections;
            lastBuySpecialBox = coaezHalloweenEvent.buySpecialBox;

            changed = true;
        }

        return changed;
    }

    public void saveConfig() {
        ScriptConfig config = coaezHalloweenEvent.getConfig();

        if (config != null) {
            config.addProperty("botState", coaezHalloweenEvent.botState.toString());
            config.addProperty("lastActivityState", coaezHalloweenEvent.lastActivityState.toString());
            config.addProperty("identifyAncientRemains", String.valueOf(coaezHalloweenEvent.identifyAncientRemains));
            config.addProperty("chaseSprite", String.valueOf(coaezHalloweenEvent.chaseSprite));
            config.addProperty("ancientRemainsCount", String.valueOf(coaezHalloweenEvent.ancientRemainsCount));
            config.addProperty("thievingDelay", String.valueOf(coaezHalloweenEvent.thievingDelay));
            config.addProperty("handInTomes", String.valueOf(coaezHalloweenEvent.handInTomes));
            config.addProperty("tomeCount", String.valueOf(coaezHalloweenEvent.tomeCount));
            config.addProperty("forceCollectionTurnIn", String.valueOf(coaezHalloweenEvent.forceCollectionTurnIn));
            config.addProperty("buySpecialBox", String.valueOf(coaezHalloweenEvent.buySpecialBox));

            config.save();
        }
    }

    public void loadConfig() {
        ScriptConfig config = coaezHalloweenEvent.getConfig();

        if (config != null) {
            config.load();

            String buySpecialBoxValue = config.getProperty("buySpecialBox");
            if (buySpecialBoxValue != null) {
                coaezHalloweenEvent.buySpecialBox = Boolean.parseBoolean(buySpecialBoxValue);
            }

            String forceCollectionTurnInValue = config.getProperty("forceCollectionTurnIn");
            if (forceCollectionTurnInValue != null) {
                coaezHalloweenEvent.forceCollectionTurnIn = Boolean.parseBoolean(forceCollectionTurnInValue);
            }

            String handInTomes = config.getProperty("handInTomes");
            if (handInTomes != null) {
                coaezHalloweenEvent.handInTomes = Boolean.parseBoolean(handInTomes);
            }

            String tomeCount = config.getProperty("tomeCount");
            if (tomeCount != null) {
                coaezHalloweenEvent.tomeCount = Integer.parseInt(tomeCount);
            }
            String thievingDelay = config.getProperty("thievingDelay");
            if (thievingDelay != null) {
                coaezHalloweenEvent.ancientRemainsCount = Integer.parseInt(thievingDelay);
            }

            String ancientRemainsCount = config.getProperty("ancientRemainsCount");
            if (ancientRemainsCount != null) {
                coaezHalloweenEvent.ancientRemainsCount = Integer.parseInt(ancientRemainsCount);
            }

            String botStateValue = config.getProperty("botState");
            if (botStateValue != null) {
                coaezHalloweenEvent.botState = CoaezEvents.BotState.valueOf(botStateValue);
            }

            String lastActivityStateValue = config.getProperty("lastActivityState");
            if (lastActivityStateValue != null) {
                coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.valueOf(lastActivityStateValue);
            }

            String identifyAncientRemainsValue = config.getProperty("identifyAncientRemains");
            if (identifyAncientRemainsValue != null) {
                coaezHalloweenEvent.identifyAncientRemains = Boolean.parseBoolean(identifyAncientRemainsValue);
            }

            String chaseSpriteValue = config.getProperty("chaseSprite");
            if (chaseSpriteValue != null) {
                coaezHalloweenEvent.chaseSprite = Boolean.parseBoolean(chaseSpriteValue);
            }

            println("Script state loaded.");
        }
    }

    public void drawSettings() {
        if (ImGui.Begin("Coaez Holiday Event Settings", ImGuiWindowFlag.AlwaysAutoResize.getValue())) {
            if (ImGui.BeginTabBar("EventTabs",0)) {
                if (ImGui.BeginTabItem("Halloween",0)) {
                    drawHalloweenTab();
                    ImGui.EndTabItem();
                }

                if (ImGui.BeginTabItem("Christmas",0)) {
                    drawChristmasTab();
                    ImGui.EndTabItem();
                }

                ImGui.EndTabBar();
            }

            saveConfig();
            ImGui.End();
        }
    }

    private void drawHalloweenTab() {
        ImGui.Text("Archaeology Event Settings");
        if (ImGui.Button("Enable Archaeology event")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.ARCHAEOLOGY);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.ARCHAEOLOGY;
            coaezHalloweenEvent.getConsole().println("Switched to Archaeology spooky event.");
        }

        ImGui.Text("For tomes: Must have War's Retreat Teleport. Community button must be visible on the settings ribbon bar.");
        coaezHalloweenEvent.handInTomes = ImGui.Checkbox("Study tomes", coaezHalloweenEvent.handInTomes);
        ImGui.SameLine();
        coaezHalloweenEvent.tomeCount = ImGui.InputInt("Study tomes when count reaches", coaezHalloweenEvent.tomeCount);

        coaezHalloweenEvent.chaseSprite = ImGui.Checkbox("Chase sprite during archaeology", coaezHalloweenEvent.chaseSprite);

        ImGui.Separator();

        ImGui.Text("Summoning Event Settings");
        if (ImGui.Button("Enable Summoning event")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.SUMMONING);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.SUMMONING;
            coaezHalloweenEvent.getConsole().println("Switched to Summoning spooky event.");
        }
        ImGui.SameLine();
        ImGui.Text("Simply start near the ritual.");

        ImGui.Separator();

        ImGui.Text("Thieving Event Settings");
        if (ImGui.Button("Enable Thieving event")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.THIEVING);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.THIEVING;
            coaezHalloweenEvent.getConsole().println("Switched to Thieving spooky event.");
        }
        ImGui.SameLine();
        coaezHalloweenEvent.thievingDelay = ImGui.InputInt("Custom delay for thieving (recommended 5+ seconds)", coaezHalloweenEvent.thievingDelay);

        ImGui.Separator();

        ImGui.Text("General Settings (Archaeology, Summoning, Thieving)");
        coaezHalloweenEvent.identifyAncientRemains = ImGui.Checkbox("Identify ancient remains", coaezHalloweenEvent.identifyAncientRemains);
        ImGui.SameLine();
        ImGui.SetItemWidth(100);
        coaezHalloweenEvent.ancientRemainsCount = ImGui.InputInt("Identify remains when count reaches", coaezHalloweenEvent.ancientRemainsCount);
        coaezHalloweenEvent.useMaizeLootTokens = ImGui.Checkbox("Redeem loot tokens", coaezHalloweenEvent.useMaizeLootTokens);
        ImGui.Text("Turn in collection is disabled automatically once we hit points cap.");
        coaezHalloweenEvent.turnInCollections = ImGui.Checkbox("Turn in collections", coaezHalloweenEvent.turnInCollections);
        ImGui.Text("Force Collection Turn-in");
        ImGui.Text("This will turn in all available collections regardless of token cap.");

        ImGui.Separator();

        ImGui.Text("Collection Turn-In Settings");
        if (ImGui.Button("Enable Turn in collections")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.TURNINCOLLECTIONS);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.TURNINCOLLECTIONS;
            coaezHalloweenEvent.getConsole().println("Switched to turning in collections event.");
        }
        ImGui.Text("Standalone collections turn in. Start near the bank with empty inventory.");
        coaezHalloweenEvent.forceCollectionTurnIn = ImGui.Checkbox("Force turn in collections (ignores token cap)", coaezHalloweenEvent.forceCollectionTurnIn);

        ImGui.Separator();

        ImGui.Text("Pumpkin Event Settings");
        if (ImGui.Button("Enable Pumpkin event")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.PUMPKIN);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.PUMPKIN;
            coaezHalloweenEvent.getConsole().println("Switched to Pumpkin spooky event.");
        }

        ImGui.Text("Only applicable to pumpkin interaction.");
        ImGui.Text("Set high value if you don't want to take breaks, or leave default for randomized breaks.");

        coaezHalloweenEvent.maxInteractionsBeforePause = ImGui.InputInt("Max interactions before break", coaezHalloweenEvent.maxInteractionsBeforePause);
        coaezHalloweenEvent.minWaitTime = ImGui.InputInt("Min wait time (seconds)", coaezHalloweenEvent.minWaitTime);
        coaezHalloweenEvent.maxWaitTime = ImGui.InputInt("Max wait time (seconds)", coaezHalloweenEvent.maxWaitTime);

        ImGui.Separator();

        if (ImGui.Button("Maze spooky event")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.MAZE);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.MAZE;
            coaezHalloweenEvent.getConsole().println("Switched to Maze spooky event.");
        }

    }

    private void drawChristmasTab() {

        if (ImGui.Button("Enable Fir Woodcutting")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.FIR_WOODCUTTING);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.FIR_WOODCUTTING;
            coaezHalloweenEvent.getConsole().println("Switched to Fir Woodcutting event.");
        }
        ImGui.Text("Start near the Fir trees.");
        ImGui.Separator();

        if (ImGui.Button("Enable Toy Crafting")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.TOY_CRAFTING);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.TOY_CRAFTING;
            coaezHalloweenEvent.getConsole().println("Switched to Toy Crafting event.");
        }
        ImGui.Text("Start near the crafting benches.");
        ImGui.Separator();

        if (ImGui.Button("Enable Snowball Fletching")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.SNOWBALL_FLETCHING);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.SNOWBALL_FLETCHING;
            coaezHalloweenEvent.getConsole().println("Switched to Snowball Fletching event.");
        }
        ImGui.Text("Start near the Snowball pile.");
        ImGui.Separator();

        if (ImGui.Button("Enable Ice Fishing")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.ICE_FISHING);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.ICE_FISHING;
            coaezHalloweenEvent.getConsole().println("Switched to Ice Fishing event.");
        }
        ImGui.Text("Start near the Icy fishing spot.");
        ImGui.Separator();
        if (ImGui.Button("Enable Hot Chocolate Cooking")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.HOT_CHOCOLATE);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.HOT_CHOCOLATE;
            coaezHalloweenEvent.getConsole().println("Switched to Hot Chocolate Cooking event.");
        }
        ImGui.Text("Start near the Hot chocolate pot.");
        ImGui.Separator();
        if (ImGui.Button("Enable Decoration Making")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.DECORATIONS);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.DECORATIONS;
            coaezHalloweenEvent.getConsole().println("Switched to Decoration Making event.");
        }
        ImGui.Text("Start near the Decoration benches.");
        ImGui.Separator();

        ImGui.Text("Start near the bank");
        if (ImGui.Button("Open boxes")) {
            coaezHalloweenEvent.setBotState(CoaezEvents.BotState.BOX_REDEMPTION);
            coaezHalloweenEvent.lastActivityState = CoaezEvents.BotState.BOX_REDEMPTION;
            coaezHalloweenEvent.getConsole().println("Switched to opening event boxes.");
        }

        ImGui.Text("Select Skill for XP Lamps:");
        ImGui.SetItemWidth(150);
        int newLampIndex = ImGui.Combo("##lampSkillOptions", selectedLampSkillIndex, skillOptions);
        if (newLampIndex != selectedLampSkillIndex) {
            selectedLampSkillIndex = newLampIndex;
            String selectedSkill = skillOptions[selectedLampSkillIndex];
            int actionId = coaezHalloweenEvent.skillActions.get(selectedSkill);
            coaezHalloweenEvent.setSelectedSkillActionId(actionId);
            coaezHalloweenEvent.getConsole().println("Lamp skill selected: " + selectedSkill + ", Action ID: " + actionId);
        }

        ImGui.Text("Select Skill for Bonus XP Stars:");
        ImGui.SetItemWidth(150);
        int newStarIndex = ImGui.Combo("##starSkillOptions", selectedStarSkillIndex, skillOptions);
        if (newStarIndex != selectedStarSkillIndex) {
            selectedStarSkillIndex = newStarIndex;
            String selectedSkill = skillOptions[selectedStarSkillIndex];
            int actionId = coaezHalloweenEvent.skillActions.get(selectedSkill);
            coaezHalloweenEvent.setSelectedSkillActionId(actionId);
            coaezHalloweenEvent.getConsole().println("Star skill selected: " + selectedSkill + ", Action ID: " + actionId);
        }

        ImGui.Separator();
        coaezHalloweenEvent.buySpecialBox = ImGui.Checkbox("Buy special boxes", coaezHalloweenEvent.buySpecialBox);
        ImGui.Text("Will buy 2 special boxes when spirit reaches 50k");
    }

    @Override
    public void drawOverlay() {
    }
    public int getLampSkillActionId() {
        return coaezHalloweenEvent.skillActions.get(skillOptions[selectedLampSkillIndex]);
    }

    public int getStarSkillActionId() {
        return coaezHalloweenEvent.skillActions.get(skillOptions[selectedStarSkillIndex]);
    }
}
