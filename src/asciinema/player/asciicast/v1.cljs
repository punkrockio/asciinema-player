(ns asciinema.player.asciicast.v1
  (:require [asciinema.vt :as vt]
            [asciinema.player.frames :as frames]))

(defn- calc-duration [stdout idle-time-limit]
  (-> (comp (frames/cap-relative-time-xf idle-time-limit)
            (frames/to-absolute-time-xf))
      (sequence stdout)
      last
      first))

(defn build-xf [width height idle-time-limit]
  (comp (frames/cap-relative-time-xf idle-time-limit)
        (frames/to-absolute-time-xf)
        (frames/data-reductions-xf vt/feed-str (vt/make-vt width height))))

(defn initialize-asciicast [asciicast vt-width vt-height idle-time-limit]
  (let [width (or vt-width (:width asciicast))
        height (or vt-height (:height asciicast))
        xf (build-xf width height idle-time-limit)
        stdout (:stdout asciicast)]
    {:version 1
     :width width
     :height height
     :duration (calc-duration stdout idle-time-limit)
     :xf xf
     :data stdout
     :frames (sequence xf stdout)}))
