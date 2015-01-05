package com.jasify.schedule.appengine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

/**
 * @author krico
 * @since 09/11/14.
 */
public final class DigestUtil {
    private static final Logger log = LoggerFactory.getLogger(DigestUtil.class);
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int SALT_LENGTH = 8;
    private static final int KEY_LENGTH = 256;
    private static final ByteOrder ORDER = ByteOrder.LITTLE_ENDIAN;
    private static final ThreadLocal<ByteBuffer> WRITER = new ThreadLocal<ByteBuffer>() {
        @Override
        protected ByteBuffer initialValue() {
            return ByteBuffer.allocate(2048).order(ORDER);
        }
    };
    public static final int DEFAULT_ITERATIONS = 16192;
    /* The bigger this is, the longer it takes for it to be hacked (and checked) */
    private static int iterations = DEFAULT_ITERATIONS;

    private DigestUtil() {
    }

    /**
     * Change the number of iterations on password encryption
     *
     * @param iterations the number
     */
    public static void setIterations(int iterations) {
        DigestUtil.iterations = iterations;
    }

    public static byte[] encrypt(String data) {
        try {
            ByteBuffer buffer = WRITER.get();
            buffer.clear();

            byte[] salt = new byte[SALT_LENGTH];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
            buffer.put(salt);
            buffer.putInt(iterations);

            PBEKeySpec spec = new PBEKeySpec(data.toCharArray(), salt, iterations, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            buffer.put(skf.generateSecret(spec).getEncoded());
            buffer.flip();
            byte[] ret = new byte[buffer.remaining()];
            buffer.get(ret);
            return ret;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("This should not happen!  " + SECRET_KEY_ALGORITHM + " not found?", e);
        }
    }

    public static boolean verify(byte[] encrypted, String checkData) {
        try {
            ByteBuffer buffer = WRITER.get();
            buffer.clear();
            buffer.put(encrypted);
            buffer.flip();
            byte[] salt = new byte[SALT_LENGTH];
            buffer.get(salt);
            int iterations = buffer.getInt();
            byte[] toVerify = new byte[buffer.remaining()];
            buffer.get(toVerify);

            PBEKeySpec spec = new PBEKeySpec(checkData.toCharArray(), salt, iterations, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            byte[] check = skf.generateSecret(spec).getEncoded();
            return Objects.deepEquals(toVerify, check);
        } catch (Exception e) {
            log.info("Failed to verify pw", e);
            return false;
        }
    }
}
