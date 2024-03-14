package com.project.barcode.barcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.project.barcode.BarcodeApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/barcode")
public class ExtractBarCode {

    @PostMapping("/extract")
    public ResponseEntity<?> extractBarCodeData(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("########### Barcode extraction started ##################");

        byte[] iv = new byte[16];
        byte[] keyBytes = Base64.getUrlDecoder().decode(BarcodeApplication.BARCODE_KEY);

        BufferedImage barcodeImage = ImageIO.read(file.getInputStream());
        String decodedData = extractBarcode(barcodeImage);
        String decryptData = EncryptDecrypt.decrypt(decodedData,keyBytes,iv);

        log.info("########### Barcode extraction completed ##################");
        return ResponseEntity.status(200).body(new BarcodeData(decryptData));
    }

    private String extractBarcode(BufferedImage barcodeImage) throws NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(barcodeImage)));
        MultiFormatReader reader = new MultiFormatReader();
        Result result = reader.decode(binaryBitmap);
        return result.getText();
    }
}
