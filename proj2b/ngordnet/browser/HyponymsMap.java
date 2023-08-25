package ngordnet.browser;

import edu.princeton.cs.algs4.In;
import ngordnet.ngrams.NGramMap;
import ngordnet.ngrams.TimeSeries;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An object that maps each unique word in the synsets file to their hyponyms.
 * The indices of hyponyms are stored in a TreeSet by alphabetical order,
 * which could be retrieved with in order traversal to return a list of
 * orderly hyponyms associated with one word.
 *
 * @author Mia Wang
 */
public class HyponymsMap {
    private HashMap<Integer, TreeSet<Integer>> IntMap;
    private HashMap<Integer, String> wordDict;
    private static final int INITIAL_CAPACITY = 11;

    /**
     * Construct a HyponymsMap from synsetFile and hyponymFile.
     */
    public HyponymsMap(String synsetFile, String hyponymFile) {
        IntMap = new HashMap<>();
        wordDict = new HashMap<>();

        In synsetReader = new In(synsetFile);
        In hyponymReader = new In(hyponymFile);

        while (synsetReader.hasNextLine()) {
            String[] r = synsetReader.readLine().split(",");
            Integer index = Integer.valueOf(r[0]);
            String word = r[1];
            wordDict.put(index, word);
        }

        while (hyponymReader.hasNextLine()) {
            TreeSet<Integer> hyponymTree = new TreeSet<>();
            String[] l = hyponymReader.readLine().split(",", 2);
            Integer key = Integer.valueOf(l[0]);
            String[] values = l[1].split(",");

            if (!IntMap.containsKey(key)) {
                IntMap.put(key, hyponymTree);
            }

            IntMap.get(key).add(key);

            for (String v : values) {
                Integer childIndex = Integer.valueOf(v);
                IntMap.get(key).add(childIndex);
            }
        }
    }

    /**
     * Return an ordered list of hyponyms of the input word.
     */
    public ArrayList getHyponyms(String word) {
        return toList(getHyponymsSet(word));
    }

    /**
     * Return a unique set of hyponyms of the input word.
     */
    private TreeSet getHyponymsSet(String word) {
        TreeSet<Integer> intSet = new TreeSet<>();
        ArrayList<Integer> indexList = getIndex(word);
        TreeSet<String> hyponymsList = new TreeSet<>();

        for (int i : indexList) {
            intSet = getHyponymsHelper(i, intSet);
        }

        if (!intSet.isEmpty()) {
            for (int j : intSet) {
                String s = wordDict.get(j);
                if (!s.contains(" ")) {
                    hyponymsList.add(s);
                } else {
                    String[] sSplit = s.split(" ");
                    for (String w : sSplit) {
                        hyponymsList.add(w);
                    }
                }
            }
        } else {
            for (int i : indexList) {
                String s = wordDict.get(i);
                if (!s.contains(" ")) {
                    hyponymsList.add(s);
                } else {
                    String[] sSplit = s.split(" ");
                    for (String w : sSplit) {
                        hyponymsList.add(w);
                    }
                }
            }
        }
        return hyponymsList;
    }

    /**
     * Return an ordered list of hyponyms of a list of words.
     */
    public ArrayList getHyponyms(List<String> words) {
        TreeSet<String> bothHyponyms = new TreeSet<>();
        List<TreeSet> setList = new ArrayList<>();

        boolean first = true;

        for (String word : words) {
            setList.add(getHyponymsSet(word));
        }

        for (TreeSet set : setList) {
            if (first) {
                bothHyponyms.addAll(set);
                first = false;
            } else {
                bothHyponyms.retainAll(set);
            }
        }
        return toList(bothHyponyms);
    }

    private class wordCount {
        private String word;
        private double count;
        public wordCount(String w, NGramMap ngm, Integer startYear, Integer endYear) {
            this.word = w;
            count = 0.0;
            TimeSeries wordTimeSeries = ngm.countHistory(w, startYear, endYear);
            for (int i = startYear; i <= endYear; i++) {
                if (wordTimeSeries.get(i) != null) {
                    count += wordTimeSeries.get(i);
                }
            }
        }
    }

    private class wordCountComparator implements Comparator<wordCount> {
        @Override
        public int compare(wordCount o1, wordCount o2) {
            if (o1.count > o2.count) {
                return -1;
            } else if (o1.count < o2.count) {
                return 1;
            }
            return 0;
        }
    }

    public Comparator<wordCount> getComparator() {
        return new wordCountComparator();
    }

    /**
     * Take in a list of word or a single word and return a list of size k of its hyponyms given a start year
     * and an end year.
     */
    public ArrayList getHyponyms(List<String> words, int k, int startYear, int endYear, NGramMap ngm) {
        if (k == 0) {
            return getHyponyms(words);
        }
        Comparator<wordCount> comparator = getComparator();
        PriorityQueue<wordCount> wordsPQ = new PriorityQueue<>(INITIAL_CAPACITY, comparator);
        ArrayList<String> hyponymsList = getHyponyms(words);
        TreeSet<String> hyponymsListWithK = new TreeSet<>();

        for (String word : hyponymsList) {
            wordCount newWord = new wordCount(word, ngm, startYear, endYear);
            if (newWord.count != 0.0) {
                wordsPQ.add(newWord);
            }
        }

        try {
            for (int i = 0; i < k; i++) {
                hyponymsListWithK.add(wordsPQ.poll().word);
            }
        } catch (Exception IndexOutOfBoundsException) {
            for (int i = 0; i < wordsPQ.size(); i++) {
                hyponymsListWithK.add(wordsPQ.poll().word);
            }
        }

        return toList(hyponymsListWithK);
    }

    /**
     * Recursively add the hyponyms of a word and their hyponyms to a TreeSet,
     * which stores the index of all hyponyms of the input word.
     */
    private TreeSet getHyponymsHelper(int i, TreeSet l) {
        if (!IntMap.containsKey(i)) {
            l.add(i);
            return l;
        }
        TreeSet<Integer> children = IntMap.get(i);
        l.add(i);

        for (int j : children) {
            l.add(j);
            if (j != i) {
                getHyponymsHelper(j, l);
                }
            }
        return l;
    }

    /**
     * A method that helps to get all the index associated with a certain word
     * in an IntMap.
     */
    private ArrayList<Integer> getIndex(String word) {
        ArrayList<Integer> IndexList = new ArrayList<>();
        for (int i = 0; i < wordDict.size(); i++) {
            List<String> l = Arrays.asList(wordDict.get(i).split(" "));
            if (l.contains(word)) {
                IndexList.add(i);
            }
        }
        return IndexList;
    }

    /**
     * Helper method that convert a tree set to list.
     */
    private ArrayList<String> toList(TreeSet ts) {
        return (ArrayList<String>) ts.stream()
                .collect(Collectors.toList());
    }


}