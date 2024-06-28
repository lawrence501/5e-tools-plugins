(require '[babashka.fs :as fs])

(defn- list-jsons [category]
  (fs/glob category "*.json"))

(def merge-data (partial
                  merge-with
                  (fn concat-merge [left right]
                    (cond
                      (vector? left) (into left right)
                      (map? left) (merge-with concat-merge left right)
                      :else right))))

(defn- merge-jsons [category]
  (transduce (map (comp json/parse-stream io/reader fs/file))
             merge-data
             (list-jsons category)))

(let [category (first *command-line-args*)]
  (assert (some? category))
  (-> (merge-jsons category)
      (update-in ["_meta" "sources"] set)
      json/generate-string
      (->> (spit (str "generated/" (or category "all") ".json")))))
