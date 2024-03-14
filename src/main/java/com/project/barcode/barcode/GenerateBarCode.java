package com.project.barcode.barcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.project.barcode.BarcodeApplication;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/barcode")
public class GenerateBarCode {

    String key = "8tDBuTmwDkuOwHPyjKMiIyrx_X3-Vcu6w0BZJnOYHxw=";


    @GetMapping("/generate")
    public void generateBarCode(@RequestParam("data") String barcodeData, HttpServletResponse response) throws Exception {
        log.info("########### Barcode generation started ##################");

        byte[] iv = new byte[16];
        byte[] keyBytes = Base64.getUrlDecoder().decode(BarcodeApplication.BARCODE_KEY);
        String encryptedMessage = EncryptDecrypt.encrypt(barcodeData,keyBytes,iv);

        BufferedImage barcodeImage = generateBarcode(encryptedMessage);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(barcodeImage, "png", baos);
        byte[] imageData = baos.toByteArray();

        response.setContentType("image/png");
        response.setContentLength(imageData.length);
        response.setHeader("Content-Disposition", "attachment; filename=Barcode.png");
        response.getOutputStream().write(imageData);

        log.info("########### Barcode generation completed ##################");
    }

    private BufferedImage generateBarcode(String barcodeData) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(barcodeData, BarcodeFormat.CODE_128, 400, 200, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
