package rt.server.services.protocol;

import java.util.Arrays;
import rt.server.ServerProperties;
import rt.server.logger.Logger; // Предполагаемый импорт для логирования

public class ProtocolUtils {

    public static String decrypt(String data, int k) {
        if (data == null || data.length() < 2) {
            Logger.log(Logger.ERROR, "ProtocolUtils.decrypt - Invalid input data: " + data);
            return null;
        }

        try {
            int key = Integer.parseInt(data.substring(0, 1));
            int delimiterIndex = data.indexOf(ServerProperties.DELIM_COMMANDS_SYMBOL);
            String data_string = (delimiterIndex > 1) ? data.substring(1, delimiterIndex) : "";

            if (!data_string.isEmpty()) {
                char[] w = data_string.toCharArray();
                for (int i = 0; i < w.length; i++) {
                    w[i] -= key + k;
                }
                String decrypted = new String(w).trim();
                //Logger.log(Logger.INFO, "ProtocolUtils.decrypt - Decrypted data: " + decrypted);
                return decrypted;
            }
            return "";
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            Logger.log(Logger.ERROR, "ProtocolUtils.decrypt - Error decrypting data: " + data + ", Exception: " + e.getMessage());
            return null;
        }
    }

    public static String crypt(String data, int k) {
        if (data == null || data.length() < 2) {
            Logger.log(Logger.ERROR, "ProtocolUtils.crypt - Invalid input data: " + data);
            return null;
        }

        try {
            int key = Integer.parseInt(data.substring(0, 1));
            String data_string = data.substring(1);
            if (data_string.length() != 0) {
                char[] w = data_string.toCharArray();
                for (int i = 0; i < w.length; i++) {
                    w[i] += key + k;
                }
                data_string = new String(w);
            }
            return data_string;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            Logger.log(Logger.ERROR, "ProtocolUtils.crypt - Error encrypting data: " + data + ", Exception: " + e.getMessage());
            return null;
        }
    }

    public static String[] getArgsFromPacket(String decrypt) {
        if (decrypt == null) {
            return new String[0];
        }

        String[] decrypted = decrypt.split(ServerProperties.DELIM_ARGUMENTS_SYMBOL);
        if (decrypted.length <= 2) {
            return new String[0];
        }

        String[] args = Arrays.copyOfRange(decrypted, 2, decrypted.length);
        return args;
    }

    public static String getNameFromPacket(String decrypted) {
        if (decrypted == null) {
            Logger.log(Logger.ERROR, "ProtocolUtils.getNameFromPacket - Decrypted data is null");
            return null;
        }

        String[] parts = decrypted.split(ServerProperties.DELIM_ARGUMENTS_SYMBOL);
        if (parts.length > 1) {
            return parts[1];
        }
        Logger.log(Logger.WARNING, "ProtocolUtils.getNameFromPacket - No name found in: " + decrypted);
        return null;
    }

    public static String getTypeFromPacket(String decrypted) {
        if (decrypted == null) {
            Logger.log(Logger.ERROR, "ProtocolUtils.getTypeFromPacket - Decrypted data is null");
            return null;
        }

        String[] parts = decrypted.split(";");
        if (parts.length > 0) {
            return parts[0];
        }
        Logger.log(Logger.WARNING, "ProtocolUtils.getTypeFromPacket - No type found in: " + decrypted);
        return null;
    }
}