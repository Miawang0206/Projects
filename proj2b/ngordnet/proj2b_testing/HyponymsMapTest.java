package ngordnet.proj2b_testing;

import ngordnet.browser.HyponymsMap;
import ngordnet.ngrams.NGramMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class HyponymsMapTest {
    @Test
    public void testHyponymsMap() {
        String sf = "./data/wordnet/synsets11.txt";
        String hf = "./data/wordnet/hyponyms11.txt";

        HyponymsMap map = new HyponymsMap(sf, hf);
        List<String> expected = List.of("augmentation","increase","jump","leap");
        assertThat(map.getHyponyms("increase")).isEqualTo(expected);

        List<String> expected1 = List.of("descent", "jump", "parachuting");
        assertThat(map.getHyponyms("descent")).isEqualTo(expected1);

        String sf2 = "./data/wordnet/synsets16.txt";
        String hf2 = "./data/wordnet/hyponyms16.txt";

        HyponymsMap map2 = new HyponymsMap(sf2, hf2);
        List<String> expected2 = List.of("alteration", "change", "demotion", "increase", "jump"
        , "leap", "modification", "saltation", "transition", "variation");
        assertThat(map2.getHyponyms("change")).isEqualTo(expected2);
        assertThat(map2.getHyponyms("act")).containsExactly("act", "action", "change", "demotion",
                "human_action", "human_activity", "variation");
        assertThat(map2.getHyponyms("transition")).containsExactly("flashback", "jump", "leap",
                "saltation", "transition");
    }
    @Test
    public void testHyponymsOfWordList() {
        String sf = "./data/wordnet/synsets16.txt";
        String hf = "./data/wordnet/hyponyms16.txt";

        HyponymsMap map = new HyponymsMap(sf, hf);
        assertThat(map.getHyponyms(List.of("occurrence", "change"))).containsExactly("alteration",
                "change", "increase", "jump", "leap", "modification", "saltation", "transition");
        assertThat(map.getHyponyms(List.of("action", "flashback"))).isEmpty();
        assertThat(map.getHyponyms(List.of("occurrence", "change", "transition"))).containsExactly("jump",
                "leap", "saltation", "transition");

        String sf1 = "./data/wordnet/synsets.txt";
        String hf1 = "./data/wordnet/hyponyms.txt";

        HyponymsMap map2 = new HyponymsMap(sf1, hf1);
        assertThat(map2.getHyponyms(List.of("video", "recording"))).containsExactly("video",
                "video_recording", "videocassette", "videotape");
        assertThat(map2.getHyponyms(List.of("pastry", "tart"))).containsExactly("apple_tart",
                "lobster_tart", "quiche", "quiche_Lorraine", "tart", "tartlet");
        assertThat(map2.getHyponyms(List.of("genus", "pondweed"))).containsExactly("Elodea", "ditchmoss",
                "genus_Elodea", "pondweed");
        System.out.println(map2.getHyponyms(List.of("pad", "movement", "set", "press", "lead", "effect", "shape",
                "center", "right")));
    }

    @Test
    public void testHyponymsWithK() {
        String sf = "./data/wordnet/synsets.txt";
        String hf = "./data/wordnet/hyponyms.txt";
        String wordFile = "./data/ngrams/top_14377_words.csv";
        String countFile = "./data/ngrams/total_counts.csv";
        NGramMap ngm = new NGramMap(wordFile, countFile);

        HyponymsMap map = new HyponymsMap(sf, hf);
        assertThat(map.getHyponyms(List.of("dog", "animal"), 5, 2000, 2020, ngm)).containsExactly(
                "dog", "pointer", "puppy", "toy");
        assertThat(map.getHyponyms(List.of("cake"), 5, 2000, 2020, ngm)).containsExactly(
                "bar", "cake", "kiss", "snap", "tablet");
        assertThat(map.getHyponyms(List.of("cake", "biscuit", "kiss"), 5, 2000, 2020, ngm))
                .containsExactly("kiss");
        assertThat(map.getHyponyms(List.of("event", "change"), 10, 1900, 2020, ngm))
                .containsExactly("change", "development", "following", "get", "going", "left", "right",
                        "section", "service", "way");
    }







}
