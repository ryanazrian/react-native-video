package com.brentvatne.exoplayer;
// package com.pahamify.android.brentvatne.exoplayer;

import com.google.android.exoplayer2.drm.MediaDrmCallback;
import com.google.android.exoplayer2.drm.ExoMediaDrm;

import java.util.UUID;
import java.io.IOException;
import java.lang.Exception;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONArray;
// import android.util.Base64;
import java.util.Base64;

public class SelfMediaDrmCallback implements MediaDrmCallback {

    // private final byte[] keyResponse = "...";

    @Override
    public byte[] executeProvisionRequest(UUID uuid, ExoMediaDrm.ProvisionRequest request) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] executeKeyRequest(UUID uuid, ExoMediaDrm.KeyRequest request) throws Exception {
        String key2;

        JSONObject requestBody = new JSONObject(new String(request.getData()));
        JSONArray temp = requestBody.getJSONArray("kids");
        String kid = temp.getString(0);

        // compute key
        // convert into base64 decimal (
        // https://www.garykessler.net/library/base64.html) , plus with index except
        // last item or array KID
        StringBuilder key = new StringBuilder();
        char[] kidChars = kid.toCharArray();
        char[] dictionary = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', '-', '/' };

        for (int i = 0; i < kidChars.length - 1; i++) {
            int index = -1;

            // standarize characters into base64 alphabet :
            // https://www.garykessler.net/library/base64.html)
            if (kidChars[i] == '+')
                kidChars[i] = '-';
            if (kidChars[i] == '_')
                kidChars[i] = '/';

            for (int j = 0; j < dictionary.length; j++)
                if (dictionary[j] == kidChars[i])
                    index = j;

            if ((index + i) <= 63) {
                index += i;
            } else {
                index += i;
                index = 63 - (index - 63);
            }
            key.append(dictionary[index]);
        }
        key.append(kidChars[kidChars.length - 1]);
        // compute key end

        // byte[] inp = Base64.decode(kid, Base64.DEFAULT);
        // Log.d("PFY", bytesToHex(inp));

        key2 = "{\"keys\":[{\"kty\":\"oct\",\"k\":\"" + key + "\",\"kid\":\"" + kid + "\"}],\"type\":\"temporary\"}";
        // Log.d("PFY", "computed key : " + key2);

        // key2 =
        // "{\"keys\":[{\"kty\":\"oct\",\"k\":\"H0mDuqH8Pd1989/7pbjwYw\",\"kid\":\"HzkAqlB7HU/yw0x0ZKR7Ew\"}],\"type\":\"temporary\"}";
        // Log.d("PFY", "harcoded key : " + key2);
        return key2.getBytes();
    }

    private final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
