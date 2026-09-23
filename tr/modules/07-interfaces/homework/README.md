# Ödev 07: Yetenekler, Çakışmalar ve Kırılgan Bir Temel

Üç bölüm. Yeteneklerle tasarlayın, Java'nın sizin yerinize çözmeyi reddettiği bir
çakışmayı çözün, sonra "kompozisyonu tercih edin" sözünü slogandan fazlası yapan
hatayı yeniden üretin.

## Birinci Bölüm: Akıllı Cihazlar

`Devices.java` dosyasını klasik biçimde oluşturun.

İki arayüz tanımlayın:

- `Switchable` — uygulayıcının sağlaması gereken bir `name()` metodu, artı
  **varsayılan** metotlar olarak `turnOn()` ve `turnOff()`
- `Dimmable` — varsayılan olarak `setLevel(int)` ve `full()`, ve seviyeyi
  0..100 aralığına sıkıştırmak için bir **özel arayüz metodu**, böylece kural bir
  kez yazılsın

Sonra üç cihaz sınıfı:

| Cihaz | Uyguladığı |
|---|---|
| `Lamp` | ikisi de |
| `Thermostat` | yalnızca `Switchable` |
| `Speaker` | ikisi de, ama `setLevel` metodunu geçersiz kılar |

`main` içinde cihazlar üzerinde **arayüz tipiyle** döngü kurun, asla somut sınıfla
değil. Bir `Thermostat` nesnesini `Dimmable` beklenen yere geçirmenin imkânsız
olduğunu gösterin ve bunu nereden bildiğinizi bir yorumda söyleyin.

## İkinci Bölüm: Çözmek Zorunda Olduğunuz Bir Çakışma

Her biri aynı ad ve imzaya sahip **varsayılan** metot içeren `Timestamped` ve
`Versioned` tanımlayın. İkisini birden uygulayan bir sınıf yazın.

Derlenmeyecek. Hatayı okuyun, sonra açıkça çözün.

Bir yorumda aldığınız hatayı alıntılayın ve Python'un metot çözümleme sırasının
sessizce seçeceği yerde Java'nın sizin yerinize seçmeyi neden reddettiğini
açıklayın.

## Üçüncü Bölüm: Bir Alt Sınıfı Kırın

Eklenen her şeyin kaydını tutan, `add` ve `addAll` metotlarını geçersiz kılan
`LoggingHashSet extends HashSet<String>` yazın.

Üç öğeyi **`addAll` kullanarak** ekleyin ve kayıt boyutunu yazdırın. 3 olmayacak.

Sonra bir `HashSet` genişletmek yerine **tutan**, aynı iki metoda sahip
`LoggingCollection` yazın. Aynı üç öğeyi ekleyin ve kayıt boyutunu yazdırın.

### Açıklayın

Kendi kelimelerinizle şunları kapsayan bir yorum yazın:

1. Mekanizma. Miras alan sürüm neden fazla sayıyor?
2. `LoggingHashSet` sınıfının kendi kaynağında neden yanlış görünen hiçbir şey yok?
3. Kompoze edilmiş sürüm farklı olarak ne yapıyor ve sorunu nasıl ortadan
   kaldırıyor?

## Kabul Kriterleri

- [ ] `java Devices.java` ile çalışıyor
- [ ] `Switchable` ve `Dimmable` her biri en az bir `default` metot içeriyor
- [ ] `Dimmable` varsayılanlarının kullandığı bir `private` arayüz metodu içeriyor
- [ ] `Speaker` bir varsayılanı geçersiz kılıyor, `Lamp` kılmıyor
- [ ] `main` içindeki döngüler arayüz tipiyle yazılmış, sınıfla değil
- [ ] Çakışan varsayılanlı sınıf `Interface.super.method()` ile çözüyor
- [ ] Bir yorum gördüğünüz gerçek derleyici hatasını alıntılıyor
- [ ] `LoggingHashSet` 3 olmayan bir kayıt boyutu yazdırıyor
- [ ] `LoggingCollection` 3 yazdırıyor
- [ ] Açıklamanız belirtiyi değil mekanizmayı isimlendiriyor

## İleri Seviye

`LoggingHashSet` sınıfını, diğer her şey aynı kalacak şekilde
`ArrayList<String>` genişletecek biçimde değiştirin. Sayı doğru çıkacak.

Bunun özgün hata hakkında size ne söylediğini ve incelediğiniz ama ebeveynini
okumadığınız bir alt sınıfa ne kadar güvenebileceğinizi bir yorumda yazın.

## İpucu, üçüncü bölüm hemen 3 veriyorsa

Hangi koleksiyonu genişlettiğinizi kontrol edin, ve öğeleri üç ayrı `add` çağrısı
yerine `addAll` ile eklediğinizden emin olun. Her iki ayrıntı da önemli ve
bunlardan biri alıştırmanın tamamı.
