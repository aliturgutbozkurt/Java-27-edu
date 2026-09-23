# Ödev 22: Kendiniz Araştırın

Bu müfredatın son ödevi Java yazmakla ilgili değil. "Bunu kullanabilir miyim"
sorusunu birincil kaynaklardan cevaplamakla ilgili, çünkü buradaki diğer her
becerinin raf ömrü var ve bunun yok.

## Birinci Bölüm: Üç Özellik, Araştırılmış

`FeatureResearch.java` dosyasını klasik biçimde oluşturun.

Bu müfredatta geçen üç özellik seçin, en az biri **Final** ve en az biri
**Preview** olsun. Her biri için [openjdk.org](https://openjdk.org/jeps/0)
üzerinden şunları bulup kaydedin:

| Alan | Nerede bulunur |
|---|---|
| JEP numarası | JEP dizini ya da sürüm sayfası |
| Durum | JEP'in kendi başlığı |
| Teslim eden sürüm | Release alanı |
| O sürüm LTS mi? | sürüm takvimi |

Bunları bir tablo olarak yazdırın, ve altına **kullandığınız bağlantıları
yazdırın**. Kaynağı olmayan bir iddia burada sayılmaz.

## İkinci Bölüm: Üç Soru

Modülün kontrol listesini bir özelliğe uygulayan bir metot yazın:

1. Final mi? Değilse hangi tur?
2. Hangi sürüm teslim etti, ve o bir LTS mi?
3. Desteklemek zorunda olduğunuz asgari sürüm nedir?

Bir karar yazdırmalı. **JDK 21** desteklemek zorunda olduğunuzu varsayarak bir
Final ve bir Preview özelliğe karşı çalıştırın.

### İlginç Sonuç

İkinizden biri Final olacak, bir LTS'te yayımlanmış olacak, ve **yine de
kullanılamaz** olacak.

Nedenini açıklayan bir yorum yazın. Cevabınız bir özelliğin **güvenli** olmasıyla
**erişilebilir** olması arasında ayrım yapmalı, çünkü bu ikisini karıştırmak
birinin kimsenin bağımlı olamayacağı bir kütüphane yayımlama biçimidir.

**Bir uygulama notu.** Sürümleri metin olarak değil sayı olarak alın. `"JDK 21"`
ile `"JDK 25"` metinlerini karşılaştırmak tesadüfen çalışır; `"JDK 9"` ile
`"JDK 25"` karşılaştırması çalışmaz. Bunun neden `java.version` ayrıştırmakla
aynı hata olduğunu bir yorumda söyleyin.

## Üçüncü Bölüm: Bir Geçmiş Bölümü Okuyun

İkiden fazla preview turu geçirmiş bir özellik seçin. JEP'ini açın ve **History**
bölümünü bulun.

Her turu listeleyin: JEP numarası, sürüm, ve inkübatör mü preview mü olduğu.

### Sonra Cevaplayın

- İnkübatör turları dahil toplam kaç tur var?
- JEP **numarası** turlar arasında değişti mi, ve bu size tekrarlanan bir
  numaranın söylemeyeceği neyi söylüyor?
- Okumadan önce varsaydığınıza kıyasla geçmişte sizi şaşırtan bir şey oldu mu?

Son soru alıştırmanın asıl amacı. Ne beklediğinizi ve ne bulduğunuzu yazın.

Sonra hızla kesinleşmiş bir özellikle karşılaştırın ve gelecekte kullanacağınız
kestirme kuralı belirtin.

## Dördüncü Bölüm: Sürümü Düzgün Denetleyin

`Runtime.version()`, `feature()` ve `java.version` özelliğini yazdırın.

Sonra asgari gerekli sürüm için bir denetim yazın.

### Açıklayın

`java.version` ayrıştırmanın neden tuzak olduğunu bir yorumda yazın. Ayrıntılı
olun: Java 9 öncesi biçim neydi, şimdi ne, her birinde sürüm numarasını hangi
bileşen ifade ediyordu, ve noktalara göre bölüp 1. indeksi alan koda ne oldu?

## Kabul Kriterleri

- [ ] `java FeatureResearch.java` ile çalışıyor
- [ ] JEP numarası, durum, sürüm ve LTS bilgisiyle üç özellik
- [ ] Kaynak bağlantıları yazdırılıyor
- [ ] Üç soru metodu bir Final ve bir Preview özellik için karar yazdırıyor
- [ ] Bir özellik Final, bir LTS'te ve yine de kullanılamaz, sebebi açıklanmış
- [ ] Güvenli ile erişilebilir ayrımı açıkça belirtilmiş
- [ ] Sürümler sayı olarak karşılaştırılıyor, nedenini söyleyen yorumla
- [ ] İnkübatör turları dahil tam bir preview geçmişi listelenmiş
- [ ] JEP numarasının değişmesi işareti açıklanmış
- [ ] Ne beklediğiniz ile ne bulduğunuz yazılmış
- [ ] Gelecekteki özellikler için bir kestirme kural belirtilmiş
- [ ] `Runtime.version()` kullanılıyor, `java.version` tuzağı açıklanmış

## İleri Seviye

Preview aşamasından sonra kesinleşmek yerine **geri çekilmiş** bir özellik bulun.

JEP'inin nedenini ne söylediğini, ve bunun preview sırasında onu kullanan kod
yayımlamış biri için ne ima ettiğini bir yorumda yazın.

## İpucu, History bölümünü bulamıyorsanız

JEP'in alt kısmına yakın, Alternatives ve Risks bölümlerinin altındadır. Belgenin
en kullanışlı ve kimsenin kaydırıp gitmediği kısmıdır.
