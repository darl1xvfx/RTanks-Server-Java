package rt.server.battles;

import rt.server.battles.tank.Tank;
import rt.server.services.calculators.DamageCalculator;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class DamageController {
    private BattleModel bm;
    
    public DamageController(BattleModel b) {
    	this.bm = b;
    }
    
    public void damageTank(BattleController controller, BattleController damager, float damage, boolean considerDD, String turret) {
    	String damageType = "NORMAL";
        if (controller != null && damager != null) {
            Tank tank = controller.tank;
            if (!tank.state.equals("newcome") && !tank.state.equals("suicide")) {
               if (!this.bm.getBattleEntity().type.equals("NONE") || controller == damager || !controller.playerTeamType.equals(damager.playerTeamType) || this.bm.getBattleEntity().friendlyFire) {
                  damage = DamageCalculator.calculateDamageWithResistance(damage, 0);
                  if (tank.isUsedEffect("armor")) {
                     damage /= 2.0F;
                  }

                  if (damager.tank.isUsedEffect("double_damage") && considerDD) {
                     damage *= 2.0F;
                  }
                  
                  DamageTankData damageData = new DamageTankData();
                  damageData.damage = damage;
                  damageData.timeDamage = System.currentTimeMillis();
                  damageData.damager = damager;
                  
                  tank.addHealth(-(DamageCalculator.calculateHealth(tank, damage)));
                  if (tank.health <= 0) {
                     tank.health = 0;
                     this.bm.killTank(controller, damager, turret);
                     damageType = "FATAL";
                  }
                  new Command(Commands.DamageTank, tank.id, (int) damage, damageType).send(damager.client);
               }
            }
         }
    }
    
    public boolean healPlayer(BattleController healer, BattleController target, float addHeal) {
        Tank targetTank = target.tank;
        if (targetTank.state.equals("newcome") || targetTank.state.equals("suicide")) {
            return false;
        }
        if (targetTank.health >= 10000) {
            return false;
        }
        targetTank.addHealth(DamageCalculator.calculateHealth(targetTank, addHeal));
        new Command(Commands.DamageTank, targetTank.id, (int) addHeal, "HEAL").send(healer.client);
        if (targetTank.health >= 10000) {
            targetTank.health = 10000;
        }
        return true;
    }
}
