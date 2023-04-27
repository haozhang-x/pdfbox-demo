package org.example.util;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;


class PdfBoxUtilsTest {

    private final String outputPath = "./pdfbox_output/";

    @Test
    void mergePdfFiles() {
        String input = FileUtil.file("classpath:设计模式之美总结.pdf").getAbsolutePath();
        String[] pdfs = {input, input};
        PdfBoxUtils.mergePdfFiles(pdfs, outputPath + "设计模式之美总结_merge.pdf");
    }

    @Test
    void mergeImageToPdf() {
        String input = FileUtil.file("classpath:设计模式之美总结.pdf").getAbsolutePath();
        String output = outputPath + "设计模式之美总结_image.pdf";
        PdfBoxUtils.mergeImageToPdf(input, output, "设计模式之美总结", FileUtil.file("classpath:images/img.png").getPath(), 0.3f);
    }

    @Test
    void mergeWordToPdf() {
        String input = FileUtil.file("classpath:设计模式之美总结.pdf").getAbsolutePath();
        String output = outputPath + "设计模式之美总结_word.pdf";
        PdfBoxUtils.mergeWordToPdf(input, output, "设计模式之美总结", "（2023年4月25日）", 40);
    }

    @Test
    void testMergeWordToPdf() {
        String input = FileUtil.file("classpath:设计模式之美总结.pdf").getAbsolutePath();
        String output = outputPath + "设计模式之美总结word_offset.pdf";
        PdfBoxUtils.mergeWordToPdf(input, output, "设计模式之美总结", "（2023年4月25日）", 30, 150, -30);
    }
}