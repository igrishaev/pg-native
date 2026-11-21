(ns pg.jni.core
  (:import
   (java.util List)
   (pg.jni Connection Statement))
  (:use criterium.core))

(set! *warn-on-reflection* true)

(defn connect ^Connection [info]
  (Connection/make info))

(defn prepare ^Statement [^Connection conn name sql]
  (.prepare conn name sql))

(defn exec-prepared [^Connection conn ^Statement st ^List params]
  (.execPrepared conn st params))


(def QUERY_SELECT_RANDOM_COMPLEX
  "
select

  x::int4                  as int4,
  x::text || 'foobar'      as line,
  x > 100500               as bool,
  x::int4                  as int4,
  x::text || 'foobar'      as line,
  x > 100500               as bool,
  x::int4                  as int4,
  x::text || 'foobar'      as line,
  x > 100500               as bool


from
  generate_series(1,50000) as s(x)

")


(def info "host=localhost port=15432 dbname=test user=test password=test")

(comment

  (with-open [conn (connect info)
              st (prepare conn "foo" QUERY_SELECT_RANDOM_COMPLEX)]
    (quick-bench
        (exec-prepared conn st []))))
