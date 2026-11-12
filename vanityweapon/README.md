# Soulbound Vanity Weapon (Fabric 1.21.1)

Adds a **VANITY** box to the left of the WEAPON box on Soulbound's Equip screen.
Pick any item from your inventory; it is shown in battle instead of your equipped weapon.
Stats, damage, skills, tiers: all still come from the real equipped weapon.

* Left-click the box: open the inventory picker. Right-click: clear.
* Server-side: stored per player (saved with the player, kept on death) and substituted into
  the data Soulbound broadcasts to other players (turn-based battles + live combat),
  so everyone sees it. Install on server AND clients (server-only = others see it but you don't).

## Build
Requires JDK 21 and internet access on first build.

    gradle wrapper --gradle-version 8.10      # one-time, if there's no ./gradlew
    ./gradlew build

Output: `build/libs/vanityweapon-1.0.0.jar` -> put in `mods/` next to turnbattle-2.0.28 + Fabric API.
No Soulbound jar is needed to compile (all mixins target it by string name).
If `fabric_version` in gradle.properties isn't found, use any current 1.21.1 build of Fabric API.

## What it hooks in Soulbound 2.0.28 (all read-only / render-only)
Server:  TurnBattleMod.broadcastEquipInfo, LiveCombat.broadcastArmed  (swap the broadcast weapon)
Client:  BattleEntityRenderer.renderBattle3D, OverhaulView.weaponFor, LiveCombatClient.armedWeapon,
         OmegaCinema.equippedWeapon, MagicantScreen.drawFigure, VoidMenuScreen.drawMember (menu preview)
UI:      EquipScreen (via Fabric screen events + an accessor for socketPos/selected)
These target Soulbound's internal names, so a Soulbound update may require adjusting them.
