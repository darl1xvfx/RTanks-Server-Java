/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package rt.server.battles.ctf;

import rt.server.battles.ctf.anticheats.CaptureTheFlagAnticheatModel;
import rt.server.battles.ctf.flags.FlagServer;
import rt.server.battles.ctf.flags.FlagState;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.utils.JSON;
import rt.server.battles.BattleController;
import rt.server.battles.BattleModel;
import java.util.ArrayList;

public class CTFModel extends CaptureTheFlagAnticheatModel {
    private BattleModel bfModel;
    private FlagServer blueFlag = new FlagServer();
    private FlagServer redFlag = new FlagServer();

    public CTFModel(BattleModel bfModel) {
        super(bfModel);
        this.bfModel = bfModel;
        this.blueFlag.flagTeamType = "BLUE";
        this.redFlag.flagTeamType = "RED";
        this.blueFlag.state = FlagState.BASE;
        this.redFlag.state = FlagState.BASE;
        this.blueFlag.basePosition = this.blueFlag.position = bfModel.map.flagBluePosition;
        this.redFlag.basePosition = this.redFlag.position = bfModel.map.flagRedPosition;
    }

    public void attemptToTakeFlag(BattleController taker, String flagTeamType) {
        FlagServer flag = this.getTeamFlag(flagTeamType);
        if (flag.owner != null) {
            return;
        }
        if (taker.playerTeamType.equals(flagTeamType)) {
            FlagServer enemyFlag = this.getEnemyTeamFlag(flagTeamType);
            if (flag.state == FlagState.DROPED) {
                this.returnFlag(taker, flag);
                return;
            }
            if (enemyFlag.owner == taker) {
                if (this.onDeliveredFlag(taker)) {
                    return;
                }
                this.bfModel.send2Battle(new Command(Commands.DeliverFlag, taker.playerTeamType, taker.tank.id));
                enemyFlag.state = FlagState.BASE;
                enemyFlag.owner = null;
                taker.flag = null;
                if (enemyFlag.returnTimer != null) {
                    enemyFlag.returnTimer.stop = true;
                    enemyFlag.returnTimer = null;
                }
                int score = 999;//(taker.playerTeamType == "BLUE" ? this.bfModel.getBattleEntity().redPeople : this.bfModel.battleInfo.bluePeople) * 10;
                taker.client.addScore(score);
                taker.userStat.score += 999;
                taker.updateStat();
                double fund = 0.0;
                ArrayList<BattleController> otherTeam = new ArrayList<BattleController>();
                for (BattleController player : this.bfModel.users.values()) {
                    if (player.playerTeamType.equals(taker.playerTeamType) || player.playerTeamType.equals("NONE")) continue;
                    otherTeam.add(player);
                }
                for (BattleController otherPlayer : otherTeam) {
                    fund += Math.sqrt((double)otherPlayer.client.user.rang * 0.125);
                }
                this.bfModel.battleFundModel.addFund((int) fund);
                if (taker.playerTeamType == "BLUE") {
                    ++this.bfModel.getBattleEntity().scoreBlue;
                    this.bfModel.send2Battle(new Command(Commands.ChangeTeamScores, "BLUE", this.bfModel.getBattleEntity().scoreBlue));
                    /*if (this.bfModel.battleInfo.numFlags == this.bfModel.battleInfo.scoreBlue) {
                        this.bfModel.tanksKillModel.restartBattle(false);
                    }*/
                } else {
                    ++this.bfModel.getBattleEntity().scoreRed;
                    this.bfModel.send2Battle(new Command(Commands.ChangeTeamScores, "RED", this.bfModel.getBattleEntity().scoreRed));
                    /*if (this.bfModel.battleInfo.numFlags == this.bfModel.battleInfo.scoreRed) {
                        this.bfModel.tanksKillModel.restartBattle(false);
                    }*/
                }
            }
        } else {
            if (this.onTakeFlag(taker, flag)) {
                return;
            }
            this.bfModel.send2Battle(new Command(Commands.FlagTaken, taker.tank.id, flagTeamType));
            flag.state = FlagState.TAKEN_BY;
            flag.owner = taker;
            taker.flag = flag;
            if (flag.returnTimer != null) {
                flag.returnTimer.stop = true;
                flag.returnTimer = null;
            }
        }
    }

    public void dropFlag(BattleController following, Vector3 posDrop) {
        FlagServer flag = this.getEnemyTeamFlag(following.playerTeamType);
        flag.state = FlagState.DROPED;
        flag.position = posDrop;
        flag.owner = null;
        following.flag = null;
        flag.returnTimer = new FlagReturnTimer(this, flag);
        flag.returnTimer.start();
        this.bfModel.send2Battle(new Command(Commands.FlagDrop, JSON.parseDropFlagCommand(flag)));
    }

    public void returnFlag(BattleController following, FlagServer flag) {
        flag.state = FlagState.BASE;
        if (flag.owner != null) {
            flag.owner.flag = null;
            flag.owner = null;
        }
        flag.position = flag.basePosition;
        if (flag.returnTimer != null) {
            flag.returnTimer.stop = true;
            flag.returnTimer = null;
        }
        String id = following == null ? null : following.tank.id;
        this.bfModel.send2Battle(new Command(Commands.ReturnFlag, flag.flagTeamType, id));
        int score = 5;
        if (following != null) {
            following.client.addScore(score);
            following.userStat.score += score;
            following.updateStat();
        }
    }

    private FlagServer getTeamFlag(String teamType) {
        if (teamType.equals("BLUE")) {
            return this.blueFlag;
        }
        if (teamType.equals("RED")) {
            return this.redFlag;
        }
        return null;
    }

    private FlagServer getEnemyTeamFlag(String teamType) {
        if (teamType.equals("BLUE")) {
            return this.redFlag;
        }
        if (teamType.equals("RED")) {
            return this.blueFlag;
        }
        return null;
    }

    public FlagServer getRedFlag() {
        return this.redFlag;
    }

    public FlagServer getBlueFlag() {
        return this.blueFlag;
    }
}

