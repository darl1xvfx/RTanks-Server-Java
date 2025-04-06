package rt.server.services.calculators;

import rt.server.battles.tank.Tank;

public class FreezeTemperatureCalculator {
    /*public static double getTemperature(Tank currState, float speed, float turnSpeed, float turretRotationSpeed) {
        double temperature_speed = (double)((speed - currState.speed) * (speed / 30.0F));
        double temperature_turn = (double)((turnSpeed - currState.turnSpeed) * (turnSpeed / 10.0F));
        double temperature_turret = (double)((turretRotationSpeed - currState.turretRotationSpeed) * (turretRotationSpeed / 10.0F));
        double temperature = -(temperature_speed + temperature_turn + temperature_turret);
        if (temperature < -2.0) {
            temperature = -2.0;
        }

        if (temperature > 0.0) {
            temperature = 0.0;
        }

        return temperature;
    }*/
}
