package com.example.smartpantry.logic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Makes ingredient names comparable: "Tomatoes " and "tomato" both become "tomato".
 * Not NLP - just lower-casing, punctuation stripping, simple singularisation and a
 * tiny synonym table.
 */
public final class IngredientNormalizer {

    private static final Map<String, String> SYNONYMS = new HashMap<>();
    static {
        SYNONYMS.put("green onion", "spring onion");
        SYNONYMS.put("scallion", "spring onion");
        SYNONYMS.put("capsicum", "bell pepper");
        SYNONYMS.put("courgette", "zucchini");
        SYNONYMS.put("aubergine", "eggplant");
        SYNONYMS.put("coriander", "cilantro");
        SYNONYMS.put("plain flour", "flour");
        SYNONYMS.put("all purpose flour", "flour");
        SYNONYMS.put("caster sugar", "sugar");
        SYNONYMS.put("chickpea", "chickpea");
        SYNONYMS.put("garbanzo bean", "chickpea");
        SYNONYMS.put("extra virgin olive oil", "olive oil");
    }

    private IngredientNormalizer() { }

    public static String normalize(String raw) {
        if (raw == null) return "";
        String s = raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z ]", " ").trim().replaceAll("\\s+", " ");
        if (s.isEmpty()) return "";
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(singular(w));
        }
        String result = sb.toString();
        String syn = SYNONYMS.get(result);
        return syn != null ? syn : result;
    }

    static String singular(String w) {
        if (w.length() <= 3) return w;
        if (w.endsWith("ies") && w.length() > 4) return w.substring(0, w.length() - 3) + "y"; // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);                        // tomatoes -> tomato
        if (w.endsWith("ches") || w.endsWith("shes") || w.endsWith("sses") || w.endsWith("xes"))
            return w.substring(0, w.length() - 2);
        if (w.endsWith("ss") || w.endsWith("us")) return w;                                  // hummus, couscous
        if (w.endsWith("s")) return w.substring(0, w.length() - 1);                          // eggs -> egg
        return w;
    }
}
