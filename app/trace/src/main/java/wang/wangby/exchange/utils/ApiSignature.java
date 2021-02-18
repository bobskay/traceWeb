package wang.wangby.exchange.utils;



import org.apache.commons.codec.binary.Hex;
import wang.wangby.exception.Message;
import wang.wangby.exchange.Constants;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ApiSignature {
    private static final String signatureMethodValue = "HmacSHA256";

    private  String secretKey;
    public ApiSignature(String secretKey){
        this.secretKey=secretKey;
    }

    public void createSignature(UrlParamsBuilder builder) {
        builder.putToUrl("recvWindow", Long.toString(Constants.DEFAULT_RECEIVING_WINDOW))
                .putToUrl("timestamp", Long.toString(System.currentTimeMillis()));

        Mac hmacSha256;
        try {
            hmacSha256 = Mac.getInstance(signatureMethodValue);
            SecretKeySpec secKey = new SecretKeySpec(secretKey.getBytes(), signatureMethodValue);
            hmacSha256.init(secKey);
        } catch (Exception e) {
            throw new Message("签名出错",e);
        }
        String payload = builder.buildSignature();
        String actualSign = new String(Hex.encodeHex(hmacSha256.doFinal(payload.getBytes())));

        builder.putToUrl("signature", actualSign);

    }

}
