import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class AesEncryptor {
    private static final char[] keyPadding = new char[]
            {'1', 'a', '3', 'b', '5', 'c', '7', 'd', '9', 'e', '0', 'f', '2', 'g', '4' };

    private static String CryptoProcessor(boolean isEncryption, String data, String password)
        throws Exception {

        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
                new CBCBlockCipher(new AESEngine()), new PKCS7Padding());

        int iKeyPadding = 0, newPassLength;
        if (password.length() > 32 || password.length() == 0)
            throw new Exception("The password contains 0 or more than 32 characters.");
        if (password.length() <= 16) newPassLength = 16;
        else if (password.length() <= 24) newPassLength = 24;
        else newPassLength = 32;
        StringBuilder passwordBuilder = new StringBuilder(password);
        while (passwordBuilder.length() < newPassLength) passwordBuilder.append(keyPadding[iKeyPadding++]);
        password = passwordBuilder.toString();
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        byte[] msg, iv = new byte[16];
        if (isEncryption){
            if (data.length() == 0)
                throw new Exception("The data contains nothing.");
            msg = data.getBytes(StandardCharsets.UTF_8);

            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
        }
        else{
            if (data.length() <= 16)
                throw new Exception("The data contains nothing.");
            byte[] dataBytes = Base64.decode(data);
            msg = new byte[dataBytes.length - 16];

            System.arraycopy(dataBytes, 0, msg, 0, dataBytes.length - 16);
            System.arraycopy(dataBytes, dataBytes.length - 16, iv, 0, 16);
            Arrays.fill(dataBytes, (byte)0);
        }

        cipher.init(isEncryption, new ParametersWithIV(new KeyParameter(passwordBytes), iv));
        byte[] result = new byte[cipher.getOutputSize(msg.length)];
        //byte[] result = new byte[1024];
        int outOff = cipher.processBytes(msg,0,msg.length, result, 0);
        String strResult;
        try {
            cipher.doFinal(result, outOff);

            if (isEncryption) strResult = Base64.toBase64String(Arrays.concatenate(result, iv));
            else strResult = new String(result, StandardCharsets.UTF_8).trim();
        } catch (IllegalStateException | DataLengthException | InvalidCipherTextException e) {
            strResult = "/error";
        }

        Arrays.fill(passwordBytes, (byte)0);
        Arrays.fill(msg, (byte)0);
        Arrays.fill(iv, (byte)0);
        Arrays.fill(result, (byte)0);

        return strResult;
    }

    public static String Encryption(String data, String password) throws Exception {
        return CryptoProcessor(true, data, password);
    }

    public static String Decryption(String cryptMsg, String password) throws Exception {
        return CryptoProcessor(false, cryptMsg, password);
    }
}
