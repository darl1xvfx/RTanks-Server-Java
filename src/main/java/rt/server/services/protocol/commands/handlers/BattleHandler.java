package rt.server.services.protocol.commands.handlers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import rt.server.battles.BattleModel;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.battles.weapons.laser.Laser;
import rt.server.client.ClientEntity;
import rt.server.logger.Logger;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.CommandHandler;
import rt.server.services.protocol.commands.Commands;

public class BattleHandler implements CommandHandler {

	@Override
	public void handle(ClientEntity client, String command, String[] args) {
		if (command.equals(Commands.GetInitDataLocalTank.command))
		{
			client.controller.createTank();
			return;
		};
		if (command.equals(Commands.AttemptToTakeBonus.command))
		{
			try {
			   BattleModel battle = client.controller.battle;
			   JSONObject obj = (JSONObject)new JSONParser().parse(args[0]);
			   battle.bonusTakeModel.onTake(client.controller, (String)obj.get("bonus_id"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		};
		if (command.equals(Commands.Chat.command))
		{
			BattleModel battle = client.controller.battle;
			battle.battleChatModel.onData(client.controller, args[0], Boolean.valueOf(args[1]));
			return;
		};
		if (command.equals(Commands.UpdateDirection.command)) {
			BattleModel battle = client.controller.battle;
			battle.send2Battle(new Command(Commands.UpdateDirection, client.controller.tank.id, args[0]));
			return;
		}
		if (command.equals(Commands.HideLaser.command)) {
			BattleModel battle = client.controller.battle;
			battle.send2Battle(new Command(Commands.HideLaser, client.user.username));
			return;
		}
		if (command.equals(Commands.RotateTurret.command))
		{
			if (client.controller.tank.state.equals("suicide")) {
				return;
			}
			BattleModel battle = client.controller.battle;
			battle.send2Battle(new Command(Commands.RotateTurret, client.user.username, args[0]));
			return;
		};
		if (command.equals(Commands.Move.command))
		{
			if (client.controller.tank.state.equals("suicide")) {
				return;
			}
			BattleModel battle = client.controller.battle;
            Vector3 pos = new Vector3(0.0f, 0.0f, 0.0f);
            Vector3 orient = new Vector3(0.0f, 0.0f, 0.0f);
            Vector3 line = new Vector3(0.0f, 0.0f, 0.0f);
            Vector3 ange = new Vector3(0.0f, 0.0f, 0.0f);
            float turretDir = 0.0f;
            int bits = 0;
            String[] temp = args[0].split("@");
            pos.x = Float.parseFloat(temp[0]);
            pos.y = Float.parseFloat(temp[1]);
            pos.z = Float.parseFloat(temp[2]);
            orient.x = Float.parseFloat(temp[3]);
            orient.y = Float.parseFloat(temp[4]);
            orient.z = Float.parseFloat(temp[5]);
            line.x = Float.parseFloat(temp[6]);
            line.y = Float.parseFloat(temp[7]);
            line.z = Float.parseFloat(temp[8]);
            ange.x = Float.parseFloat(temp[9]);
            ange.y = Float.parseFloat(temp[10]);
            ange.z = Float.parseFloat(temp[11]);
            turretDir = Float.parseFloat(args[1]);
            bits = Integer.parseInt(args[2]);
            if (client.controller.tank.position == null) {
            	client.controller.tank.position = new Vector3(0.0f, 0.0f, 0.0f);
            }
            client.controller.tank.position = pos;
            client.controller.tank.orientation = orient;
            client.controller.tank.linVel = line;
            client.controller.tank.angVel = ange;
            client.controller.tank.turretDir = turretDir;
            client.controller.tank.controllBits = bits;
            battle.moveTank(client.controller);
            return;
		};
		if (command.equals(Commands.Suicide.command))
		{
		    client.controller.battle.killTank(client.controller, null, client.user.equipment.getTurretName());
		    return;
		};
		if (command.equals(Commands.SimpleShoot.command)) {
			return;
		}
		if (command.equals(Commands.AimAtTank.command)) {
			new Command(Commands.AimAtTank).send(client);
            return;
		}
		if (command.equals(Commands.Fire.command)) {
			client.controller.battle.onFire(client.controller, args[0]);
			return;
		}

		if (command.equals(Commands.StartFire.command)) {
			WeaponHandler weapon = client.controller.tank.weapon;
			weapon.startFire(client.controller, args[0]);
			return;
		}
		if (command.equals(Commands.StopFire.command))
		{
			client.controller.tank.weapon.stopFire(client.controller);
			client.controller.battle.send2Battle(new Command(Commands.StopFire, client.user.username));
			return;
		};
		if (command.equals(Commands.ActivateItem.command))
		{
            Vector3 _tankPos;
            try {
                _tankPos = new Vector3(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
            } catch (Exception ex) {
                _tankPos = new Vector3(0.0f, 0.0f, 0.0f);
            }
			client.controller.inventoryModel.onActivateItem(args[0], true, _tankPos);
			return;
		};
		if (command.equals(Commands.MineHit.command))
		{
			client.controller.battle.battleMinesModel.hitMine(client.controller, args[0]);
			return;
		};
		if (command.equals(Commands.IExitFromBattle.command))
		{
			client.onExitFromBattle();
			return;
		};
		if (command.equals(Commands.AttemptToTakeFlag.command))
		{
			client.controller.battle.ctfModel.attemptToTakeFlag(client.controller, args[0]);
			return;
		};
		if (command.equals(Commands.FlagDrop.command))
		{
			client.controller.parseAndDropFlag(args[0]);
			return;
		};
        if (command.equals(Commands.TankCapturingPoint.command)) 
        {
            Vector3 tankPos;
            try {
                tankPos = new Vector3(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
            } catch (Exception var4) {
                tankPos = new Vector3(0.0f, 0.0f, 0.0f);
            }
            client.controller.battle.domModel.tankCapturingPoint(client.controller, args[0], tankPos);
            return;
        };
        if (command.equals(Commands.TankLeaveCapturingPoint.command))
        {
            client.controller.battle.domModel.tankLeaveCapturingPoint(client.controller, args[0]);
            return;
        };
	}
}
