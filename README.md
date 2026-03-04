# PhotoMeta

# 📷 PhotoMeta – Android Photo Metadata Explorer

Android alkalmazás, amely lehetővé teszi a felhasználók számára, hogy a készüléken található képeket importálják, azok EXIF metaadatait kiolvassák, majd az adatokat egy lokális adatbázisban tárolják és kezeljék.

Az alkalmazás célja, hogy a felhasználók könnyen áttekintsék a fotóikhoz tartozó technikai és helyadatokat (kamera, készítés ideje, GPS koordináták), valamint egyszerű keresési és rendezési lehetőségekkel navigáljanak a képek között.

---

# 🎯 Az alkalmazás célja

A modern digitális fényképek jelentős mennyiségű metaadatot tartalmaznak (EXIF), például:

- készítés időpontja  
- kamera típusa  
- expozíciós adatok  
- GPS koordináták  

A **PhotoMeta** alkalmazás célja, hogy ezeket az információkat:

- automatikusan kiolvassa a képekből  
- strukturált módon eltárolja egy lokális adatbázisban  
- listázható, kereshető és rendezhető formában jelenítse meg  

---

# 📱 Fő funkciók

## 1️⃣ Kép importálás

A felhasználó képeket tölthet be a készülék tárhelyéről.

Importálás során az alkalmazás:

- előnézetet jelenít meg a kiválasztott képről  
- kiolvassa a kép **EXIF metaadatait**  
- az adatokat elmenti a **lokális adatbázisba**

Kiolvasott adatok például:

- készítés dátuma  
- kamera gyártó és modell  
- ISO érték  
- expozíciós idő  
- GPS koordináták (ha rendelkezésre állnak)

---

## 2️⃣ Lista nézet

A betöltött képek egy listában jelennek meg.

A listában minden elem tartalmazza:

- kép előnézet  
- készítés dátuma  
- kamera modell  
- képhez tartozó cím vagy helyadat

A lista **RecyclerView segítségével** valósul meg.

### 🔎 Keresés

A felhasználó kereshet:

- cím alapján  
- kamera modell alapján  

### ↕ Rendezés

A lista rendezhető:

- dátum szerint  
- kamera típus szerint  

---

## 3️⃣ CRUD műveletek

Az alkalmazás teljes adatbázis-kezelést biztosít.

Lehetséges műveletek:

- **Create** – új kép importálása  
- **Read** – képek listázása és részletes megjelenítése  
- **Update** – képadatok módosítása  
- **Delete** – kép eltávolítása az adatbázisból  

---

# 🗺 Opcionális funkció

## Térképes megjelenítés

Ha a kép tartalmaz GPS koordinátákat, az alkalmazás képes:

- megjeleníteni a kép készítésének helyét egy térképen  
- jelölőt elhelyezni a pontos pozíción  

---

# 🧭 Navigáció

Az alkalmazás három fő nézetet tartalmaz:

### 📂 See Pictures
A mentett képek listája.

### 📸 Take Pictures
Új kép készítése vagy kép importálása.

### 📊 Stats
Statisztikai nézet, például:

- kamera modellek eloszlása  
- képek száma  
- GPS adatokkal rendelkező képek aránya  

---

# 🏗 Technológiák

Az alkalmazás a következő technológiákat használja:

- Java
- Android Studio
- Android SDK

### Android komponensek

- Single Activity architektúra
- Fragment alapú felépítés
- ConstraintLayout
- RecyclerView

### Adatkezelés

- Room (Architecture Components)
- EXIF metadata feldolgozás

### Képek kezelése

- Glide

### Verziókezelés

- Git
- GitHub / GitLab

---

# 🗃 Alkalmazás architektúra
