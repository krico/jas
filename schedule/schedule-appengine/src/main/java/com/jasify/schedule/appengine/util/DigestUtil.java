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
 * Created by krico on 09/11/14.
 */
public final class DigestUtil {
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final Logger log = LoggerFactory.getLogger(DigestUtil.class);
    private static final int SALT_LENGTH = 8;
    /* The bigger this is, the longer it takes for it to be hacked (and checked) */
    private static final int ITERATIONS = 16192;
    private static final int KEY_LENGTH = 256;
    private static final ByteOrder ORDER = ByteOrder.LITTLE_ENDIAN;
    private static final ThreadLocal<ByteBuffer> WRITER = new ThreadLocal<ByteBuffer>() {
        @Override
        protected ByteBuffer initialValue() {
            return ByteBuffer.allocate(2048).order(ORDER);
        }
    };

    private DigestUtil() {
    }

    public static byte[] encrypt(String data) {
        try {
            ByteBuffer buffer = WRITER.get();
            buffer.clear();

            byte[] salt = new byte[SALT_LENGTH];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
            buffer.put(salt);
            buffer.putInt(ITERATIONS);

            PBEKeySpec spec = new PBEKeySpec(data.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            buffer.put(skf.generateSecret(spec).getEncoded());
            buffer.flip();
            byte[] ret = new byte[buffer.remaining()];
            buffer.get(ret);
            return ret;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("This should not happen!  SHA-384 not found?", e);
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
