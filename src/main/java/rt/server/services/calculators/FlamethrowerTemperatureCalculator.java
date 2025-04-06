package rt.server.services.calculators;

public class FlamethrowerTemperatureCalculator {
   public static double getTemperature(double currState) {
      double temperature = currState;
      if (temperature > 0.5D) {
         temperature = 0.5D;
      }

      if (temperature < 0.0D) {
         temperature = 0.0D;
      }

      return temperature;
   }
}
