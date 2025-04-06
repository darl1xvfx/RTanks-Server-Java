/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package rt.server.battles.cp;

import rt.server.battles.BattleModel;
import rt.server.battles.maps.parser.parser.map.keypoints.CPKeypoint;
import rt.server.logger.Logger;
import rt.server.services.FastHashMap;
import rt.server.battles.BattleController;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.services.quartz.QuartzService;
import rt.server.services.quartz.TimeType;
import rt.server.services.quartz.impl.QuartzServiceImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ControlPointsModel {
    private static final int POINT_RADIUS = 1000;
    public final String QUARTZ_NAME;
    private FastHashMap<String, ControlPoint> points;
    private List<DominationPointHandler> pointsHandlers;
    private final BattleModel bfModel;
    private final QuartzService quartzService = QuartzServiceImpl.inject();
    private float scoreRed = 0.0f;
    private float scoreBlue = 0.0f;

    public ControlPointsModel(BattleModel bfModel) {
        this.bfModel = bfModel;
        this.QUARTZ_NAME = "ControlPointsModel " + this.hashCode() + " battle=" + bfModel.getBattleEntity().battleId;
        this.points = new FastHashMap<String, ControlPoint>();
        this.pointsHandlers = new ArrayList<DominationPointHandler>();
        int keyPointID = 0;
        for (CPKeypoint keypoint : bfModel.map.cpKeypoints) {
            ControlPoint point = new ControlPoint(keypoint.getPointId(), keypoint.getPosition().toVector3(), 1000.0);
            this.points.put(String.valueOf(keyPointID), point);
            this.pointsHandlers.add(new DominationPointHandler(point));
            keyPointID++;
        }
        this.quartzService.addJobInterval(this.QUARTZ_NAME, this.getName(), e -> this.pointsHandlers.forEach(point -> point.update()), TimeType.MS, 100L);
    }

    public void sendInitData(BattleController player) {
        JSONObject data = new JSONObject();
        JSONObject light = new JSONObject();
        JSONArray pointsData = new JSONArray();
        for (ControlPoint point : this.points.values()) {
            JSONObject obj = new JSONObject();
            obj.put("id", point.getId());
            obj.put("radius", point.getRadius());
            obj.put("x", Float.valueOf(point.getPos().x));
            obj.put("y", Float.valueOf(point.getPos().y));
            obj.put("z", Float.valueOf(point.getPos().z));
            obj.put("score", point.getScore());
            JSONArray users = new JSONArray();
            for (String userId : point.getUserIds()) {
                users.add(userId);
            }
            obj.put("occupated_users", users);
            pointsData.add(obj);
        }
        data.put("points", pointsData);
        data.put("blueCircle", 257970);
        data.put("neutralPedestalTexture", 322272);
        data.put("startCapturingSound", 485066);
        data.put("stopCapturingSound", 587770);
        data.put("lostPointSound", 882467);
        light.put("redPointColor", 16711680);
        light.put("redPointIntensity", 1);
        light.put("bluePointIntensity", 1);
        light.put("attenuationBegin", 250);
        light.put("neutralPointColor", 16777215);
        light.put("neutralPointIntensity", 0.7);
        light.put("attenuationEnd", 1000);
        light.put("bluePointColor", 26367);
        data.put("lighting", light);
        data.put("bluePedestalTexture", 261418);
        data.put("cp_pedestal", 988711);
        data.put("enemyPointCapturedSound", 515570);
        data.put("pointCapturedSound", 682388);
        data.put("redCircle", 737975);
        data.put("enemyLostPointSound", 322199);
        data.put("neutralCircle", 217990);
        data.put("redRayTip", 235520);
        data.put("redPedestalTexture", 714496);
        data.put("blueRayTip", 7758);
        data.put("blueRay", 654177);
        data.put("bigLetters", 272805);
        data.put("redRay", 914536);
        new Command(Commands.InitDOMModel, data.toJSONString()).send(player.client);
    }

    public synchronized void tankCapturingPoint(BattleController player, String pointId, Vector3 tankPos) {
        double dot;
        ControlPoint point = this.points.get(pointId);
        if (point != null && !((dot = point.getPos().distanceTo(tankPos)) > point.getRadius())) {
            point.addPlayer(player.playerTeamType, player);
            this.bfModel.send2Battle(new Command(Commands.TankCapturingPoint, String.valueOf(pointId), player.client.user.username));
        }
    }

    public synchronized void tankLeaveCapturingPoint(BattleController player, String pointId) {
        ControlPoint point = this.points.get(pointId);
        if (point != null && point.getUserIds().contains(player.client.user.username)) {
        	this.bfModel.send2Battle(new Command(Commands.TankLeaveCapturingPoint, player.client.user.username, pointId));
            point.removePlayer(player.playerTeamType, player);
        }
    }

    private void pointCapturedBy(ControlPoint point, String teamType) {
        this.bfModel.send2Battle(new Command(Commands.PointCapturedBy, teamType, point.getId()));
    }

    private void pointLostBy(ControlPoint point, String ownerTeamType) {
    	this.bfModel.send2Battle(new Command(Commands.PointLostBy, ownerTeamType, point.getId()));
    }

    public Collection<ControlPoint> getPoints() {
        return this.points.values();
    }

    public void restartBattle() {
        for (ControlPoint point : this.points.values()) {
            point.setScore(0.0);
            this.bfModel.send2Battle(new Command(Commands.SetPointScore, String.valueOf(point.getId()), String.valueOf((int) point.getScore())));
            point.setCountBlue(0);
            point.setCountRed(0);
            point.setPointCapturedByBlue(false);
            point.setPointCapturedByRed(false);
            point.getBlues().clear();
            point.getReds().clear();
            point.getUserIds().clear();
            this.scoreBlue = 0.0f;
            this.scoreRed = 0.0f;
        }
    }

    private void addPlayerScore(BattleController player, int score) {
        player.client.addScore(999);
        player.userStat.score += score;
        player.updateStat();
    }

    public void destroy() {
        this.quartzService.deleteJob(this.QUARTZ_NAME, this.getName());
        this.pointsHandlers.clear();
        this.pointsHandlers = null;
        this.points.clear();
        this.points = null;
    }

    class DominationPointHandler {
        private final ControlPoint point;
        public boolean alive = true;
        private boolean sendedZeroSpeedScore = false;

        public DominationPointHandler(ControlPoint point) {
            this.point = point;
            this.point.setTickableHandler(this);
        }

        public void update() {
            if (ControlPointsModel.this.bfModel.getBattleEntity() != null && !ControlPointsModel.this.bfModel.battleFinish) {
                if (ControlPointsModel.this.bfModel.getBattleEntity().killsLimit != 0 &&
                        (ControlPointsModel.this.scoreBlue >= (float) ControlPointsModel.this.bfModel.getBattleEntity().killsLimit ||
                                ControlPointsModel.this.scoreRed >= (float) ControlPointsModel.this.bfModel.getBattleEntity().killsLimit)) {
                    ControlPointsModel.this.bfModel.finishBattle();
                }
                if (this.point.getScore() >= 100.0 || this.point.getScore() <= -100.0) {
                    this.point.setScore(this.point.getScore() >= 100.0 ? 100.0 : -100.0);
                }
                if (this.point.getScore() == 100.0) {
                    if (!this.point.isPointCapturedByBlue()) {
                        float score = 0.2f * (float) 1 * 10.0f /
                                (float) this.point.getCountBlue();
                        for (BattleController player : this.point.getBlues()) {
                            ControlPointsModel.this.addPlayerScore(player, Math.round(score));
                        }
                        double fund = 0.0;
                        ArrayList<BattleController> otherTeam = new ArrayList<BattleController>();
                        for (BattleController otherPlayer : ControlPointsModel.this.bfModel.users.values()) {
                            if (otherPlayer.playerTeamType.equals("BLUE") || otherPlayer.playerTeamType.equals("NONE"))
                                continue;
                            otherTeam.add(otherPlayer);
                        }
                        for (BattleController otherPlayer : otherTeam) {
                            fund += Math.sqrt((double) otherPlayer.client.user.rang * 0.25);
                        }
                        ControlPointsModel.this.bfModel.battleFundModel.addFund((int) fund);
                        ControlPointsModel.this.pointCapturedBy(this.point, "blue");
                    }
                    this.point.setPointCapturedByBlue(true);
                    this.point.setPointCapturedByRed(false);
                    ControlPointsModel.this.scoreBlue += 0.02f;
                }
                else if (this.point.getScore() == -100.0) {
                    if (!this.point.isPointCapturedByRed()) {
                        float score = 0.2f * (float) 1 * 10.0f /
                                (float) this.point.getCountRed();
                        for (BattleController player : this.point.getReds()) {
                            ControlPointsModel.this.addPlayerScore(player, Math.round(score));
                        }
                        double fund = 0.0;
                        ArrayList<BattleController> otherTeam = new ArrayList<BattleController>();
                        for (BattleController otherPlayer : ControlPointsModel.this.bfModel.users.values()) {
                            if (otherPlayer.playerTeamType.equals("RED") || otherPlayer.playerTeamType.equals("NONE"))
                                continue;
                            otherTeam.add(otherPlayer);
                        }
                        for (BattleController otherPlayer : otherTeam) {
                            fund += Math.sqrt((double) otherPlayer.client.user.rang * 0.25);
                        }
                        ControlPointsModel.this.bfModel.battleFundModel.addFund((int) fund);
                        ControlPointsModel.this.pointCapturedBy(this.point, "red");
                    }
                    this.point.setPointCapturedByRed(true);
                    this.point.setPointCapturedByBlue(false);
                    ControlPointsModel.this.scoreRed += 0.02f;
                }
                else if (this.point.getScore() == 0.0) {
                    float score;
                    double fund;
                    ArrayList<BattleController> otherTeam;
                    if (this.point.isPointCapturedByBlue()) {
                        score = 0.2f * (float)1 * 10.0f /
                                (float) this.point.getCountRed();
                        for (BattleController player : this.point.getReds()) {
                            ControlPointsModel.this.addPlayerScore(player, Math.round(score));
                        }
                        fund = 0.0;
                        otherTeam = new ArrayList<BattleController>();
                        for (BattleController otherPlayer : ControlPointsModel.this.bfModel.users.values()) {
                            if (otherPlayer.playerTeamType.equals("RED") || otherPlayer.playerTeamType.equals("NONE"))
                                continue;
                            otherTeam.add(otherPlayer);
                        }
                        for (BattleController otherPlayer : otherTeam) {
                            fund += Math.sqrt((double) otherPlayer.client.user.rang * 0.25);
                        }
                        ControlPointsModel.this.bfModel.battleFundModel.addFund((int) fund);
                        ControlPointsModel.this.scoreRed += 0.02f;
                        ControlPointsModel.this.pointLostBy(this.point, "blue");
                    }
                    if (this.point.isPointCapturedByRed()) {
                        score = 0.2f * (float) 1 * 10.0f /
                                (float) this.point.getCountBlue();
                        for (BattleController player : this.point.getBlues()) {
                            ControlPointsModel.this.addPlayerScore(player, Math.round(score));
                        }
                        fund = 0.0;
                        otherTeam = new ArrayList<BattleController>();
                        for (BattleController otherPlayer : ControlPointsModel.this.bfModel.users.values()) {
                            if (otherPlayer.playerTeamType.equals("BLUE") || otherPlayer.playerTeamType.equals("NONE"))
                                continue;
                            otherTeam.add(otherPlayer);
                        }
                        for (BattleController otherPlayer : otherTeam) {
                            fund += Math.sqrt((double) otherPlayer.client.user.rang * 0.25);
                        }
                        ControlPointsModel.this.bfModel.battleFundModel.addFund((int) fund);
                        ControlPointsModel.this.scoreBlue += 0.02f;
                        ControlPointsModel.this.pointLostBy(this.point, "red");
                    }
                    this.point.setPointCapturedByRed(false);
                    this.point.setPointCapturedByBlue(false);
                }
                double addedScore = 0.0;
                if (this.point.getCountBlue() > this.point.getCountRed()) {
                    int countPeople = this.point.getCountBlue() - this.point.getCountRed();
                    addedScore = countPeople;
                } else if (this.point.getCountRed() > this.point.getCountBlue()) {
                    int countPeople = this.point.getCountRed() - this.point.getCountBlue();
                    addedScore = -countPeople;
                } else if (this.point.getScore() > 0.0 || this.point.getScore() < 0.0) {
                    if (this.point.isPointCapturedByBlue()) {
                        if (this.point.getScore() > 0.0) {
                            if (this.point.getCountRed() == 0) {
                                addedScore = 1.0;
                            }
                            if (this.point.getScore() >= 100.0) {
                                addedScore = 0.0;
                            }
                        }
                    } else if (this.point.isPointCapturedByRed()) {
                        if (this.point.getScore() < 0.0) {
                            if (this.point.getCountBlue() == 0) {
                                addedScore = -1.0;
                            }
                            if (this.point.getScore() <= -100.0) {
                                addedScore = 0.0;
                            }
                        }
                    } else {
                        addedScore = this.point.getCountBlue() == 0 ? (this.point.getScore() > 0.0 ? -1.0 : 1.0) : 0.0;
                    }
                }
                if (ControlPointsModel.this.scoreBlue > 0.0f) {
                    ControlPointsModel.this.bfModel.getBattleEntity().scoreBlue = (int) ControlPointsModel.this.scoreBlue;
                    ControlPointsModel.this.bfModel.send2Battle(new Command(Commands.ChangeTeamScores, "BLUE",
                            String.valueOf(ControlPointsModel.this.scoreBlue)));
                }
                if (ControlPointsModel.this.scoreRed > 0.0f) {
                    ControlPointsModel.this.bfModel.getBattleEntity().scoreRed = (int) ControlPointsModel.this.scoreRed;
                    ControlPointsModel.this.bfModel.send2Battle(new Command(Commands.ChangeTeamScores, "RED",
                            String.valueOf(ControlPointsModel.this.scoreRed)));
                }
                if (this.point.getScore() > 100.0 || this.point.getScore() < -100.0) {
                    addedScore = 0.0;
                }
                if (addedScore == 0.0) {
                    if (this.sendedZeroSpeedScore) {
                        return;
                    }
                    this.sendedZeroSpeedScore = true;
                } else {
                    this.sendedZeroSpeedScore = false;
                }
                this.point.setScore(this.point.getScore() + addedScore);
                ControlPointsModel.this.bfModel.send2Battle(new Command(Commands.SetPointScore,
                        String.valueOf(this.point.getId()), String.valueOf((int) this.point.getScore()),
                        String.valueOf(addedScore)));
            }
        }
    }

	public String getName() {
		return ControlPointsModel.class.getName();
	}
}