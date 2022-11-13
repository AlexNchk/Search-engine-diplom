import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> dataBase = new HashMap();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        for (File pdf : Objects.requireNonNull(pdfsDir.listFiles(), "Неверный путь к файлам")) {
            PdfDocument doc = new PdfDocument(new PdfReader(pdf));
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                int currentPage = i + 1;
                String text = PdfTextExtractor.getTextFromPage(doc.getPage(currentPage));
                String[] words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word.toLowerCase(), freqs.getOrDefault(word, 0) + 1);
                }
                for (var entry : freqs.entrySet()) {
                    List<PageEntry> wordSearchingResult;
                    if (dataBase.containsKey(entry.getKey())) {
                        wordSearchingResult = dataBase.get(entry.getKey());
                    } else {
                        wordSearchingResult = new ArrayList<>();
                    }
                    wordSearchingResult.add(new PageEntry(pdf.getName(), currentPage, entry.getValue()));
                    Collections.sort(wordSearchingResult, Collections.reverseOrder());
                    dataBase.put(entry.getKey(), wordSearchingResult);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return dataBase.get(word.toLowerCase());
    }
}
