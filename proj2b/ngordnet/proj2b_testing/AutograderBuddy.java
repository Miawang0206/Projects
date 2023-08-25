package ngordnet.proj2b_testing;

import ngordnet.main.HyponymsHandler;
import ngordnet.browser.HyponymsMap;
import ngordnet.browser.NgordnetQueryHandler;
import ngordnet.ngrams.NGramMap;


public class AutograderBuddy {
    /** Returns a HyponymHandler */
    public static NgordnetQueryHandler getHyponymHandler(
            String wordFile, String countFile,
            String synsetFile, String hyponymFile) {
        HyponymsMap hpm = new HyponymsMap(synsetFile, hyponymFile);
        NGramMap ngm = new NGramMap(wordFile, countFile);

        return new HyponymsHandler(hpm, ngm);
    }
}
