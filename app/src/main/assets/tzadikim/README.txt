Tzadikim local media (MP4 preferred, image fallback)

Each tzadik card prefers to play a short muted MP4 clip (about 6–8 seconds). If no MP4 is present for that tzadik, the app will look for a portrait image instead.

Where to put the media files
- Create a folder per tzadik under:
  app/src/main/assets/tzadikim/<slug>/
- Place ONE media file for the front of the card. Preference order:
  1) MP4 clip (first one found wins):
     - clip.mp4   (recommended)
     - video.mp4
     - portrait.mp4
  2) Portrait image (only used if no MP4 is found):
     - portrait.webp  (recommended)
     - portrait.jpg
     - portrait.jpeg
     - portrait.png

Slug rules (derived from the tzadik’s display name shown in the app):
- Lowercase, ASCII only.
- Replace '&' with 'and'.
- Replace any sequence of non [a-z0-9] characters with a single '-'.
- Collapse repeated '-' and trim leading/trailing '-'.

Examples of slugs for the current list (24 tzadikim):
- Baal Shem Tov -> baal-shem-tov
- Rashi -> rashi
- Rabbi Shimon bar Yochai -> rabbi-shimon-bar-yochai (also accepted: shimon-bar-yochai)
- Menachem Mendel Schneerson -> menachem-mendel-schneerson
- The Vilna Gaon -> the-vilna-gaon (also accepted: vilna-gaon)
- Rabbi Nachman of Breslov -> rabbi-nachman-of-breslov (also accepted: nachman-of-breslov)
- Rabbi Yosef Karo -> rabbi-yosef-karo (also accepted: yosef-karo)
- The Chafetz Chaim -> the-chafetz-chaim (also accepted: chafetz-chaim)
- Rabbi Moshe Chaim Luzzatto -> rabbi-moshe-chaim-luzzatto (also accepted: moshe-chaim-luzzatto)
- Rabbi Ovadia Yosef -> rabbi-ovadia-yosef (also accepted: ovadia-yosef)
- Rabbi Moshe Feinstein -> rabbi-moshe-feinstein (also accepted: moshe-feinstein)
- The Rambam (Maimonides) -> the-rambam-maimonides (also accepted: rambam-maimonides)
- The Ramban (Nachmanides) -> the-ramban-nachmanides (also accepted: ramban-nachmanides)
- Rabbi Yehuda HaNasi -> rabbi-yehuda-hanasi (also accepted: yehuda-hanasi)
- Rabbi Isaac Luria -> rabbi-isaac-luria (also accepted: isaac-luria)
- Rabbi Chaim of Volozhin -> rabbi-chaim-of-volozhin (also accepted: chaim-of-volozhin)
- Rabbi Elimelech of Lizhensk -> rabbi-elimelech-of-lizhensk (also accepted: elimelech-of-lizhensk)
- Rabbi Levi Yitzchak of Berditchev -> rabbi-levi-yitzchak-of-berditchev (also accepted: levi-yitzchak-of-berditchev)
- Rabbi Shneur Zalman of Liadi -> rabbi-shneur-zalman-of-liadi (also accepted: shneur-zalman-of-liadi)
- The Maggid of Mezritch -> the-maggid-of-mezritch (also accepted: maggid-of-mezritch)
- The Chozeh of Lublin -> the-chozeh-of-lublin (also accepted: chozeh-of-lublin)
- The Sfat Emet -> the-sfat-emet (also accepted: sfat-emet)
- The Netziv -> the-netziv (also accepted: netziv)
- Rabbi Chaim Kanievsky -> rabbi-chaim-kanievsky (also accepted: chaim-kanievsky)

Video guidance
- Duration: 6–8 seconds each (the app will loop).
- Orientation: cards are portrait (3:4). Prefer portrait videos; landscape will be center-cropped to fill.
- Resolution: at least 720x960 (or higher). Keep files small; under ~3–8 MB per clip is ideal.
- Audio: clips are muted in-app; you don’t need audio.

Image guidance (used only if no MP4)
- Use a clear portrait, 3:4 aspect preferred (will be center-cropped to fill).
- WebP is recommended for size/quality. Aim under ~200–400 KB if possible.

Notes
- The app does not fetch anything from Wikipedia or the network.
- Assets are bundled offline. If neither MP4 nor image is found, the card shows a "No media available" message.
