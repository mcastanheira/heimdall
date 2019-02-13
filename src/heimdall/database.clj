(ns heimdall.database)

(def checks (atom []))

(defn add-checks [new-checks]
  (swap! checks concat new-checks))

(defn get-checks [uuids]
  @checks)