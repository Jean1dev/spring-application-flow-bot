package com.flowbot.application.module.domain.usuario.service;

import com.flowbot.application.module.domain.usuario.ChavesUsuarioTypeBot;
import com.flowbot.application.module.domain.usuario.ChavesAcessoServidorUsuario;
import com.flowbot.application.module.domain.usuario.ChavesPublicasDoUsuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Abordagem com Chave Pública Única
 * <p>
 * Chave Pública Única para Todos os Usuários:
 * Vantagem: Mais simples de implementar e gerenciar.
 * Desvantagem: Se a chave privada do backend for comprometida, todas as comunicações serão vulneráveis.
 * <p>
 * Abordagem com Chaves Públicas Diferentes para Cada Usuário
 * <p>
 * Chaves Públicas Diferentes por Usuário:
 * Vantagem: Maior controle e segurança, pois a exposição de uma chave privada não compromete a comunicação de todos os usuários.
 * Desvantagem: Mais complexo de implementar e gerenciar, pois você precisa gerar, distribuir e armazenar chaves para cada usuário.
 * <p>
 * Implementação com Chaves Públicas Diferentes
 * Gerando Pares de Chaves
 * <p>
 * Geração de Pares de Chaves:
 * No backend, você pode gerar pares de chaves públicos e privados para cada usuário e armazená-los de forma segura.
 * <p>
 * Distribuindo Chaves Públicas
 * <p>
 * Distribuição de Chaves Públicas:
 * Quando um usuário faz login ou se registra, envie a chave pública correspondente para o frontend.
 */

@Service
public class SegurancaUsuarioService {

    private static final Logger log = LoggerFactory.getLogger(SegurancaUsuarioService.class);

    private static final int KEY_SIZE = 2048;
    private static final String ALGORITHM = "RSA";
    private static final String COLLECTION_NAME_API_KEYS = "usuario_api_keys";
    private static final String COLLECTION_NAME_SERVER_SECURITY_KEYS = "usuario_server_security_keys";

    private final MongoTemplate mongoTemplate;
    private final String serverPrivateKey;

    public SegurancaUsuarioService(
            MongoTemplate mongoTemplate,
            @Value("${config.private-key}") String serverPrivateKey) {
        this.mongoTemplate = mongoTemplate;
        this.serverPrivateKey = serverPrivateKey;
    }

    public ChavesPublicasDoUsuario getKeys() {
        try {
            var keyPair = generateKeyPair(serverPrivateKey);
            var base64PublicKey = getBase64PublicKey(keyPair);
            var base64PrivateKey = getBase64PrivateKey(keyPair);
            var publicKey = String.format("-----BEGIN PUBLIC KEY-----\n %s \n-----END PUBLIC KEY-----", base64PublicKey);

            mongoTemplate.save(new ChavesAcessoServidorUsuario(publicKey, base64PrivateKey), COLLECTION_NAME_SERVER_SECURITY_KEYS);

            return new ChavesPublicasDoUsuario(publicKey);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void saveApiKeys(ChavesUsuarioTypeBot chavesUsuarioTypeBot) {
        var usuarioKeys = mongoTemplate.findAll(ChavesAcessoServidorUsuario.class, COLLECTION_NAME_SERVER_SECURITY_KEYS).getLast();
        var cleanedKey = usuarioKeys.publicKey().replaceAll("-----BEGIN PUBLIC KEY-----|-----END PUBLIC KEY-----|\\s", "");

        try {
            var encryptedToken = encryptRSA(chavesUsuarioTypeBot.typebot_token(), cleanedKey);
            mongoTemplate.save(new ChavesUsuarioTypeBot(encryptedToken, chavesUsuarioTypeBot.typebot_workspaceId()), COLLECTION_NAME_API_KEYS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    public ChavesUsuarioTypeBot getApisKeys() {
        var keysEntity = mongoTemplate.findAll(ChavesUsuarioTypeBot.class, COLLECTION_NAME_API_KEYS).getLast();
        var usuarioKeys = mongoTemplate.findAll(ChavesAcessoServidorUsuario.class, COLLECTION_NAME_SERVER_SECURITY_KEYS).getLast();

        try {
            var privateKeyBase64 = getPrivateKey(usuarioKeys.privateKey());
            var decriptedToken = decrypt(keysEntity.typebot_token(), privateKeyBase64);

            return new ChavesUsuarioTypeBot(decriptedToken, keysEntity.typebot_workspaceId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedValue = cipher.doFinal(decodedValue);
        return new String(decryptedValue);
    }

    private PublicKey getPublicKeyFromBase64(String base64PublicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    private PrivateKey getPrivateKey(String base64PrivateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    private KeyPair generateKeyPair(String seed) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom = new SecureRandom(seed.getBytes());
        keyGen.initialize(KEY_SIZE, secureRandom);
        return keyGen.generateKeyPair();
    }

    private String getBase64PrivateKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    private String getBase64PublicKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    private String encryptRSA(String message, String base64PublicKey) throws Exception {
        PublicKey publicKey = getPublicKeyFromBase64(base64PublicKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
