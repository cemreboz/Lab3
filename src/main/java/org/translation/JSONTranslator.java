package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final Map<String, List<String>> languagesMap = new HashMap<>();
    private final Map<String, Map<String, String>> translationsMap = new HashMap<>();

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject countryObject = jsonArray.getJSONObject(i);
                String countryCode = countryObject.getString("code");

                JSONArray languages = countryObject.getJSONArray("languages");
                List<String> languageList = new ArrayList<>();
                for (int j = 0; j < languages.length(); j++) {
                    languageList.add(languages.getString(j));
                }
                languagesMap.put(countryCode, languageList);

                Map<String, String> translations = new HashMap<>();
                for (String language : languageList) {
                    translations.put(language, countryObject.getString(language));
                }
                translationsMap.put(countryCode, translations);

            }
        }

        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        List<String> languages = languagesMap.get(country);
        return new ArrayList<>(languages);
    }

    @Override
    public List<String> getCountries() {

        return new ArrayList<>(languagesMap.keySet());
    }

    @Override
    public String translate(String country, String language) {
        Map<String, String> translationMap = translationsMap.get(country);
        return translationMap.get(language);
    }
}
