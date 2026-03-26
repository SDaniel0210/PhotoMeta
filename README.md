# PhotoMeta

# 📷 PhotoMeta – Android Photo Metadata & Provenance Explorer

Android alkalmazás, amely lehetővé teszi a felhasználók számára, hogy a készüléken található képeket importálják, azok EXIF metaadatait kiolvassák, szerkesszék, majd az adatokat egy lokális adatbázisban tárolják és kezeljék.

Az alkalmazás célja, hogy a felhasználók könnyen áttekintsék a fotóikhoz tartozó technikai és helyadatokat (kamera, készítés ideje, GPS koordináták), valamint képes legyen a képek eredetére és módosításaira utaló digitális nyomok (AI/provenance) vizsgálatára is.

---

# Az alkalmazás célja

A modern digitális fényképek jelentős mennyiségű metaadatot tartalmaznak (EXIF), például:

* készítés időpontja
* kamera típusa
* expozíciós adatok
* GPS koordináták

A **PhotoMeta** alkalmazás célja, hogy ezeket az információkat:

* automatikusan kiolvassa a képekből
* strukturált módon eltárolja egy lokális adatbázisban
* listázható, kereshető és rendezhető formában jelenítse meg
* lehetőséget biztosítson bizonyos metaadatok szerkesztésére és visszaírására
* vizsgálja a képekhez tartozó AI/provenance (eredet) információkat

---

# Fő funkciók

## Kép importálás

A felhasználó képeket tölthet be a készülék tárhelyéről.

Importálás során az alkalmazás:

* előnézetet jelenít meg a kiválasztott képről
* kiolvassa a kép **EXIF metaadatait**
* az adatokat elmenti a **lokális adatbázisba**

Kiolvasott adatok például:

* készítés dátuma
* kamera gyártó és modell
* ISO érték
* expozíciós idő
* GPS koordináták (ha rendelkezésre állnak)

---

## Metaadatok szerkesztése

Az alkalmazás lehetőséget biztosít bizonyos metaadatok módosítására.

A felhasználó:

* szerkesztheti a képhez tartozó leírást vagy címkét
* módosíthatja a GPS koordinátákat
* kiegészítheti a képhez tartozó információkat

A módosítások:

* elmentésre kerülnek a **Room adatbázisba**
* igény esetén visszaírhatók a kép **EXIF metaadataiba**

---

## Lista nézet

A betöltött képek egy listában jelennek meg.

A listában minden elem tartalmazza:

* kép előnézet
* készítés dátuma
* kamera modell
* képhez tartozó cím vagy helyadat

A lista **RecyclerView segítségével** valósul meg.

### Keresés

A felhasználó kereshet:

* cím alapján
* kamera modell alapján

### Rendezés

A lista rendezhető:

* dátum szerint
* kamera típus szerint

---

## AI / Provenance ellenőrzés

Az alkalmazás képes megvizsgálni, hogy a kép tartalmaz-e:

* AI generálásra utaló metaadatokat
* utófeldolgozásra utaló nyomokat
* Content Credentials / provenance információkat (ha elérhető)

A rendszer:

* elemzi a képhez tartozó metaadatokat
* megpróbálja azonosítani az esetleges AI-hoz köthető nyomokat
* státuszt ad a képhez, például:

  * nincs AI-re utaló adat
  * AI-hoz köthető metadata található
  * szerkesztett kép

Megjegyzés: a felismerés metadata alapú, nem garantál 100%-os pontosságot minden esetben.

---

## CRUD műveletek

Az alkalmazás teljes adatbázis-kezelést biztosít.

Lehetséges műveletek:

* **Create** – új kép importálása
* **Read** – képek listázása és részletes megjelenítése
* **Update** – metaadatok módosítása
* **Delete** – kép eltávolítása az adatbázisból

---

# Opcionális funkció

## Térképes megjelenítés

Ha a kép tartalmaz GPS koordinátákat, az alkalmazás képes:

* megjeleníteni a kép készítésének helyét egy térképen
* jelölőt elhelyezni a pontos pozíción

---

# Navigáció

Az alkalmazás három fő nézetet tartalmaz:

### See Pictures

A mentett képek listája.

### Take Pictures

Új kép készítése vagy kép importálása.

### Stats

Statisztikai nézet, például:

* kamera modellek eloszlása
* képek száma
* GPS adatokkal rendelkező képek aránya
* AI metadata-val rendelkező képek aránya

---

# Technológiák

Az alkalmazás a következő technológiákat használja:

* Java
* Android Studio
* Android SDK

### Android komponensek

* Single Activity architektúra
* Fragment alapú felépítés
* ConstraintLayout
* RecyclerView

### Adatkezelés

* Room (Architecture Components)
* EXIF metadata feldolgozás és írás

### Képek kezelése

* Glide

### Verziókezelés

* Git
* GitHub / GitLab

---
