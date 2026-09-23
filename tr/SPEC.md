# Spesifikasyon: Java 27 Öğrenim Müfredatı

**Durum:** Teslim edildi — 24 görevin hepsi tamam, 2026-09-23'te doğrulandı
**Tarih:** 2026-09-23
**Aşama:** Specify → Plan → Tasks → Implement dizisinin 1. aşaması (Specify)

---

## Yaptığım Varsayımlar

1. Öğrenci başka bir dilde (Python, JavaScript, C#) zaten programlıyor ve
   **özellikle Java'ya** yeni. Modüller değişkenin ya da döngünün ne olduğunu
   yeniden anlatmıyor; Java'nın bunu nasıl yaptığını ve nerede ayrıştığını
   anlatıyor.
2. Özgün istekteki "takeout" kelimesi **Özet Çıkarımlar** anlamına geliyor: her
   modülü kapatan kısa bir toparlama.
3. İçerik ve tüm kod **İngilizce**. Bu spesifikasyon bir depo dokümanı, o da
   İngilizce.
4. Müfredat yalnızca **çekirdek dili ve JDK standart kütüphanesini** kapsıyor.
   Spring yok, JDBC yok, build çatısı yok.
5. Dersler ve örnek kodları **her modül klasörünün içinde birlikte** duruyor. Ödev
   çözümleri ise öğrenci kazara cevabı görmesin diye ayrı bir üst düzey
   `solutions/` ağacında.
6. Build aracı yok. Örnekler tek dosya kaynak başlatma ile çalışıyor:
   `java Example.java`.

→ Bunlardan herhangi birini şimdi düzeltin, yoksa böyle devam ediyorum.

---

## Amaç

Başka bir dil bilen bir programcıyı alıp JDK 27 üzerinde modern Java'da akıcı
kılan, kendi hızında ilerleyen bir Java müfredatı kurmak.

**Kullanıcı:** Genel programlama okuryazarlığı olan ve sıfır Java deneyimi olan
bir geliştirici.

**Başarı neye benziyor:** Öğrenci numaralı modüllerde sırayla ilerliyor. Her
modül bir alanı hafif ve sade bir tonda anlatıyor, yorumları sözdizimini
tekrarlamak yerine *nedenini* öğreten çalıştırılabilir örneklerle destekliyor. Her
modül Özet Çıkarımlar ve bir ödevle kapanıyor; referans çözümler var ama dersin
dışında duruyor.

### Bu müfredatı şekillendiren bir bulgu

JDK 27, **15 Eylül 2026**'da genel kullanıma açıldı. **LTS değil** (güncel LTS
JDK 25). Teslim ettiği JEP'ler neredeyse tamamen çalışma zamanı seviyesinde:

| JEP | Başlık | Durum |
|---|---|---|
| 523 | Make G1 the Default Garbage Collector in All Environments | Final |
| 527 | Post-Quantum Hybrid Key Exchange for TLS 1.3 | Final |
| 534 | Compact Object Headers by Default | Final |
| 536 | JFR In-Process Data Redaction | Final |
| 531 | Lazy Constants | Üçüncü Preview |
| 532 | Primitive Types in Patterns, instanceof, and switch | Beşinci Preview |
| 533 | Structured Concurrency | Yedinci Preview |
| 537 | Vector API | On İkinci İnkübatör |
| 538 | PEM Encodings of Cryptographic Objects | Üçüncü Preview |

**Sonuç:** "Java 27 öğrenmek", dokuz yeni dil özelliği öğrenmek demek değil. O
listedeki hiçbir şey yeni başlayanın kod yazma biçimini değiştirmiyor. Dürüst
çerçeve, ve bu müfredatın benimsediği çerçeve şu: *JDK 27 çalışma ortamı ve araç
zinciridir; öğrendiğiniz dil ise JDK 25 LTS'e kadar birikmiş kararlı Java'dır.*
Kapanış modülü 26 ve 27'nin gerçekte neyi değiştirdiğini, LTS olmayan sürümlerin
neden düşündürdüğünden az önem taşıdığını ve bir JEP'in nasıl okunacağını
anlatıyor.

### Giriş kararı

JEP 512 (Compact Source Files and Instance Main Methods) **JDK 25'te
kesinleşti**, dolayısıyla 27'de kararlı. Bu yüzden Modül 01 şöyle açılıyor:

```java
void main() {
    IO.println("Hello, World!");
}
```

`public class` yok, `static` yok, `String[] args` yok, import yok. Modül 05 ise
öğrenciyi bilinçli biçimde klasik forma "mezun ediyor", çünkü karşılaşacağı her
gerçek kod tabanı, öğretici ve Stack Overflow cevabı onu kullanıyor. Kolay biçimi
öğretip klasik olanı hiç açıklamamak, onu gerçek Java okuyamaz hâlde bırakırdı.

---

## Teknoloji Yığını

| Öğe | Seçim |
|---|---|
| JDK | OpenJDK 27 (GA 2026-09-15) |
| Dil seviyesi | JDK 25'e kadar kararlı özellikler; preview özellikler yalnızca son modülde, açıkça etiketli |
| Build aracı | Yok |
| Bağımlılıklar | Yok |
| Ders biçimi | Markdown |
| Örnek biçimi | Tek dosya `.java`, doğrudan çalıştırılabilir |

---

## Proje Yapısı

```
Java-27-edu/
├── README.md                          → İçindekiler, nasıl başlanır, JDK kurulumu
├── SPEC.md                            → Bu doküman
├── scripts/
│   └── verify-examples.sh             → Her örneği derleyip çalıştırır, başarısızlıkları bildirir
├── modules/
│   ├── 01-getting-started/
│   │   ├── README.md                  → Ders metni + Özet Çıkarımlar
│   │   ├── examples/
│   │   └── homework/
│   └── …
└── solutions/
    └── 01-getting-started/
```

Modül kimlikleri kebab-case, sıfır dolgulu ve bir kez atandıktan sonra sabit.
Proje ortasında asla yeniden adlandırılmıyorlar.

---

## Kod Stili

Dosya başına tek kavram. Yorumlar *nedenini* öğretiyor; asla sözdizimini
anlatmıyor.

```java
// Module 02 — examples/AutoboxingTrap.java
//
// Java has two parallel worlds for numbers: primitives (int) and objects
// (Integer). Most of the time Java quietly converts between them, which is
// convenient right up until the moment it bites you. Here is the bite.

void main() {
    Integer a = 127;
    Integer b = 127;
    IO.println(a == b);   // true — and this is the misleading one
}
```

Gelenekler: standart Java adlandırma (`PascalCase` tipler, `camelCase` üyeler,
`UPPER_SNAKE` sabitler), 4 boşluk girinti, 100 karakteri aşmayan satırlar, her
örnek argümansız tek başına çalıştırılabilir.

---

## Test Stratejisi

Burada birim testi yapılacak bir uygulama yok; teslim edilen şey öğretim
materyali. Doğruluk, **her örneğin gerçekten çalışması** demek.

- `scripts/verify-examples.sh`, `modules/**/examples/*.java` ve
  `solutions/**/*.java` dosyalarını gezer, her birini çalıştırır, sıfır olmayan
  her çıkışta ya da derleme hatasında başarısız olur.
- Bilerek derleme hatası gösteren örnekler `// EXPECT: compile-error` başlığı
  taşır ve atlanmak yerine başarısız olmaları denetlenir.
- Preview bayrağı gerektirenler `// EXPECT: preview` taşır ve
  `--enable-preview --source 27` ile çalıştırılır.
- Bir modül, örnekleri, ödev metni ve referans çözümü scriptten geçene kadar
  "bitti" sayılmaz.
- Modül 21 testi `assert` ve elle yazılmış bir çatıyla öğretir, sıfır bağımlılık
  kuralını bozmadan, ve gerçek bir JUnit kurulumunun ne katacağını anlatır.

---

## Sınırlar

**Her zaman**
- Tüm ders metnini, kodu, yorumları ve tanımlayıcıları İngilizce yazın.
- Her modüle bir Özet Çıkarımlar bölümü ve bir ödev verin.
- Bir modülü bitmiş saymadan önce her örneği çalıştırın.
- Yaygın bir hatayı anlatırken öğrencinin göreceği gerçek hata mesajını gösterin.
- Bir özellik preview, inkübatör ya da JDK sürümüne bağlıysa bunu açıkça belirtin.

**Önce sorun**
- JUnit dahil herhangi bir üçüncü parti bağımlılık eklemek.
- Maven ya da Gradle getirmek.
- Onaylanmış modül kimliklerini, sıralarını ya da müfredat taslağını değiştirmek.
- Kapsamı çatılara, Android'e ya da web geliştirmeye genişletmek.

**Asla**
- Bilerek olduğu işaretlenmeden derlenmeyen bir örnek yayımlamayın.
- JEP numarası, yayın tarihi ya da özellik iddiası uydurmayın. openjdk.org
  üzerinden doğrulayın.
- Tanımlamadan önce jargon kullanmayın.
- Kullanımdan kaldırılmış ya da önerilmeyen bir pratiği öyle olduğunu
  belirtmeden sunmayın.
- Ödev çözümlerini modül klasörünün içine koymayın.

---

## Başarı Kriterleri

Sekizinin hepsi 2026-09-23'te teslim edilen depoya karşı denetlendi. Her satır
yalnızca sağlandığını değil, **nasıl doğrulandığını** kaydediyor.

- [x] **1. `./scripts/verify-examples.sh` tüm depoda sıfırla çıkıyor.**
      130 dosya, hepsi bildirdiği gibi davranıyor, yaklaşık 70 saniyede.

- [x] **2. 22 modül klasörünün hepsi var, her biri `README.md`, en az iki
      çalıştırılabilir dosya içeren `examples/` ve `homework/README.md`
      içeriyor.** Script ile her modül dizininde denetlendi. Toplam 105 örnek
      dosya, modül başına en az 4.

- [x] **3. Her modül README'si yedi şablon bölümünün hepsini, Özet Çıkarımlar
      dahil içeriyor.** 22 dosyanın her birinde başlıklar grep ile denetlendi.
      Son geçişte `22-whats-new` içinde bir eksik bulundu ve düzeltildi.

- [x] **4. Her ödevin `solutions/<modül-kimliği>/` altında temiz çalışan eşleşen
      bir referans çözümü var.** 22 modül için 22 çözüm dizini, 25 çözüm dosyası,
      hepsi yukarıdaki doğrulama çalıştırmasına dahil.

- [x] **5. Kök `README.md` her modülü sırayla bağlıyor ve JDK 27 kurulumunu
      belgeliyor.** 22 bağlantının hepsi script ile doğrulandı. Kurulum macOS,
      Linux ve Windows için, her biri `JAVA_HOME` talimatıyla belgelendi.

- [x] **6. Hiçbir dosya İngilizce olmayan metin, tanımlayıcı ya da yorum
      içermiyor.** Her `.java`, `.md` ve `.sh` dosyası ASCII dışı harf karakterleri
      için tarandı. Hiçbiri bulunmadı.

- [x] **7. Depoda hiçbir yerde üçüncü parti bağımlılık yok.** `pom.xml`,
      `build.gradle` ya da `.jar` yok. 130 dosyadaki her import `java.*`, `javax.*`
      ya da `jdk.*` altında çözülüyor, artı bir bağımlılık değil dil özelliği olan
      JEP 511 `import module java.base` bildirimi.

- [x] **8. Bir JDK sürümü hakkındaki her olgusal iddia openjdk.org yayın
      verisine dayanıyor.** Modül ve kök README'lerde 32 ayrı openjdk.org
      bağlantısı. JDK 26 ve 27 JEP tabloları, JDK 25 kesinleşme iddiaları ve JEP
      491'in sanal iş parçacığı sabitlenmesini kaldırması, yazım sırasında
      hatırlanmak yerine kaynağından alındı.

---

## Açık Sorular, Çözüldükleri Hâliyle

1. **JDK 27 kurulumu.** Çözüldü. Uygulama başlamadan önce JDK 27 (derleme
   27+35-2325) kuruldu ve her örnek ona karşı doğrulandı.

2. **Modül 21 ve JUnit.** Sıfır bağımlılık kuralı lehine çözüldü. Test, çıplak
   `assert` artı 39 satırlık elle yazılmış bir çatıyla öğretiliyor, ve modül
   gerçek bir projede JUnit benimsemenin gerekçesini kuruyor. Kısıt dersi
   iyileştirdi: çalışan bir çatının tamamını görmek, bir framework'ün ne yaptığını
   onu kullanmaktan daha iyi açıklıyor.

3. **Preview özellikler.** Önerildiği gibi çözüldü. Yapılandırılmış eşzamanlılık
   (JEP 533) ve ilkel tip desenleri (JEP 532) yalnızca Modül 22'de, JEP numarası
   ve preview turuyla etiketli olarak, `--enable-preview` arkasında görünüyor.
   Modül preview özellik yayımlamaya açıkça karşı çıkıyor, çünkü bir preview sınıf
   dosyası bayrakla bile farklı bir sürüm tarafından reddediliyor.

---

## Uygulama Sırasında Yapılan Düzeltmeler

Kaydedildi, çünkü spesifikasyonun sınırı iddia uydurmayı yasaklıyordu ve buna
bağlı kalmak yanlış olan birkaç şeyi açığa çıkardı:

- Bir kompozisyon örneği, `ArrayList` alt sınıflamanın eklemeleri iki kez
  sayacağını iddia ediyordu. Çalıştırmak bunu çürüttü: modern `ArrayList.addAll`
  toplu kopyalıyor. Hatayı gösteren koleksiyon `HashSet`, ve örnek artık ikisini
  de gösteriyor, ki bu kırılgan temel sınıf noktasını özgününün yapacağından daha
  iyi kuruyor.
- Alıntılanan üç derleyici hatası satır numarası yanlıştı. Hepsi hatalar
  üretilerek düzeltildi.
- Bir kova indeksi gösterimi 8 kova kullanıyordu, ve `Integer.MIN_VALUE` 8'e tam
  bölünüyor, dolayısıyla hem bozuk hem doğru biçim `0` döndürüyordu ve hata
  görünmezdi. 7'ye değiştirildi.
- Bir ödevin belirtilen çıktısı referans çözümünün yazdırdığıyla çelişiyordu.
  Ödev çıktıları artık gözle değil çözüm çıktısına karşı `diff` ile
  karşılaştırılıyor.
- Yapılandırılmış eşzamanlılık preview geçmişi hafızadan yedi tur olarak
  yazılmıştı. JEP 533'ün History bölümünü okumak, öncesinde iki inkübatör turu
  daha olduğunu gösterdi, yani dokuz sürüm.
