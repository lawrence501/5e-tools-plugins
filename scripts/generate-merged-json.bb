(require '[babashka.fs :as fs])

(defn- list-jsons []
  (fs/glob "." "*.json"))

(def merge-data (partial
                  merge-with
                  (fn concat-merge [left right]
                    (cond
                      (vector? left) (into left right)
                      (map? left) (merge-with concat-merge left right)
                      :else right))))

(defn- merge-jsons []
  (transduce (map (comp json/parse-stream io/reader fs/file))
             merge-data
             (list-jsons)))

(-> (merge-jsons)
    (update-in ["_meta" "sources"] set)
    json/generate-string
    (->> (spit "generated/all.json")))
{"sources" [{"json" "LevelUp", "abbreviation" "A5E", "full" "Level Up (A5E)", "url" "https://www.levelup5e.com/", "authors" ["Level Up"], "convertedBy" ["TODO DMs"], "version" "0.0.1"}
            {"json" "sns", "abbreviation" "S&S", "full" "Spies & Spiders", "authors" ["Spies & Spiders"], "version" "0.0.1"}
            {"json" "LevelUp", "abbreviation" "A5E", "full" "Level Up (A5E)", "authors" ["Level Up"], "version" "0.0.1", "url" "https://www.levelup5e.com/"}]
 "dateAdded" 0, "dateLastModified" 0}
