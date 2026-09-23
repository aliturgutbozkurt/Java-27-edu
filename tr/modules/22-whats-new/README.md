# Modül 22: Yenilikler, Dürüstçe

Bu modül, müfredatın kendi başlığı konusunda dürüst olduğu yer.

Modül 01, JDK 27'nin çalışma ortamı ve araç zinciri olduğunu, öğrendiğiniz dilin
ise JDK 25'e kadar birikmiş kararlı Java olduğunu iddia etmişti. Yirmi bir modül
sonra, işte kanıtı.

**Aşağıdaki her sürüm iddiası kaynağına bağlanıyor.** Bu alışkanlık bu modülün
asıl konusu, çünkü içindeki diğer her şey güncelliğini yitirecek.

## Neler Öğreneceksiniz

- JDK 26 ve 27'nin gerçekte ne teslim ettiği, sürüm sayfalarından
- Altı aylık ritim, LTS, ve hangi numaranın ne için önemli olduğu
- Bir JEP nasıl okunur, ve preview tur numarasının size ne söylediği
- Yapılandırılmış eşzamanlılık ve ilkel tip desenleri, `--enable-preview` arkasında
  çalıştırılmış

## Başka Bir Dilden Geliyorsanız

| | Python | Node.js | Java |
|---|---|---|---|
| Yayın ritmi | ~yıllık | ~altı ayda bir majör | altı ayda bir, sabit tarih |
| Uzun dönem destek | minör başına ~5 yıl | çift numaralı majörler, 30 ay | iki yılda bir LTS |
| Bitmemiş özellikleri denemek | `from __future__ import` | bayraklar ya da bir yayın kanalı | `--enable-preview` |
| Spesifikasyonun yaşadığı yer | PEP'ler | TC39 önerileri | JEP'ler |

En yakın benzeri TC39'un aşamalı önerileri. Bir Java preview'ı kabaca aşama 3
önerisine denk: belirtilmiş, uygulanmış, bayrak arkasında yayımlanmış, ve
yerleşmeden önce hâlâ değişebilir.

Bilinmeye değer fark: Python'un `__future__` import'u ileri uyumludur, Java'da
ise bir **preview sınıf dosyası** farklı bir sürüm tarafından, bayrakla bile
doğrudan reddedilir. Bu tek olgu, bu modülün tavsiye ettiklerinin çoğunu
belirliyor.

## Ders

### Gerçekte ne yayımlandı

**[JDK 26](https://openjdk.org/projects/jdk/26/), GA 17 Mart 2026:**

| JEP | Başlık | Durum |
|---|---|---|
| [500](https://openjdk.org/jeps/500) | Prepare to Make Final Mean Final | Final |
| [504](https://openjdk.org/jeps/504) | Remove the Applet API | Final |
| [516](https://openjdk.org/jeps/516) | Ahead-of-Time Object Caching with Any GC | Final |
| [517](https://openjdk.org/jeps/517) | HTTP/3 for the HTTP Client API | Final |
| [522](https://openjdk.org/jeps/522) | G1 GC: Improve Throughput by Reducing Synchronization | Final |
| [524](https://openjdk.org/jeps/524) | PEM Encodings of Cryptographic Objects | İkinci Preview |
| [525](https://openjdk.org/jeps/525) | Structured Concurrency | Altıncı Preview |
| [526](https://openjdk.org/jeps/526) | Lazy Constants | İkinci Preview |
| [529](https://openjdk.org/jeps/529) | Vector API | On Birinci İnkübatör |
| [530](https://openjdk.org/jeps/530) | Primitive Types in Patterns | Dördüncü Preview |

**[JDK 27](https://openjdk.org/projects/jdk/27/), GA 15 Eylül 2026:**

| JEP | Başlık | Durum |
|---|---|---|
| [523](https://openjdk.org/jeps/523) | Make G1 the Default Garbage Collector in All Environments | Final |
| [527](https://openjdk.org/jeps/527) | Post-Quantum Hybrid Key Exchange for TLS 1.3 | Final |
| [531](https://openjdk.org/jeps/531) | Lazy Constants | Üçüncü Preview |
| [532](https://openjdk.org/jeps/532) | Primitive Types in Patterns | Beşinci Preview |
| [533](https://openjdk.org/jeps/533) | Structured Concurrency | Yedinci Preview |
| [534](https://openjdk.org/jeps/534) | Compact Object Headers by Default | Final |
| [536](https://openjdk.org/jeps/536) | JFR In-Process Data Redaction | Final |
| [537](https://openjdk.org/jeps/537) | Vector API | On İkinci İnkübatör |
| [538](https://openjdk.org/jeps/538) | PEM Encodings of Cryptographic Objects | Üçüncü Preview |

> **JDK 27'de Java yazma biçiminizi değiştiren hiçbir şey yok.** Kesinleşen beş
> JEP'inden dördü çalışma zamanı ve güvenlik işi: bir çöp toplayıcı varsayılanı,
> küçültülmüş nesne başlıkları, TLS anahtar değişimi, uçuş kaydedici maskeleme.
> Dördünden de tek satır düzenlemeden faydalanıyorsunuz. Dil biçimli her şey hâlâ
> preview, bazıları yedinci ardışık sürüm boyunca.

"Java 27'nin yeni özellikleri" vaat eden bir kurs ya listeyi şişirmek ya da
preview özellikleri yerleşmiş gibi anlatmak zorunda kalırdı. İkisi de böyle
söylemekten kötü.

**JDK 26'da bilinmeye değer iki tanesi:**
[JEP 517](https://openjdk.org/jeps/517) yerleşik `HttpClient` sınıfına HTTP/3
kazandırıyor, bir bağımlılık eklemek yerine bir sürüm isteği değiştirerek
aldığınız gerçek bir yetenek. [JEP 500](https://openjdk.org/jeps/500) ise kodun
yansımayla bir `final` alanı değiştirmesi durumunda, bunu yasaklamaya hazırlık
olarak uyarıyor; hedefi bazı serileştirme ve taklit kütüphaneleri.

### Sürümler ve LTS

Altı ayda bir, Mart ve Eylül'de, bir şey hazır olsun olmasın sabit bir tarihte
bir özellik sürümü. 2017 öncesinde sürümler özellik odaklıydı ve iki üç yılda bir
geliyordu.

| Sürüm | Tanım | Tarih |
|---|---|---|
| Java 17 | **LTS** | Eylül 2021 |
| Java 21 | **LTS** | Eylül 2023 |
| Java 25 | **LTS** | Eylül 2025 |
| Java 26 | | Mart 2026 |
| Java 27 | | Eylül 2026 |

LTS olmayan bir sürüm, bir sonraki sürüm onun yerini alana kadar güvenlik yaması
alır. Altı ay, sonra hiçbir şey. Bir LTS ise yıllarca alır, üretim sistemlerinin
neredeyse hepsinin birini çalıştırmasının sebebi budur.

> **"Java 27'de ne yeni" çoğu ekip için yanlış sorudur.** Doğrusu "Java 21'den
> beri ne yeni", çünkü gerçekte bulundukları yer orası.

| Durum | Kullanın |
|---|---|
| Öğrenme | en yeni sürüm. Özellikler önce burada kesinleşir. |
| Üretim | güncel LTS, bugün [Java 25](https://openjdk.org/projects/jdk/25/) |
| Bir kütüphane | desteklemek zorunda olduğunuz en eski LTS |

**Kimsenin fark etmediği birikim.** Herhangi bir tek sürüm cılız görünür. Java 21
ile Java 25 arasında bunların hepsi geldi, ve her biri bu müfredatta yer alıyor:

| JEP | Özellik | Modül |
|---|---|---|
| [512](https://openjdk.org/jeps/512) | Kompakt kaynak dosyaları ve örnek main metotları | 01 |
| [511](https://openjdk.org/jeps/511) | Modül içe aktarma bildirimleri | 20 |
| [513](https://openjdk.org/jeps/513) | Esnek kurucu gövdeleri | 05 |
| [506](https://openjdk.org/jeps/506) | Kapsam değerleri | 19 |
| [485](https://openjdk.org/jeps/485) | Stream gatherer'ları | 14 |
| [491](https://openjdk.org/jeps/491) | Sanal iş parçacıklarını sabitlemeden senkronize etme | 19 |

Hangi sürümde olduğunuzu denetlemek için `java.version` metnini değil
`Runtime.version()` metodunu kullanın. O metni ayrıştırmak, Java 9 biçimi
`1.8.0_301` yerine `9.0.1` yaptığında herkes için bozuldu.

### Bir JEP nasıl okunur

Java hakkında okuduğunuz her şey güncelliğini yitirir, bu dosya dahil. JEP'ler
yitirmez, çünkü onlar spesifikasyonun **ta kendisidir**.

- [openjdk.org/jeps/0](https://openjdk.org/jeps/0) dizindir
- `openjdk.org/jeps/<numara>` tek bir JEP'tir
- `openjdk.org/projects/jdk/<sürüm>` bir sürümde ne yayımlandığıdır

Alanlar, kullanışlılık sırasına göre:

| Alan | Nedeni |
|---|---|
| **Status** | yalnızca *Closed/Delivered* yayımlandı demektir |
| **Release** | Release alanı yoksa hiçbir şey için planlanmamıştır |
| **Summary** | tek paragraf, çoğu zaman ihtiyacınız olan tek şey |
| **Motivation** | *neden* var olduğu. İnsanların atladığı ve okumaya değer bölüm. |
| **Alternatives** | neyin reddedildiği. "Neden şunu yapmadılar ki" sorularının çoğunu cevaplar. |

Sözcük dağarcığı:

| Terim | Anlamı |
|---|---|
| **Preview** | eksiksiz ve belirtilmiş, henüz kalıcı değil. `--enable-preview` ister. Değişebilir ya da geri çekilebilir. |
| **İnkübatör** | bir `jdk.incubator` modülündeki API. `--add-modules` ister. Preview'dan daha az yerleşmiş. |
| **Deneysel** | `-XX:+UnlockExperimentalVMOptions` arkasındaki bir JVM özelliği |
| **Final** | kalıcı. Kaldırılmayacak. |

> **Preview tur numarası pratik bir işarettir, ilginç bir ayrıntı değil.**
> Yapılandırılmış eşzamanlılık yedincisinde. Switch için desen eşleme dört tur
> aldı. Record'lar iki. Yüksek bir numara tasarımın hâlâ tartışıldığı ve API'nin
> genellikle turlar arasında değiştiği anlamına gelir, ki burada tam olarak bu
> oldu: daha önceki yapılandırılmış eşzamanlılık preview'larına göre yazılmış kod
> bu sürümde derlenmiyor.

Herhangi bir özellik hakkında sorulacak üç soru:

1. Final mi? Değilse hangi tur, ve öncesinde kaç tane vardı?
2. Hangi sürüm teslim etti, ve o bir LTS mi?
3. Desteklemek zorunda olduğunuz asgari sürüm onu gerçekten kullanabilir mi?

### Preview özellikler, çalıştırılmış

Aşağıdaki her iki örnek de `// EXPECT: preview` işaretleyicisi taşır, dolayısıyla
doğrulama scripti onları `--enable-preview --source 27` ile çalıştırır.

**[Yapılandırılmış eşzamanlılık](https://openjdk.org/jeps/533), yedinci preview.**
Modül 18'in `ExecutorService` sınıfı iki görev gönderip birini unutmanıza izin
verir. Hiçbir şey onları birbirine bağlamaz, ve kimsenin incelemediği bir `Future`
istisnasını yutar.

```java
try (var scope = StructuredTaskScope.open()) {
    var user = scope.fork(() -> loadUser());
    var orders = scope.fork(() -> loadOrders());
    scope.join();
    return combine(user.get(), orders.get());
}
```

```
  user:ada and orders:3

  scope failed: ExecutionException
  cause: the fast one failed
```

İkinci durum anında dönüyor. Hızlı görev fırlattığı anda iki saniyelik bir kardeş
görev, boşuna beklenmek yerine **iptal edildi**.

Ve Modül 19'dan bir halkayı kapatıyor:

```
  child sees: req-7
```

Modül 19, düz bir çocuk iş parçacığının `ScopedValue` miras almadığını ve miras
almanın yapılandırılmış eşzamanlılık gerektirdiğini söylemişti. Yukarıdaki satır
o. Çalışıyor çünkü kapsam, ebeveynin çocuklarından uzun yaşayacağını garanti
ediyor, dolayısıyla bir çocuk okurken bağlama sona eremiyor.

**[İlkel tip desenleri](https://openjdk.org/jeps/532), beşinci preview.**

```
  42 instanceof byte:  true
  300 instanceof byte: false
```

Referans tipi için `instanceof` "bu nesne o tipte mi" diye sorar. İlkel tip için
farklı bir şey sorar: **bu değer kayıpsız dönüştürülebilir mi**.

Bugün o daraltma bir dönüşümdür, ve `(byte) 300` hiçbir uyarı olmadan `44` verir.
Bu Modül 02'nin taşmasıdır. Desen biçimi sarmak yerine reddeder.

**İkisini de üretimde kullanmalı mısınız?** Hayır. Bir preview sınıf dosyası,
bayrakla bile farklı bir sürüm tarafından reddedilir, dolayısıyla her JDK
yükseltmesi derlemenizi kırabilir. Okuyun, deneyin, ve kesinleşecekleri turu JEP
üzerinden izleyin.

## Çalıştırın

```bash
java modules/22-whats-new/examples/WhatChanged.java
java modules/22-whats-new/examples/ReleasesAndLts.java
java modules/22-whats-new/examples/HowToReadAJep.java

java --enable-preview --source 27 modules/22-whats-new/examples/PreviewStructuredConcurrency.java
java --enable-preview --source 27 modules/22-whats-new/examples/PreviewPrimitivePatterns.java

./scripts/verify-examples.sh modules/22-whats-new
```

## Sık Yapılan Hatalar

**Üretimde preview özellikleri etkinleştirmek.** Bir preview sınıf dosyası,
bayrakla bile farklı bir JDK sürümü tarafından reddedilir.

**Üretim sistemi için en yeni sürümün peşinden koşmak.** Altı ay yama, sonra
hiçbir şey.

**JEP yerine bir blog yazısı okumak.** O yazı farklı bir preview turuna göre
yazılmıştı.

**`java.version` ayrıştırmak.** `Runtime.version()` kullanın.

**Bir preview API'sinin çalıştığı için kararlı olduğunu varsaymak.** Yedi tur,
değişmiş olması için yedi şans demektir.

**Bir sürümü JEP sayısına göre değerlendirmek.** İlginç soru, son LTS'ten beri
neyin biriktiğidir.

## Özet Çıkarımlar

- **JDK 27, Java yazma biçiminizi değiştiren hiçbir şey teslim etmedi.**
  Kesinleşen işi çalışma zamanı ve güvenlik.
- **Üretim için önemli olan numara LTS'tir**; öğrenme için önemli olan en yeni
  sürümdür.
- **Değer, herhangi bir tek sürümün içinde değil LTS sürümleri arasında birikir.**
- **Bir JEP spesifikasyondur ve hakkındaki her makaleden uzun yaşar.** Motivation
  bölümünü okuyun.
- **Preview tur numarası size bir tasarımın ne kadar yerleştiğini söyler.**
- **Preview özellikleri asla yayımlamayın.** Sınıf dosyaları diğer sürümler
  tarafından reddedilir.

## Ödev

[homework/README.md](homework/README.md)

Bir özelliği birincil kaynaklardan araştırın ve kullanıp kullanamayacağınıza karar
verin. Referans çözüm
[`solutions/22-whats-new/`](../../../solutions/22-whats-new/) altında.
