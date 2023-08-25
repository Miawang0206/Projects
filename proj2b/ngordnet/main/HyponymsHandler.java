package ngordnet.main;
import ngordnet.browser.HyponymsMap;
import ngordnet.browser.NgordnetQuery;
import ngordnet.browser.NgordnetQueryHandler;
import ngordnet.ngrams.NGramMap;

import java.util.List;

public class HyponymsHandler extends NgordnetQueryHandler {
    private HyponymsMap hpm;
    private NGramMap ngm;
    public HyponymsHandler(HyponymsMap map, NGramMap ngram) {
        hpm = map;
        ngm = ngram;
    }
    @Override
    public String handle(NgordnetQuery q) {
        List<String> word = q.words();
        Integer startYear = q.startYear();
        Integer endYear = q.endYear();
        Integer k = q.k();

        String response = "";
        response += hpm.getHyponyms(word, k, startYear, endYear, ngm).toString();


        return response;
    }
}

