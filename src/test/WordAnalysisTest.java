import com.walltech.b2b.entrance.FileAnalysisEntrance;
import com.walltech.b2b.enumeration.FileType;
import com.walltech.b2b.service.FileAnalysisFactory;
import com.walltech.b2b.service.GetFileContentFactory;
import com.walltech.b2b.service.GetWordContentImpl;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.model.ListTables;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Segment;
import javax.swing.text.rtf.RTFEditorKit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

/**
 * Created by zeddwang on 2017/6/27.
 */
public class WordAnalysisTest {
    private static final Logger logger = LoggerFactory.getLogger(FileAnalysisTest.class);
    private static final StringBuffer baseAddress = new StringBuffer("C:/Users/zeddwang/Desktop/new_demo/");
    public static void main(String[] args) {
        try {
            File file  = new File(baseAddress.append("apia托书 11月3日.rtf").toString());
            //FileInputStream is = new FileInputStream(new File(baseAddress.toString()));
            FileInputStream inputStream = new FileInputStream(file);
//            byte[] bytes = new byte[5];
//            is.read(bytes, 0, bytes.length);
//            StringBuffer header=new StringBuffer();
//            for (byte b : bytes) {
//                String hex=Integer.toHexString(b);
//                if(hex.length()<2){// 两位以下补〇
//                    header.append('0');
//                }
//                header.append(hex);
//            }
//            boolean isRTF="7b5c727466".contentEquals(header);
//            is.close();
//            if (!isRTF){
//                getAnalysis2(inputStream);
//            }else{
//                getAnalysis3(inputStream);
//            }
            /** 工厂方法 **/
            System.out.println(FileAnalysisEntrance.beginParsing(inputStream, FileType.RTF));
            //POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
            //HWPFDocument document = new HWPFDocument(poifsFileSystem);
            /** 读文本 **/
//            String text = document.getDocumentText();
//            System.out.println(text);
//            StringBuilder doc2 = document.getText();
//            System.out.println(doc2);
            /** 读table文本 **/
//            TextPieceTable table = document.getTextTable();
//            System.out.println(table.getText().toString());
            /** 读表格 **/
//            Range rang = document.getRange();
//            TableIterator iterable = new TableIterator(rang);
//            while (iterable.hasNext()) {
//                Table tb =  iterable.next();
//                //迭代行，默认从0开始
//                for (int i = 0; i < tb.numRows(); i++) {
//                    TableRow tr = tb.getRow(i);
//                    //迭代列，默认从0开始
//                    for (int j = 0; j < tr.numCells(); j++) {
//                        TableCell td = tr.getCell(j);
//                        for(int k=0;k<td.numParagraphs();k++){
//                            Paragraph para =td.getParagraph(k);
//                            String s = para.text();
//                            System.out.println(s);
//                        }
//                    }
//                }
//            }
            /** 转html -> pdd => analysis **/
            //convert2Html(inputStream, "C:/Users/zeddwang/Desktop/FileAnalysiDemo/a.html");
            //document.close();
            //getAnalysis1(inputStream);
            //getAnalysis3(inputStream);
            inputStream.close();
        } catch (Exception ex){
            logger.error(ex.getMessage());
        }
    }
    private static String getAnalysis1(FileInputStream inputStream) throws IOException {
        HWPFDocument document = new HWPFDocument(inputStream);
        String text = document.getDocumentText();
        System.out.println(text);
        document.close();
        return text;
    }
    private static String getAnalysis2(FileInputStream inputStream) throws IOException{
        HWPFDocument document = new HWPFDocument(inputStream);
        Range range = document.getRange();
        TableIterator iterator = new TableIterator(range);
        while (iterator.hasNext()){
            Table tb = iterator.next();
            for (int i = 0; i < tb.numRows(); i++) {
                TableRow tr = tb.getRow(i);
                //迭代列，默认从0开始
                for (int j = 0; j < tr.numCells(); j++) {
                    TableCell td = tr.getCell(j);
                    for (int k = 0; k < td.numParagraphs(); k++) {
                        Paragraph para = td.getParagraph(k);
                        String s = para.text();
                        System.out.println(s);
                    }
                }
            }
        }
        document.close();
        return "";
    }

    private static String getAnalysis3(FileInputStream inputStream) throws IOException, BadLocationException {
        RTFEditorKit rtfEditorKit = new RTFEditorKit();
        DefaultStyledDocument document  = new DefaultStyledDocument();
        rtfEditorKit.read(inputStream, document, 0);
        String text = new String(document.getText(0,document.getLength()).getBytes("ISO-8859-1"),"GB2312");
        System.out.println(text);
        return "";
    }


    /** 转换测试 **/

    public static void convert2Html(FileInputStream inputStream, String outPutFile)
                 throws TransformerException, IOException, ParserConfigurationException {
        HWPFDocument wordDocument = new HWPFDocument(inputStream);
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .newDocument());
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            public String savePicture(byte[] content, PictureType pictureType,
                                      String suggestedName, float widthInches, float heightInches) {
                return suggestedName;
            }
        });
        wordToHtmlConverter.processDocument(wordDocument);
        List pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                try {
                    pic.writeImageContent(new FileOutputStream("C:/Users/zeddwang/Desktop/FileAnalysiDemo/"
                            + pic.suggestFullFileName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(outStream);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        outStream.close();
        writeFile(new String(outStream.toByteArray()), outPutFile);
    }

    /** 写文件 **/
    private static void writeFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos,"utf-8"));
            bw.write(content);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
            }
        }
    }
}
