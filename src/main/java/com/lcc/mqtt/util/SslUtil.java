package com.lcc.mqtt.util;

import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-06 16:15
 */
public class SslUtil {

    public static SSLSocketFactory getSSLSocktet(String caPath) throws Exception {
        // CA certificate is used to authenticate server
        CertificateFactory cAf = CertificateFactory.getInstance("X.509");
        FileInputStream caIn = new FileInputStream(caPath);
        X509Certificate ca = (X509Certificate) cAf.generateCertificate(caIn);
        KeyStore caKs = KeyStore.getInstance("JKS");
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", ca);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        tmf.init(caKs);

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1");
        context.init(null, tmf.getTrustManagers(), new SecureRandom());

        return context.getSocketFactory();
    }

    public static SSLContext buildSSLContext(InputStream caIn, InputStream crtIn, InputStream key, String password) throws Exception {
        CertificateFactory cAf = CertificateFactory.getInstance("X.509");
        X509Certificate ca = (X509Certificate)cAf.generateCertificate(caIn);
        KeyStore caKs = KeyStore.getInstance("JKS");
        caKs.load((InputStream)null, (char[])null);
        caKs.setCertificateEntry("JKS", ca);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        tmf.init(caKs);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate caCert = (X509Certificate)cf.generateCertificate(crtIn);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load((InputStream)null, (char[])null);
        ks.setCertificateEntry("certificate", caCert);
        ks.setKeyEntry("private-key", buildPrivateKey(key), password.toCharArray(), new Certificate[]{caCert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
        kmf.init(ks, password.toCharArray());
        SSLContext context = SSLContext.getInstance("TLSv1");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        return context;
    }

    private static PrivateKey getPrivateKey(String path) throws Exception {
        Base64 base64 = new Base64();
        byte[] buffer = base64.decode(getPem(path));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
    }

    private static String getPem(String path) throws Exception {
        FileInputStream fin = new FileInputStream(path);
        return buidPem(fin);
    }

    private static PrivateKey buildPrivateKey(InputStream path) throws Exception {
        Base64 base64 = new Base64();
        byte[] buffer = base64.decode(buidPem(path));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
    }
    private static String buidPem(InputStream in) throws Exception {
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        try {
            String readLine = null;

            while((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) != '-') {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
        } finally {
            br.close();
            reader.close();
        }

        return sb.toString();
    }
}
