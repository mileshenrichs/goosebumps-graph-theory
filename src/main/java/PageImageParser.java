import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import util.PathUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PageImageParser implements Serializable {

    public static void main(String[] args) throws Exception {
        PageImageParser parser = new PageImageParser();
        parser.parseImages();
    }

    private void parseImages() throws Exception {
//        PageContent firstPage = new PageContent(1, false, "Go on to PAGE 2.");
//        FileOutputStream file = new FileOutputStream(PathUtil.getContentsFileForPage(1));
//        ObjectOutputStream out = new ObjectOutputStream(file);
//        out.writeObject(firstPage);

        String pageImagePath = PathUtil.getImageFileForPage(1);
        detectDocumentText(pageImagePath, System.out);
    }

    /**
     * Performs document text detection on a local image file.
     *
     * @param filePath The path to the local file to detect document text on.
     * @param out A {@link PrintStream} to write the results to.
     * @throws Exception on errors while closing the client.
     * @throws IOException on Input/Output errors.
     */
    public static void detectDocumentText(String filePath, PrintStream out) throws Exception {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest
                .newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            AnnotateImageResponse res = response.getResponsesList().get(0);
            client.close();

            if (res.hasError()) {
                out.printf("Error: %s\n", res.getError().getMessage());
                return;
            }

            // For full list of available annotations, see http://g.co/cloud/vision/docs
            TextAnnotation annotation = res.getFullTextAnnotation();
            List<Block> pageBlocks = annotation.getPagesList().get(0).getBlocksList();
            String pageStoryContent = getFullBlockText(pageBlocks.get(0));
            String pageOptions = getFullBlockText(pageBlocks.get(1));

            out.println(pageStoryContent);
            out.println();
            out.println(pageOptions);
            out.println();
            out.println(annotation.getText());
        }
    }

    private static String getFullBlockText(Block block) {
        StringBuilder blockText = new StringBuilder();
        for (Paragraph para : block.getParagraphsList()) {
            StringBuilder paragraphText = new StringBuilder();
            for(Word word : para.getWordsList()) {
                StringBuilder wordTextBuilder = new StringBuilder();
                for(Symbol s : word.getSymbolsList()) {
                    wordTextBuilder.append(s.getText());
                }
                String wordText = wordTextBuilder.toString();

                // decide whether to add space before word or not (some "words" are just punctuation)
                if(wordText.length() > 1 || (wordText.length() == 1 && isAlphanumeric(wordText.charAt(0)))) {
                    paragraphText.append(" ");
                }
                paragraphText.append(wordText);
            }

            blockText.append(paragraphText);
        }

        return blockText.toString();
    }

    private static boolean isAlphanumeric(char c) {
        return Character.isLetter(c) || Character.isDigit(c);
    }

    static List<PageContent> getAllPageContents() {
        List<PageContent> pages = new ArrayList<>();
        try {
            File pageContentsDir = new File(PathUtil.getPageContentsDir());
            File[] contentsDirectoryList = pageContentsDir.listFiles();
            if (contentsDirectoryList != null) {
                for(File pageContent : contentsDirectoryList) {
                    FileInputStream file = new FileInputStream(pageContent);
                    ObjectInputStream in = new ObjectInputStream(file);
                    pages.add((PageContent) in.readObject());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return pages;
    }

    class PageContent implements Serializable {
        private static final long serialVersionUID = 1L;

        int pageNo;
        boolean isEnding;
        String options;

        PageContent(int pageNo, boolean isEnding, String options) {
            this.pageNo = pageNo;
            this.isEnding = isEnding;
            this.options = options;
        }
    }

}
