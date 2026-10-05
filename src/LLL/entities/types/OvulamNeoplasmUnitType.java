package LLL.entities.types;

import LLL.content.*;
import LLL.entities.ai.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.*;

public class OvulamNeoplasmUnitType extends UnitType{
  public OvulamNeoplasmUnitType(String name){
    super(name);
    controller = u -> TrophosomeAI.hasTarget(u) ? new TrophosomeAI() : (!playerControllable || (u.team.isAI() && !u.team.rules().rtsAi) ? aiController.get() : new CommandAI());

    outlineColor = Pal.neoplasmOutline;
    immunities.addAll(StatusEffects.burning, StatusEffects.melting);
    envDisabled = Env.none;
    drawCell = false;
    healFlash = true;
    healColor = Pal.neoplasm1;

    deathSound = OvulamSounds.unitDeathSounds.random();
  }
}
