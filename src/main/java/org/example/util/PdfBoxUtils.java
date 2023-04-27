package org.example.util;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * pdfbox工具类
 */
public class PdfBoxUtils {
    /**
     * 合并pdf文件
     *
     * @param files               要合并的pdf文件列表
     * @param destinationFileName 目标文件名
     */
    public static void mergePdfFiles(String[] files, String destinationFileName) {
        List<InputStream> inputStreams = new ArrayList<>();
        try {
            for (String filePath : files) {
                inputStreams.add(Files.newInputStream(new File(filePath).toPath()));
            }
            PDFMergerUtility mergePdf = new PDFMergerUtility();
            mergePdf.addSources(inputStreams);
            mergePdf.setDestinationFileName(destinationFileName);
            mergePdf.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
        } catch (Exception e) {
            throw new RuntimeException("合并pdf失败", e);
        } finally {
            for (InputStream in : inputStreams) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }

       /* for (String filePath : files) {
            File file = new File(filePath);
            file.deleteOnExit();
        }*/

    }

    /**
     * 合并图片到pdf中
     *
     * @param inputFilePath  输入文件路径
     * @param outputFilePath 输出文件路径
     * @param keyWords       关键字
     * @param imagePath      图片路径
     * @param imageScala     图片缩放比例
     */
    public static void mergeImageToPdf(String inputFilePath, String outputFilePath, String keyWords, String imagePath,
                                       float imageScala) {
        mergeImageToPdf(inputFilePath, outputFilePath, keyWords, imagePath, imageScala, 0, 0);
    }

    /**
     * 合并图片到pdf中
     *
     * @param inputFilePath  输入文件路径
     * @param outputFilePath 输出文件路径
     * @param keyWords       关键字
     * @param imagePath      图片路径
     * @param imageScala     图片缩放比例
     * @param offsetX        坐标x
     * @param offsetY        坐标y
     */

    public static void mergeImageToPdf(String inputFilePath, String outputFilePath, String keyWords, String imagePath,
                                       float imageScala, float offsetX, float offsetY) {
        try {
            PDDocument doc = PDDocument.load(new File(inputFilePath));
            //如图片在jar包中，这里可能无法用文件去读取。请调整使用 PDImageXObject.createFromByteArray 方式去读取图片
            PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);
            PdfBoxKeyWordPosition pdf = new PdfBoxKeyWordPosition(keyWords, inputFilePath);
            PDPageContentStream contentStream;
            List<float[]> list = pdf.getCoordinate();
            // 多页pdf的处理
            for (float[] fs : list) {
                PDPage page = doc.getPage((int) fs[2] - 1);
                float x = fs[0] + offsetX;
                float y = fs[1] + offsetY;
                contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, false, true);
                contentStream.drawImage(pdImage, x, y, pdImage.getHeight() * imageScala, pdImage.getWidth() * imageScala);
                contentStream.close();
            }
            doc.save(outputFilePath);
            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("合成图片失败", e);
        }
    }


    /**
     * 合成文字到图片中
     *
     * @param inputFilePath  输入文件路径
     * @param outputFilePath 输出文件路径
     * @param keyWords       关键字
     * @param text           文字
     * @param fontSize       字体大小
     */
    public static void mergeWordToPdf(String inputFilePath, String outputFilePath, String keyWords, String text,
                                      float fontSize) {
        mergeWordToPdf(inputFilePath, outputFilePath, keyWords, text, fontSize, 0, 0);
    }

    /**
     * 合成文字到图片中
     *
     * @param inputFilePath  输入文件路径
     * @param outputFilePath 输出文件路径
     * @param keyWords       关键字
     * @param text           文字
     * @param fontSize       字体大小
     * @param offsetX        文字x轴偏移量
     * @param offsetY        文字y轴偏移量
     */
    public static void mergeWordToPdf(String inputFilePath, String outputFilePath, String keyWords, String text,
                                      float fontSize, float offsetX, float offsetY) {
        try {
            PDDocument doc = PDDocument.load(new File(inputFilePath));
            PdfBoxKeyWordPosition pdf = new PdfBoxKeyWordPosition(keyWords, inputFilePath);
            PDPageContentStream contentStream;
            List<float[]> list = pdf.getCoordinate();
            PDType0Font font = PDType0Font.load(doc, Thread.currentThread().getContextClassLoader().getResourceAsStream("fonts/stsong.ttf"));
            // 多页pdf的处理
            for (float[] fs : list) {
                PDPage page = doc.getPage((int) fs[2] - 1);
                float x = fs[0];
                float y = fs[1];
                contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, false, true);
                //设置字体颜色为黑色
                contentStream.setNonStrokingColor(Color.BLACK);
                //Begin the Content stream
                contentStream.beginText();
                //Setting the font to the Content stream

                contentStream.setFont(font, fontSize);

                //Setting the position for the line
                contentStream.newLineAtOffset(x + offsetX, y + offsetY);
                //Adding text in the form of string
                contentStream.showText(text);
                //Ending the content stream
                contentStream.endText();
                //Closing the content stream
                contentStream.close();
            }
            //Saving the document
            doc.save(outputFilePath);
            //Closing the document
            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("合成文字失败", e);
        }

    }
}
