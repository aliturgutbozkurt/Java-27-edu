# Ödev 02: Hesabı Tutan Fiş

Bir dükkânın kasası yanlış yuvarlıyor ve kimse sebebini bulamıyor. Siz
yuvarlamayan birini yazacaksınız, bu da para tipini doğru seçmek demek.

## Görev

`Receipt.java` adında bir kompakt kaynak dosyası oluşturun. Şunları yapmalı:

1. Üç ürünü ad ve fiyatla tutsun: `19.99`, `4.05` ve `0.99`.
2. Her ürünü kendi satırında yazdırsın.
3. Ara toplamı, yüzde 20 vergiyi ve toplamı yazdırsın.
4. Aritmetiği tam doğru yapsın, ve *biçimlendirmeyi* de tam doğru yapsın.

Hedef çıktı:

```
Bread           19.99
Milk             4.05
Gum              0.99
-------------------
Subtotal        25.03
Tax (20%)        5.01
Total           30.04
```

## Para Kuralı

Fiyatlar için `double` **kullanmayın**. Modül 02 nedenini gösterdi: `0.1 + 0.2`
sonucu `0.3` değildir ve her satırda bir kuruşun yüzde biri kadar sapan bir kasa
eninde sonunda denetimden geçemez.

Her fiyatı **kuruş** sayan bir `long` olarak saklayın, yani `19.99` değeri `1999`
olur. Tüm aritmetiği tam kuruşlarla yapın ve yalnızca yazdırma anında metne
çevirin.

Kuruşu para birimi olarak biçimlendirmek için tam sayı bölmesi ve kalan size iki
yarıyı da verir:

```java
long cents = 405;
String shown = (cents / 100) + "." + (cents % 100);
```

Bunu süt için çalıştırın ve ne aldığınıza bakın. Yanlış, ve *neden* yanlış
olduğunu çözmek bu alıştırmanın en değerli kısmı. Bu fişin altı satırından dördü
aynı hataya yakalanıyor, yani savuşturabileceğiniz bir uç durum değil.

## Yuvarlama Kararı

2503 kuruşun yüzde 20'si tam olarak 500,6 kuruş. Bir kuruşun onda altısını tahsil
edemezsiniz, dolayısıyla ona ne olacağına karar vermelisiniz ve kodunuz bu kararı
kazara değil görünür biçimde vermelidir.

Ne seçerseniz seçin, ne seçtiğinizi ve nedenini söyleyen bir yorum bırakın.

## Sonra İki Tuzağı Yeniden Üretin

`Traps.java` adında ikinci bir dosya ekleyin ve şunları gösterip açıklayın:

1. **Kutulanmış tam sayılarda `==` tuzağı.** Aynı türden değerle `true` veren bir
   karşılaştırma ve `false` veren bir tane yazdırın, sonra gerçekte ne demek
   istediğinizi gösteren `.equals` sonucunu yazdırın.
2. **String değişmezliği.** Bir String metodunu sonucu saklamadan çağırın,
   değişmeyen değeri yazdırın, sonra doğru şekilde yapın.

Her birinin *neden*ini kendi kelimelerinizle açıklayan bir yorum gerekiyor.
Örneklerdeki ifadeleri kopyalamak sayılmaz. Amaç, geri anlatabilmeniz.

## Kabul Kriterleri

- [ ] Her iki dosya da `java Receipt.java` ve `java Traps.java` ile çalışıyor
- [ ] `Receipt.java` içinde hiçbir yerde `double` ya da `float` geçmiyor
- [ ] Vergi hesaplanıyor, sabit olarak yazılmıyor
- [ ] `4.05` değeri `4.5` değil `4.05` olarak yazdırılıyor
- [ ] Ara toplam, vergi ve toplam tam olarak iki ondalık basamakla yazdırılıyor
- [ ] Sütunlar hizalı
- [ ] Bir yorum, kuruşun kesrine ne olduğunu ve nedenini belirtiyor
- [ ] `Traps.java` kutulanmış `==` için hem `true` hem `false` veren durum gösteriyor
- [ ] Her tuzak kendi kelimelerinizle bir açıklama taşıyor

## İleri Seviye

Kasa artık yüzde 7,5 oranına ihtiyaç duyuyor. 2503 kuruş üzerinden yüzde 7,5
vergi 187,725 kuruş eder.

Bunu uygulayın, kesre ne olacağına karar verin ve seçiminizi bir yorumda
savunun. Tek bir doğru cevap yok, tam da bu yüzden düşünmeye değer. Gerçek ödeme
sistemlerinin bunun için yayımlanmış kuralları vardır, bazıları toplam yerine
satır bazında yuvarlamayı gerektirir ve bu da yine farklı bir toplam verir.

## İpucu, biçimlendirme sizi zorluyorsa

`String.format` dolgu ve sıfırla doldurmayı anlar:

```java
String.format("%-12s %8s", name, amount)   // left-align 12, right-align 8
String.format("%d.%02d", whole, remainder) // %02d forces two digits
```

İkincisi, daha önce bulduğunuz hatanın çözümü.
